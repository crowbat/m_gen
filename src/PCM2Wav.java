import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;


public class PCM2Wav {

	String input;
	String output;
	String header;
	FileOutputStream outFile;
	double sampleRate;
	int bitsPerSample;
	int dataLength;
	
	public static void main(String[] args) throws FileNotFoundException {
		PCM2Wav p = new PCM2Wav();
		p.getInput();
		p.getOutput();
		p.getSampleRate();
		p.getBitsPerSample();
		p.getDataLength();
		p.getHeader();
		p.writeHeader();
		p.writeWav();
	}
	
	void getInput() {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the input file: ");
		input = in.next();
	}
	
	void setInput(String in) {
		input = in;
	}
	
	void getOutput() throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the output file: ");
		output = in.next();
		outFile = new FileOutputStream(output);
	}

	void setOutput(String out) {
		output = out;
	}
	
	void getSampleRate() {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the sample rate: ");
		sampleRate = Double.parseDouble(in.next());
	}
	
	void setSampleRate(double sampRate) {
		sampleRate = sampRate;
	}
	
	void getBitsPerSample() {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the number of bits per sample (8 or 16): ");
		bitsPerSample = Integer.parseInt(in.next());
	}
	
	void setBitsPerSample(int bps) {
		bitsPerSample = bps;
	}
	
	void getDataLength() throws FileNotFoundException {
		Scanner readFileLength = new Scanner(new File(input));						//Create scanner to scan through file and count how many integers
		while(readFileLength.hasNextInt()) {										//Loop until end of file to get how many integers are in the file
			int temp = readFileLength.nextInt();
			dataLength +=1;
		}
	}
	
	void getHeader() {
		String chunkID = "52494646";												//This forms the "RIFF" part of the header
		String chunkSize;															//This represents the size of the subchunks of the wav file (not including data)
		String format = "57415645";													//This forms the "WAVE" part of the header
		String subChunk1ID = "666d7420";											//This forms the "fmt " part of the header
		String subChunk1Size = "10000000";											//This value is the size of the first subchunk (usually 16)
		String audioFormat = "0100";												//This value will be one for most wav files
		String numChannels = "0100";												//This value will be one for mono files and two for stereo files
		String byteRate;															//Bytes per second
		String blockAlign;															//Bytes per sample
		String subChunk2ID = "64617461";											//This forms the "data" part of the header
		String subChunk2Size;														//This is the size of the data part of the wav file (calculated with the length of the file passed into the method)
		String sampleRateStr;
		String bitsPerSampleStr;
		
		byteRate = Integer.toString((int) sampleRate * bitsPerSample / 8);
		blockAlign = Integer.toString(bitsPerSample / 8);
		subChunk2Size = Integer.toString(dataLength * bitsPerSample / 8);
		chunkSize = Integer.toString(Integer.parseInt(subChunk2Size) + 36);

		sampleRateStr = toLittleEndian("8000", 4);								//This section converts all non-little endian numbers into little endian hex numbers
		bitsPerSampleStr = toLittleEndian(Integer.toString(bitsPerSample), 2);
		byteRate = toLittleEndian(byteRate, 4);
		blockAlign = toLittleEndian(blockAlign, 2);
		subChunk2Size = toLittleEndian(subChunk2Size, 4);
		chunkSize = toLittleEndian(chunkSize, 4);
		
		header = chunkID + chunkSize + format + subChunk1ID + subChunk1Size + audioFormat + numChannels + sampleRateStr + byteRate + blockAlign + bitsPerSampleStr + subChunk2ID + subChunk2Size;
	}
	
	void writeHeader() throws FileNotFoundException {		//Method for creating a header for the wav file and writing it to the output file
		writeHexToWav(header, outFile);
	}

	void writeWav() {
		try {
			int count = 0;
			Scanner readFileContent = new Scanner(new File(input));				//Create scanner to scan through file later to actually process each integer
			while(readFileContent.hasNextInt()) {								//Loops through integers to convert them to hex and write to output file
				int x = readFileContent.nextInt();
				if (x >= 0) {														//If integer is positive, simply convert to little endian hex and write to file
					writeHexToWav(toLittleEndian(Integer.toString(x), 2), outFile);
				}
				else {															//If integer is negative, the two's complement of the number in hex must be calculated first
					writeHexToWav(toLittleEndian(Integer.toString(x + 65536), 2), outFile);
				}
				count += 1;
				if (count % 10000 == 0) {										//For every 10000 numbers written, display a notification
					System.out.println("Written " + count + " numbers");
				}
			}
			outFile.close();
		} catch(IOException e) {
			System.out.println("Error in printing for main");
		}
	}
	
	String toLittleEndian(String num, int numBytes) {							//takes a base 10 number inside a string and converts it to a hex string in little endian form
		String hex = Integer.toHexString(Integer.parseInt(num));						//Creates a string with a hex value equal to the number received
		char[] hexArray = hex.toCharArray();									//Creates an array to hold each hex digit from the string
		if (hexArray.length % 2 ==1) {										//If there are an odd number of digits, add a 0 to the front to make the length even
			hex = "0" + hex;
			hexArray = hex.toCharArray();
		}
		String end = "00";											//This will be added on the end of the little endian value to pad the number so it has the correct number of digits
		String value = "";											//This string will hold the ending little endian string
		for(int i = hexArray.length - 2; i>=0; i=i-2) {								//For each digit in the hex string, add it to the value string
			value+=hexArray[i];
			value+=hexArray[i+1];
		}
		for(int i = 0; i < numBytes - hexArray.length/2 ; i++) {						//If the length of the end string is not the desired length, add zeros until it is
			value+=end;
		}
		return value;
	}
	
	void writeHexToWav(String hex, FileOutputStream outFile) {						//Takes a string of hex values, converts every two hex numbers into a character and writes the character to the output file
		try {
	  		for( int i=0; i<hex.length()-1; i+=2 ){								//Loop index counts up by 2 through the hex string
	      		String out = hex.substring(i,(i+2));							//Takes out a substring of length 2 from the main hex string
				outFile.write((char) (Integer.parseInt(out,16)));					//Converts the substring into a character and writes to the outfile
	  		}
		} catch(IOException e) {
			System.out.println("Error in printing for write HexToWav");
		}
	}
}
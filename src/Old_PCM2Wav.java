import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;


public class Old_PCM2Wav {
	public static void main(String[] args) throws FileNotFoundException {
		try{
			Old_PCM2Wav a = new Old_PCM2Wav();
			Scanner in = new Scanner(System.in);
			int count = 0;
			int dataLength = 0;
			String input;

			System.out.print("Enter the file to be converted: ");					//Get name of file with integers to be converted
			input = in.next();
			Scanner readFileLength = new Scanner(new File(input));						//Create scanner to scan through file and count how many integers
			Scanner readFileContent = new Scanner(new File(input));						//Create scanner to scan through file later to actually process each integer
			while(readFileLength.hasNextInt()) {								//Loop until end of file to get how many integers are in the file
				int temp = readFileLength.nextInt();
				dataLength +=1;
			}
			System.out.println("\n" + dataLength + " integers found" + "\n");

			System.out.print("Enter the desired name for the wav output file: ");				//Lets user input the desired output file name
			input = in.next();
			FileOutputStream outFile = new FileOutputStream(input);
			
			a.writeHeader(dataLength, outFile, a);								//Takes length of the integer file and writes the header to the output file
			while(readFileContent.hasNextInt()) {								//Loops through integers to convert them to hex and write to output file
				int x = readFileContent.nextInt();
				if (x>=0) {										//If integer is positive, simply convert to little endian hex and write to file
					a.writeHexToWav(a.toLittleEndian(Integer.toString(x),2), outFile);
				}
				else {											//If integer is negative, the two's complement of the number in hex must be calculated first
					a.writeHexToWav(a.toLittleEndian(Integer.toString(x+65536),2), outFile);
				}
				count+=1;
				if (count % 10000 == 0) {								//For every 10000 numbers written, display a notification
					System.out.println("Written " + count + " numbers");
				}
			}
			outFile.close();

		} catch(IOException e) {
			System.out.println("Error in printing for main");
		}
	}
		
	public void writeHeader(int size, FileOutputStream outFile, Old_PCM2Wav a) {					//Method for creating a header for the wav file and writing it to the output file

		String header = "";															//This string will hold the final elements of the total header
																					//The below variables represent sections of the header, and are expressed in little endian form
		String chunkID = "52494646";												//This forms the "RIFF" part of the header
		String chunkSize;															//This represents the size of the subchunks of the wav file (not including data)
		String format = "57415645";													//This forms the "WAVE" part of the header
		String subChunk1ID = "666d7420";											//This forms the "fmt " part of the header
		String subChunk1Size = "10000000";											//This value is the size of the first subchunk (usually 16)
		String audioFormat = "0100";												//This value will be one for most wav files
		String numChannels = "0100";												//This value will be one for mono files and two for stereo files
		String sampleRate;															//Sample rate of the wav file (user inputted)
		String byteRate;															//Bytes per second (calculated through user input)
		String blockAlign;															//Bytes per sample (calculated through user input)
		String bitsPerSample;														//Bits per sample (user inputted, either 8 or 16)
		String subChunk2ID = "64617461";											//This forms the "data" part of the header
		String subChunk2Size;														//This is the size of the data part of the wav file (calculated with the length of the file passed into the method)
		Scanner input = new Scanner(System.in);

		System.out.print("Enter the sampling rate: ");								//User inputted sampling rate
		sampleRate = input.next();
		System.out.print("Enter the number of bits per sample (8 or 16): ");		//User inputted bits per sample
		bitsPerSample = input.next();
		while(true) {																//Error checking for value of the bits per sample
			if (bitsPerSample.equals("8") || bitsPerSample.equals("16")) {
				break;
			} else {
				System.out.print("Sorry, try again: ");
				bitsPerSample = input.next();
			}
		}

		byteRate = Integer.toString(Integer.parseInt(sampleRate) * Integer.parseInt(bitsPerSample)/8);		//This section calculates the remaining variables based on the numbers the user inputted
		blockAlign = Integer.toString(Integer.parseInt(bitsPerSample)/8);
		subChunk2Size = Integer.toString(size * Integer.parseInt(bitsPerSample)/8);
		chunkSize = Integer.toString(Integer.parseInt(subChunk2Size) + 36);

		sampleRate = toLittleEndian(sampleRate,4);								//This section converts all non-little endian numbers into little endian hex numbers
		bitsPerSample = toLittleEndian(bitsPerSample,2);
		byteRate = toLittleEndian(byteRate,4);
		blockAlign = toLittleEndian(blockAlign,2);
		subChunk2Size = toLittleEndian(subChunk2Size,4);
		chunkSize = toLittleEndian(chunkSize,4);

		header = chunkID + chunkSize + format + subChunk1ID + subChunk1Size + audioFormat + numChannels + sampleRate + byteRate + blockAlign + bitsPerSample + subChunk2ID + subChunk2Size;
		a.writeHexToWav(header, outFile);
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
	      			String output = hex.substring(i,(i+2));							//Takes out a substring of length 2 from the main hex string
				outFile.write((char) (Integer.parseInt(output,16)));					//Converts the substring into a character and writes to the outfile

	  		}

		} catch(IOException e) {
			System.out.println("Error in printing for write HexToWav");
		}
	}
}
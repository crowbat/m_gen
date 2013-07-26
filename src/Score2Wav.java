import java.io.FileNotFoundException;
import java.util.Scanner;

public class Score2Wav {

	static String inputScoreFile;
	static String outputPCMFile;
	static String outputWavFile;
	
	public static void main(String[] args) throws FileNotFoundException {
		getInput();
		Score2PCM s = new Score2PCM();
		PCM2Wav p = new PCM2Wav();
		s.setInput(getInputScoreFile());
		p.setInput(getOutputPCMFile());
		s.setSampleRate();
		p.setSampleRate(s.getSampleRate());
		p.setBitsPerSample();
		s.generatePCM();
		s.output();
		p.modifyDataLength();
		p.modifyHeader();
		p.writeHeader();
		p.writeWav();
	}
	
	static void getInput() {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the input score file: ");
		inputScoreFile = in.next();
		outputPCMFile = "../PCM/" + inputScoreFile.substring(0, inputScoreFile.length() - 9) + "PCM.txt";
		outputWavFile = "../wav/" + inputScoreFile.substring(0, inputScoreFile.length() - 9) + "wav.wav";
		inputScoreFile = "../scores/" + inputScoreFile;
	}
	
	static String getInputScoreFile() {
		return inputScoreFile;
	}
	
	static String getOutputPCMFile() {
		return outputPCMFile;
	}
	
	static String getOutputWavFile() {
		return outputWavFile;
	}
}
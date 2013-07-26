import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import static java.lang.Math.*;

public class Score2PCM {

	ArrayList<double[]> PCM = new ArrayList<double[]>();
	double sampleRate;
	String input;
	String output;
	
	public static void main(String[] args) {
		Score2PCM s = new Score2PCM();
		s.setInput();
		s.setSampleRate();
		s.generatePCM();
		System.out.println("Generated PCM numbers");
		s.output();
	}
	
	void setInput() {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the name of the input file: ");
		input = in.next();
		output = "../PCM/" + input.substring(0, input.length() - 9) + "PCM.txt";
		input = "../scores/" + input;
		System.out.println("Input score file is " + input);
		System.out.println("Output PCM file is " + output);
	}
	
	void setInput(String in) {
		input = in;
		output = "../PCM/" + input.substring(10, input.length() - 9) + "PCM.txt";
		System.out.println("Input score file is " + input);
		System.out.println("Output PCM file is " + output);
	}
	
	void setSampleRate() {
		Scanner in = new Scanner(System.in);
		while(true) {
			try {
				System.out.print("Enter the desired sampling rate: ");						// Get sample rate
				sampleRate = in.nextDouble();
				break;
			} catch (Exception e) {
				System.out.print("Sorry, try again: ");
			}
		}
	}
	
	double getSampleRate() {
		return sampleRate;
	}
	
	void generatePCM() {
		double beatPerMin;
		double[] measurePCM = new double[0];
		int beatPerMeasure;
		int measureNum = 0;
		int lineNum = 0;
		int noteNum = 0;
		
		try {
			FileReader inFile = new FileReader(input);
			BufferedReader reader = new BufferedReader(inFile);
			
			String line = reader.readLine();
			String[] beatInfo = line.split(" ");
			beatPerMin = Double.parseDouble(beatInfo[0]);
			beatPerMeasure = Integer.parseInt(beatInfo[1]);
			
			while ((line = reader.readLine()) != null) {
				lineNum += 1;
				if (line.equals("Start")) {
					measureNum += 1;
					System.out.println("Measure " + measureNum);
					measurePCM = new double[(int) (sampleRate * beatPerMeasure * 60 / beatPerMin)];
				} else if (line.equals("End")) {
					PCM.add(measurePCM);
				} else {
					noteNum += 1;
					String[] noteInfo = line.split("[\\s\\t]+");
					if (!noteInfo[0].equals("//")) {
						double[] notePCM = Note.generatePCM(noteInfo[0], Integer.parseInt(noteInfo[1]),
											Double.parseDouble(noteInfo[3]) * 60 / beatPerMin, sampleRate);
						if (noteInfo.length == 5 && noteInfo[4].equals("Y")) {
							for (int i = (int) (sampleRate * (Double.parseDouble(noteInfo[2]) - 1) * 60 / beatPerMin), k = 0;
							i < measurePCM.length && k < notePCM.length; i++, k++) {
								measurePCM[i] += notePCM[k];
							}
						 } else {
							for (int i = (int) (sampleRate * (Double.parseDouble(noteInfo[2]) - 1) * 60 / beatPerMin), k = 0;
							i < measurePCM.length && k < notePCM.length * .95; i++, k++) {
								measurePCM[i] += notePCM[k];
							}
						}
					}
				}
			}
			System.out.println("Read " + noteNum + " notes");
		} catch (IOException e) {
			System.err.println("Error in Score2PCM: measure number " + measureNum + ", line number " + lineNum);
		}
	}
	
	void output() {
		try {
			FileWriter outFile = new FileWriter(output);
			PrintWriter out = new PrintWriter(outFile);
			
			for (int i = 0; i < PCM.size(); i++) {
				for (int k = 0; k < PCM.get(i).length; k++) {
					out.println((int) PCM.get(i)[k]);
				}
			}
			out.close();
		} catch (Exception e) {
			System.err.println("Error in Score2PCM output");
		}
	}
	
	String getOutputPCMFile() {
		return output;
	}
}


class Note {
	static HashMap<String, Double> noteFrequencies = new HashMap<String, Double>();
	static {
		noteFrequencies.put("C",32.7032);
		noteFrequencies.put("Cs",34.6478);
		noteFrequencies.put("Df",34.6478);
		noteFrequencies.put("D",36.7081);
		noteFrequencies.put("Ds",38.8909);
		noteFrequencies.put("Ef",38.8909);
		noteFrequencies.put("E",41.2034);
		noteFrequencies.put("F",43.6535);
		noteFrequencies.put("Fs",46.2493);
		noteFrequencies.put("Gf",46.2493);
		noteFrequencies.put("G",48.9994);
		noteFrequencies.put("Gs",51.9131);
		noteFrequencies.put("Af",51.9131);
		noteFrequencies.put("A",55.0);
		noteFrequencies.put("As",58.2705);
		noteFrequencies.put("Bf",58.2705);
		noteFrequencies.put("B",61.7354);
	}
	public static final int WAVE_AMP = 5000;							// Amplitude of default wave
	public static final double TWO_PI = 2 * PI;
	
	static double[] generatePCM(String noteName, int octave, double noteDuration, double sampleRate) {
		double[] notePCM = new double[(int) (noteDuration * sampleRate)];
		double increment = 1.0 / sampleRate;
		double octaveMultiplier;
		double frequency;
		
		if (octave == 0) {
			octaveMultiplier = 0.5;
		} else {
			octaveMultiplier = Math.pow(2, octave - 1);
		}		
		frequency = noteFrequencies.get(noteName) * octaveMultiplier;
		for (int i = 0; i < notePCM.length; i++) {
			notePCM[i] = WAVE_AMP * 1 * sin(TWO_PI * frequency * increment * i);
		}
		return notePCM;
	}
}
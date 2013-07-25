import java.io.*;
import java.util.*;
import static java.lang.Math.*;

public class Score2PCM {

	double[] PCM;
	double sampleRate;
	String input;
	String output;
	
	public static void main(String[] args) {
	}
	
	void getFiles() {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the name of the input file: ");
		input = in.next();
		
		System.out.println("Enter the name of the output file: ");
		output = out.next();
	}
	
	void getSampleRate() {
		Scanner in = new Scanner(System.in);
		while(true) {
				try {
					System.out.print("Enter the desired sampling rate: ");						//Get sample rate
					sampleRate = in.nextDouble();
					break;
				} catch (Exception e) {
					System.out.print("Sorry, try again: ");
				}
			}
		}
	}
	
	void generatePCM() {
		try {
			FileReader inFile = new FileReader(input);
			BufferedReader reader = new BufferedReader(inFile);
			double beatPerMin;
			int beatPerMeasure;
			int measureNum = 0;
			
			String line = reader.readLine();
			String[] data = line.split(" ");
			beatPerMin = Double.parseDouble(data[0]);
			beatPerMeasure = Integer.parseInt(data[1]);
		}
	}
}


class Music {
	BufferedReader reader;										//For reading input file
	boolean done = false;										//Tells if done reading file
	String input;
	String line;											//Holds line read in from file
	PrintWriter out;
	int measureNum = 0;										//Keeps track of measure number
	double beatPerMin;										//Holds beats per minute
	double sampleRate;										//Keeps track of sample rate
	int beatPerMeasure;										//Holds beats per measure
	Scanner in = new Scanner(System.in);

	public Music(BufferedReader a, PrintWriter b, double rate) {
		reader = a;										//Set bufferedreader
		out = b;
		sampleRate = rate;
	}

	public void compose () {
		try {
			String line = reader.readLine();						//Obtain a line from input file
			String[] data = line.split(" ");						//Split the line using spaces into sections
			beatPerMin = Double.parseDouble(data[0]);					//Find out what the beats per minute is
			beatPerMeasure = Integer.parseInt(data[1]);					//Find out what the beats per measure is
			while((line = reader.readLine()) != null) {					//While there is a next line:
				if (line.equals("Start")) {						//If start of measure signal,
					measureNum+=1;
					Measure a = new Measure(reader, measureNum, sampleRate, beatPerMeasure, beatPerMin, out);	//Create new measure object
					a.action();							//Perform action
				} else {
					out.close();
					System.out.println("Thank you for using MusicGenerator");
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Error in reading header data");
		}
	}
}


class Measure {
	int numBeats;														//Number of beats in a measure
	double noteLocation;													//Beat location of the note
	double numBeatPerMin;													//Number of beats in one minute
	int measureID;														//Measure number
	int octave;														//Holds octave number
	String name;
	double sampleRate;													//Sampling rate of the sound file
	double duration;													//How long the note will last
	double[] sampleValues;
	boolean done = false;													//Tests if we want to add another note or not
	boolean continuous;													//Whether the note is to last the full duration
	PrintWriter out;
	BufferedReader reader;
	String line;
	Scanner in = new Scanner(System.in);
	Map<String, Double> notes = new HashMap<String, Double>();								//Create map object holding notes and frequencies



	public Measure(BufferedReader b, int number, double rate, int beatPerMeasure, double beatPerMin, PrintWriter a) {	//Set all values passed in
		reader = b;
		measureID = number;
		sampleRate = rate;
		numBeats = beatPerMeasure;
		numBeatPerMin = beatPerMin;
		out = a;
		sampleValues = new double[(int) (rate*beatPerMeasure*(60/beatPerMin))];
		for (int i = 0; i < sampleValues.length; i++) {
			sampleValues[i] = 0.0;
		}

		notes.put("C",32.7032);
		notes.put("Cs",34.6478);
		notes.put("Df",34.6478);
		notes.put("D",36.7081);
		notes.put("Ds",38.8909);
		notes.put("Ef",38.8909);
		notes.put("E",41.2034);
		notes.put("F",43.6535);
		notes.put("Fs",46.2493);
		notes.put("Gf",46.2493);
		notes.put("G",48.9994);
		notes.put("Gs",51.9131);
		notes.put("Af",51.9131);
		notes.put("A",55.0);
		notes.put("As",58.2705);
		notes.put("Bf",58.2705);
		notes.put("B",61.7354);
	}

	public void action() {
		try {
			while ((line = reader.readLine())!= null) {					//While there is a next line:
				if (line.equals("End")) {						//If end of measure, write measure
					writeMeasure();
					break;
				} else {
					String[] components = line.split(" ");				//Split line into components using space
					name = components[0];						//Obtain desired note name
					octave = Integer.parseInt(components[1]);			//Obtain note octave
					noteLocation = Double.parseDouble(components[2]);		//Obtain note location
					duration = Double.parseDouble(components[3]);			//Obtain note duration
					if (components[4].equals("Y")) {				//If there is a Y, note is continuous
						continuous = true;
					} else {
						continuous = false;
					}
					double powValue;						//Calculate value to multiply to note frequency
					if (octave == 0) {
					powValue = 0.5;
					} else {
						powValue = pow(2,octave-1);
					}
					Note a = new Note((notes.get(name) * powValue), duration, numBeatPerMin, continuous, sampleRate);	//Create new note object
					double[] temp = a.getSamples();										//Create array to hold PCM values for one note
					int sampleValueStart = (int) (sampleValues.length * (noteLocation - 1)/numBeats);			//Calculate where to start adding note values to the measure sample values
					for (int i = sampleValueStart, k = 0; i < sampleValues.length && k < temp.length; i++, k++) {		//Add note PCM values
						sampleValues[i] += temp[k];
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error in reading file");
		}
	}

	public void writeMeasure() {							//Method for writing measure PCM values to output file
		for(int i = 0; i < sampleValues.length; i++) {
			out.println((int) sampleValues[i]);
		}
	}
}



class Note {
	public static final int WAVE_AMP = 5000;							//Amplitude of default wave
	public static final double TWO_PI = 2 * PI;
	public static final double CONT_FACTOR = .95;						//Ratio of continuous note to non-continuous note
	public static final int SECONDS = 60;
	double frequency;													//frequency of note
	double length;														//length of a note in seconds
	boolean cont;														//Whether note is continuous
	double[] sampleValues;
	double t = 0.0;														//Time value to calculate sine values
	double increment;													//Time increment
	double sampleRate;
	int count = 0;

	public Note(double freq, double beatlength, double beatpermin, boolean continuous, double samplerate) {			//Set parameter values
		frequency = freq;
		length = (beatlength * (SECONDS/beatpermin));
		cont = continuous;
		sampleRate = samplerate;
		increment = 1/samplerate;
		sampleValues = new double[(int) (sampleRate*length)];
	}

	public double[] getSamples() {
		for (int i = 0; i < sampleValues.length; i++) {									//Set samplevalue array to all 0s
			sampleValues[i] = 0.0;
		}
		if (cont) {													//If continuous, calculate PCM values and add to samplevalue array
			while ((t<length) && (count < sampleValues.length)) {
				sampleValues[count] = WAVE_AMP * 1 * sin(TWO_PI * frequency * t);
				t += increment;
				count+=1;
			}
		} else {													//If not continuous, do same except leave last bit as 0
			while ((t < length * CONT_FACTOR) && (count < (int) (CONT_FACTOR * sampleValues.length))) {
				sampleValues[count] = (WAVE_AMP * 1 * sin(TWO_PI * frequency * t));
				t += increment;
				count += 1;
			}
		}
		return sampleValues;												//Return samplevalue array
	}
}
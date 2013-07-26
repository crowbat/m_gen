To compile all java files, go into ./src and run "javac -d ../bin *.java"
To run Score2Wav, go into ./bin and run "java Score2Wav"
Input score files should be stored in ./scores, but do not include the folder path when inputting file names into the program
During Score2Wav, an output PCM file will be generated in ./PCM
Output wav files will be generated in ./wav

Score .txt files should have the following format:
	First line contains beat per minute and beats per measure, separated by spaces
	Comments are denoted by "//" followed by a space, then the comment message
	To start a new measure, denote it with "Start" on its own line
	To end a measure, denote it with "End" on its own line
	A note has 5 parts (the 5th part is optional), and should be on its own line.  The parts are all separated by any number of spaces or tabs:
		Name of the note (use capital letters, sharps and flats are denoted by "s" or "f" connected to the note name
		Octave of the note (middle C is 4)
		Starting beat location (1 is the first possible beat in a measure)
		Beat duration
		(Optional) Include "Y" to signify that the note should continue playing directly up to the next note

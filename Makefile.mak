JCC = javac
JFLAGS = -g

default: Score2Wav.class Score2PCM.class PCM2Wav.class M_Gen.class

Score2Wav.class: Score2Wav.java
	$(JCC) $(JFLAGS) ./src/Score2Wav.java

Score2PCM.class: Score2PCM.java
	$(JCC) $(JFLAGS) ./src/Score2PCM.java
	
PCM2Wav.class: PCM2Wav.java
	$(JCC) $(JFLAGS) ./src/PCM2Wav.java
	
M_Gen.class: M_Gen.java
	$(JCC) $(JFLAGS) ./src/M_Gen.java
	
clean:
	$(RM) ./bin/*.class
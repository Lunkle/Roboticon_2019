package challenge1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class ColourReadingThread extends Thread {
	static final String COLOUR_WHITE = "W";
	static final String COLOUR_BLACK = "B";
	static final String COLOUR_UNKNOWN = "-";

	static float colourValue = 0;
//	static String colourString = COLOUR_UNKNOWN;

	// Initializing color sensor
	static Port s1 = TheProgram.brick.getPort("S1");
	static EV3ColorSensor colorSensor = new EV3ColorSensor(s1);

	public ColourReadingThread() {
	}

	int sampleNumber = 0;
	float mean = 0;

	// RUN
	// METHOD////////////////////////////////////////////////////////////////////////
	@Override
	public void run() {
		while (TheProgram.done == false) {
			colourValue = getColorValue();
			mean = (mean * sampleNumber + colourValue) / (sampleNumber + 1);
			sampleNumber++;
			System.out.println(colourValue);
		}
	}

	// Old Code don't change
	// Johnny the grammar police will hate the punctuation

	/*
	 * This code tells the light sensor to do its magic-k. It tells it to read
	 * multiple values very quickly, and return the average value.
	 */
	static SampleProvider redMode = colorSensor.getRedMode();
	static int sampleSize = redMode.sampleSize();
	static float[] sample = new float[sampleSize];

	public static float getColorValue() {
		redMode.fetchSample(sample, 0);
		float totalColourValue = 0;
		for (int i = 0; i < sampleSize; i++) {
			totalColourValue += sample[i];
		}
		float averageColourValue = totalColourValue / sampleSize;
		return averageColourValue;
	}

	/*
	 * Remember to change variables at contest. Check colours of map! Add extra
	 * colours variables as needed. This must always be done -- ALWAYS BE DONE --
	 * immediately after arriving.
	 */
	/*
	 * The following code turns original colour values into the respective colour
	 * strings (Black, White, Gray, etc.)
	 */
	public static String getColourString(float colourValue) {
		if (colourValue <= -0.1) {
			return COLOUR_UNKNOWN;
		} else if (colourValue <= TheProgram.OFFSET) {
			return COLOUR_BLACK;
		} else {
			return COLOUR_WHITE;
		}
	}

	public static String getColourString() {
		return getColourString(colourValue);
	}
}
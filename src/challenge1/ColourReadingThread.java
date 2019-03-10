package challenge1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class ColourReadingThread extends Thread {
	// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing
	static boolean printStuff = false;

	// Enum for the different colours of the map
	public static enum Colour {
		COLOUR_WHITE, COLOUR_RED, COLOUR_BLUE, COLOUR_GREEN, COLOUR_UNKNOWN
	}

	// This is the value to check against to see if the colour is actually a colour
	// or if it's just white.
	// At the competition, re-check this value.
	public static final float GRAYSCALE_THRESHOLD = 0.581f;

	// A variable used to check equality of floats.
	static final double ERROR_THRESHOLD = 0.000001;

	// Comment comment: change the comment to big program instead of small program.
	// This is the colour value that is updated and can be referenced by the small
	// program.
	static Colour colourValue = Colour.COLOUR_UNKNOWN;

	// Initializing color sensor
	static Port s1 = TheSmallProgrm.brick.getPort("S1");
	static EV3ColorSensor colorSensor = new EV3ColorSensor(s1);

	// RUN METHOD
	@Override
	public void run() {
		while (stopThread == false) {
			colourValue = getColorValue();
			print(colourValue.name());
			Delay.msDelay(2000);
		}
		colorSensor.close();
		doneThread = true;
	}

	// Old Code don't change
	// Johnny the grammar police will hate the punctuation

	/*
	 * This code tells the light sensor to do its magic-k. It tells it to read
	 * multiple values very quickly, and return the average value.
	 */

	/*
	 * Now it has been changed **Feb 14, 2019 by Donster Monster**
	 */

	/*
	 * Remember to change variables at contest. Check colours of map! Add extra
	 * colours variables as needed. This must always be done -- ALWAYS BE DONE --
	 * immediately after arriving.
	 */
	static SampleProvider RGBMode = colorSensor.getRGBMode();
	static float[] RGBSample = new float[RGBMode.sampleSize()];
	static int sampleSize = 100;

	public static Colour getColorValue() {
		double R = 0;
		double G = 0;
		double B = 0;

		for (int i = 0; i < sampleSize; i++) {
			RGBMode.fetchSample(RGBSample, 0);
			R += RGBSample[0];
			G += RGBSample[1];
			B += RGBSample[2];
		}

		R *= 1.002386635 / sampleSize;
		G *= 1.276595745 / sampleSize;
		B *= 1.613316261 / sampleSize;

		double greatestValue = Math.max(Math.max(R, G), B);

//		double grayscale = (0.2126f * R + 0.7152f * G + 0.0722f * B);
		double grayscale = R + G + B;
		if (printStuff) {
			System.out.println("R: " + round(R, 5));
			System.out.println("G: " + round(G, 5));
			System.out.println("B: " + round(B, 5));
			System.out.println("Grayscale: " + grayscale);
		}

		if (grayscale >= GRAYSCALE_THRESHOLD) {
			return Colour.COLOUR_WHITE;
		} else if (Math.abs(greatestValue - R) <= ERROR_THRESHOLD) {
			return Colour.COLOUR_RED;
		} else if (Math.abs(greatestValue - G) <= ERROR_THRESHOLD) {
			return Colour.COLOUR_GREEN;
		} else if (Math.abs(greatestValue - B) <= ERROR_THRESHOLD) {
			return Colour.COLOUR_BLUE;
		} else {
			return Colour.COLOUR_UNKNOWN;
		}

	}

	// Kinda useless function but we can't be bothered to find some powerful Java
	// library to do it for us.
	static double round(double x, int d) {
		x *= Math.pow(10, d);
		x = Math.round(x);
		x /= Math.pow(10, d);
		return x;
	}

	// Some printing methods.
	private void print(String s) {
		if (printStuff) {
			System.out.println(s);
		}
	}

	@SuppressWarnings("unused")
	private void print(float s) {
		if (printStuff) {
			System.out.println(s);
		}
	}

}

package challenge1;

import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class UltrasonicReadingThread extends Thread {
	// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing
	static boolean printStuff = true;

	static float distanceValue;

	static SampleProvider distanceMode;
	static float[] distanceSample;

	public static void init() {
		distanceMode = TheSmallProgrm.ultrasonicSensor.getDistanceMode();
		distanceSample = new float[distanceMode.sampleSize()];
	}

	// RUN METHOD
	@Override
	public void run() {
		init();
		while (stopThread == false) {
			Delay.msDelay(1);
			distanceValue = getDistanceValue();
//			System.out.println(distanceValue);
		}
		doneThread = true;
	}

	public static float getDistanceValue() {
		float distance;
		distanceMode.fetchSample(distanceSample, 0);
		distance = distanceSample[0]; // Raw data from sensor
		distance = distance * 100; // Turn into cm
		// adjusts sensor data so that it matches the measured distance and adds the
		// distance from the sensor to the middle of the robot
		distance = 0.937f * distance;
		distance = 3.5f + (distance / 2.54f); // Converts to inches
		return distance;
	}

}

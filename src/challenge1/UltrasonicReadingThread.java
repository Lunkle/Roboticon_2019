package challenge1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class UltrasonicReadingThread extends Thread {// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing
	static boolean printStuff = false;

	// This is the distance value that is updated, and can be referenced by the
	// small
	// program.
	static float distanceValue = 18;

	// Initializing ultrasonic sensor
	static Port s2 = TheSmallProgrm.brick.getPort("S2");
	static EV3UltrasonicSensor ultrasonicSensor = new EV3UltrasonicSensor(s2);

	static SampleProvider distanceMode = ultrasonicSensor.getDistanceMode();
	static float[] distanceSample = new float[distanceMode.sampleSize()];

	@Override
	public void run() {
		while (stopThread == false) {
			print("=======");
			distanceValue = getDistanceValue();
			print(distanceValue);
			Delay.msDelay(1000);
		}
		ultrasonicSensor.close();
		doneThread = true;
	}

	private float getDistanceValue() {
		float distance;
		distanceMode.fetchSample(distanceSample, 0);
		distance = distanceSample[0]; // Raw data from sensor
		distance = distance * 100; // Turn into cm
		// adjusts sensor data so that it matches the measured distance and adds the
		// distance from the sensor to the middle of the robot
		distance = 0.937f * distance + 7.13f;
		distance = distance / 2.54f; // Converts to inches
		return distance;
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

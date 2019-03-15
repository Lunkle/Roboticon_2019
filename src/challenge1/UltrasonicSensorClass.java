package challenge1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class UltrasonicSensorClass {// Variables

	// This variable is used for printing
	static boolean printStuff = false;

	// Initializing ultrasonic sensor
	static Port s2 = TheSmallProgrm.brick.getPort("S2");
	static EV3UltrasonicSensor ultrasonicSensor = new EV3UltrasonicSensor(s2);

	static SampleProvider distanceMode = ultrasonicSensor.getDistanceMode();
	static float[] distanceSample = new float[distanceMode.sampleSize()];

	public static void init() {

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

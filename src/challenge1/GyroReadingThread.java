package challenge1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;

public class GyroReadingThread extends Thread {
	// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing.
	static boolean printStuff = false;

	// This is the angle value that is updated and can be referenced by the small
	// program.
	static float angleValue;

	static final double ERROR_THRESHOLD = 0.000001;

	// Initializing gyro sensor
	static Port s3 = TheSmallProgrm.brick.getPort("S3");
	static EV3GyroSensor gyroSensor = new EV3GyroSensor(s3);

	static SampleProvider angleMode = gyroSensor.getAngleMode(); // Up here
	static float[] angleSample = new float[angleMode.sampleSize()];

	// RUN METHOD
	@Override
	public void run() {

		resetGyro();
		while (stopThread == false) {
			angleValue = getAngle();
			print(angleValue);
		}
		gyroSensor.close();
		doneThread = true;
	}

	// Resets the gyro sensor.
	public void resetGyro() {
		gyroSensor.reset();
	}

	// Super cool -- get the angle method.
	private float getAngle() {
		angleMode.fetchSample(angleSample, 0);
		return angleSample[0];
	}

	// Some printing methods.
	@SuppressWarnings("unused")
	private void print(String s) {
		if (printStuff) {
			System.out.println(s);
		}
	}

	private void print(float s) {
		if (printStuff) {
			System.out.println(s);
		}
	}
}

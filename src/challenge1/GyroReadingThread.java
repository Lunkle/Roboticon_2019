package challenge1;

import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

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

	static SampleProvider angleMode;
	static float[] angleSample;

	// RUN METHOD
	@Override
	public void run() {
		init();
		while (stopThread == false) {
			Delay.msDelay(1);
			angleValue = getAngle();
			print(angleValue);
		}
		doneThread = true;
	}

	public void init() {
		angleMode = TheSmallProgrm.gyroSensor.getAngleMode();
		angleSample = new float[angleMode.sampleSize()];
	}

	// Resets the gyro sensor.
	public void resetGyro() {
		Delay.msDelay(100);
		TheSmallProgrm.gyroSensor.reset();
		Delay.msDelay(100);
		MovementControllerThread.targetAngle = 0;
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

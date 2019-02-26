package challenge1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;

public class GyroReadingThread extends Thread {
	// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used for printing
	static boolean printStuff = true;

	static float angleValue;

	static final double ERROR_THRESHOLD = 0.000001;

	// Initializing gyro sensor
	static Port s3 = TheSmallProgrm.brick.getPort("S3");
	static EV3GyroSensor gyroSensor = new EV3GyroSensor(s3);

	static SampleProvider angleMode;
	static float[] angleSample;

	// RUN METHOD
	@Override
	public void run() {
		angleMode = gyroSensor.getAngleMode();
		angleSample = new float[angleMode.sampleSize()];
		gyroSensor.reset();
		while (stopThread == false) {
			angleValue = getAngle();
			if (printStuff) {
				System.out.println(angleValue);
			}
		}
		gyroSensor.close();
	}

	public float getAngle() {
		angleMode.fetchSample(angleSample, 0);
		return angleSample[0];
	}

	static double round(double x, int d) {
		x *= Math.pow(10, d);
		x = Math.round(x);
		x /= Math.pow(10, d);
		return x;
	}
}

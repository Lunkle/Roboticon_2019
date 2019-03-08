package challenge1;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class TouchReadingThread extends Thread {// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing
	static boolean printStuff = true;

	// This is the value that is updated, and can be referenced by the small
	// program. A value of "0" means there IS NO touch detected. A value of "1"
	// means there IS a touch detected.
	static float touchValue = 0;

	// This is the amount of time after a touch for which the
	static float lingerTime = 1000;

	// Initializing touch sensor
	static Port s4 = TheSmallProgrm.brick.getPort("S4");
	static EV3TouchSensor touchSensor = new EV3TouchSensor(s4);

	static SampleProvider touchMode = touchSensor.getTouchMode();
	static float[] touchSample = new float[touchMode.sampleSize()];

	@Override
	public void run() {
		while (stopThread == false) {
			print("=======");
			touchValue = getTouchValue();
			print(touchValue);
			Delay.msDelay(1000);
		}
		touchSensor.close();
		doneThread = true;
	}

	private float getTouchValue() {
		float touch;
		touchMode.fetchSample(touchSample, 0);
		touch = touchSample[0];
		return touch;
	}

	// Some printing methods.
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

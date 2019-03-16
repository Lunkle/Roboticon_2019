package challenge1;

import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class TouchReadingThread extends Thread {// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing
	static boolean printStuff = false;

	// This is the value that is updated, and can be referenced by the small
	// program. A value of "0" means there IS NO touch detected. A value of "1"
	// means there IS a touch detected.
	static float touchValue = 0;

	// This is the amount of time after a touch for which the
	static float lingerTime = 100;

	static SampleProvider touchMode;
	static float[] touchSample;

	@Override
	public void run() {
		init();
		long timeSinceLastTouch = 0;
		while (stopThread == false) {
			Delay.msDelay(20);
			print("=======");
			float touch = getTouchValue();
			long time = System.currentTimeMillis();
			if (touch == 0) {
				if (time - timeSinceLastTouch < lingerTime) {
					touchValue = 1;
				} else {
					touchValue = 0;
				}
			} else {
				touchValue = 1;
				timeSinceLastTouch = time;
			}
			print(touchValue);
		}
		doneThread = true;
	}

	public void init() {
		touchMode = TheSmallProgrm.touchSensor.getTouchMode();
		touchSample = new float[touchMode.sampleSize()];
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

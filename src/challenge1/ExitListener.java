package challenge1;

import lejos.hardware.Key;
import lejos.hardware.KeyListener;

public class ExitListener implements KeyListener {
	@Override
	public void keyPressed(Key k) {
		TheSmallProgrm.done = true;
		RobotMovement.stopMotors();
		TheSmallProgrm.colourReadingThread.stopThread = true;
		TheSmallProgrm.gyroReadingThread.stopThread = true;
		TheSmallProgrm.touchReadingThread.stopThread = true;
		TheSmallProgrm.movementControllerThread.stopThread = true;
		while (TheSmallProgrm.colourReadingThread.doneThread || TheSmallProgrm.gyroReadingThread.doneThread || TheSmallProgrm.movementControllerThread.doneThread) {
		}
	}

	@Override
	public void keyReleased(Key k) {
	}
}

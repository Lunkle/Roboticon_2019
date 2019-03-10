package challenge1;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;

/**
 * Roboticon 2019 Team Name: TBD Team Members: Donny Ren, Johnny Tzoganakis, Dan
 * Choi, Misha Melnyk, John Liu Competition Date: March 16-17, 2019
 * 
 * @author Donny Ren
 */

public class TheSmallProgrm {
	static boolean done = false;

	static Brick brick;

	static ColourReadingThread colourReadingThread;
	static GyroReadingThread gyroReadingThread;
	static UltrasonicReadingThread ultrasonicReadingThread;
	static TouchReadingThread touchReadingThread;
	static MovementControllerThread movementControllerThread;

	// MAIN METHOD
	public static void main(String[] args) {
		init();
		startLineFollowingThreads();
		System.out.println("Press the fat button to start.");
		Button.ENTER.waitForPress();

		gyroReadingThread.resetGyro();
		RobotMovement.moveToEnd();
//		RobotMovement.waitFiveSeconds();
//		RobotMovement.returnToStart();
//		RobotMovement.turnRight(180);
		done = true;
		end();
		while (colourReadingThread.doneThread || gyroReadingThread.doneThread || movementControllerThread.doneThread) {
			// LMAO XD ROFL LOL
		}
	}

	private static void end() {
		RobotMovement.stopMotors();
		colourReadingThread.stopThread = true;
		gyroReadingThread.stopThread = true;
		ultrasonicReadingThread.stopThread = true;
		touchReadingThread.stopThread = true;
		movementControllerThread.stopThread = true;
	}

	private static void startLineFollowingThreads() {
		// Start colour reading thread.
		colourReadingThread = new ColourReadingThread();
		colourReadingThread.start();
		// Start gyro reading thread.
		gyroReadingThread = new GyroReadingThread();
		gyroReadingThread.start();
		// Start ultrasonic reading thread.
		ultrasonicReadingThread = new UltrasonicReadingThread();
		ultrasonicReadingThread.start();
		// Start touch reading thread.
		touchReadingThread = new TouchReadingThread();
		touchReadingThread.start();
		// Start movement tracker thread.
		movementControllerThread = new MovementControllerThread();
		movementControllerThread.start();
	}

	public static void init() {
		brick = BrickFinder.getDefault();
		RobotMovement.initSpeeds();
		Button.ESCAPE.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(Key k) {
				done = true;
			}

			@Override
			public void keyReleased(Key k) {
			}
		});
	}
}
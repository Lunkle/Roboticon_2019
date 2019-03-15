package challenge1;

import java.io.IOException;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.utility.Delay;

/*
 * Roboticon 2019
 * Team Name: Team Front
 * Team Members: Donny Ren, Johnny Tzoganakis, Dan Choi, Misha Melnyk
 * Team Coach: John Liu
 * Competition Date:
 * March 16-17, 2019
 * 
 * @author Donny Ren
 */

public class TheSmallProgrm {
	static boolean done = false;

	static Brick brick;

	static ColourReadingThread colourReadingThread;
	static GyroReadingThread gyroReadingThread;
	static TouchReadingThread touchReadingThread;
	static MovementControllerThread movementControllerThread;

	// MAIN METHOD
	public static void main(String[] args) throws IOException {
		init();
		startLineFollowingThreads();
//		fw.write("Starting program\n");
		System.out.println("Press the fat button to start.");
//		Button.ENTER.waitForPress(); //Uncomment at tournament.

		gyroReadingThread.resetGyro();
		RobotMovement.moveToEnd();
//		RobotMovement.moveForward(100, true);
//		RobotMovement.waitFiveSeconds();
//		RobotMovement.returnToStart();
//		System.out.println(MovementControllerThread.rightMotorPower);
		Delay.msDelay(10000);
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
				RobotMovement.stopMotors();
				colourReadingThread.stopThread = true;
				gyroReadingThread.stopThread = true;
				touchReadingThread.stopThread = true;
				movementControllerThread.stopThread = true;
				while (colourReadingThread.doneThread || gyroReadingThread.doneThread || movementControllerThread.doneThread) {
				}
			}

			@Override
			public void keyReleased(Key k) {
			}
		});
	}
}
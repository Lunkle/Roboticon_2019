package challenge1;

import java.io.IOException;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
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

	static EV3GyroSensor gyroSensor;
	static EV3TouchSensor touchSensor;
	static EV3ColorSensor colorSensor;
	static EV3UltrasonicSensor ultrasonicSensor;

	// MAIN METHOD
	public static void main(String[] args) throws IOException {
		initPorts();
		initLineFollowingThreads();
		System.out.println("Press the fat button to start.");
		Button.ENTER.waitForPress(); // Uncomment at tournament.

		startLineFollowingThreads();
		gyroReadingThread.resetGyro();
		getXPosition();
		RobotMovement.resetTachoCounts();
		RobotMovement.moveToEnd();
		RobotMovement.waitFiveSeconds();
		RobotMovement.returnToStart();
//		System.out.println(MovementControllerThread.rightMotorPower);
		Delay.msDelay(10000);
		done = true;
		endRun();
		while (colourReadingThread.doneThread || gyroReadingThread.doneThread || movementControllerThread.doneThread) {
			// LMAO XD ROFL LOL
			Delay.msDelay(10);
		}
	}

	private static void getXPosition() {
		float distanceToLeft = UltrasonicReadingThread.distanceValue;
		MovementControllerThread.distanceFromWall = 18 - distanceToLeft;
	}

	private static void endRun() {
		RobotMovement.stopMotors();
		colorSensor.close();
		gyroSensor.close();
		ultrasonicSensor.close();
		touchSensor.close();
		colourReadingThread.stopThread = true;
		gyroReadingThread.stopThread = true;
		touchReadingThread.stopThread = true;
		movementControllerThread.stopThread = true;
	}

	private static void initLineFollowingThreads() {
		// Initialize colour reading thread.
		colourReadingThread = new ColourReadingThread();
		// Initialize gyro reading thread.
		gyroReadingThread = new GyroReadingThread();
		// Initialize touch reading thread.
		touchReadingThread = new TouchReadingThread();
		// Initialize ultrasonic reading thread.
		ultrasonicReadingThread = new UltrasonicReadingThread();
		// Initialize movement tracker thread.
		movementControllerThread = new MovementControllerThread();
	}

	private static void startLineFollowingThreads() {
		colourReadingThread.start();
		gyroReadingThread.start();
		touchReadingThread.start();
		ultrasonicReadingThread.start();
		movementControllerThread.start();
	}

	public static void initPorts() {
		brick = BrickFinder.getDefault();
		RobotMovement.initSpeeds();
		Button.ESCAPE.addKeyListener(new ExitListener());

		Port s1 = TheSmallProgrm.brick.getPort("S1");
		colorSensor = new EV3ColorSensor(s1);

		Port s2 = TheSmallProgrm.brick.getPort("S2");
		ultrasonicSensor = new EV3UltrasonicSensor(s2);

		Port s3 = TheSmallProgrm.brick.getPort("S3");
		gyroSensor = new EV3GyroSensor(s3);

		Port s4 = TheSmallProgrm.brick.getPort("S4");
		touchSensor = new EV3TouchSensor(s4);
	}

	static boolean done = false;

	static Brick brick;

	static ColourReadingThread colourReadingThread;
	static GyroReadingThread gyroReadingThread;
	static TouchReadingThread touchReadingThread;
	static UltrasonicReadingThread ultrasonicReadingThread;
	static MovementControllerThread movementControllerThread;

}
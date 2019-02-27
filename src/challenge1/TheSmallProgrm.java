package challenge1;

import challenge1.ColourReadingThread.Colour;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.utility.Delay;

/**
 * Roboticon 2019 Team Name: TBD Team Members: Donny Ren, Johnny Tzoganakis, Dan
 * Choi, Misha Melnyk, John Liu Competition Date: March 16-17, 2019
 * 
 * @author Donny Ren
 */

public class TheSmallProgrm {
	static boolean done = false;

	public static float TpStart = 90;// starting power
	public static float Tp = TpStart;// dont change
	public static float TpAcc = 1f; // rate of acceleration to TpMax
	public static float TpMax = 250; // max speed

	static Brick brick;

	static ColourReadingThread colourReadingThread;
	static GyroReadingThread gyroReadingThread;

	public static NXTRegulatedMotor rightMotor = Motor.B;
	public static NXTRegulatedMotor leftMotor = Motor.C;

	static float leftMotorSpeed = Tp;
	static float rightMotorSpeed = Tp;

	static float xPos = 0; // This is the x position of the robot in inches.
	static final float ONE_INCH = 100;

	// MAIN/////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		init();
		System.out.println("Press a button to start.");
		Button.ENTER.waitForPress();
		startLineFollowingThreads();
//		moveToEnd();
//		waitFiveSeconds();
//		returnToStart();
		done = true;
		end();
		Delay.msDelay(1000);
	}

	private static void moveToEnd() {
		setWheelsToMoveForward();
		while (true) {
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				Delay.msDelay(100);
				break;
			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
				float[] circleData = findCircle();
				float circleX = circleData[0];
				float circleY = circleData[1];
				float radius = circleData[2];
			}
		}
	}

	private static void waitFiveSeconds() {

//		Delay.msDelay(period);
	}

	private static void returnToStart() {

	}

	static final float distToColourSensor = 0;

	private static float[] findCircle() {
		while (!done) {
			System.out.println("I am a retard looking for a place to live"); // ??????
		}
		stopMotors();
		return null;
	}

	private static void setWheelsToMoveForward() {
		leftMotor.backward();
		rightMotor.backward();
	}

	private static void moveForward(float inches) {
		leftMotor.backward();
		rightMotor.backward();
		Delay.msDelay((long) (inches * ONE_INCH));
	}

	private static void stopMotors() {
		leftMotor.stop(true);
		rightMotor.stop(true);
	}

	private static void end() {
		colourReadingThread.stopThread = true;
		gyroReadingThread.stopThread = true;
		stopMotors();
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}

	private static void turnRight() {
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4

		while (GyroReadingThread.angleValue >= currentAngle - 176) {
			if (GyroReadingThread.angleValue < currentAngle - 177) {
				stopMotors();
				break;
			}
		}
		stopMotors();
	}

	private static void startLineFollowingThreads() {
		// Start colour reading thread.
		colourReadingThread = new ColourReadingThread();
		colourReadingThread.start();
		ColourReadingThread.printStuff = false;
		gyroReadingThread = new GyroReadingThread();
		gyroReadingThread.start();
	}

	public static void init() {
		brick = BrickFinder.getDefault();
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
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
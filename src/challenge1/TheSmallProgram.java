package challenge1;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;

/**
 * Roboticon 2018 Team Name: Lethargic Walnuts Team Members: Donny Ren, Dan
 * Choi, John Tzoganakis, Matthew Zhou Competition Date: March 18-19
 * 
 * @author Donny Ren
 */
public class TheProgram {
	static boolean done = false;

	static float turn;

	public static final float Kp = 2800;// 50.2// how much it turns
	public static final float Ki = 0;// 50//how much it remembers past mistakes
	public static final float Kd = 0;// 800//how hard it turns based on prediction
	public static final float Ks = 1800;// how much slower it goes

	private static final float INTEGRAL_DAMPEN_FACTOR = 0.4f;// (smaller value=shorter time it'll remember for Ki) Higher value =
	// less
	// dampening

	public static float TpStart = 90;// starting power
	public static float Tp = TpStart;// dont change
	public static float TpAcc = 2f; // rate of acceleration to TpMax
	public static float TpMax = 250; // max speed

	// Black - White average Colour Value -- Must remember to change this at
	// tournament
	public static float OFFSET = 0.061f;

	// Don't change >:)
	public static float integral = 0;
	public static float lastError = 0;
	public static float derivative = 0;
	public static float error = 0;

	static Brick brick;

	public static NXTRegulatedMotor armMotor = Motor.A;
	public static NXTRegulatedMotor rightMotor = Motor.B;
	public static NXTRegulatedMotor leftMotor = Motor.C;

	static float leftMotorSpeed = Tp;
	static float rightMotorSpeed = Tp;

	// MAIN/////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		init();
		startLineFollowingThreads();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		Button.ENTER.waitForPress();
		OFFSET = ColourReadingThread.colourValue;
		followLine();
		end();
	}

	private static void end() {
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
//		TheProgram.armMotor.rotateTo(0);
	}

	private static void followLine() {
		lastError = ColourReadingThread.colourValue - OFFSET;
		leftMotor.backward();
		rightMotor.backward();
		while (done == false) {
			if (Tp < TpMax) {
				Tp += TpAcc; // yAINT
			}
			error = ColourReadingThread.colourValue - OFFSET;
			integral = INTEGRAL_DAMPEN_FACTOR * integral + error;
			derivative = error - lastError;
			turn = (Kp * error + Ki * integral + Kd * derivative);
			curve();
			lastError = error;
		}
	}

	private static void curve() {
		leftMotorSpeed = Math.max(1, (Tp + turn) - Ks * Math.abs(error));
		rightMotorSpeed = Math.max(1, (Tp - turn) - Ks * Math.abs(error));
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}

	private static void startLineFollowingThreads() {
		// Start colour sensor
		ColourReadingThread colourReadingThread = new ColourReadingThread();
		colourReadingThread.start();
	}

	public static void init() {
		brick = BrickFinder.getDefault();
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
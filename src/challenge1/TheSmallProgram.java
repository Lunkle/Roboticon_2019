package challenge1;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;

/**
 * Roboticon 2019 Team Name: TBD Team Members: Donny Ren, Johnny Tzoganakis, Dan
 * Choi, Misha Melnyk, John Liu Competition Date: March 16-17, 2019
 * 
 * @author Donny Ren
 */

public class TheSmallProgram {
	static boolean done = false;

	public static float TpStart = 90;// starting power
	public static float Tp = TpStart;// dont change
	public static float TpAcc = 2f; // rate of acceleration to TpMax
	public static float TpMax = 250; // max speed

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
		end();
	}

	private static void end() {
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}

	private static void startLineFollowingThreads() {
		// Start colour reading thread.
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
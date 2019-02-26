/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tests;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.MirrorMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;
import lejos.utility.TextMenu;

/**
 *
 * @author ivo
 */
public class GyroTest {
	// Definition of the motors on our robot
	static final RegulatedMotor LEFT_MOTOR = MirrorMotor.invertMotor(new EV3LargeRegulatedMotor(MotorPort.A));
	static final RegulatedMotor RIGHT_MOTOR = MirrorMotor.invertMotor(new EV3LargeRegulatedMotor(MotorPort.D));
	static final double WHEELDIST = 12.0; // measures in cm
	static final double WHEEL_DIAMETER = 5.5; // Diameter in cm
	static final double WHEELCIRC = WHEEL_DIAMETER * Math.PI; // Wheel circonference
	// Pilot (easier control of both motors)
	public static final DifferentialPilot PILOT = new DifferentialPilot(WHEEL_DIAMETER, WHEEL_DIAMETER, WHEELDIST, LEFT_MOTOR, RIGHT_MOTOR, false);
	static {
		PILOT.setLinearAcceleration(20);
		PILOT.setAngularAcceleration(20);
	} // This unit depends on the unit gives for the wheels!

	// Distance Sensor
	static final SampleProvider distance = (new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"))).getMode("Distance");
	static final float[] distances = new float[distance.sampleSize()];

	// Brightness Sensor
	static final SampleProvider brightness = (new EV3ColorSensor(LocalEV3.get().getPort("S1"))).getRedMode();
	static final float[] brightnesses = new float[brightness.sampleSize()];

	// Gyro Sensor
	static final EV3GyroSensor gyroSensor = new EV3GyroSensor(LocalEV3.get().getPort("S3"));
	static {
		gyroSensor.reset();
	}
	static final SampleProvider angle = gyroSensor.getAngleMode();
	static final float[] angles = new float[angle.sampleSize()];

	// The robots LCD (text modus)
	static TextLCD textLCD = BrickFinder.getDefault().getTextLCD();

	/*
	 * The program starts inside the main method
	 */
	public static void main(String[] args) {
		// here the program starts, we simply call the main menu
		mainMenu();

		// Here the program terminates
	}

	public static void mainMenu() {
		TextMenu menu = new TextMenu(new String[] { "Sensors", "Pilot" }, 1, "Main Menu");
		int selection;

		do { // repeat the following
				// Choose from the menu
			textLCD.clear();
			selection = menu.select();
			// React on selections
			if (selection == 0) {
				sensorMenu();
			} else if (selection == 1) {
			} else if (selection == 2) { // Disabled for now
				movesMenu();
			}
		} while (selection >= 0); // repeat as long as something has been selected.
	}

	public static void sensorMenu() {
		TextMenu menu = new TextMenu(new String[] { "Brightness", "Distance", "Angle" }, 1, "Sensor Menu");
		int selection;

		do { // repeat the following
				// Choose from the menu
			textLCD.clear();
			selection = menu.select();
			// React on selections
			if (selection == 0) {
				showBrightness();
			} else if (selection == 1) {
				showDistance();
			} else if (selection == 2) {
				showAngle();
			}
		} while (selection >= 0); // repeat as long as something has been selected.

	}

	// Return the brightness between 0.0 and 1.0 (more likely between 0.0 and 0.5)
	public static double getBrightness() {
		brightness.fetchSample(brightnesses, 0);
		return (double) brightnesses[0];
	}

	// Returns the distance in cm
	public static double getDistance() {
		distance.fetchSample(distances, 0);
		return (double) distances[0] * 100;
	}

	// Calibrates and reinitialized the gyro.
	// The robot should not move when calling this method
	public static void resetGyro() {
		gyroSensor.reset();
	}

	// Returns the angle in degrees
	public static double getAngle() {
		angle.fetchSample(angles, 0);
		return (double) angles[0];
	}

	public static void showBrightness() {
		textLCD.clear();
		textLCD.drawString("Brightness", 0, 0);
		while (Button.readButtons() == 0) {
			// Get the measure
			double value = getBrightness();
			// Show the measure
			textLCD.clear(1);
			textLCD.drawString("" + value, 0, 1);
			Delay.msDelay(50);
		}
		// Wait until the button is released
		while (Button.readButtons() != 0) {
			Delay.msDelay(10);
		}
		textLCD.clear();
	}

	public static void showDistance() {
		textLCD.clear();
		textLCD.drawString("Distance", 0, 0);
		while (Button.readButtons() == 0) {
			// Get the measure
			double value = getDistance();
			// Show the measure
			textLCD.clear(1);
			textLCD.drawString("" + value, 0, 1);
			Delay.msDelay(50);
		}
		// Wait until the button is released
		while (Button.readButtons() != 0) {
			Delay.msDelay(10);
		}
		textLCD.clear();
	}

	public static void showAngle() {
		textLCD.clear();
		textLCD.drawString("Angle", 0, 0);
		// Stop the robot to calibrate the gyroscope!
		resetGyro();
		while (Button.readButtons() == 0) {
			// Get the measure
			double value = getAngle();
			// Show the measure
			textLCD.clear(1);
			textLCD.drawString("" + value, 0, 1);
			Delay.msDelay(50);
		}
		// Wait until the button is released
		while (Button.readButtons() != 0) {
			Delay.msDelay(10);
		}
		textLCD.clear();
	}

	// If you want ot use this, make sure not to initialize the PILOT!
	public static void movesMenu() {
		TextMenu menu = new TextMenu(new String[] { "Forward 0.3", "Turn 90", "Approach wall" }, 1, "Moves Menu");
		int selection;
		do { // repeat the following
				// Choose from the menu
			textLCD.clear();
			selection = menu.select();
			// React on selections
			if (selection == 0) {
				forwardDistance(30); // Advance for 30cm
			} else if (selection == 1) {
				turnOnSpot(90); // Turn 90 degrees clockwise
			} else if (selection == 2) {
				approach(20); // Approach a wall to 20cm
			}
		} while (selection >= 0); // repeat as long as something has been selected.

	}

	//
	// If you want to use code from below, do not initialize the PILOT object
	// (set it to null instead!)
	//

	// Do not initialize PILOT to use this!
	public static void approach(double stopDistance) {
		if (PILOT != null) {
			throw new RuntimeException("Do not initialize PILOT");
		}
		// Note: This is an angular speed (degrees/sec)
		int maxSpeed = 400;
		int speed = 50;
		double d;
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
		// Repeat until close to a wall
		do {
			LEFT_MOTOR.setSpeed(speed);
			RIGHT_MOTOR.setSpeed(speed);
			Delay.msDelay(20);
			d = getDistance();
			// No measure (i.e. infinity)? Assume 10m
			if (d == Double.POSITIVE_INFINITY) {
				d = 10.0;
			}
			// compute the speed depending on the distance (0.2 to deccelerate)
			speed = (int) ((d - stopDistance) / 0.2 * maxSpeed);
			if (speed < 10) {
				speed = 10;
			}
			if (speed > maxSpeed) {
				speed = maxSpeed;
			}

			textLCD.clear(0);
			textLCD.drawString("d=" + d, 0, 0);
			textLCD.clear(1);
			textLCD.drawString("speed=" + speed, 0, 1);
			// correct non-sensical speed values
		} while (d > stopDistance);
		gracefullyStop();
	}

	// Do not initialize PILOT to use this!
	public static void forwardDistance(double distance) {
		if (PILOT != null) {
			throw new RuntimeException("Do not initialize PILOT");
		}

		textLCD.clear();
		textLCD.drawString("Forward " + distance + "cm", 0, 0);

		// Reset tacho
		LEFT_MOTOR.resetTachoCount();
		LEFT_MOTOR.forward();
		RIGHT_MOTOR.forward();
		LEFT_MOTOR.setSpeed(100);
		RIGHT_MOTOR.setSpeed(100);

		// Let the motor run until we have turned the correct number of degrees
		while (LEFT_MOTOR.getTachoCount() < 360.0 * distance / WHEELCIRC) {
			Delay.msDelay(10); // Wait some more...
		}

		// Stop the motors (they will resist some force)
		gracefullyStop();
	}

	// Turn clockwise (or anticlockwise for negative angles)
	// Do not initialize PILOT to use this!
	public static void turnOnSpot(int degrees) {
		if (PILOT != null) {
			throw new RuntimeException("Do not initialize PILOT");
		}
		int target = (int) (degrees * WHEELDIST * Math.PI / WHEELCIRC);
		textLCD.clear();
		textLCD.drawString("TurnOnSpot " + degrees, 0, 0);
		textLCD.drawString("target = " + target + "deg", 0, 1);
		LEFT_MOTOR.resetTachoCount();
		if (degrees < 0) {
			LEFT_MOTOR.backward();
			RIGHT_MOTOR.forward();
		} else {
			LEFT_MOTOR.forward();
			RIGHT_MOTOR.backward();
		}
		LEFT_MOTOR.setSpeed(100);
		RIGHT_MOTOR.setSpeed(100);
		while (Math.abs(LEFT_MOTOR.getTachoCount()) < Math.abs(target)) {
			Delay.msDelay(20);
		}
		gracefullyStop();
	}

	// Do not initialize PILOT to use this!
	public static void gracefullyStop() {
		if (PILOT != null) {
			throw new RuntimeException("Do not initialize PILOT");
		}
		// Stop the motors (they will resist some force)
		LEFT_MOTOR.stop();
		RIGHT_MOTOR.stop();
		// Wait 200ms
		Delay.msDelay(200);
		// Let the motors turn free
		LEFT_MOTOR.flt();
		RIGHT_MOTOR.flt();
	}
}

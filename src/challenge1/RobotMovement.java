package challenge1;

import challenge1.ColourReadingThread.Colour;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.utility.Delay;

public class RobotMovement {

	public static NXTRegulatedMotor rightMotor = Motor.B;
	public static NXTRegulatedMotor leftMotor = Motor.C;

	// The distance of the colour sensor to the middle of the two wheels.
	static final float LENGTH_OF_COLOUR_SENSOR_ARM = 5.75f; // TBD

	// The delay such that at the Tp of 320 it will move forward one inch by the end
	// of 160 milliseconds.
	static float oneInch = 347;

	public static void stopMotors() {
		leftMotor.stop(true);
		rightMotor.stop(true);
	}

	public static void setWheelsToMoveForward() {
		leftMotor.backward();
		rightMotor.backward();
	}

	public static void setWheelsToMoveBackward() {
		leftMotor.forward();
		rightMotor.forward();
	}

	public static void turnLeft(float angle) {
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4
		float targetAngle = (currentAngle + (angle - 0.057f * MovementTrackerThread.leftMotorPower - 0.75f));
		while (GyroReadingThread.angleValue <= targetAngle) {
			if (GyroReadingThread.angleValue > targetAngle) {
				stopMotors();
				break;
			}
		}
		stopMotors();
	}

	public static void turnRight(float angle) {
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.forward();
		rightMotor.backward();
		// due to sensor/data delay, take the degree of turn you want and -4
		float targetAngle = (currentAngle - (angle - 0.057f * MovementTrackerThread.rightMotorPower - 0.75f));
		while (GyroReadingThread.angleValue >= targetAngle) {
			if (GyroReadingThread.angleValue < targetAngle) {
				stopMotors();
				break;
			}
		}
		stopMotors();
	}

	public static void moveForward(float inches) {
		leftMotor.backward();
		rightMotor.backward();
		Delay.msDelay((long) (inches * oneInch));
		stopMotors();
	}

	public static void initSpeeds() {
		leftMotor.setSpeed(MovementTrackerThread.leftMotorPower);
		rightMotor.setSpeed(MovementTrackerThread.rightMotorPower);
	}

	public static void moveToEnd() {
		while (true) {
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				Delay.msDelay(100);
				break;
			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
				float[] circleData = MovementTrackerThread.findCircle();

				float circleX = circleData[0];
				float circleY = circleData[1];
				float radius = circleData[2];
			}
		}
	}

	public static void waitFiveSeconds() {
		Delay.msDelay(5000);
	}

	public static void returnToStart() {

	}

	public static void straighten() {

	}

}

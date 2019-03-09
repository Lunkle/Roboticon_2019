package challenge1;

import challenge1.ColourReadingThread.Colour;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.utility.Delay;

public class RobotMovement {

	public static NXTRegulatedMotor rightMotor = Motor.B;
	public static NXTRegulatedMotor leftMotor = Motor.C;

	// The distance of the colour sensor to the middle of the two wheels.
	static final float LENGTH_OF_COLOUR_SENSOR_ARM = 4.53f; // TBD

	// The delay such that at the Tp of 320 it will move forward one inch by the end
	// of 160 milliseconds.
	static float oneInch = 347;

	public static void stopMotors() {
		leftMotor.stop(true);
		rightMotor.stop(true);
		MovementControllerThread.setMotorsToNoPower();
	}

	public static void setWheelsToMoveForward() {
		MovementControllerThread.setMotorsToMaxPower();
		leftMotor.backward();
		rightMotor.backward();
	}

	public static void setWheelsToMoveBackward() {
		MovementControllerThread.setMotorsToMaxPower();
		leftMotor.forward();
		rightMotor.forward();
	}

	public static void turnLeft(float angle) {
		MovementControllerThread.setMotorsToMaxPower();
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.forward();
		rightMotor.backward();
		// due to sensor/data delay, take the degree of turn you want and -4
		float targetAngle = (currentAngle + (angle - 0.057f * MovementControllerThread.leftTargetPower - 0.75f));
		while (GyroReadingThread.angleValue <= targetAngle) {
			if (GyroReadingThread.angleValue > targetAngle) {
				stopMotors();
				break;
			}
		}
		stopMotors();
	}

	public static void turnRight(float angle) {
		MovementControllerThread.setMotorsToMaxPower();
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4
		float targetAngle = (currentAngle - (angle - 0.057f * MovementControllerThread.rightTargetPower - 0.75f));
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
		leftMotor.setSpeed(MovementControllerThread.leftMotorPower);
		rightMotor.setSpeed(MovementControllerThread.rightMotorPower);
	}

	public static void moveToEnd() {
		moveForward(9);
		while (true) {
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				moveForward(6.2f);
				break;
//			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
//				float[] circleData = MovementControllerThread.findCircle();
//
//				float circleX = circleData[0];
//				float circleY = circleData[1];
//				float radius = circleData[2];
			}
		}
	}

	public static void waitFiveSeconds() {
		turnRight(180);
		Delay.msDelay(4000);
	}

	public static void returnToStart() {
		moveForward(12);
		while (true) {
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				Delay.msDelay(100);
				break;
//			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
//				float[] circleData = MovementControllerThread.findCircle();
//
//				float circleX = circleData[0];
//				float circleY = circleData[1];
//				float radius = circleData[2];
			}
		}

	}

	public static void straighten() {

	}

}

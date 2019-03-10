package challenge1;

import challenge1.ColourReadingThread.Colour;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.utility.Delay;

public class RobotMovement {

	public static boolean turning = false;

	public static NXTRegulatedMotor rightMotor = Motor.B;
	public static NXTRegulatedMotor leftMotor = Motor.C;

	// The distance of the colour sensor to the middle of the two wheels.
	static final float LENGTH_OF_COLOUR_SENSOR_ARM = 4.53f; // TBD

	public static void stopMotors() {
		turning = false;
		leftMotor.stop(true);
		rightMotor.stop(true);
		MovementControllerThread.setMotorsToNoPower();
	}

	public static void setWheelsToMoveForward() {
		turning = false;
		MovementControllerThread.setMotorsToMaxPower();
		leftMotor.backward();
		rightMotor.backward();
	}

	public static void setWheelsToMoveBackward() {
		turning = false;
		MovementControllerThread.setMotorsToMaxPower();
		leftMotor.forward();
		rightMotor.forward();
	}

	public static void turnLeft(float angle) {
		turning = true;
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
		turning = false;
	}

	public static void turnRight(float angle) {
		turning = true;
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
		turning = false;
	}

	public static void moveForward(float inches) {
		if (inches == 0) {
			return;
		}
		turning = false;
		leftMotor.backward();
		rightMotor.backward();
		float initialDisplacement = MovementControllerThread.straightDisplacement;
		float nextDisplacement = initialDisplacement;
		while (true) {
			if (nextDisplacement - initialDisplacement > inches) {
				stopMotors();
				break;
			}

		}
	}

	// Returns how many inches are moved until white is seen.
	// So much maths at work.
	public static float moveForwardUntilSeeWhite() {
		turning = false;
		leftMotor.backward();
		rightMotor.backward();
		float initialDisplacement = MovementControllerThread.straightDisplacement;
		while (true) {
			if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
				stopMotors();
				break;
			}
		}
		float finalDisplacement = MovementControllerThread.straightDisplacement;
		float totalDisplacement = finalDisplacement - initialDisplacement;
		return totalDisplacement;
	}

	public static void initSpeeds() {
		leftMotor.setSpeed(MovementControllerThread.leftMotorPower);
		rightMotor.setSpeed(MovementControllerThread.rightMotorPower);
	}

	public static void moveToEnd() {
		MovementControllerThread.setMotorsToMaxPower();
		moveForward(5);
		while (true) {
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
//				moveForward(3);	
				break;
//			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
//				stopMotors();
//				Delay.msDelay(5000);
//				setWheelsToMoveForward();
//				Delay.msDelay(100);
//				circleAvoidance();
			}
		}
	}

	public static void waitFiveSeconds() {
		turnRight(180);
		Delay.msDelay(4000);
	}

	public static void returnToStart() {
		MovementControllerThread.setMotorsToMaxPower();
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

	public static void circleAvoidance() {
		turning = true;
		final float ROBOT_WIDTH = 1.57f;
		final float ERROR_ANGLE = 0.15f;

		float angleValue = GyroReadingThread.angleValue % 360;

		float[] circleProp = MovementControllerThread.findCircle();
		float x = MovementControllerThread.xPos, y = MovementControllerThread.yPos;
		Point a = MovementControllerThread.pointOffsetByDistance(new Point(x, y), angleValue, LENGTH_OF_COLOUR_SENSOR_ARM); // Set these to the first point that is found
		Point b = new Point(circleProp[0], circleProp[1]);

		double tangentSlope = -(b.x - a.x) / (b.y - a.y);
		float targetAngle = (float) Math.atan(tangentSlope);

		if (angleValue < targetAngle) { // If on one side of the motor, spin until it faces the correct angle.
			turnLeft(targetAngle - angleValue);
		} else { // If on one side of the motor, spin until it faces the correct angle.
			turnRight(angleValue - targetAngle);
		}

		float ratio = (circleProp[2] - ROBOT_WIDTH) / (circleProp[2] + ROBOT_WIDTH);

		if (targetAngle > 0) {
			leftMotor.setSpeed(300);
			rightMotor.setSpeed(300 * ratio);
		} else {
			rightMotor.setSpeed(300);
			leftMotor.setSpeed(300 * ratio);
		}
		setWheelsToMoveForward();
		while (true) {
			if (Math.abs(angleValue - targetAngle) % 360 > ERROR_ANGLE) {
				stopMotors();
				break;
			}
		}
		turning = false;
	}

	public static void straighten() {

	}

}

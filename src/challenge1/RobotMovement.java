package challenge1;

import java.util.ArrayList;

import challenge1.ColourReadingThread.Colour;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.utility.Delay;

public class RobotMovement {

	public static boolean movingStraight = false;
	public static boolean movingBackward = false;

	public static NXTRegulatedMotor rightMotor = Motor.B;
	public static NXTRegulatedMotor leftMotor = Motor.C;

	// The distance of the colour sensor to the middle of the two wheels.
	static final float LENGTH_OF_COLOUR_SENSOR_ARM = 4.53f; // TBD

	public static void stopMotors() {
		leftMotor.stop(true);
		rightMotor.stop(true);
		movingStraight = false;
	}

	public static void setWheelsToMoveForward() {
		movingStraight = true;
		leftMotor.backward();
		rightMotor.backward();
	}

	public static void turnLeft(float angle) {
		movingStraight = false;
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.forward();
		rightMotor.backward();
		// due to sensor/data delay, take the degree of turn you want and -4
		double targetAngle = currentAngle + (angle - 0.057f * MovementControllerThread.leftMotorPower - 0.75f);
		while (GyroReadingThread.angleValue <= targetAngle) {
			if (GyroReadingThread.angleValue > targetAngle) {
				stopMotors();
				break;
			}
		}
		stopMotors();
		MovementControllerThread.targetAngle -= angle;
		movingStraight = false;
	}

	public static void turnRight(float angle) {
		movingStraight = false;
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4
		double targetAngle = currentAngle - (angle - 0.057f * MovementControllerThread.rightMotorPower - 0.75f);
		while (GyroReadingThread.angleValue >= targetAngle) {
			if (GyroReadingThread.angleValue < targetAngle) {
				stopMotors();
				break;
			}
		}
		stopMotors();
		MovementControllerThread.targetAngle += angle;
		movingStraight = false;
	}

	public static void moveForward(float inches, boolean stopAtEnd) {
		if (inches == 0) {
			return;
		}
		movingStraight = true;
		leftMotor.backward();
		rightMotor.backward();
		Delay.msDelay((int) (MovementControllerThread.timeToMoveOneInch * inches));
		if (stopAtEnd) {
			stopMotors();
		}
//		System.out.println("done moving forward");
	}

	// Returns how many inches are moved until white is seen.
	// So much maths at work.
	public static double moveForwardUntilSeeWhite() {
		if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
			return 0;
		}
		movingStraight = true;
		long initialTime = System.currentTimeMillis();
		long newTime = System.currentTimeMillis();
		leftMotor.backward();
		rightMotor.backward();
		double distance;
		while (true) {
			newTime = System.currentTimeMillis();
			distance = (newTime - initialTime) * MovementControllerThread.velocity;
			if (distance > 4.8) {
				return 4.8;
			}
			if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
				break;
			}
		}
		return distance;
	}

	public static ArrayList<Point> turnAndReturnPoints() {
		movingStraight = false;
		ArrayList<Point> points = new ArrayList<Point>();
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4
		double targetAngle = currentAngle - (360 - 0.057f * MovementControllerThread.rightMotorPower - 0.75f);
		Colour currentColour = ColourReadingThread.colourValue;
		while (GyroReadingThread.angleValue >= targetAngle) {
			if (GyroReadingThread.angleValue < targetAngle) {
				stopMotors();
				break;
			}
			Colour newColour = ColourReadingThread.colourValue;
			if (currentColour == Colour.COLOUR_BLUE) {
				if (newColour == Colour.COLOUR_WHITE) {

				}
			}
			currentColour = newColour;
		}
		stopMotors();
		return points;
	}

	public static void initSpeeds() {
		leftMotor.setSpeed((int) MovementControllerThread.leftMotorPower);
		rightMotor.setSpeed((int) MovementControllerThread.rightMotorPower);
	}

	public static void moveToEnd() {
		moveForward(10, false);
		while (true) {
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				moveForward(3, true);
				break;
			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
				MovementControllerThread.setPower(300);

				findCircle();
			}
		}

		System.out.println("reached end");
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

	public static Point pointOffsetByDistance(Point o, float angle, float dist) {
		return new Point((float) (o.x + dist * Math.cos(Math.toRadians(angle))), (float) (o.y + dist * Math.sin(Math.toRadians(angle))));
	}

	public static float[] findCircle() {
		float xPos = MovementControllerThread.xPos;
		float yPos = MovementControllerThread.yPos;
		ArrayList<Point> pointsToLeft = new ArrayList<Point>();
		ArrayList<Point> pointsToRight = new ArrayList<Point>();
		Point outerPoint = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
		double distance = moveForwardUntilSeeWhite();
		yPos += distance;
		Point oppositePoint = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
		Delay.msDelay((int) MovementControllerThread.timeToMoveOneInch);
		yPos += 1;
		ArrayList<Point> circlePoints = turnAndReturnPoints();
		if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
			while (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
				RobotMovement.turnRight(1);
			}
			while (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
				RobotMovement.turnRight(1);
			}
			points[1] = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
			while (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
				RobotMovement.turnRight(1);
			}
			points[2] = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
			Circle c = Circle.circleFromPoints(points[0], points[1], points[2]);
			float[] components = { c.center.x, c.center.y, c.rad };
			return components;
		}
		Point[] outerPoints = new Point[3];
		Point[] innerPoints = new Point[3];
		return null;
	}

//	public static void circleAvoidance() {
//		movingForward = true;
//		final float ROBOT_WIDTH = 1.57f;
//		final float ERROR_ANGLE = 0.15f;
//
//		float angleValue = GyroReadingThread.angleValue % 360;
//
//		float[] circleProp = findCircle();
//		float x = MovementControllerThread.xPos, y = MovementControllerThread.yPos;
//		Point a = pointOffsetByDistance(new Point(x, y), angleValue, LENGTH_OF_COLOUR_SENSOR_ARM); // Set these to the first point that is found
//		Point b = new Point(circleProp[0], circleProp[1]);
//
//		double tangentSlope = -(b.x - a.x) / (b.y - a.y);
//		float targetAngle = (float) Math.atan(tangentSlope);
//
//		if (angleValue < targetAngle) { // If on one side of the motor, spin until it faces the correct angle.
//			turnLeft(targetAngle - angleValue);
//		} else { // If on one side of the motor, spin until it faces the correct angle.
//			turnRight(angleValue - targetAngle);
//		}
//
//		float ratio = (circleProp[2] - ROBOT_WIDTH) / (circleProp[2] + ROBOT_WIDTH);
//
//		if (targetAngle > 0) {
//			leftMotor.setSpeed(300);
//			rightMotor.setSpeed(300 * ratio);
//		} else {
//			rightMotor.setSpeed(300);
//			leftMotor.setSpeed(300 * ratio);
//		}
//		setWheelsToMoveForward();
//		while (true) {
//			if (Math.abs(angleValue - targetAngle) % 360 > ERROR_ANGLE) {
//				stopMotors();
//				break;
//			}
//		}
//		movingForward = false;
//	}

	public static void straighten() {

	}

}

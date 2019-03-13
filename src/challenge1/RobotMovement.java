package challenge1;

import java.util.ArrayList;
import java.util.Arrays;

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
	static final float LENGTH_OF_COLOUR_SENSOR_ARM = 3.5f;

	// The distance of the ultrasonic sensor to the middle of the two wheels.
	static final float LENGTH_OF_ULTRASONIC_ARM = 3.5f;

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

	public static double distanceBeforeStopped = 0.5;

	// Returns how many inches are moved until white is seen.
	// So much maths at work.
	public static double moveForwardUntilSeeWhite(long timeStart) {
		if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
			return distanceBeforeStopped;
		}
		MovementControllerThread.setPower(200);
		movingStraight = true;
		long newTime = System.currentTimeMillis();
		leftMotor.backward();
		rightMotor.backward();
		double distance;
		while (true) {
			newTime = System.currentTimeMillis();
			distance = (newTime - timeStart) * MovementControllerThread.velocity;
			if (distance > 4.8 - distanceBeforeStopped) {
				return 4.8;
			}
			if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
				break;
			}
		}
		return distanceBeforeStopped + distance;
	}

	public static Point[] turnAndReturnPoints(float yPos) {
		movingStraight = false;
		Point[] points = new Point[5];
		float currentAngle = GyroReadingThread.angleValue;
		MovementControllerThread.setPower(100);
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4
		double targetAngle = currentAngle - (360 - 0.057f * MovementControllerThread.rightMotorPower - 0.75f);
		Colour currentColour = ColourReadingThread.colourValue;
		float angleValue = GyroReadingThread.angleValue;
		float xPos = MovementControllerThread.xPos;
		ArrayList<Float> distanceFromWallValues = new ArrayList<>();
//		Point 
		int pointIndex = 0;
		while (angleValue >= targetAngle) {
			angleValue = GyroReadingThread.angleValue;
			if (Math.abs(angleValue - 90) <= 10) {
				float distanceFromWallAtAngle = UltrasonicSensorClass.getDistanceValue();
				float distanceFromWall = (float) (distanceFromWallAtAngle * Math.cos(Math.toRadians(Math.abs(angleValue - 90))));
				distanceFromWallValues.add(distanceFromWall);
			}
			if (angleValue < targetAngle) {
				stopMotors();
				break;
			}
			Colour newColour = ColourReadingThread.colourValue;
			if ((currentColour == Colour.COLOUR_BLUE && newColour == Colour.COLOUR_WHITE) || (currentColour == Colour.COLOUR_WHITE && newColour == Colour.COLOUR_BLUE)) {
				Point foundPoint = pointOffsetByDistance(new Point(xPos, yPos), angleValue - 5, LENGTH_OF_COLOUR_SENSOR_ARM);
				System.out.println(foundPoint);
				points[pointIndex] = foundPoint;
				pointIndex++;
				stopMotors();
				Delay.msDelay(500);
				leftMotor.backward();
				rightMotor.forward();

			}
			currentColour = newColour;
		}
		stopMotors();
		float sumOfDistancesFromWall = 0;
		for (float distance : distanceFromWallValues) {
			sumOfDistancesFromWall += distance;
		}
		float averageDistanceFromWall = sumOfDistancesFromWall / distanceFromWallValues.size();
		points[4] = new Point(averageDistanceFromWall + LENGTH_OF_ULTRASONIC_ARM, 0);
		MovementControllerThread.detectedWall = true;
		return points;
	}

	public static void initSpeeds() {
		leftMotor.setSpeed((int) MovementControllerThread.leftMotorPower);
		rightMotor.setSpeed((int) MovementControllerThread.rightMotorPower);
	}

	public static void moveToEnd() {
		moveForward(3, false);
		while (true) {
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				moveForward(3, true);
				break;
			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
				long timeWhenSeeBlue = System.currentTimeMillis();
				float[] circle = findCircle(timeWhenSeeBlue);
				if (circle != null) {

					for (int i = 0; i < circle.length; i++) {
						System.out.println("Final Circle component " + i + "," + circle[i]);
					}
				}
				break;// To remove
			}
		}

		System.out.println("reached end");
	}

	public static void waitFiveSeconds() {
		turnRight(180);
		Delay.msDelay(4000);
	}

//	public static void returnToStart() {
//		moveForward(12);
//		while (true) {
//			setWheelsToMoveForward();
//			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
//				Delay.msDelay(100);
//				break;
////			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
////				float[] circleData = MovementControllerThread.findCircle();
////
////				float circleX = circleData[0];
////				float circleY = circleData[1];
////				float radius = circleData[2];
//			}
//		}
//	}

	public static Point pointOffsetByDistance(Point o, float angle, float dist) {
		return new Point((float) (o.x - dist * Math.sin(Math.toRadians(angle))), (float) (o.y + dist * Math.cos(Math.toRadians(angle))));
	}

	public static float[] findCircle(long timeWhenSeeBlue) {
//		stopMotors();
		float xPos = MovementControllerThread.xPos;
		float yPos = 0;
		double distance = moveForwardUntilSeeWhite(timeWhenSeeBlue); // Gotta catch em all.
		Point outerPoint = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
		yPos += distance;
		Point oppositePoint = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
		System.out.println("Distance is " + distance);
		System.out.println(outerPoint);
		System.out.println(oppositePoint);
//		moveForward(1, true);
//		Delay.msDelay((int) MovementControllerThread.timeToMoveOneInch);
		yPos += 1;
		Point[] circlePoints = turnAndReturnPoints(yPos);
		Point[][] splitPoints = splitInMiddle(xPos, Arrays.copyOfRange(circlePoints, 0, 4));
		Point[] below = splitPoints[0];
		Point[] above = splitPoints[1];
		Circle innerCircle;
		Circle outerCircle;
		if (above.length >= 3) { // On the left of the circle.
			return null;
		} else if (below.length >= 3) {// On the right of the circle.
			return null;
		} else { // 2 on either side; in the middle of the circle.
			// Inner Circle
			innerCircle = Circle.circleFromPoints(oppositePoint, circlePoints[0], circlePoints[3]);
			// Outer Circle
			outerCircle = Circle.circleFromPoints(outerPoint, circlePoints[1], circlePoints[2]);
			System.out.println(innerCircle);
			System.out.println(outerCircle);
		}
		Point averageCenter = Point.averagePoint(innerCircle.center, outerCircle.center);
		float averageRadius = (innerCircle.radius + outerCircle.radius) / 2;
		return new float[] { averageCenter.x, averageCenter.y, averageRadius };
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

	public static Point[][] splitInMiddle(float x, Point[] ps) {
		ArrayList<Point> below = new ArrayList<>(), above = new ArrayList<>();
		for (Point p : ps) {
//			(p.x > x ? above : below).add(p);
			if (p.x > x) {
				above.add(p);
			} else {
				below.add(p);
			}
		}
		return new Point[][] { below.toArray(new Point[below.size()]), above.toArray(new Point[above.size()]) };
	}

	public static void straighten() {

	}

	static float roundAny(float x, int d) {
		x *= Math.pow(10, d);
		x = Math.round(x);
		x /= Math.pow(10, d);
		return x;
	}

}

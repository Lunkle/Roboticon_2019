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

	public static NXTRegulatedMotor rightMotor = Motor.A;
	public static NXTRegulatedMotor leftMotor = Motor.D;

	// The distance of the colour sensor to the middle of the two wheels.
	static final float LENGTH_OF_COLOUR_SENSOR_ARM = 3.5f;

	// The distance of the ultrasonic sensor to the middle of the two wheels.
	static final float LENGTH_OF_ULTRASONIC_ARM = 3.5f;

	static final float HALF_ROBOT_WIDTH = 1.57f;
	static final float ERROR_ANGLE = 2.5f;

	public static void stopMotors() {
		leftMotor.stop(true);
		rightMotor.stop(true);
		movingStraight = false;
	}

	public static void resetTachoCounts() {
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
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
			Delay.msDelay(1);
			if (GyroReadingThread.angleValue > targetAngle) {
				stopMotors();
				break;
			}
		}
		stopMotors();
		MovementControllerThread.targetAngle += angle;
	}

	public static void turnRight(float angle) {
		movingStraight = false;
		float currentAngle = GyroReadingThread.angleValue;
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4
		double targetAngle = currentAngle - (angle - 0.057f * MovementControllerThread.rightMotorPower - 0.75f);
		while (GyroReadingThread.angleValue >= targetAngle) {
			Delay.msDelay(1);
			if (GyroReadingThread.angleValue < targetAngle) {
				stopMotors();
				break;
			}
		}
		stopMotors();
		MovementControllerThread.targetAngle -= angle;
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

	public static void moveBackward(float inches, boolean stopAtEnd) {
		if (inches == 0) {
			return;
		}
		movingStraight = true;
		movingBackward = true;
		leftMotor.forward();
		rightMotor.forward();
		Delay.msDelay((int) (MovementControllerThread.timeToMoveOneInch * inches));
		if (stopAtEnd) {
			stopMotors();
		}
		movingBackward = false;
	}

	public static double distanceBeforeStopped = 0.0;

	// Returns how many inches are moved until white is seen.
	// So much maths at work.
	public static double moveForwardUntilSeeWhite(long timeStart) {
		if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
			return distanceBeforeStopped;
		}
		MovementControllerThread.setPower(100);
		movingStraight = true;
		long newTime = System.currentTimeMillis();
		leftMotor.backward();
		rightMotor.backward();
		double distance;
		while (true) {
			Delay.msDelay(1);
			newTime = System.currentTimeMillis();
			distance = (newTime - timeStart) * MovementControllerThread.velocity;
			if (distance > 4.8 - distanceBeforeStopped) {
				stopMotors();
				return 4.8;
			}
			if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {
				stopMotors();
				break;
			}
		}
		return distanceBeforeStopped + distance;
	}

	public static Point[] turnAndReturnPoints(float yPos) {
		movingStraight = false;
		Point[] points = new Point[5];
		float currentAngle = GyroReadingThread.angleValue;
		MovementControllerThread.setPower(80);
		leftMotor.backward();
		rightMotor.forward();
		// due to sensor/data delay, take the degree of turn you want and -4
		double targetAngle = currentAngle - (360 - 0.057f * MovementControllerThread.rightMotorPower - 0.75f);
		Colour oldColour = ColourReadingThread.colourValue;
		float angleValue = GyroReadingThread.angleValue;
		float xPos = MovementControllerThread.xPos;
		ArrayList<Float> distanceFromWallValues = new ArrayList<>();
//		Point 
		int pointIndex = 0;
		while (angleValue >= targetAngle) {
			Delay.msDelay(1);
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
			if ((oldColour == Colour.COLOUR_BLUE && newColour == Colour.COLOUR_WHITE) || (oldColour == Colour.COLOUR_WHITE && newColour == Colour.COLOUR_BLUE)) {
				Point foundPoint = pointOffsetByDistance(new Point(xPos, yPos), angleValue + 2, LENGTH_OF_COLOUR_SENSOR_ARM);
				System.out.println(foundPoint);
				points[pointIndex] = foundPoint;
				pointIndex++;
//				stopMotors();
//				leftMotor.backward();
//				rightMotor.forward();

			}
			oldColour = newColour;
		}
		stopMotors();
		float sumOfDistancesFromWall = 0;
		for (float distance : distanceFromWallValues) {
			sumOfDistancesFromWall += distance;
		}
		float averageDistanceFromWall = sumOfDistancesFromWall / distanceFromWallValues.size();
		points[4] = new Point(averageDistanceFromWall + LENGTH_OF_ULTRASONIC_ARM, 0);
		MovementControllerThread.detectedWall = true;
		MovementControllerThread.targetAngle -= 360;
		return points;
	}

	public static void initSpeeds() {
		leftMotor.setSpeed((int) MovementControllerThread.leftMotorPower);
		rightMotor.setSpeed((int) MovementControllerThread.rightMotorPower);
	}

	public static void moveToEnd() {
		movingStraight = true;
		MovementControllerThread.setPower(200);
		moveForward(22, true);
		Delay.msDelay(1000);
		avoidCircle(-5, 4, 6.5f, 0, 0);
		MovementControllerThread.setPower(200);
		moveForward(18, true);
		Delay.msDelay(1000);
		avoidCircle(-1, 12.5f, 8.5f, 0, 0);
		while (true) {
			MovementControllerThread.setPower(200);
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				System.out.println("found green");
				moveForward(6, true);
				turnRight(180);
				break;
			} else if (TouchReadingThread.touchValue == 1) {
				gotoGreenZone();
				System.out.println("reached end");
				break;
//			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
//				long timeWhenSeeBlue = System.currentTimeMillis();
//				float[] circle = findCircle(timeWhenSeeBlue);
//
//				if (circle != null) {
//					MovementControllerThread.distanceFromWall = circle[4];
//					System.out.println("(x-(" + circle[0] + "))^2 + (y-(" + circle[1] + "))^2 = " + Math.pow(circle[2], 2));
//					avoidCircle(circle[0], circle[1], circle[2], circle[3], circle[4]);
//				}
			}
		}
	}

	public static void waitFiveSeconds() {
		Delay.msDelay(3000);
	}

	public static void returnToStart() {
		movingStraight = true;
		MovementControllerThread.setPower(130);
		moveForward(3, false);
		while (true) {
			MovementControllerThread.setPower(200);
			setWheelsToMoveForward();
			if (ColourReadingThread.colourValue == Colour.COLOUR_GREEN) {
				System.out.println("found green");
				moveForward(6, true);
				break;
			} else if (TouchReadingThread.touchValue == 1) {
				gotoGreenZone();
				System.out.println("reached end");
				break;
//			} else if (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {
//				long timeWhenSeeBlue = System.currentTimeMillis();
//				float[] circle = findCircle(timeWhenSeeBlue);
//
//				if (circle != null) {
//					MovementControllerThread.distanceFromWall = circle[4];
//					System.out.println("(x-(" + circle[0] + "))^2 + (y-(" + circle[1] + "))^2 = " + Math.pow(circle[2], 2));
//					avoidCircle(circle[0], circle[1], circle[2], circle[3], circle[4]);
//				}
			}
		}
	}

	public static Point pointOffsetByDistance(Point o, float angle, float dist) {
		return new Point((float) (o.x - dist * Math.sin(Math.toRadians(angle))), (float) (o.y + dist * Math.cos(Math.toRadians(angle))));
	}

	public static float[] findCircle(long timeWhenSeeBlue) {
//		stopMotors();
		float xPos = MovementControllerThread.xPos;
		float yPos = 0;
		double distance = moveForwardUntilSeeWhite(timeWhenSeeBlue); // Gotta catch em all.
		if (distance >= 4.8) {
			return null;
		}
		Point outerPoint = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
		System.out.println(outerPoint);
		yPos += distance;
		Point oppositePoint = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, LENGTH_OF_COLOUR_SENSOR_ARM);
		System.out.println(oppositePoint);
//		moveForward(1, true);
//		Delay.msDelay((int) MovementControllerThread.timeToMoveOneInch);
		moveForward(1, true);
		yPos += 1.6;
		Point[] circlePoints = turnAndReturnPoints(yPos);
		System.out.println("Distance is " + distance);
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
		Point tangentPoint = Point.averagePoint(outerPoint, oppositePoint);
		return new float[] { averageCenter.x, averageCenter.y, averageRadius, tangentPoint.x, tangentPoint.y };
	}

	public static void avoidCircle(float xCenter, float yCenter, float radius, float xTangent, float yTangent) {
		movingStraight = false;
		float rawAngleValue = GyroReadingThread.angleValue % 360;
//		float xPos = MovementControllerThread.xPos;
		Point tangentPoint = pointOffsetByDistance(new Point(xTangent, yTangent), rawAngleValue, LENGTH_OF_COLOUR_SENSOR_ARM); // Set these to the first point that is found
		Point center = new Point(xCenter, yCenter);
		float angleValue = rawAngleValue < 0 ? rawAngleValue + 360 : rawAngleValue;

		float rawTargetAngle = (float) Math.toDegrees(Math.atan2((center.y - tangentPoint.y), (center.x - tangentPoint.x)));

		float targetSpinAngle = rawTargetAngle < 0 ? rawTargetAngle + 360 : rawTargetAngle;
		if ((center.x - tangentPoint.x) < 0) {
			targetSpinAngle -= 180;

			targetSpinAngle -= 15;
		} else {
			targetSpinAngle += 15;

		}

		float theta = targetSpinAngle - angleValue;

		System.out.println((center.x - tangentPoint.x) + " " + (center.y - tangentPoint.y));
		System.out.println("Angle Value: " + angleValue + " Target Angle: " + targetSpinAngle);
		theta = theta < 0 ? theta + 360 : theta;

		boolean turnRight = false;
		if (theta < 180) {
			turnLeft(theta);
			turnRight = true;
			MovementControllerThread.targetAngle -= theta;
		} else {
			turnRight(360 - theta);
			MovementControllerThread.targetAngle += 360 - theta;
		}

		// Now move along the outer boundaries of the known circle
		aroundCircleUsingDifferentMotorPower(radius, turnRight);

//		aroundCircleLineFollower(turnRight);

		System.out.println("avoided");
	}

	public static void aroundCircleUsingDifferentMotorPower(float radius, boolean turnRight) {

		float velocityRatio = (radius - HALF_ROBOT_WIDTH) / (radius + HALF_ROBOT_WIDTH);
		float power = 400;
		float powerValue = 1f * (float) MovementControllerThread.velocityToPower(MovementControllerThread.powerToVelocity(power) * velocityRatio);
		if (turnRight) {
			MovementControllerThread.leftMotorPower = power;
			MovementControllerThread.rightMotorPower = powerValue;
			leftMotor.setSpeed(power);
			rightMotor.setSpeed(powerValue);
		} else {
			MovementControllerThread.rightMotorPower = power;
			MovementControllerThread.leftMotorPower = powerValue;
			rightMotor.setSpeed(power);
			leftMotor.setSpeed(powerValue);
		}
		setWheelsToMoveForward();
		while (true) {
			Delay.msDelay(1);
			if (Math.abs(GyroReadingThread.angleValue - MovementControllerThread.targetAngle) < ERROR_ANGLE) {
				stopMotors();
				break;
			}
		}

	}

	public static void aroundCircleLineFollower(boolean turnRight) {

		float power = 300;

		setWheelsToMoveForward();
		// moveForward(1, false);

		while (Math.abs(GyroReadingThread.angleValue) % 360 > ERROR_ANGLE * 2) {
			leftMotor.setSpeed(power); // Go straight
			rightMotor.setSpeed(power);
			moveForward(1.5f, false);
			Colour newColour = ColourReadingThread.colourValue;
			if (turnRight) {
				while (newColour != Colour.COLOUR_BLUE) {
					newColour = ColourReadingThread.colourValue;
					// rotate right until see blue line
					turnRight(1);
					System.out.println("angle value " + Math.abs(GyroReadingThread.angleValue) % 360);
					if (Math.abs(GyroReadingThread.angleValue) % 360 <= ERROR_ANGLE * 2) {
						System.out.println("RIGHT SIDE ZERO");
						newColour = ColourReadingThread.colourValue;
						moveForward(1, true);
						while (newColour != Colour.COLOUR_WHITE) {
							newColour = ColourReadingThread.colourValue;
							moveForward(0.5f, false);
						}
						return;
					}

				}

			} else {
				while (newColour != Colour.COLOUR_BLUE) {
					newColour = ColourReadingThread.colourValue;
					// rotate left until see blue line
					turnLeft(1);
					System.out.println("angle value " + Math.abs(GyroReadingThread.angleValue) % 360);
					if (Math.abs(GyroReadingThread.angleValue) % 360 <= ERROR_ANGLE * 2) {
						System.out.println("RIGHT SIDE ZERO");
						newColour = ColourReadingThread.colourValue;
						moveForward(1, true);
						while (newColour != Colour.COLOUR_WHITE) {
							newColour = ColourReadingThread.colourValue;
							moveForward(0.5f, false);
						}
						return;
					}

				}

			}

		}

	}

	public static Point[][] splitInMiddle(float x, Point[] ps) {
		ArrayList<Point> below = new ArrayList<>(), above = new ArrayList<>();
		for (Point p : ps) {
			if (p.x > x) {
				above.add(p);
			} else {
				below.add(p);
			}
		}
		return new Point[][] { below.toArray(new Point[below.size()]), above.toArray(new Point[above.size()]) };
	}

	public static float getDistanceStopped(float power) {
		return 0;
	}

	public static void straighten() {

	}

	static float roundAny(float x, int d) {
		x *= Math.pow(10, d);
		x = Math.round(x);
		x /= Math.pow(10, d);
		return x;
	}

	public static void gotoGreenZone() {
		float widthOfMap = 36;
		turnRight(90);
		float distance = UltrasonicSensorClass.getDistanceValue();
		if (distance < widthOfMap / 2) {
			moveForward(widthOfMap / 2 - distance, true);
		} else {
			moveBackward(distance - widthOfMap / 2, true);

		}
		System.out.println("distance to wall is " + distance);
		turnRight(90);
	}
}

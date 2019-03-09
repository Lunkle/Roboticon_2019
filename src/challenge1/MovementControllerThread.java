package challenge1;

import challenge1.ColourReadingThread.Colour;

public class MovementControllerThread extends Thread {
	// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing.
	static boolean printStuff = true;

	public static final float INITIAL_POWER = 150;

	public static final float ACCELERATION = 500;
	public static final float DECELERATION = 100000;

	// The maximum safe power for robot. (Donny note: not actually sure what the
	// maximum safe power is... BTW safe means that the motor accurately runs at the
	// given speed and doesn't get capped off by the physical limitations of the
	// motor.)
	public static final float MAX_POWER = 400;

	// Left and right motors' current and target powers
	static float leftMotorPower = INITIAL_POWER;
	static float rightMotorPower = INITIAL_POWER;

	public static float leftTargetPower = INITIAL_POWER;
	public static float rightTargetPower = INITIAL_POWER;

	// This is the x position of the robot in inches.
	static float xPos = 0;
	// This is the y position of the robot in inches.
	static float yPos = 0;

	// We need to keep track of time in this class because time is important.
	long oldTime;
	long time;

	// RUN METHOD
	@Override
	public void run() {
		time = System.currentTimeMillis();
		while (!stopThread) {
			oldTime = time;
			time = System.currentTimeMillis();
			float deltaTime = time - oldTime;
			float instantaneousAcceleration = ACCELERATION * deltaTime / 1000.0f;
			float instantaneousDeceleration = DECELERATION * deltaTime / 1000.0f;
			print("============");
			print(instantaneousAcceleration);
			print(leftMotorPower);
			print(rightMotorPower);
			print(deltaTime);
			print(time);
			if (leftMotorPower < leftTargetPower) {
				leftMotorPower = Math.min(leftMotorPower + instantaneousAcceleration, leftTargetPower);
			} else {
				leftMotorPower = Math.max(leftMotorPower - instantaneousDeceleration, leftTargetPower);
			}
			if (rightMotorPower < rightTargetPower) {
				rightMotorPower = Math.min(rightMotorPower + instantaneousAcceleration, rightTargetPower);
			} else {
				rightMotorPower = Math.max(rightMotorPower - instantaneousDeceleration, rightTargetPower);
			}
			RobotMovement.leftMotor.setSpeed(leftMotorPower);
			RobotMovement.rightMotor.setSpeed(rightMotorPower);
		}
		doneThread = true;
	}

	public static void setMotorsToMaxPower() {
		leftTargetPower = MAX_POWER;
		rightTargetPower = MAX_POWER;
	}

	public static void setMotorsToNoPower() {
		leftTargetPower = 0;
		rightTargetPower = 0;
	}

	public static float[] findCircle() {
		Point[] points = new Point[3];

		points[0] = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, RobotMovement.LENGTH_OF_COLOUR_SENSOR_ARM);

		RobotMovement.moveForward(1);

		if (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {

			while (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {

				RobotMovement.turnRight(1);

			}

			while (ColourReadingThread.colourValue == Colour.COLOUR_BLUE) {

				RobotMovement.turnRight(1);

			}

			points[1] = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, RobotMovement.LENGTH_OF_COLOUR_SENSOR_ARM);

			while (ColourReadingThread.colourValue == Colour.COLOUR_WHITE) {

				RobotMovement.turnRight(1);

			}

			points[2] = pointOffsetByDistance(new Point(xPos, yPos), GyroReadingThread.angleValue, RobotMovement.LENGTH_OF_COLOUR_SENSOR_ARM);

			Circle c = Circle.circleFromPoints(points[0], points[1], points[2]);
			float[] components = { c.center.x, c.center.y, c.rad };
			return components;

		}

		return null;
	}

	// Some printing methods.
	private void print(String s) {
		if (printStuff) {
			System.out.println(s);
		}
	}

	@SuppressWarnings("unused")
	private void print(float s) {
		if (printStuff) {
			System.out.println(s);
		}
	}

	private static Point pointOffsetByDistance(Point o, float angle, float dist) {

		return new Point((float) (o.x + dist * Math.cos(angle)), (float) (o.y + dist * Math.sin(angle)));
	}
}

package challenge1;

public class MovementControllerThread extends Thread {
	// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing.
	static boolean printStuff = false;

	public static final double INITIAL_POWER = 320;

	/*
	 * The maximum safe power for robot. (Donny note: not actually sure what the
	 * maximum safe power is... BTW safe means that the motor accurately runs at the
	 * given speed and doesn't get capped off by the physical limitations of the
	 * motor.)
	 */
	public static double MAX_POWER = 400;

	// Left and right motors' current and target powers
	static double leftMotorPower = 300; // INITIAL_POWER;
	static double rightMotorPower = 300; // velocityToPower(powerToVelocity(400) * 0.6);

	// this variable records whether or not we've detected the wall yet.a
	static boolean detectedWall = false;

	static float distanceFromWall = 0;

	// This is the x position of the robot in inches.
	static float xPos = 0;

	static double velocity = 0;

	static float targetAngle = 0;

	// We need to keep track of time in this class because time is important.
	long oldTime;
	long time;

	// Adjust sharpness (in rotations per second).
	int adjust = 30;

	// The delay such that at the Tp of 320 it will move forward one inch by the end
	// of 160 milliseconds.
	// Equation of given power to time taken: t = 300 * e^(-0.0131p + 1.61) + 146
	static double timeToMoveOneInch = 347;

	// RUN METHOD
	@Override
	public void run() {
		time = System.currentTimeMillis();
		while (!stopThread) {
			oldTime = time;
			time = System.currentTimeMillis();
			float deltaTime = time - oldTime;
			double power = (leftMotorPower + rightMotorPower) / 2.0f; // Going for that equal treatment of left and right motors.. oh yeah.
			velocity = powerToVelocity(power);
			timeToMoveOneInch = 1.0 / velocity;
			float straightDisplacement = (float) (velocity * deltaTime);
			double rightTempPower = rightMotorPower;
			double leftTempPower = leftMotorPower;
			if (RobotMovement.movingStraight == true) {
				float angle = GyroReadingThread.angleValue;
				if (RobotMovement.movingBackward) {
					xPos += Math.sin(Math.toRadians(angle)) * straightDisplacement;
					if (GyroReadingThread.angleValue > targetAngle) {
						leftTempPower = Math.max(10, leftMotorPower - adjust);
					} else {
						rightTempPower = Math.max(10, rightMotorPower - adjust);
					}
				} else {
					xPos -= Math.sin(Math.toRadians(angle)) * straightDisplacement;
					if (GyroReadingThread.angleValue > targetAngle) {
						rightTempPower = Math.max(10, rightMotorPower - adjust);
					} else {
						leftTempPower = Math.max(10, leftMotorPower - adjust);
					}
				}
			}
			RobotMovement.leftMotor.setSpeed((float) leftTempPower);
			RobotMovement.rightMotor.setSpeed((float) rightTempPower);
//			System.out.println(roundAny(xPos, 2) + " " + roundAny(yPos, 2));
//			Delay.msDelay(1000);
		}
		doneThread = true;
	}

	// Set the motors speed
	public static void setPower(float power) {
		leftMotorPower = power;
		rightMotorPower = power;
		velocity = powerToVelocity(power);
		RobotMovement.leftMotor.setSpeed(power);
		RobotMovement.rightMotor.setSpeed(power);
	}

	// Converts power to velocity.
	public static double powerToVelocity(double power) {
		return 1.0 / (300 * Math.pow(Math.E, -0.0131 * power + 1.61) + 146);
	}

	// Converts velocity to power.
	public static double velocityToPower(double velocity) {
		return (Math.log(((1 / velocity) - 146) / 300) - 1.61) / -0.0131;
	}

	double roundAny(double x, int d) {
		x *= Math.pow(10, d);
		x = Math.round(x);
		x /= Math.pow(10, d);
		return x;
	}
}

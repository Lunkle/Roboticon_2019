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

	// The delay such that at the Tp of 320 it will move forward one inch by the end
	// of 160 milliseconds.
	// Equation of given power to time taken: t = 300 * e^(-0.0131p + 1.61) + 146
	static float timeToMoveOneInch = 347;

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
			float power = (leftMotorPower + rightMotorPower) / 2.0f; // Going for that equal treatment of left and right motors.. oh yeah.
			timeToMoveOneInch = 300 * (float) Math.pow(Math.E, -0.0131f * power + 1.61f) + 146;
			double velocity = 1.0 / timeToMoveOneInch;
			float straightDisplacement = (float) (velocity * deltaTime);
			if (RobotMovement.movingForward == true) {
				float angle = GyroReadingThread.angleValue;
				yPos += Math.cos(angle) * straightDisplacement;
				xPos += Math.sin(angle) * straightDisplacement;
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
				if (GyroReadingThread.angleValue > 0) {
					print("LOOOOOOOOOOOl");
					rightMotorPower -= 5;
				} else {
					print("xDDDDDDDDDD"); // lol xd
					leftMotorPower -= 5;
				}
				RobotMovement.leftMotor.setSpeed(leftMotorPower);
				RobotMovement.rightMotor.setSpeed(rightMotorPower);
			}
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

	// Some printing methods.
	private void print(String s) {
		if (printStuff) {
			System.out.println(s);
		}
	}

	private void print(float s) {
		if (printStuff) {
			System.out.println(s);
		}
	}

	float roundAny(float x, int d) {
		x *= Math.pow(10, d);
		x = Math.round(x);
		x /= Math.pow(10, d);
		return x;
	}
}

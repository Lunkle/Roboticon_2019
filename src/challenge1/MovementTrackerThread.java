package challenge1;

public class MovementTrackerThread extends Thread {
	// Variables

	// This variable is used for ending the thread
	boolean stopThread = false;

	// This variable is used by The Small/Big Program to see if the thread is done
	// running.
	boolean doneThread = false;

	// This variable is used for printing.
	static boolean printStuff = false;

	public static final float INITIAL_POWER = 150;

	public static final float ACCELERATION = 100;
	public static final float DECELERATION = 200;

	// The maximum safe power for robot. (Donny note: not actually sure what the
	// maximum safe power is... BTW safe means that the motor accurately runs at the
	// given speed and doesn't get capped off by the physical limitations of the
	// motor.)
	public static float maxPower = 320;

	// Left and right motors' current and target powers
	static float leftMotorPower = INITIAL_POWER;
	static float rightMotorPower = INITIAL_POWER;

	public static float leftTargetPower = maxPower;
	public static float rightTargetPower = maxPower;

	// This is the x position of the robot in inches.
	static float xPos = 0;
	// This is the y position of the robot in inches.
	static float yPos = 0;

	// We need to keep track of time in this class because time is important.
	float time;

	// RUN METHOD
	@Override
	public void run() {
		time = System.currentTimeMillis();
		while (!stopThread) {
			if (leftMotorPower < leftTargetPower) {
				leftMotorPower = Math.min(leftMotorPower + ACCELERATION, leftTargetPower);
			} else {
				leftMotorPower = Math.min(leftMotorPower - DECELERATION, leftTargetPower);
			}
		}
		doneThread = true;
	}

	public static float[] findCircle() {
		System.out.println("I am a retard looking for a place to live"); // ??????
		Point o1 = new Point(xPos, 0);
		// Ok do some wizardry.
//		RobotMovement.
		RobotMovement.moveForward(2);
		RobotMovement.stopMotors();
		return null;
	}
}

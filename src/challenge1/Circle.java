package challenge1;

public class Circle {
	Point center;
	float radius;

	private static float threshold = 0.00001f;

	Circle(Point c, float r) {
		center = c;
		radius = r;
	}

	private static boolean isPerpendicular(Point a, Point b, Point c) {
		float xDeltaA = b.x - a.x;
		float yDeltaA = b.y - a.y;
		float xDeltaB = c.x - b.x;
		float yDeltaB = c.y - b.y;

		if (Math.abs(yDeltaA) <= threshold || Math.abs(yDeltaB) <= threshold || Math.abs(xDeltaA) <= threshold || Math.abs(xDeltaB) <= threshold)
			return true;

		return false;
	}

	private static Circle calcCircle(Point a, Point b, Point c) {
		float xDeltaA = b.x - a.x;
		float yDeltaA = b.y - a.y;
		float xDeltaB = c.x - b.x;
		float yDeltaB = c.y - b.y;

		if (Math.abs(xDeltaA) <= threshold && Math.abs(yDeltaB) <= threshold) {
			Point center = new Point(0.5f * (b.x + c.x), 0.5f * (a.y + b.y));
			float r = center.distTo(a);
			return new Circle(center, r);
		}

		float slopeA = yDeltaA / xDeltaA;
		float slopeB = yDeltaB / xDeltaB;

		if (Math.abs(slopeA - slopeB) <= threshold)
			return null; // Co-linear

		float x = (slopeA * slopeB * (a.y - c.y) + slopeB * (a.x + b.x) - slopeA * (b.x + c.x)) / (2 * (slopeB - slopeA));
		float y = -1 * (x - (a.x + b.x) / 2) / slopeA + (a.y + b.y) / 2;
		Point center = new Point(x, y);

		float r = center.distTo(a);
		return new Circle(center, r);
	}

	public static Circle circleFromPoints(Point a, Point b, Point c) {

		if (!isPerpendicular(a, b, c))
			return calcCircle(a, b, c);
		else if (!isPerpendicular(a, c, b))
			return calcCircle(a, c, b);
		else if (!isPerpendicular(b, a, c))
			return calcCircle(b, a, c);
		else if (!isPerpendicular(b, c, a))
			return calcCircle(b, c, a);
		else if (!isPerpendicular(c, b, a))
			return calcCircle(c, b, a);
		else if (!isPerpendicular(c, a, b))
			return calcCircle(c, a, b);

		return null;
	}

	@Override
	public String toString() {
		return center.toString() + "," + radius;
	}
}

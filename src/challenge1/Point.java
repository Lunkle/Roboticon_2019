package challenge1;

public class Point {
	
	public static final boolean PRINT_IN_GRAPHING_MODE = true;
	
	
	float x, y;

	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	Point(Point p) {
		x = p.x;
		y = p.y;
	}

	static Point averagePoint(Point... points) {
		Point mid = new Point(0, 0);
		for (Point point : points) {
			mid.x += point.x;
			mid.y += point.y;
		}
		mid.x /= points.length;
		mid.y /= points.length;
		return mid;
	}

	float distTo(Point b) {
		return (float) (Math.sqrt((this.x - b.x) * (this.x - b.x) + (this.y - b.y) * (this.y - b.y)));
	}

	static float dist(Point a, Point b) {
		return (float) (Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)));
	}

	@Override
	public String toString() {
		String string = this.x + "," + this.y;
		if (PRINT_IN_GRAPHING_MODE) {
			string = "(" + string + ")";
		}
		return string;
	}
}

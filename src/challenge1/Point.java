package challenge1;

public class Point {
	float x, y;

	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	Point(Point p) {
		x = p.x;
		y = p.y;
	}

	float distTo(Point b) {
		return (float) (Math.sqrt((this.x - b.x) * (this.x - b.x) + (this.y - b.y) * (this.y - b.y)));
	}

	static float dist(Point a, Point b) {
		return (float) (Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)));
	}
}
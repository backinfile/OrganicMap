package com.backinfile.map.model;

public class Point {
	public static final double THRESHOLD = 0.0001;

	private float x;
	private float y;

	public Point() {
	}

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public void setPoint(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public double getDistance(Point p) {
		return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
	}

	public Point getTranslated(float dx, float dy) {
		return new Point(this.x + dx, this.y + dy);
	}

	public void translate(Point point) {
		this.x += point.x;
		this.y += point.y;
	}

	public Point mul(float value) {
		return new Point(this.x * value, this.y * value);
	}

	public Point sub(Point p) {
		return new Point(this.x - p.x, this.y - p.y);
	}

	public Point add(Point p) {
		return new Point(this.x + p.x, this.y + p.y);
	}

	public boolean isSame(Point p) {
		return Math.abs(this.x - p.x) < THRESHOLD && Math.abs(this.y - p.y) < THRESHOLD;
	}

	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}
}

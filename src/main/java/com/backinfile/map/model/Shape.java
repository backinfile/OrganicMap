package com.backinfile.map.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Shape {
	private static long IDMAX = 0;

	protected List<Point> points = new ArrayList<>();
	protected final long id;

	public Shape() {
		id = IDMAX++;
	}

	public Shape(Collection<Point> points) {
		this();
		this.points.addAll(points);
	}

	public long getId() {
		return id;
	}

	public Point getCenter() {
		Point center = new Point();
		for (var p : points) {
			center.translate(p);
		}
		center.setX(center.getX() / points.size());
		center.setY(center.getY() / points.size());
		return center;
	}

	/**
	 * 将p换成target
	 */
	public void replacePoint(Point p, Point target) {
		for (int i = 0; i < this.points.size(); i++) {
			if (this.points.get(i) == p) {
				this.points.set(i, target);
			}
		}
	}

	public List<Point> getPoints() {
		return points;
	}

	/**
	 * 生成本形状的完全复制
	 */
	public abstract Shape deepCopy();

	public List<Point> getCopyPoints() {
		List<Point> result = new ArrayList<Point>();
		for (var p : this.points) {
			result.add(new Point(p));
		}
		return result;
	}

	@Override
	public String toString() {
		return points.toString();
	}
}

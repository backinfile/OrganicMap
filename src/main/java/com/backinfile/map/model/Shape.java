package com.backinfile.map.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Shape {
	protected List<Point> points = new ArrayList<>();

	public Shape() {
	}

	public Shape(Collection<Point> points) {
		this.points.addAll(points);
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
	
	public List<Point> getPoints() {
		return points;
	}
	
	@Override
	public String toString() {
		return points.toString();
	}
}

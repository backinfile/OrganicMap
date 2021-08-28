package com.backinfile.map.model;

import java.util.List;

import com.backinfile.map.SysException;

public class Triangle extends Shape {

	public Triangle() {
		super();
	}

	public Triangle(List<Point> points) {
		super(points);
		if (points.size() != 3) {
			throw new SysException("error in Triangle create");
		}
	}

}

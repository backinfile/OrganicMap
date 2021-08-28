package com.backinfile.map.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backinfile.map.model.Point;
import com.backinfile.map.model.Rectangle;
import com.backinfile.map.model.Shape;
import com.backinfile.support.Timing;

import javassist.bytecode.CodeIterator.Gap;

public class RelaxManager {
	private Map<Point, Point> pointMovements = new HashMap<>();

	@Timing
	public void relax(List<Shape> shapes, float rate) {
		pointMovements.clear();
		for (var shape : shapes) {
			if (!(shape instanceof Rectangle)) {
				continue;
			}
			var rectangle = (Rectangle) shape;
			var corners = getCornerPoints(rectangle);
			for (int i = 0; i < 4; i++) {
				Point p = rectangle.getPoints().get(i);
				Point corner = corners.get(i);
				getPointMovement(p).translate(corner.sub(p));
			}
		}

		for (var entry : pointMovements.entrySet()) {
			var p = entry.getKey();
			var m = entry.getValue();
			p.translate(m.mul(rate));
		}

	}

	private static final int[] DX = new int[] { 1, 1, -1, -1 };
	private static final int[] DY = new int[] { 1, -1, 1, -1 };

	private List<Point> getCornerPoints(Rectangle rectangle) {
		List<Point> result = new ArrayList<>();
		var center = rectangle.getCenter();
		var suggestGap = (float) (GenManager.Gap / 2 * Math.cos(Math.PI / 4));
		for (int i = 0; i < 4; i++) {
			var p = rectangle.getPoints().get(i);
			var distance = (float) p.getDistance(center);
			result.add(center.add(p.sub(center).mul(suggestGap / distance)));
		}
		return result;
	}

	private Point getPointMovement(Point point) {
		Point movement = pointMovements.get(point);
		if (movement == null) {
			movement = new Point();
			pointMovements.put(point, movement);
		}
		return movement;
	}

}

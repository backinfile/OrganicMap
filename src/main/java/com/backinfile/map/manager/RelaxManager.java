package com.backinfile.map.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backinfile.map.model.Point;
import com.backinfile.map.model.Rectangle;
import com.backinfile.map.model.Shape;
import com.backinfile.support.Time2;
import com.backinfile.support.Timing;

public class RelaxManager {
	public static final float DEFAULT_RATE = 0.1f;
	private Map<Point, Point> pointMovements = new HashMap<>();
	public double lastLoss = 0;
	public long timesTotal = 0;

	@Timing(minTime = Time2.SEC)
	public void relaxRepeat(List<Shape> shapes, int times) {
		float rate = DEFAULT_RATE;
		for (int i = 0; i < times; i++) {
			lastLoss = relax(shapes, rate);
		}

		timesTotal += times;
//		Log.game.info("relax size={} rate={} timesTotal={} loss={}", shapes.size(), rate, timesTotal, lastLoss);
	}

	public double relax(List<Shape> shapes, float rate) {
		double loss = 0;
		pointMovements.clear();
		for (var shape : shapes) {
			if (!(shape instanceof Rectangle)) {
				continue;
			}
			var rectangle = (Rectangle) shape;
//			rectangle.reorder();
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
			var finalMove = m.mul(rate);
			p.translate(finalMove);
			loss += finalMove.getVectorLengthPow();
		}

		return loss;
	}

	private List<Point> getCornerPoints(Rectangle rectangle) {
		List<Point> result = new ArrayList<>();
		var center = rectangle.getCenter();
		var suggestGap = GenManager.Gap / 2 * Math.cos(Math.PI / 4);
		for (int i = 0; i < 4; i++) {
			var p1 = rectangle.getPoints().get((i + 4 - 1) % 4);
//			var p2 = rectangle.getPoints().get((i + 4 + 0) % 4);
			var p3 = rectangle.getPoints().get((i + 4 + 1) % 4);
			var target = p3.sub(p1).roate90().unit().mul(suggestGap).add(center);
			result.add(target);
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

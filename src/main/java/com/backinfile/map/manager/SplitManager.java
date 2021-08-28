package com.backinfile.map.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.backinfile.map.model.Point;
import com.backinfile.map.model.Rectangle;
import com.backinfile.map.model.Shape;
import com.backinfile.map.model.Triangle;
import com.backinfile.support.Timing;

public class SplitManager {
	private static List<Point> pointCache = new ArrayList<>();

	@Timing
	public static List<Rectangle> splitShapes(List<Shape> shapes) {
		List<Rectangle> result = new ArrayList<>();
		pointCache.clear();
		for (var shape : shapes) {
			if (shape instanceof Rectangle) {
				// 分解平行四边形
				for (int i = 0; i < 4; i++) {
					var p1 = shape.getPoints().get((i + 4 - 1) % 4);
					var p2 = shape.getPoints().get((i + 4 + 0) % 4);
					var p3 = shape.getPoints().get((i + 4 + 1) % 4);
					var np1 = p1.add(p2).mul(0.5f);
					var np2 = new Point(p2);
					var np3 = p3.add(p2).mul(0.5f);
					var np4 = np2.add(np3.sub(np2).add(np1.sub(np2)));
					result.add(new Rectangle(Arrays.asList(getCachePoint(np1), getCachePoint(np2), getCachePoint(np3),
							getCachePoint(np4))));
				}
			} else if (shape instanceof Triangle) {
				for (int i = 0; i < 3; i++) {
					var p1 = shape.getPoints().get((i + 3 - 1) % 3);
					var p2 = shape.getPoints().get((i + 3 + 0) % 3);
					var p3 = shape.getPoints().get((i + 3 + 1) % 3);
					var np1 = p1.add(p2).mul(0.5f);
					var np2 = new Point(p2);
					var np3 = p3.add(p2).mul(0.5f);
					var np4 = shape.getCenter();
					result.add(new Rectangle(Arrays.asList(getCachePoint(np1), getCachePoint(np2), getCachePoint(np3),
							getCachePoint(np4))));
				}
			}
		}
		pointCache.clear();
		return result;
	}

	private static Point getCachePoint(Point p) {
		for (var cp : pointCache) {
			if (cp.isSame(p)) {
				return cp;
			}
		}
		pointCache.add(p);
		return p;
	}
}

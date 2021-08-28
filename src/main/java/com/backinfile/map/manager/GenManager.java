package com.backinfile.map.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.backinfile.map.model.Point;
import com.backinfile.map.model.Rectangle;
import com.backinfile.map.model.Shape;
import com.backinfile.map.model.Triangle;
import com.backinfile.support.Timing;

public class GenManager {
	public static final int Gap = 40;
	public static final int PointNumber = 5;
	public static final int RectSize = 50;

	private static Map<Integer, Point> pointCache = new HashMap<>();

	@Timing
	public static List<Triangle> genTriangles() {
		List<Triangle> triangles = new ArrayList<>();
		pointCache.clear();
		for (int x = -PointNumber + 1; x < PointNumber; x++) {
			for (int y = -PointNumber + 1; y < PointNumber; y++) {
				Point p1 = getCachePoint(x, y);
				Point p2 = getCachePoint(x + 1, y);
				Point p3 = getCachePoint(x, y + 1);
				Point p4 = getCachePoint(x + 1, y + 1);
				triangles.add(new Triangle(Arrays.asList(p1, p2, p3)));
				triangles.add(new Triangle(Arrays.asList(p4, p2, p3)));
			}
		}
		pointCache.clear();
		return triangles;
	}

	@Timing
	public static List<Shape> randomMerge(List<Triangle> triangles, long seed) {
		List<Shape> shapes = new ArrayList<>();
		Set<Triangle> mergedTriangles = new HashSet<>();
		var random = new Random(seed);
		// 随机合并两个为长方形
		for (int round = 0; round < PointNumber; round++) {
			for (int i = 0; i < triangles.size(); i++) {
				var triangle = triangles.get(i);
				if (mergedTriangles.contains(triangle)) {
					continue;
				}
				if (random.nextBoolean()) {
					continue;
				}

				var mergableTriangles = getMergableTriangles(triangles, triangle, mergedTriangles);
				if (!mergableTriangles.isEmpty()) {
					int rnd = random.nextInt(mergableTriangles.size());
					var target = mergableTriangles.get(rnd);
					shapes.add(merge(triangle, target));
					mergedTriangles.add(triangle);
					mergedTriangles.add(target);
				}
			}
		}
		// 剩下的保留
		for (var tri : triangles) {
			if (!mergedTriangles.contains(tri)) {
				shapes.add(tri);
			}
		}
		return shapes;
	}

	private static Rectangle merge(Triangle t1, Triangle t2) {
		Set<Point> points = new HashSet<>();
		points.addAll(t1.getPoints());
		points.addAll(t2.getPoints());
		Rectangle rectangle = new Rectangle(points);
		rectangle.reorder();
//		Log.game.info("merge {} {}", t1, t2);
		return rectangle;
	}

	private static List<Triangle> getMergableTriangles(List<Triangle> triangles, Triangle target,
			Set<Triangle> exceptions) {
		List<Triangle> result = new ArrayList<>();
		for (var tri : triangles) {
			if (target == tri) {
				continue;
			}
			if (exceptions.contains(tri)) {
				continue;
			}
			var count = target.getPoints().stream().filter(p -> tri.getPoints().contains(p)).count();
			if (count == 2) {
				result.add(tri);
			}
		}
		return result;
	}

	private static Point getCachePoint(int x, int y) {
		var p = pointCache.get(x * PointNumber * 2 + y);
		if (p == null) {
			p = new Point(x * Gap, y * Gap);
			pointCache.put(x * PointNumber * 2 + y, p);
		}
		return p;
	}
}

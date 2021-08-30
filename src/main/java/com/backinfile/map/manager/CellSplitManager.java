package com.backinfile.map.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.backinfile.map.model.Point;
import com.backinfile.map.model.Rectangle;
import com.backinfile.map.model.Shape;
import com.backinfile.support.Utils;

public class CellSplitManager {
	private ShapeViewManager shapeViewManager;
	private Random random;

	public CellSplitManager(ShapeViewManager shapeViewManager, Random random) {
		this.shapeViewManager = shapeViewManager;
		this.random = random;

		if (this.shapeViewManager == null) {
			this.shapeViewManager = new ShapeViewManager();
		}
		if (this.random == null) {
			this.random = new Random();
		}
	}

	public void randomSplit4(List<Shape> shapes) {
		CellSplitInfo info = null;
		while (info == null) {
			info = getRandomCellSplitInfo(shapes);
		}
		int r1Index = info.r1.getPoints().indexOf(info.pc);

		info.r4.replacePoint(info.pc, info.pc1);
		info.r1.replacePoint(info.pc, info.pc1);
		info.r2.replacePoint(info.pc, info.pc3);
		info.r3.replacePoint(info.pc, info.pc3);
		var splited = new Rectangle(Arrays.asList(info.pc1, info.pc2, info.pc3, info.pc4));
		shapes.add(splited);
		int splitedIndex = info.r1.getPoints().indexOf(info.pc1);

		var r1p1 = info.r1.getPoints().get((r1Index + 0) % 4);
		var r1p2 = info.r1.getPoints().get((r1Index + 1) % 4);
		var r1p3 = info.r1.getPoints().get((r1Index + 2) % 4);
		var r1p4 = info.r1.getPoints().get((r1Index + 3) % 4);
		var r1p12 = r1p1.add(r1p2).mul(0.5f);
		var r1p34 = r1p3.add(r1p4).mul(0.5f);
		var splitView = new Rectangle(Utils.asList(splitedIndex, r1p1.copy(), r1p12.copy(), r1p34.copy(), r1p4.copy()),
				false);
		var r1View = new Rectangle(Utils.asList(r1Index, r1p12.copy(), r1p2.copy(), r1p3.copy(), r1p34.copy()), false);
		shapeViewManager.preSetShapeView(splited, splitView);
		shapeViewManager.preSetShapeView(info.r1, r1View);
	}

	private static class CellSplitInfo {
		public Point pc;
		public Rectangle r1;
		public Rectangle r2;
		public Rectangle r3;
		public Rectangle r4;
		public Point pc1;
		public Point pc2;
		public Point pc3;
		public Point pc4;
	}

	private CellSplitInfo getRandomCellSplitInfo(List<Shape> shapes) {
		var info = new CellSplitInfo();
		Shape shape = shapes.get(random.nextInt(shapes.size()));
		if (!(shape instanceof Rectangle)) {
			return null;
		}
		Rectangle rectangle = (Rectangle) shape;
		info.pc = rectangle.getPoints().get(random.nextInt(4));
		var neighborShapesInfo = getNeighborShapesInfo(shapes, info.pc);
		if (neighborShapesInfo == null) {
			return null;
		}
		info.r1 = (Rectangle) neighborShapesInfo.shapes.get(0);
		info.r2 = (Rectangle) neighborShapesInfo.shapes.get(1);
		info.r3 = (Rectangle) neighborShapesInfo.shapes.get(2);
		info.r4 = (Rectangle) neighborShapesInfo.shapes.get(3);
		var pc1 = neighborShapesInfo.getNextPoint(0);
		var pc2 = neighborShapesInfo.getNextPoint(1);
		var pc3 = neighborShapesInfo.getNextPoint(2);
		var pc4 = neighborShapesInfo.getNextPoint(3);
		info.pc1 = pc1.add(info.pc).mul(0.5f);
		info.pc2 = pc2;
		info.pc3 = pc3.add(info.pc).mul(0.5f);
		info.pc4 = pc4;
		return info;
	}

	private NeighborShapesInfo getNeighborShapesInfo(List<Shape> shapes, Point p) {
		Map<Shape, Point> resultMap = new HashMap<>();
		for (var shape : shapes) {
			var points = shape.getPoints();
			var index = points.indexOf(p);
			if (index >= 0) {
				resultMap.put(shape, points.get((index + 1) % points.size()));
			}
		}
		// 然后把shape顺时针排列
		if (resultMap.size() == 4) { // TODO 支持其他划分
			var orderPoints = Rectangle.getReorderPoints(resultMap.values());
			List<Shape> result = new ArrayList<>(resultMap.keySet());
			Utils.sort(result, shape -> orderPoints.indexOf(resultMap.get(shape)));
			NeighborShapesInfo info = new NeighborShapesInfo();
			info.nextPointMap.putAll(resultMap);
			info.shapes.addAll(result);
			return info;
		}
		return null;
	}

	private static class NeighborShapesInfo {
		public Map<Shape, Point> nextPointMap = new HashMap<>();
		public List<Shape> shapes = new ArrayList<>();

		public Point getNextPoint(int index) {
			return nextPointMap.get(shapes.get(index));
		}
	}
}

package com.backinfile.map.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.backinfile.map.Log;
import com.backinfile.map.SysException;
import com.backinfile.support.Utils;

public class Rectangle extends Shape {

	public Rectangle() {
		super();
	}

	public Rectangle(Collection<Point> points) {
		this(points, true);
	}

	public Rectangle(Collection<Point> points, boolean reorder) {
		super(points);

		if (points.size() != 4) {
			throw new SysException("error in Rectangle create");
		}

		if (reorder) {
			reorder();
		}
	}

	public void reorder() {
		var array = new ArrayList<>(this.points);
		this.points.clear();
		this.points.addAll(getReorderPoints(array));
	}

	// 调整顶点顺序, 只适用于凸四边形
	// 顶点顺时针排序
	public static List<Point> getReorderPoints(Collection<Point> points) {
		if (points.size() != 4) {
			throw new SysException("error in reorder4points");
		}
		List<Point> result = new ArrayList<>();
		var array = points;
		for (var p1 : array) {
			for (var p2 : array) {
				if (p1 != p2) {
					var p3 = array.stream().filter(p -> p != p1 && p != p2).findAny().get();
					var p4 = array.stream().filter(p -> p != p1 && p != p2 && p != p3).findAny().get();
					// 如果其余两点分布在线端两侧，
					if (Utils.calcArea(p1, p2, p3) > 0 && Utils.calcArea(p1, p2, p4) < 0) {
						result.add(p1);
						result.add(p3);
						result.add(p2);
						result.add(p4);
						return result;
					}
				}
			}
		}
		Log.game.warn("order point failed");
		return new ArrayList<>(points);
	}

	@Override
	public Shape deepCopy() {
		return new Rectangle(getCopyPoints());
	}

}

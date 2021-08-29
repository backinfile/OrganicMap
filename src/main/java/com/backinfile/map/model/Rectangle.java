package com.backinfile.map.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.backinfile.map.SysException;
import com.backinfile.support.Utils;

public class Rectangle extends Shape {

	public Rectangle() {
		super();
	}

	public Rectangle(Collection<Point> points) {
		super(points);

		if (points.size() != 4) {
			throw new SysException("error in Rectangle create");
		}
		reorder();
	}

	// 调整顶点顺序, 只适用于凸四边形
	// 顶点顺时针排序
	public void reorder() {
		var array = new ArrayList<>(this.points);
		for (var p1 : array) {
			for (var p2 : array) {
				if (p1 != p2) {
					var p3 = array.stream().filter(p -> p != p1 && p != p2).findAny().get();
					var p4 = array.stream().filter(p -> p != p1 && p != p2 && p != p3).findAny().get();
					// 如果其余两点分布在线端两侧，
					if (Utils.calcArea(p1, p2, p3) > 0 && Utils.calcArea(p1, p2, p4) < 0) {
						this.points.clear();
						this.points.add(p1);
						this.points.add(p3);
						this.points.add(p2);
						this.points.add(p4);
						return;
					}
				}
			}
		}
	}

	public Map<Point, Point> getApproPoints() {
		Map<Point, Point> result = new HashMap<>();

		var center = getCenter();
		var p1 = getPoints().get(0);
		var p2 = getPoints().get(1);

		return result;
	}

}

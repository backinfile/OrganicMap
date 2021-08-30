package com.backinfile.map.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backinfile.map.model.Point;
import com.backinfile.map.model.Shape;

public class ShapeViewManager {
	private static final float VIEW_SPEED = 0.1f;

	private Map<Long, Shape> views = new HashMap<>();
	public List<Long> visibleList = new ArrayList<>();

	public Collection<Shape> getViews() {
		if (visibleList.isEmpty()) {
			return views.values();
		}
		List<Shape> visibleShapes = new ArrayList<>();
		for (var id : visibleList) {
			var view = views.get(id);
			if (view != null) {
				visibleShapes.add(view);
			}
		}
		return visibleShapes;
	}

	public void preSetShapeView(Shape shape, Shape view) {
		views.put(shape.getId(), view);
	}

	public double update(List<Shape> shapes) {
		double loss = 0;
		List<Long> updatedViews = new ArrayList<>();
		for (var shape : shapes) {
			var view = views.get(shape.getId());
			if (view == null) {
				view = shape.deepCopy();
				views.put(shape.getId(), view);
			}
			updatedViews.add(view.getId());
			for (int i = 0; i < view.getPoints().size(); i++) {
				loss += doMove(view.getPoints().get(i), shape.getPoints().get(i));
			}
		}
		// 移除消失的view
		for (var view : new ArrayList<>(views.values())) {
			if (!updatedViews.contains(view.getId())) {
				views.remove(view.getId());
				loss += GenManager.Gap;
			}
		}
		return loss;
	}

	/**
	 * @return loss
	 */
	private double doMove(Point p, Point target) {
		if (p.isSame(target)) {
			return 0;
		}
		var distance = p.getDistance(target);
		if (distance <= VIEW_SPEED) {
			p.setPoint(target);
			return distance * distance;
		}
		var moveLength = distance / 100;
		var translate = target.sub(p).unit().mul(moveLength);
		p.translate(translate);
		return moveLength * moveLength;
	}
}

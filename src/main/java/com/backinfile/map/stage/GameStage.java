package com.backinfile.map.stage;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.map.Settings;
import com.backinfile.map.manager.GameManager;
import com.backinfile.map.manager.GenManager;
import com.backinfile.map.manager.RelaxManager;
import com.backinfile.map.manager.SplitManager;
import com.backinfile.map.model.Point;
import com.backinfile.map.model.Shape;
import com.backinfile.support.Time2;
import com.backinfile.support.TimerQueue;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameStage extends Stage {

	private ShapeRenderer renderer;
	private List<Shape> shapes;
	private TimerQueue timerQueue;
	private long relaxTimerId;

	public GameStage(Viewport viewport) {
		super(viewport);
		renderer = new ShapeRenderer();
		shapes = new ArrayList<>();
		timerQueue = new TimerQueue();

		shapes.addAll(SplitManager.splitShapes(GenManager.randomMerge(GenManager.genTriangles(), 1234)));

		RelaxManager relaxManager = new RelaxManager();

		relaxTimerId = timerQueue.applyTimer(Time2.SEC / 30, Time2.SEC, () -> {
			var loss = relaxManager.relax(shapes, 0.1f);
			if (loss < 0.3f) {
				GameManager.takeScreenshot();
				timerQueue.removeTimer(relaxTimerId);
			}
		});
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		timerQueue.update();
	}

	@Override
	public void draw() {
		super.draw();
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLACK);
		for (var shape : shapes) {
			drawShape(shape);
		}
		renderer.end();

	}

	private void drawLine(Point a, Point b) {
		var at = a.getTranslated(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
		var bt = b.getTranslated(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
		renderer.rectLine(at.getX(), at.getY(), bt.getX(), bt.getY(), 3);
	}

	private void circle(Point point, float radius) {
		var p = point.getTranslated(Settings.SCREEN_WIDTH / 2, Settings.SCREEN_HEIGHT / 2);
		renderer.circle(p.getX(), p.getY(), radius);
	}

	private void drawShape(Shape shape) {
		var lastPoint = shape.getPoints().get(shape.getPoints().size() - 1);
		for (var point : shape.getPoints()) {
			drawLine(point, lastPoint);
			lastPoint = point;
		}
	}

}

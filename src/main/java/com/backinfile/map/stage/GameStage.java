package com.backinfile.map.stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.backinfile.map.Log;
import com.backinfile.map.Settings;
import com.backinfile.map.manager.CellSplitManager;
import com.backinfile.map.manager.GameManager;
import com.backinfile.map.manager.GenManager;
import com.backinfile.map.manager.RelaxManager;
import com.backinfile.map.manager.ShapeViewManager;
import com.backinfile.map.manager.SplitManager;
import com.backinfile.map.model.Point;
import com.backinfile.map.model.Shape;
import com.backinfile.support.Time2;
import com.backinfile.support.TimerQueue;
import com.backinfile.support.Timing;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameStage extends Stage {
	public static boolean EnableShapeColor = false;
	public static float RELAX_RATE = 0.2f;

	private ShapeRenderer renderer;
	private TimerQueue timerQueue;
	private Random random;

	private List<Shape> shapes;
	private ShapeViewManager shapeViewManager;
	private RelaxManager relaxManager;

	private long relaxTimerId;

	public GameStage(Viewport viewport) {
		super(viewport);
		renderer = new ShapeRenderer();
		timerQueue = new TimerQueue();
		random = new Random();

		shapes = new ArrayList<>();
		shapeViewManager = new ShapeViewManager();
		relaxManager = new RelaxManager();

		init();
	}

	@Timing("game stage init")
	private void init() {
		shapes.addAll(SplitManager.splitShapes(GenManager.randomMerge(GenManager.genTriangles(), random)));
		// 初始化
		shapeViewManager.update(shapes);

//		relaxManager.relaxRepeat(shapes, 2000);

//		timerQueue.applyTimer(Time2.SEC, () -> {
//			cellSplitManager.randomSplit4(shapes);
//		});
	}

	public void relaxTimerStart() {
		relaxTimerId = timerQueue.applyTimer(Time2.SEC / 30, Time2.SEC, () -> {
			var loss = relaxManager.relax(shapes, RELAX_RATE);
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
		relaxManager.relaxRepeat(shapes, 3);
		shapeViewManager.update(shapes);
	}

	@Override
	public void draw() {
		super.draw();
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLACK);
		for (var shape : shapeViewManager.getViews()) {
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
			renderer.setColor(getColor(shape.getId()));
			drawLine(point, lastPoint);
			lastPoint = point;
		}
	}

	private Color getColor(long key) {
		if (!EnableShapeColor) {
			return Color.LIGHT_GRAY;
		}
		switch ((int) (key % 7)) {
		case 0:
			return Color.BLACK;
		case 1:
			return Color.BLUE;
		case 2:
			return Color.YELLOW;
		case 3:
			return Color.RED;
		case 4:
			return Color.DARK_GRAY;
		case 5:
			return Color.GREEN;
		case 6:
			return Color.GRAY;
		case 7:
			return Color.SKY;
		}
		return Color.BLACK;
	}
}

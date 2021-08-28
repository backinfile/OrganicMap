package com.backinfile.map.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameScreen implements Screen {
	private GameStage gameStage;

	public GameScreen() {
	}

	@Override
	public void show() {
		gameStage = new GameStage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	@Override
	public void render(float delta) {
		gameStage.act(delta);

		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameStage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
}

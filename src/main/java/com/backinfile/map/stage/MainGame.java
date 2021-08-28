package com.backinfile.map.stage;

import com.badlogic.gdx.Game;

public class MainGame extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}

}

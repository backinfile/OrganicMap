package com.backinfile.map;

import com.backinfile.map.stage.MainGame;
import com.backinfile.support.ReflectionUtils;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DestopLuncher {
	public static void main(String[] args) {

		ReflectionUtils.initTimingMethod("com.backinfile");

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
		config.setResizable(false);
		new Lwjgl3Application(new MainGame(), config);
	}
}

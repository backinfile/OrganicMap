package com.backinfile.map.manager;

import java.util.zip.Deflater;

import com.backinfile.support.Timing;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameManager {

	@Timing
	public static void takeScreenshot() {
		var pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		PixmapIO.writePNG(Gdx.files.local("output/output.png"), pixmap, Deflater.DEFAULT_COMPRESSION, true);
	}
}

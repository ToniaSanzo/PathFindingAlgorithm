package com.toni.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.toni.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Path Finding Algorithm";
		config.width = 400;
		config.height = 700;
		config.useGL30 = false;
		config.resizable = true;

		new LwjglApplication(new Game(), config);
	}
}

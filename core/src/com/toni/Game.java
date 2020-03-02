package com.toni;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.toni.managers.GameInputProcessor;
import com.toni.managers.GameStateManager;


public class Game extends ApplicationAdapter {

	public static int WIDTH;              // Width of the game-universe
	public static int HEIGHT;             // Height of the game-universe

	public static OrthographicCamera cam; // Camera used to view the game-universe
	private GameStateManager gsm;

	/**
	 * Create called on startup
	 */
	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();                       // set width
		HEIGHT = Gdx.graphics.getHeight();                     // set height

		cam = new OrthographicCamera(WIDTH, HEIGHT);           // specify camera dimensions
		cam.translate(WIDTH/2, HEIGHT/2);               // Move right: Width/2 & Move left: Height/2
		cam.update();                                          // update cam to have the new dimensions

		GameInputProcessor gIP = new GameInputProcessor(cam);
		Gdx.input.setInputProcessor(gIP);                      // input's processed by the GameInputProcessor class

		gsm = new GameStateManager();
	}

	@Override
	public void render () {
		// Clear screen to black
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void dispose () {}
}

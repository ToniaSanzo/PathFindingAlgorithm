package com.toni;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.toni.managers.GameInputProcessor;
import com.toni.managers.GameKeys;
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
		handleInput();
		cam.update();

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

	private void handleInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			System.out.println("Up");
			cam.translate(0f,3f,0f);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){ cam.translate(0,-3,0); }

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){ cam.translate(-3,0,0); }

		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){ cam.translate(3,0,0); }
	}
}

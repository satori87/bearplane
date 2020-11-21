package com.bg.bearplane.engine;

import com.badlogic.gdx.Screen;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.net.TCPClient;

public class GameScreen implements Screen {

	public Bearable game;

	public GameScreen(Bearable game) {
		super();
		this.game = game;

	}

	@Override
	public void render(float delta) {
		try {
			// libGDX calls render only so we must distinguish
			// between update and render ourselves
			// ignore delta, we use our own timing system in Scene
			game.doTimers(System.currentTimeMillis());
			Scene.updateScene();
			if (game instanceof TCPClient) {
				TCPClient c = (TCPClient) game;
				c.processPacketQueue();
			}
			game.update();
			Scene.renderScene();
		} catch (Exception e) {
			Log.error(e);
		}
	}

	@Override
	public void resize(int width, int height) {
		try {
			Scene.setupScreen(game.getGameWidth(), game.getGameHeight());
		} catch (Exception e) {
			Log.error(e);
		}
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		// Leave blank
	}
}

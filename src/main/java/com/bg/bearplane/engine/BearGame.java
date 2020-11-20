package com.bg.bearplane.engine;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.net.NetworkRegistrar;
import com.bg.bearplane.net.TCPClient;

public class BearGame extends com.badlogic.gdx.Game {

	public static Bearable game;
	public BearScreen gameScreen;
	public static BearNecessities assets;
	public NetworkRegistrar network;

	public BearGame(Bearable theGame, BearNecessities assets, NetworkRegistrar network) {
		super();
		game = theGame;
		BearGame.assets = assets;
		this.network = network;
	}

	@Override
	public void create() {
		try {
			Scene.init();
			if (game instanceof TCPClient) {
				TCPClient c = (TCPClient) game;
				network.registerClasses(c.client);
			}
			game.addScenes();
			game.addTimers();
			LoadScene ls = new LoadScene();
			ls.game = game;
			Scene.addScene("load", ls);
			Scene.change("load");
			gameScreen = new BearScreen(game);
			setScreen(gameScreen);
			preloadAssets();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}

	}

	@Override
	public void dispose() {
		super.dispose();
		assets.manager.dispose();
		game.dispose();
	}

	private static void preloadAssets() {
		assets.preloadNecessities();
		assets.preload();
	}

	public static void loadAssets() {
		assets.loadNecessities();
		assets.load();
	}

	public static void updateAssetManager() {
		try {
			assets.manager.update();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public static void loaded() {
		game.loaded();
	}

	public static boolean isAssetLoadingDone() {
		try {
			return assets.manager.isFinished();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return false;
	}

	public static float getAssetLoadProgress() {
		try {
			return assets.manager.getProgress();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return 0;
	}

	public static LwjglApplicationConfiguration getApplicationConfiguration(String name, int gameWidth, int gameHeight,
			boolean fullscreen, boolean resizable) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		try {
			cfg.title = name;
			cfg.width = gameWidth;
			cfg.height = gameHeight;
			cfg.resizable = resizable;
			//cfg.width = 1366;
			//cfg.height = 768;
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			cfg.y = screenSize.height / 2 - cfg.height / 2 - 32;
			cfg.x = 0;
			cfg.y = 0;
	        cfg.resizable = false;
	        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
	       // cfg.vSyncEnabled = false;
	        //cfg.foregroundFPS = 60;
	        //cfg.backgroundFPS = 60;
			if (fullscreen) {
				double width = screenSize.getWidth();
				double height = screenSize.getHeight();
				cfg.fullscreen = true;
				cfg.width = (int) Math.round(width);
				cfg.height = (int) Math.round(height);
				cfg.resizable = false;

			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return cfg;
	}

}

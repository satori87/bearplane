package com.bg.bearplane.engine;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bg.bearplane.gui.Scene;
import com.bg.bearplane.net.NetworkRegistrar;
import com.bg.bearplane.net.TCPClient;

public class BearGame extends com.badlogic.gdx.Game {

	public static Bearable game;
	public BearScreen gameScreen;
	public static BearNecessities assets;
	public NetworkRegistrar network;

	public BearGame(Bearable theGame) {
		super();
		game = theGame;
		BearGame.assets = game.getAssets();
		this.network = (NetworkRegistrar)game.getNetwork();
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

	public LwjglApplicationConfiguration getApplicationConfiguration() {
		String name = game.getGameName();
		int windowWidth = game.getWindowWidth();
		int windowHeight = game.getWindowHeight();
		boolean fullscreen = game.isFullscreen();
		boolean resizable = game.isResizable();
		boolean vSync = game.isvSync();		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		try {
			cfg.addIcon(game.getNecessitiesPath() + "/icon.png", FileType.Local);
			cfg.title = name;
			cfg.width = windowWidth;
			cfg.height = windowHeight;
			cfg.resizable = resizable;
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			cfg.y = screenSize.height / 2 - cfg.height / 2 - 32;
			cfg.x = 0;
			cfg.y = 0;
	        cfg.resizable = false;
	        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
	        cfg.vSyncEnabled = vSync;
	        cfg.fullscreen = fullscreen;
			if (fullscreen) {
				cfg.fullscreen = true;
				cfg.width = (int) Math.round(screenSize.getWidth());
				cfg.height = (int) Math.round(screenSize.getHeight());
				cfg.resizable = false;
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		return cfg;
	}

}

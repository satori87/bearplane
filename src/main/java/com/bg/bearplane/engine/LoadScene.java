package com.bg.bearplane.engine;

import com.bg.bearplane.gui.Scene;

public class LoadScene extends Scene {

	Bearable game;

	long update = 0;
	float progress = 0;

	public void start() {
		super.start();
	}

	public void update() {
		Bearplane.updateAssetManager();
		if (Bearplane.isAssetLoadingDone()) {
			Bearplane.loadAssets();
			Bearplane.loaded();
		} else {
			super.update();
		}
	}

	public void render() {
		super.render();
		if (tick > update) {
			update = tick + 100;
			progress = Bearplane.getAssetLoadProgress();
		}
		drawFont(0, game.getGameWidth() / 2, game.getGameHeight() / 2, (int) (progress * 100f) + "%", true, 3f);

	}

	@Override
	public void buttonPressed(int id) {

	}

	@Override
	public void enterPressedInField(int id) {

	}

}

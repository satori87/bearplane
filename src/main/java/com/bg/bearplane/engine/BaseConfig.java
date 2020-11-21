package com.bg.bearplane.engine;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class BaseConfig implements Serializable {
	public int DISPLAY_MODE = DisplayMode.WINDOW;
	public int WINDOW_WIDTH = 1366;
	public int WINDOW_HEIGHT = 768;
	public boolean RESIZABLE = false;
	public boolean ISVSYNC = true;
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
}

package com.bg.bearplane.engine;

public interface BearEssentials {

	public void preload();

	public void preloadNecessities();

	public void load();

	public void loadNecessities();

	public float getStringWidth(String s, float scale, float padding, float spacing);

	public void dispose();


}

package com.bg.bearplane.engine;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public class Effect {

	public int id = 0;
	public int type = 0;
	public PooledEffect fx;
	public int x = 0; // att location
	public int y = 0;
	public int modX = 0;
	public int modY = 0;
	public int i = 0;
	public boolean visible = true;
	public float scale = 0;

	public Effect(int id, int type, int i, int x, int y, int modX, int modY, float scale) {
		this.id = id;
		this.type = type;
		this.i = i;
		this.x = x;
		this.y = y;
		this.modX = modX;
		this.modY = modY;
	}

	public DrawTask render() {
		return new DrawTask(3, id, x, y, scale);
	}
	
	public int getTrueY() {
		return y * 32 + 16 + modY;
	}

}

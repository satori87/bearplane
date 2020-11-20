package com.bg.bearplane.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bg.bearplane.engine.Bearplane;

public class Frame extends Component {

	public boolean useBackground = true;
	public boolean useFrame = true;

	public boolean relative = false;

	public List<Frame> frames = new LinkedList<Frame>();
	public List<Button> buttons = new LinkedList<Button>();
	public List<Label> labels = new LinkedList<Label>();
	public List<TextBox> textBoxes = new LinkedList<TextBox>();
	public List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
	public List<ListBox> listBoxes= new ArrayList<ListBox>();

	Frame parent = null;


	public List<TextBox> fields;
	
	public Frame(Scene scene) {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
		useBackground = false;
		centered = false;
		useFrame = false;
	}
	
	public Frame(Scene scene, int x, int y, int width, int height, boolean useBackground, boolean centered,
			boolean useFrame) {
		super(scene, 0, x, y);
		this.width = width;
		this.height = height;
		this.useBackground = useBackground;
		this.centered = centered;
		this.useFrame = useFrame;
	}

	public Frame(Scene scene, int x, int y, int width, int height, boolean useBackground, boolean centered) {
		this(scene, x, y, width, height, useBackground, centered, true);
	}

	int focus = 0;

	void nextFocus() {
		if (textBoxes.size() > 0) {
			boolean f = false;
			for (TextBox t : textBoxes) {
				if (t.focus) {
					f = true;
				}
			}
			if (f) {
				if (textBoxes.get(focus) != null) {
					textBoxes.get(focus).focus = false;
				}
				focus++;
				if (focus >= textBoxes.size()) {
					focus = 0;
				}
				if (textBoxes.get(focus) != null) {
					textBoxes.get(focus).focus = true;
				}
			}
		}
	}

	public void update() {
		for (Frame d : frames) {
			d.updateComponent(tick);
		}
		for (Button b : buttons) {
			b.updateComponent(tick);
		}
		for (TextBox t : textBoxes) {
			t.updateComponent(tick);
		}
		for (CheckBox c : checkBoxes) {
			c.updateComponent(tick);
		}
		for (ListBox c : listBoxes) {
			c.updateComponent(tick);
		}
	}

	public void render() {

		if (centered) {
			this.x -= (width / 2);
			this.y -= (height / 2);
		}
		if (parent != null && relative) {
			this.x += parent.x;
			this.y += parent.y;
		}
		TextureRegion[] framePiece = Bearplane.assets.framePiece;

		if (useBackground) {
			scene.draw(Bearplane.assets.bg[1], x + 4, y + 4, width - 8, height - 8, 0, 0, width, height);
		}
		if (useFrame) {
			// draw top left
			scene.drawRegion(framePiece[0], x, y, false, 0, 1);
			// top right
			scene.drawRegion(framePiece[1], x + width - 32, y, false, 0, 1);
			// bottom left
			scene.drawRegion(framePiece[2], x, y + height - 32, false, 0, 1);
			// bottom right
			scene.drawRegion(framePiece[3], x + width - 32, y + height - 32, false, 0, 1);

			// left side
			for (int b = 32; b < height - 32; b += 32) {
				scene.drawRegion(framePiece[4], x, y + b, false, 0, 1);
			}
			// right side
			for (int b = 32; b < height - 32; b += 32) {
				scene.drawRegion(framePiece[5], x + width - 32, y + b, false, 0, 1);
			}
			// top side
			for (int b = 32; b < width - 32; b += 32) {
				scene.drawRegion(framePiece[6], x + b, y, false, 0, 1);
			}
			// bottom side
			for (int b = 32; b < width - 32; b += 32) {
				scene.drawRegion(framePiece[7], x + b, y + height - 32, false, 0, 1);
			}
		}
		if (centered) {
			this.x += (width / 2);
			this.y += (height / 2);
		}
		if (parent != null && relative) {
			this.x -= parent.x;
			this.y -= parent.y;
		}
		for (Frame d : frames) {
			d.renderComponent();
		}
		for (Button b : buttons) {
			b.renderComponent();
		}
		for (Label l : labels) {
			l.renderComponent();
		}
		for (TextBox t : textBoxes) {
			t.renderComponent();
		}
		for (CheckBox c : checkBoxes) {
			c.renderComponent();
		}
		for(ListBox c : listBoxes) {
			c.renderComponent();
		}
	}

}

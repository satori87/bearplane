package com.bg.bearplane.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.bg.bearplane.engine.Bearplane;
import com.bg.bearplane.engine.Util;
import com.bg.bearplane.engine.Log;

public abstract class Scene {

	public static HashMap<String, Scene> scenes = new HashMap<String, Scene>();
	public static Scene scene;

	// To use: in game class, call Scene.init(), then Scene.updateScene() and
	// Scene.renderScene()
	// Scenes have to be added with addScene(name, Scene)

	// libgdx stuff
	private static float screenWidth, screenHeight;
	public static float viewWidth, viewHeight;
	public static int originX, originY;
	public static OrthographicCamera cam;
	public static OrthographicCamera curCam;
	private static ShapeRenderer shapeRenderer;
	public static SpriteBatch batcher;

	public static InputHandler input;

	public static long lastRepeat = 0;
	public static long tick = 0;

	public boolean shifting = false;
	public boolean alting = false;
	public boolean ctrling = false;

	public boolean autoCenter = false;

	// iface stuff
	public HashMap<String, Frame> frames = new HashMap<String, Frame>();
	public HashMap<String, Button> buttons = new HashMap<String, Button>();
	public HashMap<String, Label> labels = new HashMap<String, Label>();
	public HashMap<String, Field> fields = new HashMap<String, Field>();
	public HashMap<String, CheckBox> checkBoxes = new HashMap<String, CheckBox>();
	public HashMap<String, Window> windows = new HashMap<String, Window>();
	public HashMap<String, ListBox> listBoxes = new HashMap<String, ListBox>();

	public long startStamp = 0;
	public boolean started = false;

	public abstract void buttonPressed(String id);

	public abstract void enterPressedInField(String id);

	//int focus;

	public static boolean locked = false;

	public void enterPressedInList(String id) {

	}

	public void listChanged(String id, int sel) {

	}

	public static boolean shifting() {
		return input.keyDown[Keys.SHIFT_LEFT] || input.keyDown[Keys.SHIFT_RIGHT];
	}

	public static void init() {
		try {
			input = new InputHandler();
			Gdx.input.setInputProcessor(input);
			setupScreen(Bearplane.game.getGameWidth(), Bearplane.game.getGameHeight());
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public Button addButton(Scene scene, String id, int x, int y, int width, int height, String text) {
		return addButton(scene, id, x, y, width, height, text, false);
	}

	public Button addButton(Scene scene, String id, int x, int y, int width, int height, String text, boolean toggle) {
		return buttons.put(id, new Button(scene, id, x, y, width, height, text, toggle));
	}
	
	public static Button getButton(HashMap<String, Button> buttons, String id) {
		return buttons.get(id);
	}
	
	public Button getButton(String id) {
		return buttons.get(id);
	}
	
	public Frame addFrame(Scene scene, String id, int x, int y, int w, int h, boolean bg, boolean center) {
		return frames.put(id, new Frame(scene, id, x, y, w, h, bg, center));
	}
	
	public Frame getFrame(String id) {
		return frames.get(id);
	}
	
	public Frame getFrame(HashMap<String, Frame> frames, String id) {
		return frames.get(id);
	}
	
	public Label addLabel(Scene scene, String id, int x, int y, float s, String t, Color c, boolean center) {
		return labels.put(id, new Label(scene,id,x,y,s,t,c,center));
	}
	
	public static Label getLabel(HashMap<String, Label> labels, String id) {
		return labels.get(id);
	}
	
	public Label getLabel(String id) {
		return labels.get(id);
	}
	
	public Field addField(Scene scene, String id, int max, boolean focus, int x, int y, int width, boolean centered, Frame frame) {
		return fields.put(id, new Field(scene, id, max, focus, x, y, width, centered,frame));
	}
	
	public Field addField(Scene scene, String id, int max, boolean focus, int x, int y, int width, boolean centered) {
		return addField(scene, id, max, focus, x,y,width,centered,null);
	}
	
	public Field addField(Frame f,String id,  String name, int maxLength, boolean allowLetters) {
		int w = 100;
		f.addLabel(this, id, modX + 10 + c * cW, modY + 110 + r * 60, 1f, name + ":", Color.WHITE, false);
		Field t = addField(this, id, maxLength, false, modX + 120 + c * cW, modY + 70 + r * 60, w, false);
		t.allowLetters = allowLetters;
		r++;
		return t;
	}

	public List<Button> addRadio(Frame f, String[] names, String[] ids, int w, int h) {
		int count = 0;
		List<Button> bl = new ArrayList<Button>();
		Button b;
		for (String i : ids) {
			b = f.addButton(this, i, modX + 40 + c * cW + count * (w + 4), modY + 116 + r * 60, w, h, names[count]);
			b.toggle = true;
			count++;
			bl.add(b);
		}
		r++;
		return bl;
	}

	public Field addField(Frame f, String id, String name, int maxLength) {
		return addField(f, id, name, maxLength, false);
	}

	public static void updateScene() {
		if (scene != null) {
			if (!locked) {
				scene.updateBase();
			} else if (msgBoxFrame != null) {
				// we have a msg box
				msgBoxFrame.updateComponent(tick);
				if (msgBoxOK.justClicked) {
					if (msgBoxMsgs.size() > 0) {
						msgBoxMsgs.remove(0);
					}
					if (msgBoxMsgs.size() == 0) {
						msgBoxFrame = null;
						input.wasMouseJustClicked[0] = false;
						scene.deselectAllButtons();
						unlock();
					}
				}
			}
		} else {
			// Log.fatal("NOSCENE");
		}
		input.keyPress.clear();
	}

	public void deselectAllButtons() {
		for (Button b : buttons.values()) {
			if (!b.toggle) {
				b.sel = false;
				b.click = false;
				b.justClicked = false;
			}
		}
	}

	public void updateBase() {
		try {
			tick = System.currentTimeMillis();
			shifting = input.keyDown[59] || input.keyDown[60];
			alting = input.keyDown[57] || input.keyDown[58];
			ctrling = input.keyDown[129] || input.keyDown[130];

			for (Frame d : frames.values())
				d.updateComponent(tick);
			for (Button b : buttons.values())
				b.updateComponent(tick);
			for (Field t : fields.values())
				t.updateComponent(tick);
			for (CheckBox c : checkBoxes.values())
				c.updateComponent(tick);
			for (ListBox l : listBoxes.values()) {
				l.updateComponent(tick);
			}
			for (int i = 0; i < 10; i++) {
				if (input.wasMouseJustClicked[i]) { // none of the scene objects caught this
					mouseDown(input.mouseDownX[i], input.mouseDownY[i], i);
					input.wasMouseJustClicked[i] = false;
				} else if (input.wasMouseJustReleased[i]) {

					mouseUp(input.mouseUpX[i], input.mouseUpY[i], i);
					input.wasMouseJustReleased[i] = false;
				}
			}
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public static void renderScene() {
		try {
			if (scene == null) {
				return;
			}
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batcher.enableBlending();
			batcher.begin();
			scene.renderBase();
			if (msgBoxFrame != null) {
				msgBoxFrame.renderComponent();
				String ss = msgBoxMsgs.get(0);
				List<String> lines = Util.wrapText(2f, Bearplane.game.getGameWidth() / 2, ss);
				int i = 0;
				for (String s : lines) {
					i++;
					scene.drawFontAbs(0, Bearplane.game.getGameWidth() / 2,
							Bearplane.game.getGameHeight() / 2 - 170 + i * 40, s, true, 2.0f);
				}
			}
			batcher.end();
			letterBox();
		} catch (Exception e) {
			Log.error(e);

		}
	}
	
	public abstract void render();
	
	public abstract void update();

	public void renderBase() {
		try {
			// overload only in some scenes
			for (Frame d : frames.values()) {
				d.renderComponent();
			}
			for (Button b : buttons.values()) {
				b.renderComponent();
			}
			for (Label l : labels.values()) {
				l.renderComponent();
			}
			for (Field t : fields.values()) {
				t.renderComponent();
			}
			for (CheckBox c : checkBoxes.values()) {
				c.renderComponent();
			}
			for (ListBox l : listBoxes.values()) {
				l.renderComponent();
			}
		} catch (Exception e) {
			Log.error(e);

		}
		if (autoCenter) {
			moveCameraTo(Bearplane.game.getGameWidth() / 2, Bearplane.game.getGameHeight() / 2);
		}
	}

	public void mouseDown(int x, int y, int button) {
		// non dialog, non button, non text touches. overload this in specific scene
	}

	public void mouseUp(int x, int y, int button) {
		// non dialog, non button, non text touches. overload this in specific scene
	}

	public void checkBox(String id) {
		// overload this to get notified of checkbox activity
	}

	public void clear() {
		// dude make all of these extend something so you can fix this ridiculous list
		frames.clear();
		buttons.clear();
		labels.clear();
		fields.clear();
		checkBoxes.clear();
		startStamp = tick;
	}

	public void start() {
		try {
			started = true;
			clear();
			startStamp = tick;
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void addButtons(int x, int y, int width, int height, int padding, String[] text, String[] ids, boolean up,
			boolean toggle) {
		int n = text.length;
		int bX = x;
		int bY = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		if (n % 2 != 0) {
			if (up) {
				bY += (padding + height) / 2;
			} else {
				bX += (width + padding);
			}
		}
		for (int c = 0; c < n; c++) {
			buttons.put(ids[c], new Button(this, ids[c], bX, bY, width, height, text[c], toggle));
			if (up) {
				bY += (height + padding);
			} else {
				bX += (width + padding);
			}
		}
	}

	public void addButtons(HashMap<String, Button> buttons, int x, int y, int width, int height, int padding,
			String[] text, String[] ids, boolean up, boolean toggle) {
		int n = text.length;
		int bX = x;
		int bY = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		// if (!up) {
		// bY = x;
		// bX = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		// }
		if (n % 2 != 0) {
			if (up) {
				// bY += (padding + height) / 2;
			} else {
				bX += (width + padding);
			}
		}
		for (int c = 0; c < n; c++) {
			buttons.put(ids[c], new Button(this, ids[c], bX, bY, width, height, text[c], toggle));
			if (up) {
				// bY += (height + padding);
			} else {
				bX += (width + padding);
			}
		}
	}

	void nextFocus() {
		if (fields.size() > 0) {
			boolean f = false;
			for (Field t : fields.values()) {
				if (t.focus) {
					f = true;
				}
			}
			if (f) {
				if (fields.get(focus) != null) {
					fields.get(focus).focus = false;
				}
				focus++;
				if (focus >= fields.size()) {
					focus = 0;
				}
				if (fields.get(focus) != null) {
					fields.get(focus).focus = true;
				}
			} else {
				Log.warn("Scene.nextFocus() did a funny thing?");
			}
		}
	}

	public void clip(int x, int y, int width, int height) {
		try {
			batcher.flush();
			Rectangle scissors = new Rectangle();
			Rectangle clipBounds = new Rectangle(x, y, width, height);
			ScissorStack.calculateScissors(cam, batcher.getTransformMatrix(), clipBounds, scissors);
			ScissorStack.pushScissors(scissors);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void endClip() {
		try {
			batcher.flush();
			ScissorStack.popScissors();
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void draw(Texture t, int x, int y, int w, int h, int srcX, int srcY, int srcW, int srcH) {
		try {
			batcher.draw(t, x, y, w, h, srcX, srcY, srcW, srcH, false, true);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void drawAbs(Texture t, int x, int y, int w, int h, int srcX, int srcY, int srcW, int srcH) {
		try {
			batcher.draw(t, x + curCam.position.x - Bearplane.game.getGameWidth() / 2,
					y + curCam.position.y - Bearplane.game.getGameHeight() / 2, w, h, srcX, srcY, srcW, srcH, false,
					true);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void drawAbs(Texture t, int x, int y, int srcX, int srcY, int w, int h) {
		try {
			drawAbs(t, x, y, w, h, srcX, srcY, w, h);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void draw(Texture t, int x, int y, int srcX, int srcY, int w, int h) {
		try {
			draw(t, x, y, w, h, srcX, srcY, w, h);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void drawRegion(TextureRegion region, float X, float Y, boolean centered, float rotation, float scale) {
		try {
			if (region == null) {
				return;
			}
			int width = region.getRegionWidth();
			int height = region.getRegionHeight();
			float eX = 0;
			float eY = 0;
			// if (gameState == 3) {
			// eX = X + originX;
			// eY = Y + originY;
			// } else {
			eX = X;
			eY = Y;
			// }
			if (centered) {
				eX -= (width / 2);
				eY -= (height / 2);
			}
			// we gotta round the floats
			int dX = Math.round(eX);
			int dY = Math.round(eY);
			if (centered) {
				batcher.draw(region, dX, dY, width / 2, height / 2, width, height, scale, scale, rotation);
			} else {
				batcher.draw(region, dX, dY, 0, 0, width, height, scale, scale, rotation);
			}
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void drawRegionAbout(TextureRegion region, float X, float Y, float aboutX, float aboutY, boolean centered,
			float rotation, float scale) {
		try {
			if (region == null) {
				return;
			}
			int width = region.getRegionWidth();
			int height = region.getRegionHeight();
			float eX = X + originX;
			float eY = Y + originY;
			if (centered) {
				eX -= (width / 2);
				eY -= (height / 2);
			}
			// we gotta round the floats
			float orX = aboutX + originX;
			float orY = aboutY + originY;
			int dX = Math.round(eX);
			int dY = Math.round(eY);
			int oX = Math.round(orX);
			int oY = Math.round(orY);
			batcher.draw(region, dX, dY, oX, oY, width, height, scale, scale, rotation);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void drawFontAbs(int type, float X, float Y, String s, boolean centered, float scale, Color col) {
		try {
			if (s.length() < 1) {
				return;
			}
			float curX = X;
			float padding = 0 * scale;
			float spacing = 1.0f * scale;
			float total = 0;
			float oX, oY;
			// get a quick count of width
			if (centered) {
				total = Bearplane.assets.getStringWidth(s, scale, padding, spacing);
				oX = Math.round(-total / 2);
				oY = Math.round((scale * -16.0f) / 2);
			} else {
				oX = 0;
				oY = 0;
			}
			batcher.setColor(col);
			for (char c : s.toCharArray()) {
				int ascii = (int) c;
				if (Bearplane.assets.fontWidth[ascii] > 0) {
					drawRegion(Bearplane.assets.font[type][ascii],
							Math.round(curX + padding + oX + curCam.position.x - Bearplane.game.getGameWidth() / 2),
							Math.round(Y + oY + curCam.position.y - Bearplane.game.getGameHeight() / 2), false, 0,
							scale);
					curX += Bearplane.assets.fontWidth[ascii] * scale + padding * 2 + spacing;
				}
			}
			batcher.setColor(Color.WHITE);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void drawFontAbs(int type, float X, float Y, String s, boolean centered, float scale) {
		drawFontAbs(type, X, Y, s, centered, scale, Color.WHITE);
	}

	public void drawFont(int type, float X, float Y, String s, boolean centered, float scale, Color col) {
		try {
			if (s.length() < 1) {
				return;
			}
			float curX = X;
			float padding = 0 * scale;
			float spacing = 1.0f * scale;
			float total = 0;
			float oX, oY;
			// get a quick count of width
			if (centered) {
				total = Bearplane.assets.getStringWidth(s, scale, padding, spacing);
				oX = Math.round(-total / 2);
				oY = Math.round((scale * -16.0f) / 2);
			} else {
				oX = 0;
				oY = 0;
			}
			batcher.setColor(col);
			for (char c : s.toCharArray()) {
				int ascii = (int) c;
				if (Bearplane.assets.fontWidth[ascii] > 0) {
					drawRegion(Bearplane.assets.font[type][ascii], Math.round(curX + padding + oX), Math.round(Y + oY),
							false, 0, scale);
					curX += Bearplane.assets.fontWidth[ascii] * scale + padding * 2 + spacing;
				}
			}
			batcher.setColor(Color.WHITE);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public void drawFont(int type, float X, float Y, String s, boolean centered, float scale) {
		drawFont(type, X, Y, s, centered, scale, Color.WHITE);
	}

	public static int getRelativeX(int x) {
		// return x;
		return Math.round(((float) x / Gdx.graphics.getWidth()) * viewWidth - originX);
	}

	public static int getRelativeY(int y) {
		// return y;
		return Math.round(((float) y / Gdx.graphics.getHeight()) * viewHeight - originY);
	}

	private static void letterBox() {
		try {
			int x = Math.round(cam.position.x) - Math.round(Bearplane.game.getGameWidth() / 2 + originX);
			int y = Math.round(cam.position.y) - Math.round(Bearplane.game.getGameHeight() / 2 + originY);
			// ensure our letterbox area is completely black (or filled with
			// whatever letterbox design we choose
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(0, 0, 0, 1);
			if (originY > 0) {
				shapeRenderer.rect(x, y - 1, viewWidth, originY + 1); // Top bar
				shapeRenderer.rect(x, y + viewHeight - originY, viewWidth, originY + 1); // Bottom
																							// bar
			} else if (originX > 0) {
				shapeRenderer.rect(x - 1, y, originX + 1, viewHeight); // Left bar
				shapeRenderer.rect(x + viewWidth - originX, y, originX, viewHeight + 1); // Right
																							// bar
			}
			shapeRenderer.end();
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public static void moveCameraBy(float x, float y) {
		try {
			cam.position.y += y;
			cam.position.x += x;
			cam.update();
			batcher.setProjectionMatrix(cam.combined);
			shapeRenderer.setProjectionMatrix(cam.combined);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public static void moveCameraTo(float x, float y) {
		try {
			cam.position.y = Math.round(y);
			cam.position.x = Math.round(x);
			cam.update();
			batcher.setProjectionMatrix(cam.combined);
			shapeRenderer.setProjectionMatrix(cam.combined);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public static void changeCamera(OrthographicCamera c) {
		try {
			curCam = c;
			batcher.setProjectionMatrix(c.combined);
			shapeRenderer.setProjectionMatrix(c.combined);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	static List<String> msgBoxMsgs = new LinkedList<String>();
	static Frame msgBoxFrame;
	static Button msgBoxOK;

	public void msgBox(String s) {
		msgBoxMsgs.add(s);
		lock();
		if (msgBoxFrame == null) {
			int gw = Bearplane.game.getGameWidth();
			int gh = Bearplane.game.getGameHeight();
			msgBoxFrame = new Frame(this, "frame", gw / 2, gh / 2, 750, 384, true, true, true);
			msgBoxOK = new Button(this, "ok", gw / 2, gh / 2 + 128, 128, 48, "OK", false);
			msgBoxFrame.buttons.put("ok", msgBoxOK);
		}
	}

	public static void BADmsgBox(String s) {
		try {
			final JDialog dialog = new JDialog();
			dialog.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(dialog, s, "Odyssey", JOptionPane.OK_OPTION);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public static void setupScreen(float gameWidth, float gameHeight) {
		try {
			Log.debug("Set screen");
			screenWidth = Gdx.graphics.getWidth();
			screenHeight = Gdx.graphics.getHeight();
			float screenR = (float) screenWidth / (float) screenHeight;
			float gameR = gameWidth / gameHeight;
			if (screenR == gameR) {
				originX = 0;
				originY = 0;
				viewWidth = gameWidth;
				viewHeight = gameHeight;
			} else if (screenR > gameR) {
				viewWidth = gameHeight * screenR;
				viewHeight = gameHeight;
				originX = (int) ((viewWidth - gameWidth) / 2.0f);
				originY = 0;
			} else if (screenR < gameR) {
				viewWidth = gameWidth;
				viewHeight = gameWidth / screenR;
				originX = 0;
				originY = (int) ((viewHeight - gameHeight) / 2.0f);
			}
			// input.ratio = screenR / gameR;
			// Set up our camera, which handles the screen scaling, use viewWidth to
			// include letterbox area
			cam = new OrthographicCamera();
			cam.setToOrtho(true, Math.round(viewWidth), Math.round(viewHeight));
			curCam = cam;
			// Create our sprite batcher and shape renderer from the camera
			batcher = new SpriteBatch();
			batcher.setProjectionMatrix(cam.combined);
			shapeRenderer = new ShapeRenderer();
			shapeRenderer.setProjectionMatrix(cam.combined);
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public static Scene lastScene = null;

	public static void change(String to) {
		try {
			input.wasMouseJustClicked[0] = false;
			Scene s = scenes.get(to);
			if (s != null) {
				lastScene = scene;
				scene = s;
				if (!scene.started) {
					scene.start();
				}
				scene.switchTo();
			}
		} catch (Exception e) {
			Log.error(e);

		}
	}

	public static Scene get(String get) {
		return scenes.get(get);
	}

	public static void addScene(String name, Scene s) {
		scenes.put(name, s);
	}

	public void switchTo() {
		// overload this if you want to

	}

	public static void lock() {
		locked = true;
	}

	public static void unlock() {
		locked = false;
	}

	public int r = 0;
	public int c = 0;
	public int cW = 250;
	public int modX = 0;
	public int modY = 0;

}

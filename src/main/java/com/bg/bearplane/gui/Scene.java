package com.bg.bearplane.gui;

import java.util.ArrayList;
import java.util.Collection;
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
import com.bg.bearplane.engine.BearGame;
import com.bg.bearplane.engine.BearTool;
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
	public List<Frame> frames = new LinkedList<Frame>();
	public List<Button> buttons = new LinkedList<Button>();
	public List<Label> labels = new LinkedList<Label>();
	public List<TextBox> textBoxes = new LinkedList<TextBox>();
	public List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
	public HashMap<String, Window> windows = new HashMap<String, Window>();
	public ArrayList<ListBox> listBoxes = new ArrayList<ListBox>();

	public long startStamp = 0;
	public boolean started = false;

	public abstract void buttonPressed(int id);

	public abstract void enterPressedInField(int id);

	int focus = 0;

	public static boolean locked = false;

	public void enterPressedInList(int id) {

	}

	public void listChanged(int id, int sel) {

	}

	public static boolean shifting() {
		return input.keyDown[Keys.SHIFT_LEFT] || input.keyDown[Keys.SHIFT_RIGHT];
	}

	public static void init() {
		try {
			input = new InputHandler();
			Gdx.input.setInputProcessor(input);
			setupScreen(BearGame.game.getGameWidth(), BearGame.game.getGameHeight());
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public static Button getButton(List<Button> buttons, int id) {
		for (Button b : buttons) {
			if (b.id == id) {
				return b;
			}
		}
		return null;
	}

	public static void updateScene() {
		if (scene != null) {
			if (!locked) {
				scene.update();
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
		for (Button b : buttons) {
			if (!b.toggle) {
				b.sel = false;
				b.click = false;
				b.justClicked = false;
			}
		}
	}

	public void update() {
		try {
			tick = System.currentTimeMillis();
			shifting = input.keyDown[59] || input.keyDown[60];
			alting = input.keyDown[57] || input.keyDown[58];
			ctrling = input.keyDown[129] || input.keyDown[130];

			for (Frame d : frames)
				d.updateComponent(tick);
			for (Button b : buttons)
				b.updateComponent(tick);
			for (TextBox t : textBoxes)
				t.updateComponent(tick);
			for (CheckBox c : checkBoxes)
				c.updateComponent(tick);
			for (ListBox l : listBoxes) {
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
			System.exit(0);
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
			scene.render();
			if (msgBoxFrame != null) {
				msgBoxFrame.renderComponent();
				String ss = msgBoxMsgs.get(0);
				List<String> lines = BearTool.wrapText(2f, BearGame.game.getGameWidth() / 2, ss);
				int i = 0;
				for (String s : lines) {
					i++;
					scene.drawFontAbs(0, BearGame.game.getGameWidth() / 2,
							BearGame.game.getGameHeight() / 2 - 170 + i * 40, s, true, 2.0f);
				}
			}
			batcher.end();
			letterBox();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void render() {
		try {
			// overload only in some scenes
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
			for (ListBox l : listBoxes) {
				l.renderComponent();
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
		if (autoCenter) {
			moveCameraTo(BearGame.game.getGameWidth() / 2, BearGame.game.getGameHeight() / 2);
		}
	}

	public void mouseDown(int x, int y, int button) {
		// non dialog, non button, non text touches. overload this in specific scene
	}

	public void mouseUp(int x, int y, int button) {
		// non dialog, non button, non text touches. overload this in specific scene
	}

	public void checkBox(int id) {
		// overload this to get notified of checkbox activity
	}

	public void clear() {
		// dude make all of these extend something so you can fix this ridiculous list
		frames.clear();
		buttons.clear();
		labels.clear();
		textBoxes.clear();
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
			System.exit(0);
		}
	}

	public void addButtons(int x, int y, int width, int height, int padding, String[] text, int[] ids, boolean up,
			boolean toggle) {
		int n = text.length;
		int bX = x;
		int bY = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		// if (!up) {
		// bY = x;
		// bX = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		// }
		if (n % 2 != 0) {
			if (up) {
				bY += (padding + height) / 2;
			} else {
				bX += (width + padding);
			}
		}
		for (int c = 0; c < n; c++) {
			buttons.add(new Button(this, ids[c], bX, bY, width, height, text[c], toggle));
			if (up) {
				bY += (height + padding);
			} else {
				bX += (width + padding);
			}
		}
	}
	

	public void addButtons(List<Button> buttons, int x, int y, int width, int height, int padding, String[] text, int[] ids, boolean up,
			boolean toggle) {
		int n = text.length;
		int bX = x;
		int bY = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		// if (!up) {
		// bY = x;
		// bX = y - (padding / 2) - (height / 2) - (((n - 1) / 2) * (padding + height));
		// }
		if (n % 2 != 0) {
			if (up) {
				//bY += (padding + height) / 2;
			} else {
				bX += (width + padding);
			}
		}
		for (int c = 0; c < n; c++) {
			buttons.add(new Button(this, ids[c], bX, bY, width, height, text[c], toggle));
			if (up) {
				//bY += (height + padding);
			} else {
				bX += (width + padding);
			}
		}
	}

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
			System.exit(0);
		}
	}

	public void endClip() {
		try {
			batcher.flush();
			ScissorStack.popScissors();
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void draw(Texture t, int x, int y, int w, int h, int srcX, int srcY, int srcW, int srcH) {
		try {
			batcher.draw(t, x , y , w, h, srcX, srcY, srcW, srcH, false, true);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void drawAbs(Texture t, int x, int y, int w, int h, int srcX, int srcY, int srcW, int srcH) {
		try {
			batcher.draw(t, x + curCam.position.x - BearGame.game.getGameWidth() / 2,
					y + curCam.position.y - BearGame.game.getGameHeight() / 2, w, h, srcX, srcY, srcW, srcH, false,
					true);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void drawAbs(Texture t, int x, int y, int srcX, int srcY, int w, int h) {
		try {
			drawAbs(t, x, y, w, h, srcX, srcY, w, h);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void draw(Texture t, int x, int y, int srcX, int srcY, int w, int h) {
		try {
			draw(t, x, y, w, h, srcX, srcY, w, h);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
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
			int dX = Math.round(eX );
			int dY = Math.round(eY );
			if (centered) {
				batcher.draw(region, dX, dY, width / 2, height / 2, width, height, scale, scale, rotation);
			} else {
				batcher.draw(region, dX, dY, 0, 0, width, height, scale, scale, rotation);
			}
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
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
			System.exit(0);
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
				total = BearGame.assets.getStringWidth(s, scale, padding, spacing);
				oX = Math.round(-total / 2);
				oY = Math.round((scale * -16.0f) / 2);
			} else {
				oX = 0;
				oY = 0;
			}
			batcher.setColor(col);
			for (char c : s.toCharArray()) {
				int ascii = (int) c;
				if (BearGame.assets.fontWidth[ascii] > 0) {
					drawRegion(BearGame.assets.font[type][ascii],
							Math.round(curX + padding + oX + curCam.position.x - BearGame.game.getGameWidth() / 2),
							Math.round(Y + oY + curCam.position.y - BearGame.game.getGameHeight() / 2), false, 0,
							scale);
					curX += BearGame.assets.fontWidth[ascii] * scale + padding * 2 + spacing;
				}
			}
			batcher.setColor(Color.WHITE);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
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
				total = BearGame.assets.getStringWidth(s, scale, padding, spacing);
				oX = Math.round(-total / 2);
				oY = Math.round((scale * -16.0f) / 2);
			} else {
				oX = 0;
				oY = 0;
			}
			batcher.setColor(col);
			for (char c : s.toCharArray()) {
				int ascii = (int) c;
				if (BearGame.assets.fontWidth[ascii] > 0) {
					drawRegion(BearGame.assets.font[type][ascii], Math.round(curX + padding + oX), Math.round(Y + oY),
							false, 0, scale);
					curX += BearGame.assets.fontWidth[ascii] * scale + padding * 2 + spacing;
				}
			}
			batcher.setColor(Color.WHITE);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
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
			int x = Math.round(cam.position.x) - Math.round(BearGame.game.getGameWidth() / 2 + originX);
			int y = Math.round(cam.position.y) - Math.round(BearGame.game.getGameHeight() / 2 + originY);
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
			System.exit(0);
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
			System.exit(0);
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
			System.exit(0);
		}
	}

	public static void changeCamera(OrthographicCamera c) {
		try {
			curCam = c;
			batcher.setProjectionMatrix(c.combined);
			shapeRenderer.setProjectionMatrix(c.combined);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	static List<String> msgBoxMsgs = new LinkedList<String>();
	static Frame msgBoxFrame;
	static Button msgBoxOK;

	public void msgBox(String s) {
		msgBoxMsgs.add(s);
		lock();
		if (msgBoxFrame == null) {
			int gw = BearGame.game.getGameWidth();
			int gh = BearGame.game.getGameHeight();
			msgBoxFrame = new Frame(this, gw / 2, gh / 2, 750, 384, true, true, true);
			msgBoxOK = new Button(this, 9999, gw / 2, gh / 2 + 128, 128, 48, "OK", false);
			msgBoxFrame.buttons.add(msgBoxOK);
		}
	}

	public static void BADmsgBox(String s) {
		try {
			final JDialog dialog = new JDialog();
			dialog.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(dialog, s, "Odyssey", JOptionPane.OK_OPTION);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
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
			System.exit(0);
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
			System.exit(0);
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
	
	public TextBox addField(Frame f, String name, int maxLength, boolean allowLetters) {
		int w = 100;
		f.labels.add(new Label(this, modX + 10 + c * cW, modY + 110 + r * 60, 1f, name + ":", Color.WHITE, false));
		TextBox t = new TextBox(this, 0, maxLength, false, modX + 120 + c * cW, modY + 70 + r * 60, w, false);
		f.fields.add(t);
		t.allowLetters = allowLetters;
		f.textBoxes.add(t);
		r++;
		return t;
	}

	public List<Button> addRadio(Frame f, String[] names, int[] ids, int w, int h) {
		int count = 0;
		List<Button> bl = new ArrayList<Button>();
		Button b;
		for (int i : ids) {
			b = new Button(this, i, modX + 40 + c * cW + count * (w + 4), modY + 116 + r * 60, w, h, names[count]);
			b.toggle = true;
			f.buttons.add(b);
			count++;
			bl.add(b);
		}
		r++;
		return bl;
	}

	public TextBox addField(Frame f, String name, int maxLength) {
		return addField(f, name, maxLength, false);
	}
}

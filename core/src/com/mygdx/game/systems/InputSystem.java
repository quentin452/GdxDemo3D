package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.GameSettings;
import com.mygdx.game.components.IntentBroadcastComponent;

/**
 * Created by user on 8/24/15.
 */
public class InputSystem extends EntitySystem {

	public static final String tag = "InputSystem";
	public final IntIntMap keys = new IntIntMap();
	public final Family family;
	public InputProcessor inputProcessor;
	IntentBroadcastComponent intent;
	Vector2 moveDirection = new Vector2();
	float zoom;
	ArrayMap<Integer, TouchData> touchMap = new ArrayMap<Integer, TouchData>();

	boolean killKeyBroadcasted = false;

	public InputSystem(IntentBroadcastComponent intent) {
		family = Family.all(IntentBroadcastComponent.class).get();
		zoom = GameSettings.CAMERA_MAX_ZOOM;
		this.intent = intent;
		inputProcessor = new MyInputListener();

	}

	@Override
	public void addedToEngine(Engine engine) {
	}

	@Override
	public void update(float deltaTime) {
		moveDirection.setZero();
		if (keys.containsKey(GameSettings.KEY_PAN_FORWARD)) {
			moveDirection.y += 1;
		}
		if (keys.containsKey(GameSettings.KEY_PAN_BACKWARD)) {
			moveDirection.y -= 1;
		}
		if (keys.containsKey(GameSettings.KEY_PAN_LEFT)) {
			moveDirection.x -= 1;
		}
		if (keys.containsKey(GameSettings.KEY_PAN_RIGHT)) {
			moveDirection.x += 1;
		}
		moveDirection.nor();

		if (zoom > GameSettings.CAMERA_MAX_ZOOM) {
			zoom = GameSettings.CAMERA_MAX_ZOOM;
		} else if (zoom < GameSettings.CAMERA_MIN_ZOOM) {
			zoom = GameSettings.CAMERA_MIN_ZOOM;
		}

		intent.moveDirection.set(moveDirection);
		intent.zoom = zoom;

		if (moveDirection.isZero() && touchMap.containsKey(0)) {
			TouchData data = touchMap.get(0);
			if (data.isDragging) {
				intent.dragStart.set(data.down);
				intent.dragCurrent.set(data.lastDrag);
				intent.isDragging = true;
				intent.pan = false;
				intent.rotate = false;
				switch (data.button) {
					case Input.Buttons.LEFT:
						intent.pan = true;
						break;
					case Input.Buttons.RIGHT:
						intent.rotate = true;
						break;
					default:
						break;
				}
			}
			data.isDragging = false;

			intent.doubleClick = data.doubleClick;
		}

		if (keys.containsKey(GameSettings.KEY_KILL_SELECTED)) {
			if (!killKeyBroadcasted) {
				intent.killSelected = true;
				killKeyBroadcasted = true;
			} else {
				intent.killSelected = false;
			}
		} else {
			intent.killSelected = false;
			killKeyBroadcasted = false;
		}

	}

	public class MyInputListener extends ClickListener implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			keys.put(keycode, keycode);
			if (keycode == GameSettings.KEY_DRAW_COLLISION_DEBUG) {
				GameSettings.DRAW_DEBUG = !GameSettings.DRAW_DEBUG;
			}
			if (keycode == GameSettings.KEY_DISPLAY_SHADOWBUFFER) {
				GameSettings.DISPLAY_SHADOWBUFFER = !GameSettings.DISPLAY_SHADOWBUFFER;
			}
			if (keycode == GameSettings.KEY_PAUSE) {

				if (GameSettings.GAME_SPEED == 1) {
					GameSettings.GAME_SPEED = 0;
				} else if (GameSettings.GAME_SPEED == 0) {
					GameSettings.GAME_SPEED = 0.05f;
				} else if (GameSettings.GAME_SPEED == 0.05f) {
					GameSettings.GAME_SPEED = 1;
				}

			}
			return true;
		}

		@Override
		public boolean keyUp(int keycode) {
			keys.remove(keycode, 0);
			return true;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			if (!touchMap.containsKey(pointer)) {
				touchMap.put(pointer, new TouchData());
			}
			TouchData data = touchMap.get(pointer);
			if (data.button == -1) {
				data.down.set(screenX, screenY);
				data.button = button;
				data.isDragging = false;
			}
			long clickTime = TimeUtils.millis();
			if (clickTime - data.lastClickTime < 200) {
				data.doubleClick = true;
			} else {
				data.doubleClick = false;
			}
			data.lastClickTime = clickTime;
			return true;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if (!intent.isDragging) {
				intent.click.set(screenX, screenY);
			}
			intent.dragStart.setZero();
			intent.dragCurrent.setZero();
			intent.isDragging = false;
			intent.pan = false;
			intent.rotate = false;

			touchMap.get(pointer).reset();

			return true;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			TouchData data = touchMap.get(pointer);
			data.lastDrag.set(screenX, screenY);
			data.isDragging = true;
			return true;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			zoom += GameSettings.CAMERA_ZOOM_STEP * amount;
			return true;
		}
	}

	private class TouchData {
		int dragHistoryCursor = 0;
		Vector2 down = new Vector2();
		Vector2 lastDrag = new Vector2();
		long lastClickTime = 0;
		int button;
		boolean isDragging = false;
		boolean doubleClick = false;

		public TouchData() {
			reset();
		}

		public void reset() {
			dragHistoryCursor = 0;
			down.setZero();
			lastDrag.setZero();
			button = -1;
			isDragging = false;
			doubleClick = false;
		}
	}

}
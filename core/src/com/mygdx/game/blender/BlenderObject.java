package com.mygdx.game.blender;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by user on 7/31/15.
 */
public abstract class BlenderObject {
	public static class BLight extends BlenderObject {
		public Color lamp_color;
		public float lamp_energy;
		public float lamp_distance;
		public float lamp_falloff;
	}

	public static class BModel extends BlenderObject {
		public String model_file_name;
	}

	public static class BCamera extends BlenderObject {
		public float fov;
	}

	public static class BEmpty extends BlenderObject {

	}

	public String type;
	public String name;
	public Vector3 position;
	public Vector3 rotation;
	public Vector3 scale;
	public boolean[] layers;
	public ArrayMap<String, String> custom_properties;
}

/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.mygdx.game.blender;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Class for deserializing Blender objects from json
 *
 *  @author jsjolund
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

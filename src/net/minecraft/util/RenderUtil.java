package net.minecraft.util;

public class RenderUtil {
	public static final int ATLAS_SIZE = 16;
	public static Vector2f textureIndexToUV(int index, Vector2f absUv) {
		return new Vector2f((index % ATLAS_SIZE) / (float)ATLAS_SIZE + absUv.x / ATLAS_SIZE, (index / ATLAS_SIZE / (float)ATLAS_SIZE - absUv.y / ATLAS_SIZE + 1f / ATLAS_SIZE));
	}
}

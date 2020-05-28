package net.minecraft.util;

public class RenderUtil {
	public static Vector2f textureIndexToUV(int index, Vector2f absUv) {
		return new Vector2f((index % 4f) / 4f + absUv.x / 4f, (index / 4 / 4f - absUv.y / 4f + 1f / 4f));
	}
}

package net.minecraft.util;

public class Vector4f {
	public float x = 0, y = 0, z = 0, w = 1;
	public Vector4f() {
		this(0, 0, 0, 0);
	}
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
}

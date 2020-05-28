package net.minecraft.util;

public class Vector3f {
	public float x = 0, y = 0, z = 0;
	public Vector3f() {
		this(0, 0, 0);
	}
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public float length() {
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	public Vector3f normalize() {
		Vector3f normalized = new Vector3f(x, y, z);
		float length = normalized.length();
		normalized.x /= length;
		normalized.y /= length;
		normalized.z /= length;
		return normalized;
	}
}

package net.minecraft.util;

public class Transform {
	public Vector3f position, rotation, scale;
	public Transform() {
		this(new Vector3f(), new Vector3f());
	}
	public Transform(Vector3f position) {
		this(position, new Vector3f());
	}
	public Transform(Vector3f position, Vector3f rotation) {
		this(position, rotation, new Vector3f());
		scale.x = 1; scale.y = 1; scale.z = 1;
	}
	public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
}

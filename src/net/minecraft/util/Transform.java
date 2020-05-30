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
	public Transform copy() {
		Transform newT = new Transform();
		newT.position.x = position.x;
		newT.position.y = position.y;
		newT.position.z = position.z;
		newT.rotation.x = rotation.x;
		newT.rotation.y = rotation.y;
		newT.rotation.z = rotation.z;
		newT.scale.x = scale.x;
		newT.scale.y = scale.y;
		newT.scale.z = scale.z;
		return newT;
		
	}
}

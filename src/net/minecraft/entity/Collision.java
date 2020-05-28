package net.minecraft.entity;

import net.minecraft.Main;
import net.minecraft.level.block.Block;
import net.minecraft.util.Transform;
import net.minecraft.util.Vector3f;

public class Collision {
	public static final float STEP = 0.1f;
	public static void move(Transform transform, Vector3f direction) {
		if(!check(transform, new Vector3f(direction.x, 0, 0))) {
			transform.position.x += direction.x;
		}
		if(!check(transform, new Vector3f(0, direction.y, 0))) {
			transform.position.y += direction.y;
		}
		if(!check(transform, new Vector3f(0, 0, direction.z))) {
			transform.position.z += direction.z;
		}
	}
	public static boolean check(Transform transform, Vector3f direction) {
		boolean collided = false;
		float px = transform.position.x+direction.x;
		float py = transform.position.y+direction.y;
		float pz = transform.position.z+direction.z;
		float step = STEP;
		for(float i = -transform.scale.x/2.0f; i <= transform.scale.x/2.0f; i+=step) {
			for(float j = -transform.scale.y/2.0f; j <= transform.scale.y/2.0f; j+=step) {
				if(Block.BLOCKS[Main.level.getBlock((int)(px+i), (int)(py+j), (int)(pz+transform.scale.z/2.0f))].isSolid()) {
					collided = true;
				}
				if(Block.BLOCKS[Main.level.getBlock((int)(px+i), (int)(py+j), (int)(pz-transform.scale.z/2.0f))].isSolid()) {
					collided = true;
				}
			}
		}
		for(float i = -transform.scale.x/2.0f; i <= transform.scale.x/2.0f; i+=step) {
			for(float j = -transform.scale.z/2.0f; j <= transform.scale.z/2.0f; j+=step) {
				if(Block.BLOCKS[Main.level.getBlock((int)(px+i), (int)(py+transform.scale.y/2.0f), (int)(pz+j))].isSolid()) {
					collided = true;
				}
				if(Block.BLOCKS[Main.level.getBlock((int)(px+i), (int)(py-transform.scale.y/2.0f), (int)(pz+j))].isSolid()) {
					collided = true;
				}
			}
		}
		for(float i = -transform.scale.y/2.0f; i <= transform.scale.y/2.0f; i+=step) {
			for(float j = -transform.scale.z/2.0f; j <= transform.scale.z/2.0f; j+=step) {
				if(Block.BLOCKS[Main.level.getBlock((int)(px+transform.scale.x/2.0f), (int)(py+i), (int)(pz+j))].isSolid()) {
					collided = true;
				}
				if(Block.BLOCKS[Main.level.getBlock((int)(px-transform.scale.x/2.0f), (int)(py+i), (int)(pz+j))].isSolid()) {
					collided = true;
				}
			}
		}
		return collided;
	}
}

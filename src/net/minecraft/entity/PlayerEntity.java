package net.minecraft.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.Main;
import net.minecraft.level.block.Block;
import net.minecraft.util.Vector3f;

public class PlayerEntity extends Entity {
	public PlayerEntity() {
		transform.scale = new Vector3f(0.6f, 1.6f, 0.6f);
	}
	float airTime = 0;
	boolean isGrounded = false;
	boolean jumping = false, jumpRequest = false;
	@Override
	public void tick(float delta) {
		float moveSpeed = 5;
		float turnSpeed = 1;
		float jumpForce = 10;
		
		if(Main.level.generated) {
			if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
				float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * moveSpeed);
				float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * moveSpeed);
				//float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)) * moveSpeed);
				Vector3f direction = new Vector3f(dx * delta, 0, -dz * delta);
				Collision.move(transform, direction);
				if(Collision.check(transform, direction) && isGrounded) {
					jumpRequest = true;
				}
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
				float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * moveSpeed);
				float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * moveSpeed);
				//float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)) * moveSpeed);
				Vector3f direction = new Vector3f(-dx * delta, 0, dz * delta);
				Collision.move(transform, direction);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
				float dx = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * moveSpeed);
				float dz = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * moveSpeed);
				Vector3f direction = new Vector3f(-dx * delta, 0, dz * delta);
				Collision.move(transform, direction);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
				float dx = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * moveSpeed);
				float dz = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * moveSpeed);
				Vector3f direction = new Vector3f(dx * delta, 0, -dz * delta);
				Collision.move(transform, direction);
			}
			if(jumpRequest) {
				transform.position.y += Collision.STEP;
				jumping = true;
				jumpRequest = false;
			}
			if(jumping) {
				Collision.move(transform, new Vector3f(0, jumpForce*delta, 0));
			}
			isGrounded = Collision.check(transform, new Vector3f(0, -Collision.STEP, 0));
			if(!isGrounded) {
				airTime += delta;
				Collision.move(transform, new Vector3f(0, -Main.level.getGravity()*airTime*delta, 0));
			}else {
				airTime = 0;
				jumping = false;
			}
		}
		
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				if(Keyboard.getEventKey() == Keyboard.KEY_E) {
					Mouse.setGrabbed(!Mouse.isGrabbed());
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					Main.exiting = true;
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_SPACE && isGrounded) {
					jumpRequest = true;
				}
			}
		}
		while(Mouse.next()) {
			if(Mouse.getEventButtonState()) {
				if(Mouse.getEventButton() == 0) {
					raycastBreak();
				}else if(Mouse.getEventButton() == 1) {
					raycastPlace();
				}
			}
		}
		if(Mouse.isGrabbed()) {
			transform.rotation.x += Mouse.getDY() * turnSpeed;
			transform.rotation.y -= Mouse.getDX() * turnSpeed;
			if(transform.rotation.x < -89) {
				transform.rotation.x = -89;
			}
			if(transform.rotation.x > 89) {
				transform.rotation.x = 89;
			}
		}
	}
	public void raycastBreak() {
		float x = transform.position.x;
		float y = transform.position.y + Main.level.getPlayerHeight();
		float z = transform.position.z;
		float horiz = (float) (Math.cos(Math.toRadians(transform.rotation.x)));
		float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * horiz);
		float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * horiz);
		float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)));
		float dist = 0, step = 0.5f;
		while(!Block.BLOCKS[Main.level.getBlock((int)x, (int)y, (int)z)].isSolid() && dist < 16) {
			x += dx * step;
			y += dy * step;
			z -= dz * step;
			dist += step;
		}
		if(Block.BLOCKS[Main.level.getBlock((int)x, (int)y, (int)z)].isSolid()) {
			Main.level.setBlock((int)x, (int)y, (int)z, Block.AIR.getId());
		}
	}
	public void raycastPlace() {
		float x = transform.position.x;
		float y = transform.position.y + Main.level.getPlayerHeight();
		float z = transform.position.z;
		float horiz = (float) (Math.cos(Math.toRadians(transform.rotation.x)));
		float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * horiz);
		float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * horiz);
		float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)));
		float dist = 0, step = 0.5f;
		while(!Block.BLOCKS[Main.level.getBlock((int)x, (int)y, (int)z)].isSolid() && dist < 16) {
			x += dx * step;
			y += dy * step;
			z -= dz * step;
			dist += step;
		}
		if(Block.BLOCKS[Main.level.getBlock((int)x, (int)y, (int)z)].isSolid()) {
			x -= dx * step;
			y -= dy * step;
			z += dz * step;
			Main.level.setBlock((int)x, (int)y, (int)z, Block.STONE.getId());
		}
	}
	@Override
	public void draw() {
		
	}
	@Override
	public void load() {
		
	}
	@Override
	public void unload() {
		
	}
}

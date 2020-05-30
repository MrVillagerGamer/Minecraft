package net.minecraft.entity;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.Main;
import net.minecraft.util.Transform;
import net.minecraft.util.Vector3f;

public abstract class CreatureEntity extends Entity {
	protected EntityPart[] parts = new EntityPart[0];
	private float airTime = 0;
	boolean isGrounded = false;
	private boolean jumping = false;
	boolean jumpRequest = false;
	float jumpForce, moveSpeed, turnSpeed;
	int moveDir = 0;
	boolean blocked = false;
	private int health;
	private int maxHealth;
	@Override
	public void load() {
		for(EntityPart part : parts) {
			part.load();
		}
	}
	@Override
	public void unload() {
		for(EntityPart part : parts) {
			part.unload();
		}
	}
	@Override
	public void draw() {
		for(EntityPart part : parts) {
			part.draw();
		}
	}
	public boolean check(Transform transform, float angle) {
		float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)));
		float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)));
		//float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)) * moveSpeed);
		Vector3f direction = new Vector3f(dx * 0.5f, 0, -dz * 0.5f);
		return Collision.check(transform, direction);
	}
	public boolean check(float angle) {
		float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)));
		float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)));
		//float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)) * moveSpeed);
		Vector3f direction = new Vector3f(dx * 0.5f, 0, -dz * 0.5f);
		return Collision.check(transform, direction);
	}
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	public void setHealth(int health) {
		if(health <= 0) {
			dead = true;
			return;
		}
		health = Math.min(health, maxHealth);
		this.health = health;
	}
	public int getHealth() {
		return health;
	}
	public int getMaxHealth() {
		return maxHealth;
	}
	@Override
	public void tick(float delta) {
		super.tick(delta);
		if(Main.level.generated && active) {
			if(moveDir == 1) {
				float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * moveSpeed);
				float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * moveSpeed);
				//float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)) * moveSpeed);
				Vector3f direction = new Vector3f(dx * delta, 0, -dz * delta);
				Collision.move(transform, direction);
				Transform transform2 = transform.copy();
				transform2.position.y += 1.1f;
				if(!Collision.check(transform2, direction)) {
					blocked = false;
				}
				if(Collision.check(transform, direction) && !Collision.check(transform2, direction) && isGrounded) {
					jumpRequest = true;
				}else if(Collision.check(transform, direction) && Collision.check(transform2, direction) && isGrounded) {
					blocked = true;
				}
			}
			if(moveDir == -1) {
				float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * moveSpeed);
				float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * moveSpeed);
				//float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)) * moveSpeed);
				Vector3f direction = new Vector3f(-dx * delta, 0, dz * delta);
				Collision.move(transform, direction);
			}
			/*
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
			*/
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
		
	}
}

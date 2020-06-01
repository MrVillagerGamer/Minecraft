package net.minecraft.entity;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.Constants;
import net.minecraft.Main;
import net.minecraft.client.Client;
import net.minecraft.level.block.Block;
import net.minecraft.level.item.ItemStack;
import net.minecraft.util.Vector3f;

public class PlayerEntity extends CreatureEntity {
	public PlayerEntity() {
		transform.scale = new Vector3f(0.6f, 1.6f, 0.6f);
		moveSpeed = 5;
		turnSpeed = 1;
		jumpForce = 10;
		setMaxHealth(20);
		setHealth(getMaxHealth());
	}
	@Override
	public void setHealth(int health) {
		super.setHealth(health);
		System.out.println("Health: " + health);
	}
	private int sel = 0;
	private float breakTime = 0;
	@Override
	public void tick(float delta) {
		ticking = true;
		super.tick(delta);
		moveDir = 0;
		if(Main.level.input.forward) {
			moveDir = 1;
		}else if(Main.level.input.backward) {
			moveDir = -1;
		}
		if(Main.level.input.create && Main.level.lastUi == null) {
			breakTime += delta;
			raycastPlace();
		}
		if(Main.level.input.destroy && Main.level.lastUi == null) {
			breakTime += delta;
			raycastBreak();
		}
		if(Main.level.input.inventory) {
			Mouse.setGrabbed(!Mouse.isGrabbed());
			Main.level.ui = Mouse.isGrabbed()?null:Main.level.inventoryUi;
		}
		if(Main.level.input.escape) {
			Main.exiting = true;
		}
		if(Main.level.input.jump && isGrounded) {
			jumpRequest = true;
		}
		if(Mouse.isGrabbed()) {
			transform.rotation.x += Main.level.input.turny * turnSpeed;
			transform.rotation.y -= Main.level.input.turnx * turnSpeed;
			if(transform.rotation.x < -89) {
				transform.rotation.x = -89;
			}
			if(transform.rotation.x > 89) {
				transform.rotation.x = 89;
			}
		}
		Client.setPlayer(transform);
		ticking = false;
	}
	public void raycastBreak() {
		float x = transform.position.x;
		float y = transform.position.y + Main.level.getPlayerHeight();
		float z = transform.position.z;
		float horiz = (float) (Math.cos(Math.toRadians(transform.rotation.x)));
		float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * horiz);
		float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * horiz);
		float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)));
		float dist = 0, step = 0.1f;
		while(!Block.BLOCKS[Main.level.getBlock((int)x, (int)y, (int)z)].isSolid() && dist < 16) {
			x += dx * step;
			y += dy * step;
			z -= dz * step;
			dist += step;
		}
		if(Block.BLOCKS[Main.level.getBlock((int)x, (int)y, (int)z)].isSolid()) {
			int blockId = Main.level.getBlock((int)x, (int)y, (int)z);
			if(Main.level.gamemode == Constants.GAMEMODE_CREATIVE || breakTime > 0.5f) {
				if(Main.level.gamemode == Constants.GAMEMODE_SURVIVAL) Main.level.addItemToInventory(blockId, 1);
				Main.level.setBlock((int)x, (int)y, (int)z, Block.AIR.getId());
				breakTime = 0;
			}
		}
	}
	public void raycastPlace() {
		if(Main.level.inventory[Main.level.inventoryUi.sel].count == 0 && Main.level.gamemode == Constants.GAMEMODE_SURVIVAL) return;
		float x = transform.position.x;
		float y = transform.position.y + Main.level.getPlayerHeight();
		float z = transform.position.z;
		float horiz = (float) (Math.cos(Math.toRadians(transform.rotation.x)));
		float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * horiz);
		float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * horiz);
		float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)));
		float dist = 0, step = 0.1f;
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
			
			Main.level.setBlock((int)x, (int)y, (int)z, (char)Main.level.inventory[Main.level.inventoryUi.sel].item);
				
			if(Main.level.gamemode == Constants.GAMEMODE_SURVIVAL) Main.level.removeItemFromInventory(Main.level.inventory[Main.level.inventoryUi.sel].item, 1);
			//Main.level.setBlock((int)x, (int)y, (int)z, Block.WATER.getId());
			//Main.level.test.transform.position.x = x; 
			//Main.level.test.transform.position.y = y+3;
			//Main.level.test.transform.position.z = z;
			//ZombieEntity zombie = new ZombieEntity();
			//zombie.transform.position = new Vector3f((int)x+0.5f, (int)y+2.6f, (int)z+0.5f);
			//Main.level.addEntity(zombie);
		}
	}
	@Override
	public void draw() {
		
	}
	@Override
	public void load() {
		Main.level.blockDispUI.id = 0;
		Main.level.blockDispUI.count = 0;
		Main.level.ui = null;
	}
	@Override
	public void unload() {
		
	}
}

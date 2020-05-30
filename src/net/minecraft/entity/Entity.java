package net.minecraft.entity;

import java.util.ArrayList;

import net.minecraft.Main;
import net.minecraft.util.IDrawable;
import net.minecraft.util.ITickable;
import net.minecraft.util.Transform;
import net.minecraft.util.Vector2f;

public abstract class Entity implements ITickable, IDrawable {
	public static final int ENTITY_DISTANCE = 4*16;
	// Never reassign transform because doing so would 
	// break the AI system and maybe some other things!
	public Transform transform = new Transform();
	public boolean active = false;
	public boolean dead = false;
	@Override
	public void tick(float delta) {
		if(Main.level.localPlayer == null) {
			return;
		}
		Vector2f diff = new Vector2f();
		diff.x = transform.position.x - Main.level.localPlayer.transform.position.x;
		diff.y = transform.position.z - Main.level.localPlayer.transform.position.z;
		float len = (float) Math.sqrt(diff.x*diff.x+diff.y*diff.y);
		if(len > ENTITY_DISTANCE) {
			active = false;
		}else {
			active = true;
		}
	}
}

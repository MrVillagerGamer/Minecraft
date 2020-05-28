package net.minecraft.entity;

import java.util.ArrayList;

import net.minecraft.util.IDrawable;
import net.minecraft.util.ITickable;
import net.minecraft.util.Transform;

public abstract class Entity implements ITickable, IDrawable {
	protected ArrayList<Entity> children = new ArrayList<>();
	public Transform transform = new Transform();
}

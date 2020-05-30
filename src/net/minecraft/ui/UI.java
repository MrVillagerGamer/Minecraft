package net.minecraft.ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.Main;
import net.minecraft.util.IDrawable;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vector2f;

public abstract class UI implements ITickable, IDrawable{
	protected UI parent;
	protected UI[] children = new UI[0];
	protected float x, y, w, h;
	protected boolean clicked;
	public boolean inBounds(float mx, float my) {
		return mx>x&&mx<x+w&&my>y&&my<y+h;
	}
	public Vector2f getAbsolutePosition() {
		UI cur = this;
		Vector2f p = new Vector2f();
		while(cur != null) {
			p.x += cur.x;
			p.y += cur.y;
			cur = cur.parent;
		}
		return p;
	}
	@Override
	public void tick(float delta) {
		for(UI ui : children) {
			ui.tick(delta);
		}
		int wd = (Display.getWidth()-Display.getHeight())/2;
		float mx = Mouse.getX()-wd;
		float my = Mouse.getY();
		mx = (mx / Display.getHeight() * 2 - 1);
		my = (my / Display.getHeight() * 2 - 1);
		if(Main.level.input.destroy && inBounds(mx, my)) {
			clicked = true;
			onClick(x, y);
		}
	}
	@Override
	public void draw() {
		for(UI ui : children) {
			ui.draw();
		}
	}
	@Override
	public void load() {
		for(UI ui : children) {
			ui.load();
		}
	}
	@Override
	public void unload() {
		for(UI ui : children) {
			ui.unload();
		}
	}
	public abstract void onClick(float x2, float y2);
}

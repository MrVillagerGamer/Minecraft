package net.minecraft.ui;

import org.lwjgl.opengl.GL11;

import net.minecraft.level.Chunk;
import net.minecraft.util.Vector3f;
import net.minecraft.util.Vector4f;

public abstract class ButtonUI extends UI{
	private Vector4f color;
	private int dlid = -1;
	public ButtonUI(Vector4f color) {
		this.color = color;
	}
	@Override
	public void load() {
		super.load();
		if(dlid != -1) {
			GL11.glDeleteLists(dlid, 1);
		}
		dlid = GL11.glGenLists(1);
		GL11.glNewList(dlid, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES);
		for(int p = 0; p < 6; p++) {
			float x = Chunk.verts[Chunk.tris[0][p]].x;
			float y = Chunk.verts[Chunk.tris[0][p]].y;
			x *= w;
			y *= h;
			x += this.x;
			y += this.y;
			GL11.glColor4f(color.x, color.y, color.z, color.w);
			GL11.glVertex2f(x, y);
		}
		GL11.glEnd();
		GL11.glEndList();
	}
	@Override
	public void unload() {
		super.unload();
		if(dlid != -1) {
			GL11.glDeleteLists(dlid, 1);
			dlid = -1;
		}
	}
	@Override
	public void draw() {
		super.draw();
		if(dlid != -1) {
			GL11.glCallList(dlid);
		}
	}
}

package net.minecraft.ui;

import org.lwjgl.opengl.GL11;

import net.minecraft.level.Chunk;
import net.minecraft.util.Vector3f;
import net.minecraft.util.Vector4f;

public abstract class PanelUI extends UI {
	private Vector4f color;
	private int dlid = -1;
	public PanelUI(Vector3f color, Vector4f dims) {
		this.color = new Vector4f(color.x, color.y, color.z, 0.67f);
		this.x = dims.x;
		this.y = dims.y;
		this.w = dims.z;
		this.h = dims.w;
	}
	public PanelUI(Vector4f dims) {
		this.color = new Vector4f(0, 0, 0, 0.67f);
		this.x = dims.x;
		this.y = dims.y;
		this.w = dims.z;
		this.h = dims.w;
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
			GL11.glVertex3f(x, y, 0);
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
		if(dlid != -1) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glCallList(dlid);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
		super.draw();
	}
}

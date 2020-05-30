package net.minecraft.ui;

import org.lwjgl.opengl.GL11;

import net.minecraft.level.Chunk;
import net.minecraft.util.RenderUtil;
import net.minecraft.util.Vector2f;
import net.minecraft.util.Vector3f;

public abstract class ImageUI extends UI{
	private int dlid = -1, tidx = 0;
	public ImageUI(int tidx, float x, float y, float w, float h) {
		this.tidx = tidx;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
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
			float x = Chunk.verts[Chunk.tris[0][p]].x * w;
			float y = Chunk.verts[Chunk.tris[0][p]].y * h;
			Vector2f uv = RenderUtil.textureIndexToUV(tidx, Chunk.uvs[p]);
			GL11.glColor3f(1, 1, 1);
			GL11.glTexCoord2f(uv.x, uv.y);
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

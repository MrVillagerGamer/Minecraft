package net.minecraft.ui;

import org.lwjgl.opengl.GL11;

import net.minecraft.level.Chunk;
import net.minecraft.level.block.Block;
import net.minecraft.level.item.Item;
import net.minecraft.util.RenderUtil;
import net.minecraft.util.Vector2f;

public abstract class IconUI extends UI{
	private int dlid = -1;
	public int id = 0;
	public IconUI(int id, float x, float y, float w, float h) {
		this.id = id;
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
		for(int f = 0; f < 6; f++) {
			int tidx = Item.ITEMS[id].getTextureIndex(f);
			for(int p = 0; p < 6; p++) {
				float x = Chunk.verts[Chunk.tris[f][p]].x * w;
				float y = Chunk.verts[Chunk.tris[f][p]].y * h;
				float z = Chunk.verts[Chunk.tris[f][p]].z * (w+h)/2f;
				Vector2f uv = RenderUtil.textureIndexToUV(tidx, Chunk.uvs[p]);
				GL11.glColor3f(1, 1, 1);
				if(Chunk.verts[Chunk.tris[f][p]].y < 0.5f) {
					GL11.glColor3f(0.5f, 0.5f, 0.5f);
				}
				GL11.glTexCoord2f(uv.x, uv.y);
				GL11.glVertex3f(x, y, z);
			}
			if(Block.BLOCKS[id] == null) {
				return;
			}
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
			GL11.glPushMatrix();
			GL11.glTranslatef(getAbsolutePosition().x, getAbsolutePosition().y, 0);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPushMatrix();
			if(Block.BLOCKS[id] != null) {
				GL11.glRotatef(45, 0, 1, 0);
				GL11.glRotatef(22, 1, 0, 1);
			}
			GL11.glCallList(dlid);
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
	}
}

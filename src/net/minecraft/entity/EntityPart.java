package net.minecraft.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.level.Chunk;
import net.minecraft.level.block.Block;
import net.minecraft.util.RenderUtil;
import net.minecraft.util.Transform;
import net.minecraft.util.Vector2f;
import net.minecraft.util.Vector3f;
import net.minecraft.util.Vector4f;

public class EntityPart {
	private Transform transform;
	private Vector3f origin;
	private Vector4f[] uvst;
	int dlid = -1;
	public EntityPart(Transform transform, Vector3f origin, Vector4f[] uvst) {
		this.transform = transform;
		this.origin = origin;
		this.uvst = uvst;
	}
	public EntityPart(Transform transform, Vector3f origin, Vector4f uvst) {
		this(transform, origin, new Vector4f[] {uvst, uvst, uvst, uvst, uvst, uvst});
	}
	public void unload() {
		if(dlid != -1) {
			GL11.glDeleteLists(dlid, 1);
		}
	}
	public void load() {
		if(dlid != -1) {
			GL11.glDeleteLists(dlid, 1);
		}
		dlid = GL11.glGenLists(1);
		GL11.glNewList(dlid, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES);
		for(int f = 0; f < 6; f++) {
			float nx = Chunk.norms[f].x;
			float ny = Chunk.norms[f].y;
			float nz = Chunk.norms[f].z;
			for(int p = 0; p < 6; p++) {
				float px = -0.5f + Chunk.verts[Chunk.tris[f][p]].x;
				float py = -0.5f + Chunk.verts[Chunk.tris[f][p]].y;
				float pz = -0.5f + Chunk.verts[Chunk.tris[f][p]].z;
				GL11.glColor3f(1, 1, 1);
				GL11.glNormal3f(nx, ny, nz);
				Vector4f uvst = this.uvst[f];
				Vector2f uv = Chunk.uvs[p];
				float u = uvst.x;
				float v = uvst.y;
				if(uv.x > 0.5f) {
					u = uvst.z;
				}
				if(uv.y > 0.5f) {
					v = uvst.w;
				}
				GL11.glTexCoord2f(u, v);
				GL11.glVertex3f(px, py, pz);
			}
		}
		GL11.glEnd();
		GL11.glEndList();
	}
	public void draw() {
		if(dlid != -1) {
			GL11.glPushMatrix();
			GL11.glTranslatef(origin.x, origin.y, origin.z);
			GL11.glRotatef(transform.rotation.x, 1, 0, 0);
			GL11.glRotatef(transform.rotation.y, 0, 1, 0);
			GL11.glRotatef(transform.rotation.z, 0, 0, 1);
			GL11.glTranslatef(-origin.x, -origin.y, -origin.z);
			GL11.glTranslatef(transform.position.x, transform.position.y, transform.position.z);
			GL11.glScalef(transform.scale.x, transform.scale.y, transform.scale.z);
			GL11.glCallList(dlid);
			GL11.glPopMatrix();
		}
	}
}

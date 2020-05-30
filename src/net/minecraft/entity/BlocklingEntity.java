package net.minecraft.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.Main;
import net.minecraft.level.Chunk;
import net.minecraft.level.block.Block;
import net.minecraft.util.RenderUtil;
import net.minecraft.util.Vector2f;
import net.minecraft.util.Vector3f;

public class BlocklingEntity extends CreatureEntity{
	int dlid = -1;
	EntityAI ai;
	int blockId;
	public BlocklingEntity(int blockId) {
		this.blockId = blockId;
		transform.scale = new Vector3f(0.6f, 0.6f, 0.6f);
		moveSpeed = 2;
		turnSpeed = 90;
		jumpForce = 5;
		ai = new BasicEntityAI(this, Main.level.localPlayer.transform);
		setMaxHealth(10);
		setHealth(getMaxHealth());
	}
	float damageTime = 0;
	@Override
	public void tick(float delta) {
		if(active) {
			if(Main.level.localPlayer != null && Main.level.localPlayer instanceof CreatureEntity) {
				Vector3f dist = new Vector3f();
				dist.x = Main.level.localPlayer.transform.position.x;
				dist.y = Main.level.localPlayer.transform.position.y;
				dist.z = Main.level.localPlayer.transform.position.z;
				dist.x -= transform.position.x;
				dist.y -= transform.position.y;
				dist.z -= transform.position.z;
				if(dist.length() < 2) {
					if(damageTime >= 1f) {
						((CreatureEntity)Main.level.localPlayer).setHealth(((CreatureEntity)Main.level.localPlayer).getHealth()-1);
						damageTime -= 1f;
					}
					damageTime += delta;
				}else {
					damageTime = 0;
				}
			}
			ai.tick(delta);
		}
		super.tick(delta);
	}
	@Override
	public void draw() {
		GL11.glCallList(dlid);
	}
	@Override
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
				int t = Block.BLOCKS[blockId].getTextureIndex(f);
				Vector2f tex = RenderUtil.textureIndexToUV(t, Chunk.uvs[p]);
				float u = tex.x;
				float v = tex.y;
				GL11.glTexCoord2f(u, v);
				GL11.glVertex3f(px, py, pz);
			}
		}
		GL11.glEnd();
		GL11.glEndList();
	}
	@Override
	public void unload() {
		if(dlid != -1) {
			GL11.glDeleteLists(dlid, 1);
		}
	}
}

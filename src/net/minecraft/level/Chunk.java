package net.minecraft.level;

import org.lwjgl.opengl.GL11;

import net.minecraft.Main;
import net.minecraft.level.block.Block;
import net.minecraft.util.IDrawable;
import net.minecraft.util.RenderUtil;
import net.minecraft.util.Vector2f;
import net.minecraft.util.Vector3f;

public class Chunk implements IDrawable {
	public static final int SIZE = 16, HEIGHT = 128;
	public char[][][] block = new char[SIZE][HEIGHT][SIZE];
	public byte[][][] light = new byte[SIZE][HEIGHT][SIZE];
	public byte[][][] lightProp = new byte[SIZE][HEIGHT][SIZE];
	public byte[][][] water = new byte[SIZE][HEIGHT][SIZE];
	public boolean[][][] waterLock = new boolean[SIZE][HEIGHT][SIZE];
	public int x, z;
	public int dlid = -1;
	public static Vector3f[] verts = new Vector3f[] {
        new Vector3f(0.0f, 0.0f, 0.0f),
        new Vector3f(1.0f, 0.0f, 0.0f),
        new Vector3f(1.0f, 1.0f, 0.0f),
        new Vector3f(0.0f, 1.0f, 0.0f),
        new Vector3f(0.0f, 0.0f, 1.0f),
        new Vector3f(1.0f, 0.0f, 1.0f),
        new Vector3f(1.0f, 1.0f, 1.0f),
        new Vector3f(0.0f, 1.0f, 1.0f),
    };
	public static Vector3f[] norms = new Vector3f[] {
		new Vector3f(0, 0, -1),
        new Vector3f(0, 0, 1),
        new Vector3f(0, 1, 0),
        new Vector3f(0, -1, 0),
        new Vector3f(-1, 0, 0),
        new Vector3f(1, 0, 0)
	};
    public static int[][] tris = new int[][] {

        // Back, Front, Top, Bottom, Left, Right
		// 0 1 2 2 1 3
		{0, 3, 1, 1, 3, 2}, // Back Face
		{5, 6, 4, 4, 6, 7}, // Front Face
		{3, 7, 2, 2, 7, 6}, // Top Face
		{1, 5, 0, 0, 5, 4}, // Bottom Face
		{4, 7, 0, 0, 7, 3}, // Left Face
		{1, 2, 5, 5, 2, 6} // Right Face
	};

    public static Vector2f[] uvs = new Vector2f[] {
        new Vector2f(0.0f, 0.0f),
        new Vector2f(0.0f, 1.0f),
        new Vector2f(1.0f, 0.0f),
        new Vector2f(1.0f, 0.0f),
        new Vector2f(0.0f, 1.0f),
        new Vector2f(1.0f, 1.0f)
    };
    boolean spreadWater = false;
    public void spreadWater() {
    	if(!spreadWater) {
    		return;
    	}
    	spreadWater = false;
    	for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				for(int k = 0; k < SIZE; k++) {
					if(water[i][j][k] != 0 && !waterLock[i][j][k]) {
						waterLock[i][j][k] = true;
						Main.level.spreadWater(i+x, j, k+z);
						spreadWater = true;
					}
				}
			}
    	}
    }
    // Called once per chunk and surrounding chunks
	public void propagateLight() {
		// Diffuse the light per-axis
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				for(int k = 0; k < SIZE; k++) {
					int level = Main.level.getLightSourceLevel(i+x, j, k+z);
					if(level <= Main.level.getSunLightLevel()) {
						continue;
					}
					level -= Main.level.getSunLightLevel();
					for(int f = -level; f <= level; f++) {
						byte level1 = (byte)(Math.max(level+Main.level.getSunLightLevel()-Math.abs(f), Main.level.getLightLevel(i+x, j, k+f+z)));
						Main.level.setLightLevel(i+x, j, k+f+z, level1);
					}
				}
			}
		}
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				for(int k = 0; k < SIZE; k++) {
					int level = Main.level.getLightLevel(i+x, j, k+z);
					if(level <= Main.level.getSunLightLevel()) {
						continue;
					}
					level -= Main.level.getSunLightLevel();
					for(int f = -level; f <= level; f++) {
						byte level1 = (byte)(Math.max(level+Main.level.getSunLightLevel()-Math.abs(f), Main.level.getLightLevel(i+f+x, j, k+z)));
						Main.level.setLightLevel(i+f+x, j, k+z, level1);
					}
				}
			}
		}
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				for(int k = 0; k < SIZE; k++) {
					int level = Main.level.getLightLevel(i+x, j, k+z);
					if(level <= Main.level.getSunLightLevel()) {
						continue;
					}
					level -= Main.level.getSunLightLevel();
					for(int f = -level; f <= level; f++) {
						byte level1 = (byte)(Math.max(level+Main.level.getSunLightLevel()-Math.abs(f), Main.level.getLightLevel(i+x, j+f, k+z)));
						Main.level.setLightLevel(i+x, j+f, k+z, level1);
					}
				}
			}
		}
	}
	// Called once per chunk
	public void propagateSunlight() {
		for(int x = 0; x < SIZE; x++) {
			for(int z = 0; z < SIZE; z++) {
				int d = 15-Main.level.getSunLightLevel();
				for(int y = HEIGHT-1; y >= 0; y--) {
					if(Block.BLOCKS[block[x][y][z]].isSolid()
							|| Block.BLOCKS[block[x][y][z]].isFluid()) {
						d+=15;
					}
					d = Math.min(d, 15);
					lightProp[x][y][z] = (byte)(15-d);
				}
			}
		}
		// Diffuse the light per-axis
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				for(int k = 0; k < SIZE; k++) {
					int level = Main.level.getLightLevel(i+x, j, k+z);
					if(level <= Main.level.getSunLightLevel()) {
						continue;
					}
					level -= Main.level.getSunLightLevel();
					for(int f = -level; f <= level; f++) {
						byte level1 = (byte)(Math.max(level+Main.level.getSunLightLevel()-Math.abs(f), Main.level.getLightLevel(i+x, j, k+f+z)));
						Main.level.setLightLevel(i+x, j, k+f+z, level1);
					}
				}
			}
		}
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				for(int k = 0; k < SIZE; k++) {
					int level = Main.level.getLightLevel(i+x, j, k+z);
					if(level <= Main.level.getSunLightLevel()) {
						continue;
					}
					level -= Main.level.getSunLightLevel();
					for(int f = -level; f <= level; f++) {
						byte level1 = (byte)(Math.max(level+Main.level.getSunLightLevel()-Math.abs(f), Main.level.getLightLevel(i+f+x, j, k+z)));
						Main.level.setLightLevel(i+f+x, j, k+z, level1);
					}
				}
			}
		}
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				for(int k = 0; k < SIZE; k++) {
					int level = Main.level.getLightLevel(i+x, j, k+z);
					if(level <= Main.level.getSunLightLevel()) {
						continue;
					}
					level -= Main.level.getSunLightLevel();
					for(int f = -level; f <= level; f++) {
						byte level1 = (byte)(Math.max(level+Main.level.getSunLightLevel()-Math.abs(f), Main.level.getLightLevel(i+x, j+f, k+z)));
						Main.level.setLightLevel(i+x, j+f, k+z, level1);
					}
				}
			}
		}
	}
	@Override
	public void draw() {
		if(dlid != -1) {
			GL11.glCallList(dlid);
		}
	}
	private float getWaterLevel(int x, int y, int z) {
		float val = Main.level.getWaterLevel(x, y, z);
		return val;
	}
	@Override
	public void load() {
		if(dlid != -1) {
			GL11.glDeleteLists(dlid, 1);
		}
		dlid = GL11.glGenLists(1);
		GL11.glNewList(dlid, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES);
		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < HEIGHT; y++) {
				for(int z = 0; z < SIZE; z++) {
					if(Block.BLOCKS[block[x][y][z]].isSolid()) {
						for(int f = 0; f < 6; f++) {
							float nx = norms[f].x;
							float ny = norms[f].y;
							float nz = norms[f].z;
							if(!Block.BLOCKS[Main.level.getBlock(x+this.x+(int)nx, y+(int)ny, z+this.z+(int)nz)].isSolid() || Block.BLOCKS[block[x][y][z]].isTransparent() || Block.BLOCKS[Main.level.getBlock(x+this.x+(int)nx, y+(int)ny, z+this.z+(int)nz)].isTransparent()) {
								for(int p = 0; p < 6; p++) {
									float px = x + verts[tris[f][p]].x;
									float py = y + verts[tris[f][p]].y;
									float pz = z + verts[tris[f][p]].z;
									float b = Main.level.getLightLevel((int)(px+this.x+0.5f), (int)(py-0.5f), (int)(pz+this.z+0.5f))/16.0f;
									b += Main.level.getLightLevel((int)(px+this.x+0.5f), (int)(py+0.5f), (int)(pz+this.z+0.5f))/16.0f;
									b += Main.level.getLightLevel((int)(px+this.x-0.5f), (int)(py-0.5f), (int)(pz+this.z+0.5f))/16.0f;
									b += Main.level.getLightLevel((int)(px+this.x-0.5f), (int)(py+0.5f), (int)(pz+this.z+0.5f))/16.0f;
									b += Main.level.getLightLevel((int)(px+this.x+0.5f), (int)(py-0.5f), (int)(pz+this.z-0.5f))/16.0f;
									b += Main.level.getLightLevel((int)(px+this.x+0.5f), (int)(py+0.5f), (int)(pz+this.z-0.5f))/16.0f;
									b += Main.level.getLightLevel((int)(px+this.x-0.5f), (int)(py-0.5f), (int)(pz+this.z-0.5f))/16.0f;
									b += Main.level.getLightLevel((int)(px+this.x-0.5f), (int)(py+0.5f), (int)(pz+this.z-0.5f))/16.0f;
									b /= 7.0f;
									b += 1f / 8.0f;
									GL11.glColor3f(b, b, b);
									GL11.glNormal3f(nx, ny, nz);
									int t = Block.BLOCKS[block[x][y][z]].getTextureIndex(f);
									Vector2f tex = RenderUtil.textureIndexToUV(t, uvs[p]);
									float u = tex.x;
									float v = tex.y;
									GL11.glTexCoord2f(u, v);
									GL11.glVertex3f(px, py, pz);
								}
							}
						}
					}else if(water[x][y][z] != 0) {
						for(int f = 0; f < 6; f++) {
							float nx = norms[f].x;
							float ny = norms[f].y;
							float nz = norms[f].z;
							if(Main.level.getWaterLevel(x+this.x+(int)nx, y+(int)ny, z+this.z+(int)nz) == 0 &&
									!Block.BLOCKS[Main.level.getBlock(x+this.x+(int)nx, y+(int)ny, z+this.z+(int)nz)].isSolid()) {
								for(int p = 0; p < 6; p++) {
									float px = x + verts[tris[f][p]].x;
									float py = y + verts[tris[f][p]].y;
									float pz = z + verts[tris[f][p]].z;
									float b1 = getWaterLevel((int)px+this.x-1, (int)py-1, (int)pz+this.z-1)/30.0f;
									float b2 = getWaterLevel((int)px+this.x, (int)py-1, (int)pz+this.z-1)/30.0f;
									float b3 = getWaterLevel((int)px+this.x-1, (int)py-1, (int)pz+this.z)/30.0f;
									float b4 = getWaterLevel((int)px+this.x, (int)py-1, (int)pz+this.z)/30.0f;
									
									float b = 0;
									if(b1 > b) b = b1;
									if(b2 > b) b = b2;
									if(b3 > b) b = b3;
									if(b4 > b) b = b4;
									
									py = y + verts[tris[f][p]].y * b;
									GL11.glColor3f(1, 1, 1);
									GL11.glNormal3f(nx, ny, nz);
									int t = Block.BLOCKS[Block.WATER.getId()].getTextureIndex(f);
									Vector2f tex = RenderUtil.textureIndexToUV(t, uvs[p]);
									float u = tex.x;
									float v = tex.y;
									GL11.glTexCoord2f(u, v);
									GL11.glVertex3f(px, py, pz);
								}
							}
						}
					}
				}
			}
		}
		GL11.glEnd();
		GL11.glEndList();
	}
	@Override
	public void unload() {
		if(dlid != -1) {
			GL11.glDeleteLists(dlid, 1);
			dlid = -1;
		}
	}
}

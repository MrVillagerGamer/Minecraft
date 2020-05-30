package net.minecraft.level;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.flowpowered.noise.module.source.Perlin;

import net.minecraft.entity.BlocklingEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PlayerEntity;
import net.minecraft.level.block.Block;
import net.minecraft.ui.HotbarUI;
import net.minecraft.ui.IconUI;
import net.minecraft.ui.InventoryUI;
import net.minecraft.ui.UI;
import net.minecraft.util.IDrawable;
import net.minecraft.util.ITickable;
import net.minecraft.util.Transform;
import net.minecraft.util.Vector2f;
import net.minecraft.util.Vector3f;

public class Level implements ITickable, IDrawable {
	private ArrayList<Entity> entities = new ArrayList<>();
	public Entity localPlayer = null;
	public Entity test = null;
	public boolean generated = false;
	private Chunk[][] chunks = new Chunk[256][256];
	private ArrayList<Chunk> loadedChunks = new ArrayList<>();
	private ArrayList<Chunk> chunksToLoad = new ArrayList<>();
	private ArrayList<Chunk> chunkUpdates = new ArrayList<>();
	private Texture tex;
	public UI ui = null;
	public UI lastUi = ui;
	public IconUI blockDispUI;
	public InputState input = new InputState();
	public InventoryUI inventoryUi;
	public void addEntity(Entity e) {
		e.load();
		entities.add(e);
	}
	public void setWaterLevel(int x, int y, int z, byte l) {
		if(y < 0 || y >= Chunk.HEIGHT) {
			return;
		}
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if(cx < 0 || cz < 0 || cx >= chunks.length || cz >= chunks[0].length) {
			return;
		}
		int bx = x % Chunk.SIZE;
		int bz = z % Chunk.SIZE;
		if(bx < 0 || bz < 0) {
			return;
		}
		if(chunks[cx][cz] == null) {
			return;
		}
		chunks[cx][cz].water[bx][y][bz] = l;
		addToList(chunkUpdates, chunks[cx][cz]);
		int x2 = cx+1>=chunks.length?cx:cx+1;
		int z2 = cz+1>=chunks[0].length?cz:cz+1;
		int x1 = cx-1<0?cx:cx-1;
		int z1 = cz-1<0?cz:cz-1;
		if(bx == 15 && bz == 15) {
			addToList(chunkUpdates, chunks[x2][z2]);
		}
		if(bx == 15 && bz == 0) {
			addToList(chunkUpdates, chunks[x2][z1]);
		}
		if(bx == 0 && bz == 15) {
			addToList(chunkUpdates, chunks[x1][z2]);
		}
		if(bx == 0 && bz == 0) {
			addToList(chunkUpdates, chunks[x1][z1]);
		}
		if(bx == 0) {
			addToList(chunkUpdates, chunks[x1][cz]);
		}
		if(bx == 15) {
			addToList(chunkUpdates, chunks[x2][cz]);
		}
		if(bz == 0) {
			addToList(chunkUpdates, chunks[cz][z1]);
		}
		if(bz == 15) {
			addToList(chunkUpdates, chunks[cz][z2]);
		}
	}
	public void setLightLevel(int x, int y, int z, byte l) {
		if(y < 0 || y >= Chunk.HEIGHT) {
			return;
		}
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if(cx < 0 || cz < 0 || cx >= chunks.length || cz >= chunks[0].length) {
			return;
		}
		int bx = x % Chunk.SIZE;
		int bz = z % Chunk.SIZE;
		if(bx < 0 || bz < 0) {
			return;
		}
		if(chunks[cx][cz] == null) {
			return;
		}
		chunks[cx][cz].lightProp[bx][y][bz] = l;
	}
	public byte getWaterLevel(int x, int y, int z) {
		if(y < 0 || y >= Chunk.HEIGHT) {
			return 0;
		}
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if(cx < 0 || cz < 0 || cx >= chunks.length || cz >= chunks[0].length) {
			
			return 0;
		}
		int bx = x % Chunk.SIZE;
		int bz = z % Chunk.SIZE;
		if(bx < 0 || bz < 0) {
			return 0;
		}
		if(chunks[cx][cz] == null) {
			return 0;
		}
		return chunks[cx][cz].water[bx][y][bz];
	}
	public byte getLightLevel(int x, int y, int z) {
		if(y < 0 || y >= Chunk.HEIGHT) {
			return 0;
		}
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if(cx < 0 || cz < 0 || cx >= chunks.length || cz >= chunks[0].length) {
			
			return 0;
		}
		int bx = x % Chunk.SIZE;
		int bz = z % Chunk.SIZE;
		if(bx < 0 || bz < 0) {
			return 0;
		}
		if(chunks[cx][cz] == null) {
			return 0;
		}
		return chunks[cx][cz].lightProp[bx][y][bz];
	}
	public byte getLightSourceLevel(int x, int y, int z) {
		if(y < 0 || y >= Chunk.HEIGHT) {
			return 0;
		}
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if(cx < 0 || cz < 0 || cx >= chunks.length || cz >= chunks[0].length) {
			return 0;
		}
		int bx = x % Chunk.SIZE;
		int bz = z % Chunk.SIZE;
		if(bx < 0 || bz < 0) {
			return 0;
		}
		if(chunks[cx][cz] == null) {
			return 0;
		}
		return chunks[cx][cz].light[bx][y][bz];
	}
	public void setLocalPlayer(Entity entity) {
		localPlayer = entity;
	}
	public char getBlock(int x, int y, int z) {
		if(y < 0 || y >= Chunk.HEIGHT) {
			return 0;
		}
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if(cx < 0 || cz < 0 || cx >= chunks.length || cz >= chunks[0].length) {
			return 0;
		}
		int bx = x % Chunk.SIZE;
		int bz = z % Chunk.SIZE;
		if(bx < 0 || bz < 0) {
			return 0;
		}
		if(chunks[cx][cz] == null) {
			return 0;
		}
		return chunks[cx][cz].block[bx][y][bz];
	}
	
	public byte getSunLightLevel() {
		return 15;
	}
	public float getGravity() {
		return 10f;
	}
	private int skydlid = -1;
	public void unloadSky() {
		if(skydlid != -1) {
			GL11.glDeleteLists(skydlid, 1);
		}
	}
	public void loadSky() {
		if(skydlid != -1) {
			GL11.glDeleteLists(skydlid, 1);
		}
		skydlid = GL11.glGenLists(1);
		GL11.glNewList(skydlid, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES);
		float d = (float) (10000/2f);
		for(int f = 0; f < 6; f++) {
			for(int p = 0; p < 6; p++) {
				Vector3f col = new Vector3f();
				Vector3f v = new Vector3f();
				v.x = Chunk.verts[Chunk.tris[f][p]].x * d * 2 - d;
				v.y = Chunk.verts[Chunk.tris[f][p]].y * d * 2 - d;
				v.z = Chunk.verts[Chunk.tris[f][p]].z * d * 2 - d;
				GL11.glColor3f(col.x, col.y, col.z);
				GL11.glVertex3f(v.x, v.y, v.z);
			}
		}
		GL11.glEnd();
		GL11.glEndList();
	}
	public void draw() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		Vector3f pos = localPlayer.transform.position;
		if(getWaterLevel((int)(pos.x), (int)(pos.y+getPlayerHeight()+0.5f), (int)(pos.z)) > 0) {
			FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
			fogColor.put(underwaterColor.x);
			fogColor.put(underwaterColor.y);
			fogColor.put(underwaterColor.z);
			fogColor.put(1);
			fogColor.flip();
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.2f);
		}else {
			FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
			fogColor.put(skyColor1.x);
			fogColor.put(skyColor1.y);
			fogColor.put(skyColor1.z);
			fogColor.put(1);
			fogColor.flip();
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.0133f);
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0f, Display.getWidth()/(float)Display.getHeight(), 0.1f, 10000.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		if(localPlayer != null) {
			float px = localPlayer.transform.position.x;
			float py = localPlayer.transform.position.y + getPlayerHeight();
			float pz = localPlayer.transform.position.z;
			Transform transform = localPlayer.transform;
			float horiz = (float) (Math.cos(Math.toRadians(transform.rotation.x)));
			float dx = (float) (Math.cos(Math.toRadians(transform.rotation.y)) * horiz);
			float dz = (float) (Math.sin(Math.toRadians(transform.rotation.y)) * horiz);
			float dy = (float) (Math.sin(Math.toRadians(transform.rotation.x)));
			GLU.gluLookAt(0, 0, 0, dx, dy, dz, 0, 1, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glCallList(skydlid);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glLoadIdentity();
			GLU.gluLookAt(px, py, pz, px+dx, py+dy, pz-dz, 0, 1, 0);
		}
		for(Entity entity : entities) {
			if(!entity.active) {
				continue;
			}
			GL11.glPushMatrix();
			Vector3f position = entity.transform.position;
			GL11.glTranslatef(position.x, position.y, position.z);
			GL11.glRotatef(entity.transform.rotation.x, 1, 0, 0);
			GL11.glRotatef(entity.transform.rotation.y, 0, 1, 0);
			GL11.glRotatef(entity.transform.rotation.z, 0, 0, 1);
			entity.draw();
			GL11.glPopMatrix();
		}
		for(Chunk chunk : loadedChunks) {
			GL11.glPushMatrix();
			GL11.glTranslatef(chunk.x, 0, chunk.z);
			chunk.draw();
			GL11.glPopMatrix();
		}
		float aspect = Display.getWidth()/(float)Display.getHeight();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(-aspect, aspect, -1, 1, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		getUINonNull(ui).draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	Vector3f skyColor1 = new Vector3f(0.7f, 0.85f, 1.0f);
	Vector3f underwaterColor = new Vector3f(0.2f, 0.35f, 0.5f);
	@Override
	public void load() {
		inventoryUi = new InventoryUI();
		blockDispUI = new IconUI(1, -0.9f, -0.9f, 0.2f, 0.2f) {
			@Override
			public void onClick(float x, float y) {
				
			}
		};
		try {
			tex = TextureLoader.getTexture("PNG", new FileInputStream("res/terrain.png"));
			tex.bind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		} catch (IOException e) {
			e.printStackTrace();
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0f);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
		FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
		fogColor.put(skyColor1.x);
		fogColor.put(skyColor1.y);
		fogColor.put(skyColor1.z);
		fogColor.put(1);
		fogColor.flip();
		GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
		GL11.glFogf(GL11.GL_FOG_DENSITY, 0.01f);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		loadSky();
		getUINonNull(ui).load();
		localPlayer = new PlayerEntity();
		localPlayer.transform.position = new Vector3f(chunks.length*Chunk.SIZE/2, 64, chunks[0].length*Chunk.SIZE/2);
		localPlayer.transform.rotation = new Vector3f(-30, -45, 0);
		entities.add(localPlayer);
		for(Entity entity : entities) {
			entity.load();
		}
	}
	// If ui is null, return default ui
	// otherwise return the ui parameter
	public UI getUINonNull(UI ui) {
		if(ui == null) {
			return blockDispUI;
		}
		return ui;
	}
	@Override
	public void unload() {
		getUINonNull(ui).load();
		unloadSky();
		for(Entity entity : entities) {
			entity.unload();
		}
		entities.clear();
		for(Chunk chunk : loadedChunks) {
			chunks[chunk.x/Chunk.SIZE][chunk.z/Chunk.SIZE] = null;
			chunk.unload();
		}
		loadedChunks.clear();
		tex.release();
		generated = false;
		
	}
	public int getRenderDistance() {
		return 12;
	}
	public int getSeaLevel() {
		return 40;
	}
	@Override
	public void tick(float delta) {
		input.scroll = Mouse.getDWheel()/100;
		input.forward = Keyboard.isKeyDown(Keyboard.KEY_W);
		input.backward = Keyboard.isKeyDown(Keyboard.KEY_S);
		input.inventory = false; input.jump = false;
		input.escape = false; input.create = false;
		input.destroy = false;
		input.turnx = Mouse.getDX();
		input.turny = Mouse.getDY();
		
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				input.inventory = Keyboard.getEventKey() == Keyboard.KEY_E;
				input.jump = Keyboard.getEventKey() == Keyboard.KEY_SPACE;
				input.escape = Keyboard.getEventKey() == Keyboard.KEY_ESCAPE;
			}
		}

		while(Mouse.next()) {
			if(Mouse.getEventButtonState()) {
				input.create = Mouse.getEventButton() == 1;
				input.destroy = Mouse.getEventButton() == 0;
			}
		}
		if(lastUi != ui) {
			getUINonNull(lastUi).unload();
			getUINonNull(ui).load();
		}
		lastUi = ui;
		getUINonNull(ui).tick(delta);
		Mouse.setGrabbed(ui==null);
		for(int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			entity.tick(delta);
			if(entity.dead) {
				if(entity != localPlayer) {
					entities.remove(entity);
				}else {
					unload();
					load();
				}
			}
		}
		int camX = 0, camZ = 0;
		if(localPlayer != null) {
			camX = (int) (localPlayer.transform.position.x / Chunk.SIZE);
			camZ = (int) (localPlayer.transform.position.z / Chunk.SIZE);
		}
		if(chunksToLoad.size() == 0) {
			boolean done = false;
			Perlin mod = new Perlin();
			for(int x = camX-getRenderDistance(); x < camX+getRenderDistance(); x++) {
				for(int z = camZ-getRenderDistance(); z < camZ+getRenderDistance(); z++) {
					if(done || x < 0 || z < 0 || x >= chunks.length || z >= chunks.length || chunks[x][z] != null) continue;
					Chunk chunk = new Chunk();
					for(int i = 0; i < Chunk.SIZE; i++) {
						for(int k = 0; k < Chunk.SIZE; k++) {
							int h = (int)(mod.getValue((x*Chunk.SIZE+i)/256f, (z*Chunk.SIZE+k)/256f, 0)*32f+48f);
							for(int j = 0; j < h; j++) {
								if(j == h-1) {
									chunk.block[i][j][k] = Block.GRASS.getId();
									if(j <= getSeaLevel()) {
										chunk.block[i][j][k] = Block.DIRT.getId();
									}
									int th = 4;
									if(new Random().nextInt()%100==0 && i >= 3 && k >= 3 && i < Chunk.SIZE-3 && k < Chunk.SIZE-3 && j+th+1 < Chunk.HEIGHT && j > getSeaLevel()) {
										
										for(int l = -2; l <= 2; l++) {
											for(int m = -2; m <= 2; m++) {
												chunk.block[i+l][h+th-2][k+m] = Block.LEAVES.getId();
												chunk.block[i+l][h+th-1][k+m] = Block.LEAVES.getId();
											}
										}
										for(int l = -1; l <= 1; l++) {
											for(int m = -1; m <= 1; m++) {
												chunk.block[i+l][h+th][k+m] = Block.LEAVES.getId();
											}
											chunk.block[i+l][h+th+1][k] = Block.LEAVES.getId();
											chunk.block[i][h+th+1][k+l] = Block.LEAVES.getId();
										}
										for(int l = 0; l < 4; l++) {
											chunk.block[i][h+l][k] = Block.WOOD.getId();
										}
									}
								}else if(j >= h-3) {
									chunk.block[i][j][k] = Block.DIRT.getId();
								}else {
									chunk.block[i][j][k] = Block.STONE.getId();
								}
							}
							for(int j = 0; j < Chunk.HEIGHT; j++) {
								if(j < getSeaLevel() && !Block.BLOCKS[chunk.block[i][j][k]].isSolid()) {
									chunk.water[i][j][k] = 15;
									chunk.waterLock[i][j][k] = true;
								}
								chunk.light[i][j][k] = Block.BLOCKS[chunk.block[i][j][k]].getLightLevel();
								
							}
						}
					}
					chunk.x = x * Chunk.SIZE;
					chunk.z = z * Chunk.SIZE;
					chunks[x][z] = chunk;
					chunk.propagateSunlight();
					chunk.propagateLight();
					int x1 = x-1<0?x:x-1;
					int z1 = z-1<0?z:z-1;
					int x2 = x+1>=chunks.length?x:x+1;
					int z2 = z+1>=chunks[0].length?z:z+1;
					if(chunks[x][z1] != null) {
						//chunks[x][z1].propagateSunlight();
						chunks[x][z1].propagateLight();
						loadLater(chunks[x][z1]);
					}
					if(chunks[x][z2] != null) {
						//chunks[x][z2].propagateSunlight();
						chunks[x][z2].propagateLight();
						loadLater(chunks[x][z2]);
					}
					if(chunks[x1][z] != null) {
						//chunks[x1][z].propagateSunlight();
						chunks[x1][z].propagateLight();
						loadLater(chunks[x1][z]);
					}
					if(chunks[x2][z] != null) {
						//chunks[x2][z].propagateSunlight();
						chunks[x2][z].propagateLight();
						loadLater(chunks[x2][z]);
					}
					if(chunks[x1][z1] != null) {
						//chunks[x1][z1].propagateSunlight();
						chunks[x1][z1].propagateLight();
						loadLater(chunks[x1][z1]);
					}
					if(chunks[x1][z2] != null) {
						//chunks[x1][z2].propagateSunlight();
						chunks[x1][z2].propagateLight();
						loadLater(chunks[x1][z2]);
					}
					if(chunks[x2][z1] != null) {
						//chunks[x2][z1].propagateSunlight();
						chunks[x2][z1].propagateLight();
						loadLater(chunks[x2][z1]);
					}
					if(chunks[x2][z2] != null) {
						//chunks[x2][z2].propagateSunlight();
						chunks[x2][z2].propagateLight();
						loadLater(chunks[x2][z2]);
					}
					loadLater(chunk);
					done = true;
				}
			}
		}
		if(chunksToLoad.size() > 0) {
			Chunk chunk = chunksToLoad.remove(0);
			chunk.load();
			if(!loadedChunks.contains(chunk)) {
				loadedChunks.add(chunk);
			}
		}else {
			generated = true;
		}
		
	}
	public float getPlayerHeight() {
		if(localPlayer != null) {
			return localPlayer.transform.scale.y/2;
		}
		return 0;
	}
	public void spreadAllWater() {
		for(Chunk chunk : loadedChunks) {
			if(chunk.spreadWater) {
				chunk.spreadWater();
			}
		}
		for(Chunk chunk : chunkUpdates) {
			chunk.propagateLight();
			chunk.load();
		}
		chunkUpdates.clear();
	}
	public void spreadWater(int x, int y, int z) {
		byte wl = getWaterLevel(x, y, z);
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				if(i == 0 && j == 0) {
					continue;
				}
				if(i != 0 && j != 0) {
					continue;
				}
				if(getWaterLevel(x+i, y, z+j) != 0) {
					continue;
				}
				byte wl2 = getWaterLevel(x+i, y, z+j);
				boolean t = !Block.BLOCKS[getBlock(x+i, y, z+j)].isSolid();
				boolean b = !Block.BLOCKS[getBlock(x+i, y-1, z+j)].isSolid();
				
				if(wl2 == 0 && wl > 4 && t && !b) {
					setWaterLevel(x+i, y, z+j, (byte)(wl-4));
					int cx = (x+i)/Chunk.SIZE;
					int cz = (z+j)/Chunk.SIZE;
					if(cx != x/Chunk.SIZE || cz!=z/Chunk.SIZE) {
						if(chunks[cx][cz] != null) {
							chunks[cx][cz].spreadWater = true;
						}
					}
				}else if(wl2 == 0 && wl > 2 && t && b) {
					setWaterLevel(x+i, y-1, z+j, (byte)Math.min(15, wl+3));
					int cx = (x+i)/Chunk.SIZE;
					int cz = (z+j)/Chunk.SIZE;
					if(cx != x/Chunk.SIZE || cz!=z/Chunk.SIZE) {
						if(chunks[cx][cz] != null) {
							chunks[cx][cz].spreadWater = true;
						}
					}
				}
				
			}
		}
	}
	public void setBlock(int x, int y, int z, char id) {
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		Chunk chunk = chunks[cx][cz];
		int bx = x % Chunk.SIZE;
		int bz = z % Chunk.SIZE;
		int by = y % Chunk.HEIGHT;
		chunk.block[bx][by][bz] = Block.BLOCKS[id].isFluid()?0:id;
		chunk.light[bx][by][bz] = Block.BLOCKS[id].getLightLevel();
		chunk.water[bx][by][bz] = (byte) (Block.BLOCKS[id].isFluid()?15:0);
		if(Block.BLOCKS[id].isFluid()) {
			chunk.spreadWater = true;
		}
		chunk.propagateSunlight();
		chunk.propagateLight();
		loadLater(chunk);
		int x1 = cx-1<0?cx:cx-1;
		int z1 = cz-1<0?cz:cz-1;
		int x2 = cx+1>=chunks.length?cx:cx+1;
		int z2 = cz+1>=chunks[0].length?cz:cz+1;
		if(chunks[cx][z1] != null) {
			//chunks[x][z1].propagateSunlight();
			chunks[cx][z1].propagateLight();
			loadLater(chunks[cx][z1]);
		}
		if(chunks[cx][z2] != null) {
			//chunks[x][z2].propagateSunlight();
			chunks[cx][z2].propagateLight();
			loadLater(chunks[cx][z2]);
		}
		if(chunks[x1][cz] != null) {
			//chunks[x1][z].propagateSunlight();
			chunks[x1][cz].propagateLight();
			loadLater(chunks[x1][cz]);
		}
		if(chunks[x2][cz] != null) {
			//chunks[x2][z].propagateSunlight();
			chunks[x2][cz].propagateLight();
			loadLater(chunks[x2][cz]);
		}
		if(chunks[x1][z1] != null) {
			//chunks[x1][z1].propagateSunlight();
			chunks[x1][z1].propagateLight();
			loadLater(chunks[x1][z1]);
		}
		if(chunks[x1][z2] != null) {
			//chunks[x1][z2].propagateSunlight();
			chunks[x1][z2].propagateLight();
			loadLater(chunks[x1][z2]);
		}
		if(chunks[x2][z1] != null) {
			//chunks[x2][z1].propagateSunlight();
			chunks[x2][z1].propagateLight();
			loadLater(chunks[x2][z1]);
		}
		if(chunks[x2][z2] != null) {
			//chunks[x2][z2].propagateSunlight();
			chunks[x2][z2].propagateLight();
			loadLater(chunks[x2][z2]);
		}
	}
	public void addToList(ArrayList<Chunk> chunks, Chunk chunk) {
		if(!chunks.contains(chunk) && chunk != null) {
			chunks.add(chunk);
		}
	}
	public void loadLater(Chunk chunk) {
		if(!chunksToLoad.contains(chunk)) {
			chunksToLoad.add(chunk);
		}
	}
	public void removeEntity(Entity entity) {
		if(entities.contains(entity)) {
			entity.unload();
			entities.remove(entity);
		}
	}
}

package net.minecraft;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import net.minecraft.level.Level;
import net.minecraft.level.block.Block;
import net.minecraft.level.item.Item;

public class Main {
	public static Level level;
	public static boolean exiting = false;
	public static void main(String[] args) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.setResizable(true);
		Display.setTitle("Minecraft");
		Display.create();
		Block.registerBlocks();
		Item.registerItems();
		level = new Level();
		level.load();
		long lastTime = System.nanoTime();
		int frameCount = 0;
		float frameTime = 0;
		while(!Display.isCloseRequested()) {
			long time = System.nanoTime();
			float delta = (float) ((time-lastTime)/1000000000D);
			lastTime = time;
			frameCount++;
			float oldFrameTime = frameTime;
			frameTime += delta;
			if((int)(oldFrameTime*20) != (int)(frameTime*20)) {
				level.spreadAllWater();
			}
			if(frameTime >= 1f) {
				Display.setTitle("Minecraft - " + frameCount + " FPS");
				frameTime = 0;
				frameCount = 0;
			}
			level.tick(delta);
			level.draw();
			Display.update();
			if(exiting) {
				break;
			}
		}
		level.unload();
		Display.destroy();
	}
}

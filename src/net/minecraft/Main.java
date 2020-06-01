package net.minecraft;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import net.minecraft.level.Level;
import net.minecraft.level.block.Block;
import net.minecraft.level.item.Item;
import net.minecraft.client.Client;
import net.minecraft.server.Server;

public class Main implements Runnable {
	public static Level level;
	public static boolean exiting = false;
	public static boolean running = false;
	private static InetAddress ip;
	public static void main(String[] args) throws LWJGLException, IOException, InterruptedException {
		Block.registerBlocks();
		Item.registerItems();
		running = true;
		Thread serverThread = null;
		if(args.length == 0 || (args.length > 1 && args[0].equals("host"))) {
			if(args.length > 1) {
				ip = Inet4Address.getByName(args[1]);
			}else {
				ip = Inet4Address.getLocalHost();
			}
			serverThread = new Thread(new Main());
			serverThread.start();
		}else if(args.length > 1 && args[0].equals("join")) {
			ip = InetAddress.getByName(args[1]);
		}
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.setResizable(true);
		Display.setTitle("Minecraft");
		Display.create();
		level = new Level();
		level.load();
		Client.begin(ip, 5050);
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
		Client.running = false;
		Server.running = false;
		if(serverThread != null) {
			Server.end();
			serverThread.join();
		}
		level.unload();
		Display.destroy();
	}
	@Override
	public void run() {
		try {
			Server.begin(ip, 5050);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}

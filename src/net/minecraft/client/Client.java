package net.minecraft.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PlayerEntity;
import net.minecraft.entity.ZombieEntity;
import net.minecraft.level.Chunk;
import net.minecraft.level.block.Block;
import net.minecraft.net.packet.GenChunkPacket;
import net.minecraft.net.packet.GenPlayerPacket;
import net.minecraft.net.packet.ReadyPacket;
import net.minecraft.net.packet.ReturnPacket;
import net.minecraft.net.packet.SetBlockPacket;
import net.minecraft.net.packet.SetPlayerPacket;
import net.minecraft.util.Transform;
import net.minecraft.util.Vector3f;

public class Client implements Runnable {
	private static ArrayList<Object> packetQueue = new ArrayList<>();
	private static ArrayList<Socket> sockets = new ArrayList<>();
	public static void begin(InetAddress ip, int port) throws IOException {
		Socket s = new Socket(ip, port);
		new Thread(new Client(s)).start();
	}
	private Socket s;
	private int cid = 0;
	boolean generated = false;
	public Client(Socket s) {
		this.s = s;
		packetQueue = new ArrayList<>();
		sockets.add(s);
		
		GenPlayerPacket genPlayer = new GenPlayerPacket();
		genPlayer.x = 128*16;
		genPlayer.z = 128*16;
		genPlayer.y = 64;
		packetQueue.add(genPlayer);
	}
	public static boolean running = true;
	private static SetPlayerPacket setPlayer = new SetPlayerPacket();
	public static void setBlock(char cx, char cz, byte bx, byte by, byte bz, char id) {
		SetBlockPacket packet = new SetBlockPacket();
		packet.cx = cx;
		packet.cz = cz;
		packet.bx = bx;
		packet.bz = bz;
		packet.by = by;
		packet.id = id;
		packetQueue.add(packet);
	}
	public static void setPlayer(Transform t) {
		setPlayer.x = t.position.x;
		setPlayer.y = t.position.y;
		setPlayer.z = t.position.z;
		setPlayer.rx = 0;
		setPlayer.ry = t.rotation.y;
		setPlayer.rz = 0;
		if(!packetQueue.contains(setPlayer)) {
			packetQueue.add(setPlayer);
		}
	}
	private void handleOutputPacket() throws IOException {
		//packetQueue.get(cid).add(o);
		if(packetQueue.size() > 0) {
			Object o = packetQueue.remove(0);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(o);
		}else {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(new ReadyPacket());
		}
	}
	private HashMap<Integer, Entity> players = new HashMap<>();
	public void handleInputPacket(Object packet) throws IOException {
		if(packet instanceof GenChunkPacket) {
			if(((GenChunkPacket)packet).x > 128*16 && ((GenChunkPacket)packet).z > 128*16) {
				generated = true;
			}
			Main.level.addChunk(((GenChunkPacket)packet).x, ((GenChunkPacket)packet).z, ((GenChunkPacket)packet).blocks);
		}
		if(packet instanceof SetBlockPacket) {
			SetBlockPacket setb = ((SetBlockPacket)packet);
			int x = (int)setb.cx * Chunk.SIZE + (int)setb.bx;
			int z = (int)setb.cz * Chunk.SIZE + (int)setb.bz;
			int y = (int)setb.by;
			Main.level.setBlockAsync(x, y, z, setb.id);
		}
		if(packet instanceof SetPlayerPacket) {
			SetPlayerPacket setp = ((SetPlayerPacket)packet);
			float x = setp.x;
			float z = setp.z;
			float y = setp.y;
			if(Main.level.localPlayer != null && setp.id == -1) {
				while(Main.level.localPlayer.ticking);
				Main.level.localPlayer.transform.position.x = x;
				Main.level.localPlayer.transform.position.y = y;
				Main.level.localPlayer.transform.position.z = z;
			}else if(setp.id != -1) {
				//System.out.println(setp.id + ": " + setp.x + ", " + setp.y + ", " + setp.z);
				if(players.containsKey(setp.id)) {
					while(players.get(setp.id).ticking);
					players.get(setp.id).transform.position = new Vector3f(setp.x, setp.y, setp.z);
					players.get(setp.id).transform.rotation = new Vector3f(setp.rx, setp.ry, setp.rz);
				}else {
					System.err.println("SetPlayerPacket received before GenPlayerPacket!");
				}
			}
		}
		if(packet instanceof GenPlayerPacket) {
			GenPlayerPacket setp = ((GenPlayerPacket)packet);
			ZombieEntity zombie = new ZombieEntity();
			zombie.transform.position = new Vector3f(setp.x, setp.y, setp.z);
			zombie.transform.rotation = new Vector3f(setp.rx, setp.ry, setp.rz);
			players.put(setp.id, zombie);
			Main.level.addEntity(zombie);
		}
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(new ReadyPacket());
	}
	@Override
	public void run() {
		while(running) {
			try {
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				Object data = in.readObject();
				if(data instanceof ReadyPacket) {
					handleOutputPacket();
				}else {
					handleInputPacket(data);
				}
			} catch (SocketException e) {
				Main.exiting = true;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

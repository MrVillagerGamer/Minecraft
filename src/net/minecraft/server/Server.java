package net.minecraft.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import net.minecraft.Main;
import net.minecraft.level.Chunk;
import net.minecraft.level.block.Block;
import net.minecraft.net.packet.GenChunkPacket;
import net.minecraft.net.packet.GenPlayerPacket;
import net.minecraft.net.packet.ReadyPacket;
import net.minecraft.net.packet.SetBlockPacket;
import net.minecraft.net.packet.SetPlayerPacket;

public class Server implements Runnable {
	public static final String COMMAND_GAMEMODE = "gamemode";
	public static final String COMMAND_TIME = "time";
	public static final String COMMAND_WEATHER = "weather";
	public static final int CID_SRV = Integer.MIN_VALUE;
	
	private static ArrayList<ArrayList<Object>> packetQueue = new ArrayList<>();
	private static ArrayList<Socket> sockets = new ArrayList<>();
	private static ArrayList<ServerPlayer> connectedPlayers = new ArrayList<>();
	private static ArrayList<ArrayList<ServerPlayer>> sentPlayers = new ArrayList<>();
	private static ServerSocket socket;
	public static void begin(InetAddress ip, int port) throws IOException {
		socket = new ServerSocket(port, 0, ip);
		while(running) {
			try {
				Socket s = socket.accept();
				new Thread(new Server(s)).start();
			} catch (IOException e) {
				
			}
		}
	}
	public static void end() {
		try {
			socket.close();
		} catch (IOException e) {
			
		}
	}
	public static boolean entityUpdateInProgress() {
		for(ArrayList<Object> list : packetQueue) {
			for(Object packet : list) {
				if(packet instanceof GenPlayerPacket) {
					return true;
				}
				if(packet instanceof SetPlayerPacket) {
					return true;
				}
			}
		}
		return false;
	}
	private Socket s;
	private ServerPlayer connectedPlayer;
	private int cid = 0;
	private static ServerChunk[][] chunks = new ServerChunk[256][256];
	private boolean[][] sentChunks = new boolean[256][256];
	public Server(Socket s) {
		this.s = s;
		cid = packetQueue.size();
		packetQueue.add(new ArrayList<>());
		sentPlayers.add(new ArrayList<>());
		sockets.add(s);
	}
	public void setCommand(String cmd) {
		
	}
	public void setPlayers() {
		if(connectedPlayer == null) return;
		for(int i = 0; i < connectedPlayers.size(); i++) {
			if(connectedPlayers.get(i).id == connectedPlayer.id) continue;
			SetPlayerPacket setPlayer = new SetPlayerPacket();
			setPlayer.x = connectedPlayer.x;
			setPlayer.y = connectedPlayer.y;
			setPlayer.z = connectedPlayer.z;
			setPlayer.rx = connectedPlayer.rx;
			setPlayer.ry = connectedPlayer.ry;
			setPlayer.rz = connectedPlayer.rz;
			setPlayer.id = connectedPlayer.id;
			packetQueue.get(connectedPlayers.get(i).id).add(setPlayer);
		}
	}
	public void genPlayers() {
		if(connectedPlayer == null) return;
		for(int i = 0; i < connectedPlayers.size(); i++) {
			if(connectedPlayers.get(i).id == connectedPlayer.id) continue;
			if(sentPlayers.get(connectedPlayers.get(i).id).contains(connectedPlayer)) continue;
			GenPlayerPacket genPlayer = new GenPlayerPacket();
			genPlayer.x = connectedPlayer.x;
			genPlayer.y = connectedPlayer.y;
			genPlayer.z = connectedPlayer.z;
			genPlayer.rx = connectedPlayer.rx;
			genPlayer.ry = connectedPlayer.ry;
			genPlayer.rz = connectedPlayer.rz;
			genPlayer.id = connectedPlayer.id;
			packetQueue.get(connectedPlayers.get(i).id).add(genPlayer);
			sentPlayers.get(connectedPlayers.get(i).id).add(connectedPlayer);
		}
	}
	private void handleOutputPacket() throws IOException {
		//packetQueue.get(cid).add(o);
		if(packetQueue.get(cid).size() > 0) {
			Object o = packetQueue.get(cid).remove(0);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(o);
		}else {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(new ReadyPacket());
		}
	}
	public void handleInputPacket(Object packet) throws IOException {
		if(packet instanceof GenPlayerPacket) {
			connectedPlayer = new ServerPlayer();
			connectedPlayer.x = ((GenPlayerPacket)packet).x;
			connectedPlayer.y = ((GenPlayerPacket)packet).y;
			connectedPlayer.z = ((GenPlayerPacket)packet).z;
			connectedPlayer.id = cid;
			connectedPlayers.add(connectedPlayer);
			genPlayers();
			for(int x = (int)connectedPlayer.x/Chunk.SIZE-Main.level.getRenderDistance(); x <= (int)connectedPlayer.x/Chunk.SIZE+Main.level.getRenderDistance(); x++) {
				for(int z = (int)connectedPlayer.z/Chunk.SIZE-Main.level.getRenderDistance(); z <= (int)connectedPlayer.z/Chunk.SIZE+Main.level.getRenderDistance(); z++) {
					GenChunkPacket packet2 = new GenChunkPacket();
					packet2.x = (char) x;
					packet2.z = (char) z;
					packet2.blocks = new char[Chunk.SIZE][Chunk.HEIGHT][Chunk.SIZE];
					ServerChunk serverChunk = chunks[x][z];
					if(serverChunk == null) {
						serverChunk = new ServerChunk();
						serverChunk.blocks = ChunkGenerator.generate(x, z);
						chunks[x][z] = serverChunk;
					}
					for(int i = 0; i < Chunk.SIZE; i++) {
						for(int j = 0; j < Chunk.HEIGHT; j++) {
							for(int k = 0; k < Chunk.SIZE; k++) {
								packet2.blocks[i][j][k] = serverChunk.blocks[i][j][k];
							}
						}
					}
					packetQueue.get(cid).add(packet2);
					sentChunks[x][z] = true;
				}
			}
			SetPlayerPacket setPlayer = new SetPlayerPacket();
			setPlayer.x = connectedPlayer.x;
			setPlayer.y = connectedPlayer.y;
			setPlayer.z = connectedPlayer.z;
			setPlayer.id = -1;
			packetQueue.get(cid).add(setPlayer);
			setPlayers();
		}else if(packet instanceof SetPlayerPacket) {
			connectedPlayer.x = ((SetPlayerPacket)packet).x;
			connectedPlayer.y = ((SetPlayerPacket)packet).y;
			connectedPlayer.z = ((SetPlayerPacket)packet).z;
			connectedPlayer.rx = ((SetPlayerPacket)packet).rx;
			connectedPlayer.ry = ((SetPlayerPacket)packet).ry;
			connectedPlayer.rz = ((SetPlayerPacket)packet).rz;
			setPlayers();
		}else if(packet instanceof SetBlockPacket) {
			int cx = ((SetBlockPacket)packet).cx;
			int cz = ((SetBlockPacket)packet).cz;
			if(chunks[cx][cz] != null) {
				int bx = ((SetBlockPacket)packet).bx;
				int by = ((SetBlockPacket)packet).by;
				int bz = ((SetBlockPacket)packet).bz;
				chunks[cx][cz].blocks[bx][by][bz] = ((SetBlockPacket)packet).id;
			}
			
			for(int i = 0; i < packetQueue.size(); i++) {
				if(i == cid) continue;
				packetQueue.get(i).add((SetBlockPacket)packet);
			}
		}
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(new ReadyPacket());
	}
	public static boolean running = true;
	@Override
	public void run() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(new ReadyPacket());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while(running) {
			genPlayers();
			if(connectedPlayer != null) {
				for(int x = (int)connectedPlayer.x/Chunk.SIZE-Main.level.getRenderDistance(); x <= (int)connectedPlayer.x/Chunk.SIZE+Main.level.getRenderDistance(); x++) {
					for(int z = (int)connectedPlayer.z/Chunk.SIZE-Main.level.getRenderDistance(); z <= (int)connectedPlayer.z/Chunk.SIZE+Main.level.getRenderDistance(); z++) {
						if(x < 0 || x >= chunks.length || z < 0 || z >= chunks.length) continue;
						if(!sentChunks[x][z]) {
							if(chunks[x][z] == null) {
								ServerChunk chunk = new ServerChunk();
								chunk.blocks = ChunkGenerator.generate(x, z);
								chunks[x][z] = chunk;
							}
							ServerChunk chunk = chunks[x][z];
							GenChunkPacket packet2 = new GenChunkPacket();
							packet2.x = (char)x;
							packet2.z = (char)z;
							packet2.blocks = chunk.blocks;
							packetQueue.get(cid).add(packet2);
							sentChunks[x][z] = true;
						}
					}
				}
			}
			try {
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				Object data = in.readObject();
				if(data instanceof ReadyPacket) {
					handleOutputPacket();
				}else {
					handleInputPacket(data);
				}
				
			} catch (SocketException e) {
				return;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

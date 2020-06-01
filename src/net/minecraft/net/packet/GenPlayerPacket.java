package net.minecraft.net.packet;

import java.io.Serializable;

// Sent from the client to the server to generate a player.

@SuppressWarnings("serial")
public class GenPlayerPacket implements Serializable{
	public float x;
	public float y;
	public float z;
	public float rx;
	public float ry;
	public float rz;
	public int id;
}

package net.minecraft.net.packet;

import java.io.Serializable;

// Updates the player's data

@SuppressWarnings("serial")
public class SetPlayerPacket implements Serializable{
	public float x, y, z;
	public float rx;
	public float ry;
	public float rz;
	public int id;
}

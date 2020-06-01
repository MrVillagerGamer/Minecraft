package net.minecraft.net.packet;

import java.io.Serializable;

// Tells a client to update a chunk or tells the server to tell other clients.
//   cx, cz = The position of the chunk in chunks
//   bx, by, bz = The block's position relative to the bottom-left-front corner of the chunk

@SuppressWarnings("serial")
public class SetBlockPacket implements Serializable{
	public char cx, cz;
	public byte bx, by, bz;
	public char id;
}

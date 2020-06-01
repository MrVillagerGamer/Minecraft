package net.minecraft.net.packet;

import java.io.Serializable;

// Gets a block from the server
//   cx, cz = The position of the chunk in chunks
//   bx, by, bz = The block's position relative to the bottom-left-front corner of the chunk

@SuppressWarnings("serial")
public class GetBlockPacket implements Serializable{
	public char cx, cz;
	public byte bx, by, bz;
}

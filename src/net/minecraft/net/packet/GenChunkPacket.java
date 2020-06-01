package net.minecraft.net.packet;

import java.io.Serializable;

// Tells a client to create and begin rendering a chunk
//   x, z = The chunk's position (In chunks)
//   blocks = The blocks in the chunk

@SuppressWarnings("serial")
public class GenChunkPacket implements Serializable {
	public char x;
	public char z;
	public char[][][] blocks;
}

package net.minecraft.net.packet;

import java.io.Serializable;

// Holds the returned data from a server

@SuppressWarnings("serial")
public class ReturnPacket implements Serializable{
	public Object returned;
}

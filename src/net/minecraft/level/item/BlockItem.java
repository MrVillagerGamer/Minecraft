package net.minecraft.level.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.level.block.Block;

public class BlockItem extends Item {
	private char blockId;
	public BlockItem(char blockId) {
		this.blockId = blockId;
	}
	@Override
	public int getTextureIndex(int face) {
		return Block.BLOCKS[blockId].getTextureIndex(face);
	}

	@Override
	public int getAttackDamage() {
		return 0;
	}

	@Override
	public Block[] getUsefulOn() {
		return new Block[0];
	}
	
}

package net.minecraft.level.item;

import java.util.List;

import net.minecraft.level.block.Block;

public abstract class Item {
	public static final Item[] ITEMS = new Item[256];
	public static void registerItem(int id, Item item) {
		item.setId((char)id);
		ITEMS[id] = item;
	}
	public static void registerItems() {
		registerItem(Block.AIR.getId(), new BlockItem(Block.AIR.getId()));
		registerItem(Block.STONE.getId(), new BlockItem(Block.STONE.getId()));
		registerItem(Block.GRASS.getId(), new BlockItem(Block.GRASS.getId()));
		registerItem(Block.DIRT.getId(), new BlockItem(Block.DIRT.getId()));
		registerItem(Block.WATER.getId(), new BlockItem(Block.WATER.getId()));
		registerItem(Block.WOOD.getId(), new BlockItem(Block.WOOD.getId()));
		registerItem(Block.LEAVES.getId(), new BlockItem(Block.LEAVES.getId()));
	}
	private char id;
	public abstract int getTextureIndex(int face);
	public abstract int getAttackDamage();
	public abstract Block[] getUsefulOn();
	private void setId(char id) {
		this.id = id;
	}
	public char getId() {
		return id;
	}
}

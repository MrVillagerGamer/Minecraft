package net.minecraft.level.block;

public abstract class Block {
	public static final Block[] BLOCKS = new Block[256];
	public static final Block AIR = new AirBlock();
	public static final Block STONE = new StoneBlock();
	public static final Block GRASS = new GrassBlock();
	public static final Block DIRT = new DirtBlock();
	public static final Block WATER = new WaterBlock();
	public static final Block WOOD = new WoodBlock();
	public static final Block LEAVES = new LeavesBlock();
	public static void registerBlock(int id, Block block) {
		block.setId((char)id);
		BLOCKS[id] = block;
	}
	public static void registerBlocks() {
		registerBlock(0, AIR);
		registerBlock(1, STONE);
		registerBlock(2, GRASS);
		registerBlock(3, DIRT);
		registerBlock(4, WATER);
		registerBlock(5, WOOD);
		registerBlock(6, LEAVES);
	}
	private char id = 0;
	public abstract byte getLightLevel();
	public abstract int getTextureIndex(int face);
	public abstract boolean isSolid();
	public abstract boolean isTransparent();
	public abstract boolean isFluid();
	private void setId(char id) {
		this.id = id;
	}
	public char getId() {
		return id;
	}
}

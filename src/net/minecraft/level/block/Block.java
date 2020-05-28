package net.minecraft.level.block;

public abstract class Block {
	public static final Block[] BLOCKS = new Block[256];
	public static final Block AIR = new AirBlock();
	public static final Block STONE = new StoneBlock();
	public static final Block GRASS = new GrassBlock();
	public static final Block DIRT = new DirtBlock();
	public static final Block WATER = new WaterBlock();
	public static void registerBlock(Block block) {
		for(int i = 0; i < 256; i++) {
			if(BLOCKS[i] == null) {
				block.setId((char)i);
				BLOCKS[i] = block;
				return;
			}
		}
	}
	public static void registerBlocks() {
		registerBlock(AIR);
		registerBlock(STONE);
		registerBlock(GRASS);
		registerBlock(DIRT);
		registerBlock(WATER);
	}
	private char id = 0;
	public abstract byte getLightLevel();
	public abstract int getTextureIndex(int face);
	public abstract boolean isSolid();
	public abstract boolean isFluid();
	private void setId(char id) {
		this.id = id;
	}
	public char getId() {
		return id;
	}
}

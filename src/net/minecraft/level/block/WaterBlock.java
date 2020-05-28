package net.minecraft.level.block;

public class WaterBlock extends Block {
	@Override
	public byte getLightLevel() {
		return 0;
	}
	@Override
	public int getTextureIndex(int face) {
		return 4;
	}
	@Override
	public boolean isSolid() {
		return false;
	}
	@Override
	public boolean isFluid() {
		return true;
	}
}

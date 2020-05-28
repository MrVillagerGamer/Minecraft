package net.minecraft.level.block;

public class AirBlock extends Block {
	@Override
	public int getTextureIndex(int face) {
		return 0;
	}
	@Override
	public boolean isSolid() {
		return false;
	}
	@Override
	public byte getLightLevel() {
		return 0;
	}
	@Override
	public boolean isFluid() {
		return false;
	}
}

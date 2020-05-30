package net.minecraft.level.block;

public class StoneBlock extends Block {
	@Override
	public byte getLightLevel() {
		return 0;
	}
	@Override
	public int getTextureIndex(int face) {
		return 3;
	}
	@Override
	public boolean isSolid() {
		return true;
	}
	@Override
	public boolean isFluid() {
		return false;
	}
	@Override
	public boolean isTransparent() {
		return false;
	}
}

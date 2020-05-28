package net.minecraft.level.block;

public class GlowstoneBlock extends Block {
	@Override
	public byte getLightLevel() {
		return 15;
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
}

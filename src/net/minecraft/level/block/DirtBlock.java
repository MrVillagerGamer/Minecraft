package net.minecraft.level.block;

public class DirtBlock extends Block{
	@Override
	public byte getLightLevel() {
		return 0;
	}
	@Override
	public int getTextureIndex(int face) {
		return 2;
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

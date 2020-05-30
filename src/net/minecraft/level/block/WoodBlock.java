package net.minecraft.level.block;

public class WoodBlock extends Block {

	@Override
	public byte getLightLevel() {
		return 0;
	}

	@Override
	public int getTextureIndex(int face) {
		return 5;
	}
	@Override
	public boolean isSolid() {
		return true;
	}
	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public boolean isFluid() {
		return false;
	}

}

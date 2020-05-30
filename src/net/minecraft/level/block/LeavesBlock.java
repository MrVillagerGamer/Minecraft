package net.minecraft.level.block;

public class LeavesBlock extends Block {

	@Override
	public byte getLightLevel() {
		return 0;
	}

	@Override
	public int getTextureIndex(int face) {
		return 6;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}

	@Override
	public boolean isFluid() {
		return false;
	}

}

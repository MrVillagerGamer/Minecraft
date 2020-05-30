package net.minecraft.level.block;

public class GrassBlock extends Block {
	@Override
	public int getTextureIndex(int face) {
		if(face == 2) {
			return 0;
		}
		if(face == 3) {
			return 2;
		}
		return 1;
	}

	@Override
	public byte getLightLevel() {
		return 0;
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

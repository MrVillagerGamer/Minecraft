package net.minecraft.ui;

import net.minecraft.level.block.Block;

public class HotbarUI extends UI{
	public int[] inv = new int[] {1, 2, 3, 4};
	public HotbarUI() {
		int id = 0;
		children = new UI[1];
		children[0] = new IconUI(id, -8/10f, -8/20f, 2f/10f, 2f/10f) {
			@Override
			public void onClick(float x, float y) {
				
			}
		};
		children[0].parent = this;
	}
	public void setItemId(int id) {
		children[0].unload();
		children[0] = new IconUI(id, -8/10f, -8/20f, 2f/10f, 2f/10f) {
			@Override
			public void onClick(float x, float y) {
				
			}
		};
		children[0].parent = this;
		children[0].load();
	}
	@Override
	public void onClick(float x, float y) {
		
	}
}

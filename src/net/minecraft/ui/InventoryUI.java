package net.minecraft.ui;

import net.minecraft.Main;
import net.minecraft.level.item.ItemStack;
import net.minecraft.util.Vector4f;

public class InventoryUI extends PanelUI{
	public char sel;
	public InventoryUI() {
		super(new Vector4f(-0.77f, -0.6f, 0.77f*2, 0.6f*2));
	}
	public void setItems(ItemStack[] items) {
		children = new UI[items.length];
		for(int i = 0; i < items.length; i++) {
			int ix = (i % 5);
			int iy = (i / 5);
			int id = iy%2;
			children[i] = new IconUI(items[i], ix*0.25f-0.67f+id*0.125f, iy*0.25f-0.5f, 0.2f, 0.2f) {
				@Override
				public void onClick(float x, float y) {
					for(int i = 0; i < Main.level.inventoryUi.children.length; i++) {
						if(Main.level.inventoryUi.children[i] == this) {
							sel = (char) i;
						}
					}
					Main.level.blockDispUI.id = (char)id;
					Main.level.blockDispUI.count = count;
					if(Main.level.ui == null) {
						Main.level.blockDispUI.unload();
						Main.level.blockDispUI.load();
					}
					Main.level.ui = null;
				}
			};
		}
	}
	@Override
	public void onClick(float x, float y) {
		
	}
}

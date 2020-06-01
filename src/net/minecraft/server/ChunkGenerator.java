package net.minecraft.server;

import java.util.Random;

import com.flowpowered.noise.module.source.Perlin;

import net.minecraft.level.Chunk;
import net.minecraft.level.block.Block;

public class ChunkGenerator {
	public static char[][][] generate(int x, int z) {
		Perlin mod = new Perlin();
		char[][][] blocks = new char[Chunk.SIZE][Chunk.HEIGHT][Chunk.SIZE];
		for(int i = 0; i < Chunk.SIZE; i++) {
			for(int k = 0; k < Chunk.SIZE; k++) {
				int h = (int) (mod.getValue((i+x*Chunk.SIZE)/256f, (k+z*Chunk.SIZE)/256f, 0) * 32f + 32f);
				for(int j = 0; j < Chunk.HEIGHT; j++) {
					char b = Block.AIR.getId();
					if(j > h && j < 32) b = Block.WATER.getId();
					if(j == h) b = Block.GRASS.getId();
					if(j <= h-1 && j > h-3) b = Block.DIRT.getId();
					if(j <= h-3) b = Block.STONE.getId();
					
					if(b != Block.AIR.getId()) blocks[i][j][k] = b;
				}
				int th = 4;
				if(new Random().nextInt()%250 == 0 && i > 2 && k > 2 && i < Chunk.SIZE-2 && k < Chunk.SIZE-2 && h < Chunk.HEIGHT-16 && h > 34) {
					for(int l = 0; l < th; l++) {
						blocks[i][h+l][k] = Block.WOOD.getId();
					}
					for(int l = -2; l <= 2; l++) {
						for(int m = -2; m <= 2; m++) {
							blocks[i+l][h+th-1][k+m] = Block.LEAVES.getId();
							blocks[i+l][h+th][k+m] = Block.LEAVES.getId();
						}
					}
					for(int l = -1; l <= 1; l++) {
						for(int m = -1; m <= 1; m++) {
							blocks[i+l][h+th+1][k+m] = Block.LEAVES.getId();
						}
						blocks[i+l][h+th+2][k] = Block.LEAVES.getId();
						blocks[i][h+th+2][k+l] = Block.LEAVES.getId();
					}
				}
			}
		}
		return blocks;
	}
}

package net.minecraft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {
	public static String readText(String path) {
		BufferedReader br = new BufferedReader(new InputStreamReader(FileUtil.class.getResourceAsStream(path)));
		try {
			String line;
			String text = "";
			while((line = br.readLine()) != null) {
				text += line;
			}
			return text;
		} catch(IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}

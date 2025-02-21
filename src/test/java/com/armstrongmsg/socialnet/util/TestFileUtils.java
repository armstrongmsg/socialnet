package com.armstrongmsg.socialnet.util;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestFileUtils {
	public static void createFileWithContent(String path, byte[] content) throws IOException, FileNotFoundException {
		File f = new File(path);
		f.createNewFile();
		FileOutputStream out = new FileOutputStream(f);
		out.write(content);
		out.close();
	}
	
	public static void assertFileHasContent(String path, byte[] content) throws FileNotFoundException, IOException {
		File f = new File(path);
		FileInputStream s = new FileInputStream(f);
		assertArrayEquals(content, s.readAllBytes());
		s.close();
	}
}

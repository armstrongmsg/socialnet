package com.armstrongmsg.socialnet.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.After;
import org.junit.Before;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;

public class PersistenceTest {
	public static final String TEST_DIRECTORY = "/tmp/test_db";
	
	@Before
	public void setUp() throws FatalErrorException {
		File testDirectory = new File(TEST_DIRECTORY);
		
		if (!testDirectory.exists()) {
			testDirectory.mkdir();
		}
	}
	
	@After
	public void tearDown() throws IOException {
		deleteDir(TEST_DIRECTORY);
	}
	
	// sourced from stackoverflow https://stackoverflow.com/a/27917071/4157070
	private void deleteDir(String path) throws IOException {
		Path directory = Paths.get(path);
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
		   @Override
		   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		       Files.delete(file);
		       return FileVisitResult.CONTINUE;
		   }

		   @Override
		   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		       Files.delete(dir);
		       return FileVisitResult.CONTINUE;
		   }
		});
	}
}

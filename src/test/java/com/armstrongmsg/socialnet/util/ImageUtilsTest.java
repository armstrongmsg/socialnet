package com.armstrongmsg.socialnet.util;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageUtilsTest {
	private static final int TEST_IMAGE_WIDTH = 1000;
	private static final int TEST_IMAGE_HEIGHT = 1000;
	private static final int TEST_IMAGE_RESCALE_WIDTH = 500;
	private static final int TEST_IMAGE_RESCALE_HEIGHT = 500;
	private ImageUtils imageUtils;
	
	@Test
	public void testRescale() throws IOException {
		imageUtils = new ImageUtils();
		byte[] imageData = getTestImageData(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT);
		
		byte[] rescaleData = imageUtils.rescale(imageData, TEST_IMAGE_RESCALE_WIDTH, TEST_IMAGE_RESCALE_HEIGHT);
		
		InputStream in = new ByteArrayInputStream(rescaleData); 
		BufferedImage bufferedImage = ImageIO.read(in);
		assertEquals(bufferedImage.getHeight(), 500);
		assertEquals(bufferedImage.getWidth(), 500);
	}
	
	private byte[] getTestImageData(int width, int height) throws IOException {
		BufferedImage newImage = new BufferedImage(width, height,
	            BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics = newImage.createGraphics();
	    try {
	        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        graphics.setBackground(Color.BLACK);
	        graphics.clearRect(0, 0, width, height);
	    } finally {
	        graphics.dispose();
	    }

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    
	    ImageIO.write(newImage, "JPG", out);
    	byte[] newImageData = out.toByteArray();
    	out.close();
	    return newImageData;	
	}
}

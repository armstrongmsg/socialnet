package com.armstrongmsg.socialnet.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageUtils {
	private static final String OUTPUT_TYPE = "JPG";

	public byte[] rescale(byte[] image, int maxHeight, int maxWidth) throws IOException {
		InputStream in = new ByteArrayInputStream(image); 
		BufferedImage bufferedImage = ImageIO.read(in);

		int height = bufferedImage.getHeight();
		int targetHeight = height;
		double heightRatio = 1;	

		int width = bufferedImage.getWidth();
		int targetWidth = width;
		double widthRatio = 1;
		
		if (height > maxHeight) {
			heightRatio = new Double(maxHeight)/height;
		}
		
		if (width > maxWidth) {
			widthRatio = new Double(maxWidth)/width;
		}
		
		targetHeight = getTargetHeight(height, heightRatio, width, widthRatio);
		targetWidth = getTargetWidth(height, heightRatio, width, widthRatio);
		
		BufferedImage newImage = doScale(bufferedImage, targetHeight, targetWidth);
	    return tryToGetNewImageData(newImage);
	}

	private int getTargetWidth(int height, double heightRatio, int width, double widthRatio) {
		if (height > width) {
			return new Double(heightRatio*width).intValue();
		} else {
			return new Double(widthRatio*width).intValue();
		}
	}

	private int getTargetHeight(int height, double heightRatio, int width, double widthRatio) {
		if (height > width) {
			return new Double(heightRatio*height).intValue();
		} else {
			return new Double(widthRatio*height).intValue();
		}
	}

	private BufferedImage doScale(BufferedImage bufferedImage, int targetHeight, int targetWidth) {
		BufferedImage newImage = new BufferedImage(targetWidth, targetHeight,
	            BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics = newImage.createGraphics();
	    try {
	        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        graphics.setBackground(Color.BLACK);
	        graphics.clearRect(0, 0, targetWidth, targetHeight);
	        graphics.drawImage(bufferedImage, 0, 0, targetWidth, targetHeight, null);
	    } finally {
	        graphics.dispose();
	    }
		return newImage;
	}

	private byte[] tryToGetNewImageData(BufferedImage newImage) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

	    try {
	    	ImageIO.write(newImage, OUTPUT_TYPE, out);
	    	byte[] newImageData = out.toByteArray();
	    	out.close();
	    	return newImageData;
	    } finally { 
	    	out.close();
	    }
	}
}

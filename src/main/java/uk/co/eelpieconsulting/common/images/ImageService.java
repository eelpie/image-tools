package uk.co.eelpieconsulting.common.images;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import uk.co.eelpieconsulting.common.images.exceptions.ImageResizingException;
import uk.co.eelpieconsulting.common.images.exceptions.InvalidImageFileException;

public class ImageService {
	
	private final static Logger log = Logger.getLogger(ImageService.class);
	
	private static final String JPEG = "jpg";
	private static final String PNG = "png";

	public boolean isValid(byte[] bytes) {
		try {
			BufferedImage parsedImage = ImageIO.read(new ByteArrayInputStream(bytes));
			return parsedImage != null;
			
		} catch (IOException e) {
			log.warn("IOException while parsing image bytes", e);
		}
		return false;
	}
		
	public BufferedImage parseBytes(byte[] bytes) throws InvalidImageFileException {
		try {
			final BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));	
			if (image == null) {
				log.info("Invalid image");
				throw new InvalidImageFileException();
			}
			
			log.debug("Parsed image of type " + image.getType() + " and size " + image.getWidth() + " x " +image.getHeight());
			return image;
			
		} catch (IOException e) {
			log.error("Error parsing image bytes", e);
			throw new InvalidImageFileException();
		}
	}
	
	public BufferedImage rotateClockwise(BufferedImage originalImage, int degrees) {
		final int originalHeight = originalImage.getHeight();
		final int originalWidth = originalImage.getWidth();
		
		if (degrees == 0) {
			log.debug("Rotation requested was 0 degress; no rotation actually required");
			return originalImage;
		}
		
		log.debug("Rotating image of size: " + originalWidth + "x" + originalHeight + " " + degrees + " degrees");
		BufferedImage rotatedImage = new BufferedImage(originalHeight, originalWidth, originalImage.getType());
		Graphics2D g = rotatedImage.createGraphics();
		
		if (degrees == 90) {
			g.rotate(Math.toRadians(90), 0, 0);
			g.drawImage(originalImage, 0, -originalHeight, originalWidth, originalHeight, null);
		
		} else if (degrees == 180) {
			rotatedImage = new BufferedImage(originalWidth, originalHeight, originalImage.getType());
			g = rotatedImage.createGraphics();
			g.rotate(Math.toRadians(180), 0, 0);
			g.drawImage(originalImage, -originalWidth, -originalHeight, originalWidth, originalHeight, null);
			
		} else if (degrees == 270){
			g.rotate(Math.toRadians(-90), 0, 0);
			g.drawImage(originalImage, -originalWidth, 0, originalWidth, originalHeight, null);
		}
		
		g.dispose();		
		return rotatedImage;
	}
	
	public byte[] makeSizedJpeg(BufferedImage originalImage, int x, int y, boolean preserveAspectRatio, boolean upscale) throws ImageResizingException {
		if (!upscale) {
			if (originalImage.getWidth() < x && originalImage.getHeight() < y) {
				return resizeAndTrim(originalImage, originalImage.getWidth(), originalImage.getHeight(), JPEG);
			}
		}
			
		if (preserveAspectRatio) {
			return makeSizedJpegPreservingAspectRatio(originalImage, x, y);
		}
		return resizeAndTrim(originalImage, x, y, JPEG);
	}
	
	public byte[] makeSizedPng(BufferedImage originalImage, int x, int y) throws ImageResizingException {
		return resizeAndTrim(originalImage, x, y, PNG);
	}
	
	public byte[] makeSizedJpegPreservingAspectRatio(BufferedImage originalImage, int x, int y) throws ImageResizingException {
		final int originalWidth = originalImage.getWidth();
		final int originalHeight = originalImage.getHeight();
		
		if (originalWidth > originalHeight) {
			final double scalingRatio = (double) x / (double) originalWidth;
			return resizeAndTrim(originalImage, x, (int) Math.round((originalHeight * scalingRatio)), JPEG);
		} else {
			final double scalingRatio = (double) y / (double) originalHeight;
			return resizeAndTrim(originalImage, (int) Math.round((originalWidth * scalingRatio)), y, JPEG);			
		}
	}
		
	private byte[] resizeAndTrim(BufferedImage originalImage, int width, int height, String type) throws ImageResizingException {
		final IMOperation op = new IMOperation();
		op.addImage();
		op.resize(width, height, "^");
		op.gravity("Center");
		op.crop(width, height, 0, 0);
		op.addImage();
		
		try {
			File tempFile = File.createTempFile("image", "." + type);
			final ConvertCmd convert = new ConvertCmd();
			convert.run(op, originalImage, tempFile.getAbsolutePath());
			
			byte[] output = FileUtils.readFileToByteArray(tempFile);
			tempFile.delete();
			return output;
			
		} catch (Exception e) {
			log.error("Error while resizing and trimming image", e);
			throw new ImageResizingException();			
		}
	}

	public byte[] writeImageToJpeg(BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, JPEG, baos);
		baos.flush();
		return baos.toByteArray();
	}
	
}

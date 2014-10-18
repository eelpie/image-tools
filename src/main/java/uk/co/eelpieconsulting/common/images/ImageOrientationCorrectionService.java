package uk.co.eelpieconsulting.common.images;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import uk.co.eelpieconsulting.common.images.exceptions.InvalidImageFileException;

public class ImageOrientationCorrectionService {
	
	private final static Logger log = Logger.getLogger(ImageOrientationCorrectionService.class);
	
	private final ImageService imageService;
	private final ExifRotationDetectionService exifRotationDetectionService;
	
	public ImageOrientationCorrectionService(ImageService imageService, ExifRotationDetectionService exifRotationDetectionService) {
		this.imageService = imageService;
		this.exifRotationDetectionService = exifRotationDetectionService;
	}
	
	public BufferedImage correctOrientation(byte[] imageBytes, int degreesOfRotationRequired) throws InvalidImageFileException {
		final BufferedImage originalImage = imageService.parseBytes(imageBytes);
		
		log.info("Image rotated " + degreesOfRotationRequired + " degrees based on a manual rotation argument");
		return imageService.rotateClockwise(originalImage, degreesOfRotationRequired);		
	}
	
	public BufferedImage correctOrientation(byte[] imageBytes) throws InvalidImageFileException {
		final BufferedImage originalImage = imageService.parseBytes(imageBytes);		
		
		final int degreesOfRotationRequired = exifRotationDetectionService.determineExifOrientationRotation(imageBytes);		
		if (degreesOfRotationRequired != 0) {
			log.info("Image was rotated " + degreesOfRotationRequired + " degrees based on EXIF orientation data");
			return imageService.rotateClockwise(originalImage, degreesOfRotationRequired);
		}
		return originalImage;	
	}
	
}

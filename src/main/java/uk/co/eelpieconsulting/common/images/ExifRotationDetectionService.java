package uk.co.eelpieconsulting.common.images;

import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.eelpieconsulting.common.images.exceptions.InvalidImageFileException;

import com.google.common.collect.Maps;

public class ExifRotationDetectionService {

	private final static Logger log = Logger.getLogger(ExifRotationDetectionService.class);

	private final ExifService exifService;
	
	private final Map<String, Integer> corrections;
	
	public ExifRotationDetectionService(ExifService exifService) {
		this.exifService = exifService;
		
		corrections = Maps.newHashMap();
		corrections.put("Right side, top (Rotate 90 CW)", 90);
		corrections.put("Bottom, right side (Rotate 180)", 180);
		corrections.put("Left side, bottom (Rotate 270 CW)", 270);
	}
	
	public int determineExifOrientationRotation(byte[] imageBytes) throws InvalidImageFileException {
		int degreesOfRotationRequired = 0;
		final String exifOrientationDescription = exifService.getOrientation(imageBytes);		
		if (exifOrientationDescription != null && corrections.containsKey(exifOrientationDescription)) {
			degreesOfRotationRequired = corrections.get(exifOrientationDescription);
			log.debug("Exif data indicates that image needs to be rotated: " + exifOrientationDescription);
		}
		return degreesOfRotationRequired;
	}
	
}

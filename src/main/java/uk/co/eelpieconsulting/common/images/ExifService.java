package uk.co.eelpieconsulting.common.images;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.eelpieconsulting.common.images.exceptions.InvalidImageFileException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.google.common.collect.Maps;

public class ExifService {

	private static Logger log = Logger.getLogger(ExifService.class);
	
	private static final int ORIENTATION_TAG = 274;
	
	public String getOrientation(byte[] imageBytes) throws InvalidImageFileException {
		try {
			final Metadata metadata = getAllMetadata(new ByteArrayInputStream(imageBytes));	
			if (metadata.containsDirectory(ExifIFD0Directory.class)) {
				ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
				if (directory.containsTag(ORIENTATION_TAG)) {
					final String orientationValue = directory.getDescription(274);
					log.info("Found orientation exif tag: " + orientationValue);
					return orientationValue;					
				}
			}
			return null;
			
		} catch (ImageProcessingException e) {
			log.error("ImageProcessingException while getting orientation: " + e);
			// ie. PNG images: ImageProcessingException while getting orientation: com.drew.imaging.ImageProcessingException: File format is not supported
			return null;
			
		} catch (IOException e) {
			log.error("IOException while getting orientation: " + e);
			throw new InvalidImageFileException();
		}
	}
	
	public Map<String, Map<String, String>> getAll(byte[] imageBytes) throws ImageProcessingException, IOException {		
		final Metadata metadata = getAllMetadata(new ByteArrayInputStream(imageBytes));
		return transformMetadata(metadata);		
	}

	private Metadata getAllMetadata(InputStream inputStream) throws ImageProcessingException, IOException {
		return ImageMetadataReader.readMetadata(new BufferedInputStream(inputStream), false);
	}
	
	private Map<String, Map<String, String>> transformMetadata(final Metadata metadata) {
		final Map<String, Map<String, String>> directoriesMap = Maps.newHashMap();
		for (Directory directory : metadata.getDirectories()) {
			final Map<String, String> directoryContents = Maps.newHashMap();			
			Collection<Tag> tags = directory.getTags();
			for (Tag tag : tags) {
				directoryContents.put(tag.getTagName(), tag.getDescription());
			}
			directoriesMap.put(directory.getName(), directoryContents);
		}
		return directoriesMap;
	}
	
}

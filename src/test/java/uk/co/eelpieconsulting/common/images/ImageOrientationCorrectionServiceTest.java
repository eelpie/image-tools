package uk.co.eelpieconsulting.common.images;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.eelpieconsulting.common.images.ExifRotationDetectionService;
import uk.co.eelpieconsulting.common.images.ImageOrientationCorrectionService;
import uk.co.eelpieconsulting.common.images.ImageService;

public class ImageOrientationCorrectionServiceTest {

	@Mock
	private ImageService imageService;
	@Mock
	private ExifRotationDetectionService exifRotationDetectionService;

	@Mock 
	private BufferedImage originalBufferedImage;
	@Mock 
	private BufferedImage rotatedImage;
	
	private ImageOrientationCorrectionService service;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = new ImageOrientationCorrectionService(imageService, exifRotationDetectionService);
	}
	
	@Test
	public void shouldReturnOriginalImageIfNoExifOrientationHintIsFound() throws Exception {
		byte[] originalImageBytes = "animage".getBytes();
		when(exifRotationDetectionService.determineExifOrientationRotation(originalImageBytes)).thenReturn(0);
		when(imageService.parseBytes(originalImageBytes)).thenReturn(originalBufferedImage);
		
		BufferedImage correctedImage = service.correctOrientation(originalImageBytes);
		
		assertEquals(originalBufferedImage, correctedImage);
	}
	
	@Test
	public void shouldReturnRotatedImageWhenExifOrientationHintIsFound() throws Exception {
		byte[] originalImageBytes = "asidewaysimage".getBytes();
		when(exifRotationDetectionService.determineExifOrientationRotation(originalImageBytes)).thenReturn(270);
		when(imageService.parseBytes(originalImageBytes)).thenReturn(originalBufferedImage);
		when(imageService.rotateClockwise(originalBufferedImage, 270)).thenReturn(rotatedImage);
		
		final BufferedImage correctedImage = service.correctOrientation(originalImageBytes);

		assertEquals(rotatedImage, correctedImage);
	}
	
}
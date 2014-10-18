package uk.co.eelpieconsulting.common.images;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.eelpieconsulting.common.images.ExifRotationDetectionService;
import uk.co.eelpieconsulting.common.images.ExifService;

public class ExifRotationDetectionServiceTest {

	@Mock
	private ExifService exifService;
	
	private byte[] imageBytes;
	
	private ExifRotationDetectionService service;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = new ExifRotationDetectionService(exifService);
		imageBytes = "asidewaysimage".getBytes();
	}
	
	@Test
	public void shouldIndicateNoRotationRequiredItNoExifOrientationHintFound() throws Exception {
		when(exifService.getOrientation(imageBytes)).thenReturn(null);

		final int rotationRequired = service.determineExifOrientationRotation(imageBytes);

		assertEquals(0, rotationRequired);		
	}
	
	@Test
	public void shouldCorrectOrientationFor90CWExifHints() throws Exception {
		when(exifService.getOrientation(imageBytes)).thenReturn("Right side, top (Rotate 90 CW)");
		
		final int rotationRequired = service.determineExifOrientationRotation(imageBytes);

		assertEquals(90, rotationRequired);
	}
	
	@Test
	public void shouldCorrectOrientationFor180CWExifHints() throws Exception {
		when(exifService.getOrientation(imageBytes)).thenReturn("Bottom, right side (Rotate 180)");
		
		final int rotationRequired = service.determineExifOrientationRotation(imageBytes);

		assertEquals(180, rotationRequired);		
	}
	
	@Test
	public void shouldCorrectOrientationFor270CWExifHints() throws Exception {
		when(exifService.getOrientation(imageBytes)).thenReturn("Left side, bottom (Rotate 270 CW)");
		
		final int rotationRequired = service.determineExifOrientationRotation(imageBytes);

		assertEquals(270, rotationRequired);		
	}
	
}
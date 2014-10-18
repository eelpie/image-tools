package uk.co.eelpieconsulting.common.images;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import uk.co.eelpieconsulting.common.images.ExifService;

public class ExifServiceTest {

	private ExifService service;
	
	@Before
	public void setup() {
		this.service = new ExifService();
	}

	@Test
	public void canReadOrientationFromPhoto() throws Exception {
		assertEquals("Right side, top (Rotate 90 CW)", service.getOrientation(loadTestImage("image-with-exif-rotation.jpg")));
	}
	
	@Test
	public void canReadOrientationFrom270DegreeRotatedPhoto() throws Exception {
		assertEquals("Left side, bottom (Rotate 270 CW)", service.getOrientation(loadTestImage("rotated-portrait-image.jpg")));
	}
	
	@Test
	public void canReadAllExifDataFromImage() throws Exception {
		Map<String, Map<String, String>> all = service.getAll(loadTestImage("image-with-gps-exif.jpg"));
		
		Map<String, String> gps = all.get("GPS");
		
		assertEquals("34.0° 1.0' 53.999999999998636\"", gps.get("GPS Latitude"));
		assertEquals("-118.0° 28.0' 50.39999999999168\"", gps.get("GPS Longitude"));

	}
	
	private byte[] loadTestImage(String filename) throws FileNotFoundException, IOException {
		return IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(filename));
	}

}

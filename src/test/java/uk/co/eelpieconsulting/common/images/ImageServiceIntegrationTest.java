package uk.co.eelpieconsulting.common.images;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import uk.co.eelpieconsulting.common.images.ExifRotationDetectionService;
import uk.co.eelpieconsulting.common.images.ExifService;
import uk.co.eelpieconsulting.common.images.ImageOrientationCorrectionService;
import uk.co.eelpieconsulting.common.images.ImageService;

public class ImageServiceIntegrationTest {

	private ImageService service;
	private ImageOrientationCorrectionService imageOrientationCorrectionService;

	@Before
	public void setup() {
		this.service = new ImageService();
		this.imageOrientationCorrectionService = new ImageOrientationCorrectionService(service, new ExifRotationDetectionService(new ExifService()));
	}
	
	@Test
	public void canValidateParseableImages() throws Exception {
		assertTrue(service.isValid(loadTestImage("image-with-exif-rotation.jpg")));
	}
	
	@Test
	public void canDetectInvalidImages() throws Exception {
		assertFalse(service.isValid(loadTestImage("not-an-image.txt")));
	}
	
	@Test
	public void canCorrectOrientationOf90DegreeImage() throws Exception {
		BufferedImage correctedImage = imageOrientationCorrectionService.correctOrientation(loadTestImage("image-with-exif-rotation.jpg"));
		
		IOUtils.write(service.writeImageToJpeg(correctedImage), new FileOutputStream(workingFolderFile("corrected90.jpg")));
	}
	
	@Test
	public void canManuallyOverruleIncorrectExifOrientation() throws Exception {		
		byte[] loadTestImage = loadTestImage("incorrect-exif-rotation.jpg");
		BufferedImage correctedImage = imageOrientationCorrectionService.correctOrientation(loadTestImage, 0);
		
		
		byte[] makeSizedJpeg = service.makeSizedJpeg(correctedImage, 1200, 1200, true, false);
		
		IOUtils.write(makeSizedJpeg, new FileOutputStream(workingFolderFile("manually-set-rotation.jpg")));
	}
	
	@Test
	public void canRotateImage180Degrees() throws Exception {
		BufferedImage rotatedImage = service.rotateClockwise(service.parseBytes(loadTestImage("landscape-photo.jpg")), 180);
		
		IOUtils.write(service.writeImageToJpeg(rotatedImage), new FileOutputStream(workingFolderFile("rotated180.jpg")));		
	}
		
	@Test
	public void canCorrectOrientationOf270DegreeImage() throws Exception {
		BufferedImage correctedImage = imageOrientationCorrectionService.correctOrientation(loadTestImage("rotated-portrait-image.jpg"));
		
		IOUtils.write(service.writeImageToJpeg(correctedImage), new FileOutputStream(workingFolderFile("corrected270.jpg")));
	}
	
	@Test
	public void canResizeRotatedImage() throws Exception {		
		BufferedImage correctedImage = imageOrientationCorrectionService.correctOrientation(loadTestImage("rotated-portrait-image.jpg"));
		
		byte[] makeSizedJpeg = service.makeSizedJpeg(correctedImage, 1200, 1200, true, false);
		
		IOUtils.write(makeSizedJpeg, new FileOutputStream(workingFolderFile("rotated270andresized.jpg")));
	}
	
	@Test
	public void canCropLanscapeImageToSquare() throws Exception {
		BufferedImage landScapeImage = service.parseBytes(loadTestImage("landscape-photo.jpg"));
		
		byte[] makeSizedJpeg = service.makeSizedJpeg(landScapeImage, 300, 300, false, true);
		
		IOUtils.write(makeSizedJpeg, new FileOutputStream(workingFolderFile("square-cropped-landscape.jpg")));		
	}
	
	private File workingFolderFile(String filename) {
		String workingDir = System.getProperty("user.dir");
		return new File(workingDir + "/target/" + filename);
	}
	
	private byte[] loadTestImage(String filename) throws FileNotFoundException, IOException {
		return IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(filename));
	}

}

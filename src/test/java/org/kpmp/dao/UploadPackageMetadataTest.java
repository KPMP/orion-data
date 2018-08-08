package org.kpmp.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UploadPackageMetadataTest {

	UploadPackage uploadPackage;
	UploadPackageMetadata uploadPackageMetadata;
	Date now;
	DateFormat dateFormat;
	List<FileSubmission> fileSubmissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		now = new Date();

		uploadPackage = mock(UploadPackage.class);
		Protocol protocol = mock(Protocol.class);
		when(protocol.getProtocol()).thenReturn("The Protocol");
		when(uploadPackage.getCreatedAt()).thenReturn(now);
		when(uploadPackage.getExperimentDate()).thenReturn(now);
		when(uploadPackage.getId()).thenReturn(1);
		when(uploadPackage.getSubjectId()).thenReturn("42");
		when(uploadPackage.getUniversalId()).thenReturn("uuid-package");
		when(uploadPackage.getProtocol()).thenReturn(protocol);

		PackageType packageType = mock(PackageType.class);
		when(packageType.getPackageType()).thenReturn("Big Data");
		when(uploadPackage.getPackageType()).thenReturn(packageType);

		SubmitterDemographics submitterDemographics = mock(SubmitterDemographics.class);
		when(submitterDemographics.getFirstName()).thenReturn("Mattie");
		when(submitterDemographics.getLastName()).thenReturn("Dayta");

		InstitutionDemographics institutionDemographics = mock(InstitutionDemographics.class);
		when(institutionDemographics.getInstitutionName()).thenReturn("Mars University");

		FileSubmission fileSubmission = mock(FileSubmission.class);
		when(fileSubmission.getFilename()).thenReturn("filename");
		when(fileSubmission.getFilePath()).thenReturn("/package1/filename");
		when(fileSubmission.getFileSize()).thenReturn(Long.valueOf(12345));
		when(fileSubmission.getInstitution()).thenReturn(institutionDemographics);
		when(fileSubmission.getSubmitter()).thenReturn(submitterDemographics);
		fileSubmissions = Arrays.asList(fileSubmission);
		when(uploadPackage.getFileSubmissions()).thenReturn(fileSubmissions);

		FileMetadataEntries fileMetadata = mock(FileMetadataEntries.class);
		when(fileMetadata.getMetadata()).thenReturn("file description");
		when(fileSubmission.getFileMetadata()).thenReturn(fileMetadata);
		when(fileSubmission.getUniversalId()).thenReturn("uuid-file");

		uploadPackageMetadata = new UploadPackageMetadata(uploadPackage);

	}

	@After
	public void tearDown() throws Exception {
		uploadPackageMetadata = null;
	}

	@Test
	public void testConstructor() throws Exception {
		assertEquals(dateFormat.format(now), uploadPackageMetadata.getCreatedAt());
		assertEquals(dateFormat.format(now), uploadPackageMetadata.getExperimentDate());
		assertEquals("Mars University", uploadPackageMetadata.getInstitution());
		assertEquals("uuid-package", uploadPackageMetadata.getId());
		assertEquals("Big Data", uploadPackageMetadata.getPackageType());
		assertEquals("42", uploadPackageMetadata.getSubjectId());
		assertEquals("Mattie", uploadPackageMetadata.getSubmitterFirstName());
		assertEquals("Dayta", uploadPackageMetadata.getSubmitterLastName());
		assertEquals("The Protocol", uploadPackageMetadata.getProtocol());
	}

	@Test
	public void testConstructorWithPackageOther() throws Exception {
		PackageTypeOther packageTypeOther = mock(PackageTypeOther.class);
		when(packageTypeOther.getPackageType()).thenReturn("Other Packagey Package");
		when(uploadPackage.getPackageTypeOther()).thenReturn(packageTypeOther);
		uploadPackageMetadata = new UploadPackageMetadata(uploadPackage);
		assertEquals(packageTypeOther.getPackageType(), uploadPackageMetadata.getPackageType());
	}

	@Test
	public void testGenerateJSON() throws JsonProcessingException {
		String actual = uploadPackageMetadata.generateJSON();
		String date = dateFormat.format(now);
		String expected = "{\"id\":\"uuid-package\",\"subjectId\":\"42\",\"experimentDate\":\""
				+ date + "\",\"createdAt\":\"" + date
				+ "\",\"packageType\":\"Big Data\",\"protocol\":\"The Protocol\",\"submitterFirstName\":\"Mattie\",\"submitterLastName\":\"Dayta\","
				+ "\"institution\":\"Mars University\",\"files\":[{\"path\":\"/package1/filename\",\"size\":12345,\"fileName\":\"filename\",\"description\":\"file description\",\"universalId\":\"uuid-file\"}]}";
		assertEquals(expected, actual);
	}

	@Test
	public void testSetters() throws Exception {
		uploadPackageMetadata.setCreatedAt("2014-04-16");
		uploadPackageMetadata.setExperimentDate("2017-07-08");
		uploadPackageMetadata.setId("9");
		uploadPackageMetadata.setInstitution("Miskatonic University");
		uploadPackageMetadata.setPackageType("Packagey Package");
		uploadPackageMetadata.setSubjectId("242");
		uploadPackageMetadata.setSubmitterFirstName("Zap");
		uploadPackageMetadata.setSubmitterLastName("Branigan");
		uploadPackageMetadata.setProtocol("The Protocol");

		assertEquals("2014-04-16", uploadPackageMetadata.getCreatedAt());
		assertEquals("2017-07-08", uploadPackageMetadata.getExperimentDate());
		assertEquals("9", uploadPackageMetadata.getId());
		assertEquals("Miskatonic University", uploadPackageMetadata.getInstitution());
		assertEquals("Packagey Package", uploadPackageMetadata.getPackageType());
		assertEquals("242", uploadPackageMetadata.getSubjectId());
		assertEquals("Zap", uploadPackageMetadata.getSubmitterFirstName());
		assertEquals("Branigan", uploadPackageMetadata.getSubmitterLastName());
		assertEquals("The Protocol", uploadPackageMetadata.getProtocol());

	}

}

package org.kpmp.upload.deprecated;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.UniversalIdGenerator;
import org.kpmp.dao.deprecated.FileMetadataEntries;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.FileSubmissionsRepository;
import org.kpmp.dao.deprecated.InstitutionDemographics;
import org.kpmp.dao.deprecated.PackageType;
import org.kpmp.dao.deprecated.PackageTypeOther;
import org.kpmp.dao.deprecated.Protocol;
import org.kpmp.dao.deprecated.SubmitterDemographics;
import org.kpmp.dao.deprecated.UploadPackage;
import org.kpmp.upload.deprecated.FileMetadataRepository;
import org.kpmp.upload.deprecated.InstitutionRepository;
import org.kpmp.upload.deprecated.PackageInformation;
import org.kpmp.upload.deprecated.PackageTypeOtherRepository;
import org.kpmp.upload.deprecated.PackageTypeRepository;
import org.kpmp.upload.deprecated.ProtocolRepository;
import org.kpmp.upload.deprecated.SubmitterRepository;
import org.kpmp.upload.deprecated.UploadPackageIds;
import org.kpmp.upload.deprecated.UploadPackageMySQLRepository;
import org.kpmp.upload.deprecated.UploadService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UploadServiceTest {

	@Mock
	private UploadPackageMySQLRepository uploadPackageRepository;
	@Mock
	private FileSubmissionsRepository fileSubmissionsRepository;
	@Mock
	private SubmitterRepository submitterRepository;
	@Mock
	private InstitutionRepository institutionRepository;
	@Mock
	private FileMetadataRepository fileMetadataRepository;
	@Mock
	private PackageTypeRepository packageTypeRepository;
	@Mock
	private PackageTypeOtherRepository packageTypeOtherRepository;
	@Mock
	private UniversalIdGenerator uuidGenerator;
	@Mock
	private ProtocolRepository protocolRepository;

	private UploadService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new UploadService(uploadPackageRepository, fileSubmissionsRepository, submitterRepository,
				institutionRepository, fileMetadataRepository, packageTypeRepository, packageTypeOtherRepository,
				uuidGenerator, protocolRepository);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testSaveUploadPackage() {
		when(uuidGenerator.generateUniversalId()).thenReturn("UUID");
		Date experimentDate = new Date();
		UploadPackage savedPackage = mock(UploadPackage.class);
		when(savedPackage.getId()).thenReturn(5);
		when(uploadPackageRepository.save(any(UploadPackage.class))).thenReturn(savedPackage);
		PackageType packageType = mock(PackageType.class);
		when(packageTypeRepository.findByPackageType("packageType")).thenReturn(packageType);
		PackageInformation packageInformation = new PackageInformation();
		packageInformation.setExperimentDate(experimentDate);
		packageInformation.setPackageType("packageType");
		packageInformation.setProtocol("protocol");
		Protocol protocol = mock(Protocol.class);
		when(protocolRepository.findByProtocol("protocol")).thenReturn(protocol);
		PackageTypeOther packageTypeOther = new PackageTypeOther();

		int packageId = service.saveUploadPackage(packageInformation, packageTypeOther);

		assertEquals(5, packageId);
		ArgumentCaptor<UploadPackage> packageCaptor = ArgumentCaptor.forClass(UploadPackage.class);
		verify(uploadPackageRepository).save(packageCaptor.capture());
		UploadPackage uploadedPackage = packageCaptor.getValue();
		assertEquals(experimentDate, uploadedPackage.getExperimentDate());
		assertEquals(packageType, uploadedPackage.getPackageType());
		assertEquals(packageTypeOther, uploadedPackage.getPackageTypeOther());
		verify(uuidGenerator).generateUniversalId();
		assertEquals("UUID", uploadedPackage.getUniversalId());
		assertEquals(experimentDate, packageCaptor.getValue().getExperimentDate());
		assertEquals(packageType, packageCaptor.getValue().getPackageType());
		assertEquals(packageTypeOther, packageCaptor.getValue().getPackageTypeOther());
		assertEquals(protocol, packageCaptor.getValue().getProtocol());
	}

	@Test
	public void testSaveSubmitterInfo() throws Exception {
		PackageInformation packageInfo = new PackageInformation();
		packageInfo.setFirstName("first name");
		SubmitterDemographics submitter = mock(SubmitterDemographics.class);
		when(submitter.getId()).thenReturn(55);
		when(submitterRepository.save(any(SubmitterDemographics.class))).thenReturn(submitter);

		int submitterId = service.saveSubmitterInfo(packageInfo);

		assertEquals(55, submitterId);
		ArgumentCaptor<SubmitterDemographics> submitterCaptor = ArgumentCaptor.forClass(SubmitterDemographics.class);
		verify(submitterRepository).save(submitterCaptor.capture());
		assertEquals("first name", submitterCaptor.getValue().getFirstName());
	}

	@Test
	public void testFindInstitutionId() throws Exception {
		PackageInformation packageInfo = new PackageInformation();
		packageInfo.setInstitutionName("institutionName");
		InstitutionDemographics institution = mock(InstitutionDemographics.class);
		when(institution.getId()).thenReturn(66);
		when(institutionRepository.findByInstitutionName("institutionName")).thenReturn(institution);

		int institutionId = service.findInstitutionId(packageInfo);

		assertEquals(66, institutionId);
		verify(institutionRepository).findByInstitutionName("institutionName");
	}

	@Test
	public void testAddFileToPackage() throws Exception {
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackageRepository.findById(1)).thenReturn(uploadPackage);
		SubmitterDemographics submitter = mock(SubmitterDemographics.class);
		when(submitterRepository.findById(2)).thenReturn(submitter);
		InstitutionDemographics institution = mock(InstitutionDemographics.class);
		when(institutionRepository.findById(3)).thenReturn(institution);
		FileMetadataEntries fileMetadataEntries = mock(FileMetadataEntries.class);
		when(fileMetadataRepository.save(any(FileMetadataEntries.class))).thenReturn(fileMetadataEntries);
		File file = mock(File.class);
		when(file.getName()).thenReturn("filename.txt");
		when(file.getPath()).thenReturn("/data/package1/filename.txt");
		when(file.length()).thenReturn(444l);
		UploadPackageIds packageIds = new UploadPackageIds();
		packageIds.setPackageId(1);
		packageIds.setSubmitterId(2);
		packageIds.setInstitutionId(3);
		when(uuidGenerator.generateUniversalId()).thenReturn("UUID");

		service.addFileToPackage(file, "fileMetadataString", packageIds);

		ArgumentCaptor<FileSubmission> fileSubmissionCaptor = ArgumentCaptor.forClass(FileSubmission.class);
		verify(fileSubmissionsRepository).save(fileSubmissionCaptor.capture());
		FileSubmission fileSubmission = fileSubmissionCaptor.getValue();
		assertEquals("/data/package1/filename.txt", fileSubmission.getFilePath());
		assertEquals(fileMetadataEntries, fileSubmission.getFileMetadata());
		assertEquals("filename.txt", fileSubmission.getFilename());
		assertEquals(new Long(444), fileSubmission.getFileSize());
		assertEquals(uploadPackage, fileSubmission.getUploadPackage());
		assertEquals(submitter, fileSubmission.getSubmitter());
		assertEquals(institution, fileSubmission.getInstitution());
		assertEquals("UUID", fileSubmission.getUniversalId());
		ArgumentCaptor<FileMetadataEntries> fileMetadataCaptor = ArgumentCaptor.forClass(FileMetadataEntries.class);
		verify(fileMetadataRepository).save(fileMetadataCaptor.capture());
		assertEquals("fileMetadataString", fileMetadataCaptor.getValue().getMetadata());
	}

	@Test
	public void testSavePackageTypeOther() throws Exception {
		PackageTypeOther savedPackageTypeOther = mock(PackageTypeOther.class);
		when(packageTypeOtherRepository.save(any(PackageTypeOther.class))).thenReturn(savedPackageTypeOther);

		PackageTypeOther result = service.savePackageTypeOther("packageTypeOther");

		assertEquals(savedPackageTypeOther, result);
		ArgumentCaptor<PackageTypeOther> packageTypeOtherCaptor = ArgumentCaptor.forClass(PackageTypeOther.class);
		verify(packageTypeOtherRepository).save(packageTypeOtherCaptor.capture());
		assertEquals("packageTypeOther", packageTypeOtherCaptor.getValue().getPackageType());
	}

	@Test
	public void testCreateFileSubmission() throws Exception {

		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackageRepository.findById(1)).thenReturn(uploadPackage);
		SubmitterDemographics submitter = mock(SubmitterDemographics.class);
		when(submitterRepository.findById(2)).thenReturn(submitter);
		InstitutionDemographics institution = mock(InstitutionDemographics.class);
		when(institutionRepository.findById(3)).thenReturn(institution);
		FileMetadataEntries fileMetadataEntries = mock(FileMetadataEntries.class);
		when(fileMetadataRepository.save(any(FileMetadataEntries.class))).thenReturn(fileMetadataEntries);
		File file = mock(File.class);
		when(file.getName()).thenReturn("filename.txt");
		when(file.getPath()).thenReturn("/data/package1/filename.txt");
		when(file.length()).thenReturn(444l);
		UploadPackageIds packageIds = new UploadPackageIds();
		packageIds.setPackageId(1);
		packageIds.setSubmitterId(2);
		packageIds.setInstitutionId(3);
		when(uuidGenerator.generateUniversalId()).thenReturn("UUID");

		FileSubmission fileSubmission = service.createFileSubmission(file, fileMetadataEntries, institution, submitter,
				uploadPackage);

		assertEquals("/data/package1/filename.txt", fileSubmission.getFilePath());
		assertEquals(fileMetadataEntries, fileSubmission.getFileMetadata());
		assertEquals("filename.txt", fileSubmission.getFilename());
		assertEquals(new Long(444), fileSubmission.getFileSize());
		assertEquals(uploadPackage, fileSubmission.getUploadPackage());
		assertEquals(submitter, fileSubmission.getSubmitter());
		assertEquals(institution, fileSubmission.getInstitution());
		assertEquals("UUID", fileSubmission.getUniversalId());

	}

	@Test
	public void testCreateUploadPackage() throws Exception {
		UploadPackage uploadPackage = mock(UploadPackage.class);
		when(uploadPackageRepository.findById(1)).thenReturn(uploadPackage);
		SubmitterDemographics submitter = mock(SubmitterDemographics.class);
		when(submitterRepository.findById(2)).thenReturn(submitter);
		InstitutionDemographics institution = mock(InstitutionDemographics.class);
		when(institutionRepository.findById(3)).thenReturn(institution);
		FileMetadataEntries fileMetadataEntries = mock(FileMetadataEntries.class);
		when(fileMetadataRepository.save(any(FileMetadataEntries.class))).thenReturn(fileMetadataEntries);
		File file = mock(File.class);
		when(file.getName()).thenReturn("filename.txt");
		when(file.getPath()).thenReturn("/data/package1/filename.txt");
		when(file.length()).thenReturn(444l);
		UploadPackageIds packageIds = new UploadPackageIds();
		packageIds.setPackageId(1);
		packageIds.setSubmitterId(2);
		packageIds.setInstitutionId(3);
		when(uuidGenerator.generateUniversalId()).thenReturn("UUID");

		FileSubmission fileSubmission = service.createFileSubmission(file, fileMetadataEntries, institution, submitter,
				uploadPackage);

		assertEquals("/data/package1/filename.txt", fileSubmission.getFilePath());
		assertEquals(fileMetadataEntries, fileSubmission.getFileMetadata());
		assertEquals("filename.txt", fileSubmission.getFilename());
		assertEquals(new Long(444), fileSubmission.getFileSize());
		assertEquals(uploadPackage, fileSubmission.getUploadPackage());
		assertEquals(submitter, fileSubmission.getSubmitter());
		assertEquals(institution, fileSubmission.getInstitution());
		assertEquals("UUID", fileSubmission.getUniversalId());

	}

}
package org.kpmp.packages;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kpmp.Application;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
@WebAppConfiguration
public class PackageControllerIntegrationTest {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	private MockMvc mockMvc;

	@Autowired
	private PackageRepository packageRepository;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private FilePathHelper filePathHelper;

	private Package defaultPackage;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
		Package uploadedPackage = new Package();
		uploadedPackage.setPackageType("mRNA");
		uploadedPackage.setSubmitter(new User());
		uploadedPackage.setInstitution("University of Colorado");
		uploadedPackage.setCreatedAt(new Date());
		uploadedPackage.setDescription("description of package");
		User user = new User();
		user.setId("1234");
		uploadedPackage.setSubmitter(user);
		defaultPackage = packageRepository.save(uploadedPackage);

		Path dataDirectory = Files.createTempDirectory("packageFileHandler");
		ReflectionTestUtils.setField(filePathHelper, "basePath", dataDirectory.toAbsolutePath().toString());

		MockMultipartFile file = new MockMultipartFile("qqfile", "file.txt", "text/plain", "file contents".getBytes());

		mockMvc.perform(RestDocumentationRequestBuilders
				.fileUpload("/v1/packages/{packageId}/files", defaultPackage.getPackageId()).file(file)
				.param("qqfilename", "attachment.txt").param("qqtotalfilesize", "100").param("qqtotalparts", "1")
				.param("qqpartindex", "0"));

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPackages() throws Exception {
		mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/packages")).andExpect(status().isOk())
				.andDo(document("listPackages", responseFields(
						fieldWithPath("[]")
								.description("The list of packages that have been uploaded to the data lake"),
						fieldWithPath("[].packageInfo.packageId")
								.description("The generated universal id for the uploaded package"),
						fieldWithPath("[].packageInfo.packageType")
								.description("The type of data contained in the package, ex: Sub-segment RNAseq"),
						fieldWithPath("[].packageInfo.createdAt").description("The date this package was uploaded"),
						fieldWithPath("[].packageInfo.submitter")
								.description("The person who submitted this package"),
						fieldWithPath("[].packageInfo.protocol").description("The protocol used to generate this data"),
						fieldWithPath("[].packageInfo.subjectId")
								.description("The subject or sample id associated with this data"),
						fieldWithPath("[].packageInfo.experimentDate").description("The date this data was generated"),
						fieldWithPath("[].packageInfo.description").description(
								"A free text field used to describe the data in the package, the experiment, etc"),
						fieldWithPath("[].packageInfo.attachments")
								.description("The list of files that are included in this package"),
						fieldWithPath("[].downloadable")
								.description("Indicates whether this package is available to download"),
						fieldWithPath("[].packageInfo.institution")
								.description("The name of the institution where this data was generated"),
						fieldWithPath("[].packageInfo.attachments[].id")
								.description("The generated universal id for this file"),
						fieldWithPath("[].packageInfo.attachments[].size")
								.description("The size in bytes of the attached file"),
						fieldWithPath("[].packageInfo.attachments[].fileName")
								.description("The name of the attched file"))));
	}

	@Test
	public void testPostPackageInformation() throws Exception {
		Package packageInfo = new Package();
		packageInfo.setCreatedAt(new Date());
		packageInfo.setDescription("this package contains a lot of data generated by my new technique");
		packageInfo.setExperimentDate(new Date());
		packageInfo.setInstitution("University of Michigan");
		packageInfo.setPackageType("Bulk RNA-Seq");
		packageInfo.setProtocol("Pilot 1");
		packageInfo.setSubjectId("12345");
		User user = new User();
		user.setId("1234");
		packageInfo.setSubmitter(user);

		mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/packages").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(packageInfo)))
				.andExpect(status().isOk())
				.andDo(document("uploadPackageInfo",
						requestFields(
								fieldWithPath("submitter")
										.description("The person who submitted this package"),
								fieldWithPath("createdAt").description("The date this package was uploaded"),
								fieldWithPath("description").description(
										"A free text field used to describe the data in the package, the experiment, etc"),
								fieldWithPath("experimentDate").description("The date this data was generated"),
								fieldWithPath("institution")
										.description("The name of the institution where this data was generated"),
								fieldWithPath("packageType").description(
										"The type of data contained in the package, ex: Sub-segment RNAseq"),
								fieldWithPath("protocol").description("The protocol used to generate this data"),
								fieldWithPath("subjectId").description("The date this data was generated"),
								fieldWithPath("packageId").description(
										"The generated universal id for the uploaded package.  DO NOT PROVIDE A VALUE"),
								fieldWithPath("attachments")
										.description("The list of files that are included in this package"))));
	}

	@Test
	public void testPostFileToPackage() throws Exception {

		MockMultipartFile file = new MockMultipartFile("qqfile", "file.txt", "text/plain", "file contents".getBytes());

		mockMvc.perform(RestDocumentationRequestBuilders
				.fileUpload("/v1/packages/{packageId}/files", defaultPackage.getPackageId()).file(file)
				.param("qqfilename", "file.txt").param("qqtotalfilesize", "100").param("qqtotalparts", "1")
				.param("qqpartindex", "0"))
				.andExpect(status().isOk())
				.andDo(document("postFileToPackage",
						pathParameters(parameterWithName("packageId")
								.description("The generated universal id for the uploaded package")),
						requestParameters(
								parameterWithName("qqfilename")
										.description("The name of the file being attached to the package"),
								parameterWithName("qqtotalfilesize")
										.description("The size of the file in bytes being transferred"),
								parameterWithName("qqtotalparts")
										.description("The total number of chunks the file will be divided into"),
								parameterWithName("qqpartindex")
										.description("The index (starting at 0) of this file part")),
						responseFields(fieldWithPath("success")
								.description("Indication of whether the file chunk was successfully processed"))));
	}

	@Test
	public void testFinishUpload() throws Exception {
		mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/packages/{packageId}/files/finish",
				defaultPackage.getPackageId()))
				.andExpect(status().isOk())
				.andDo(document("finishPackage",
						pathParameters(parameterWithName("packageId")
								.description("The generated universal id for the uploaded package")),
						responseFields(fieldWithPath("success")
								.description("Indication of whether the file chunk was successfully processed"))));
	}

	@Test
	public void testDownloadPackage() throws Exception {
		// create the zip file
		String zipFileName = filePathHelper.getZipFileName(defaultPackage.getPackageId());
		File downloadFile = new File(zipFileName);
		downloadFile.createNewFile();

		mockMvc.perform(
				RestDocumentationRequestBuilders.get("/v1/packages/{packageId}/files", defaultPackage.getPackageId()))
				.andExpect(status().isOk())
				.andDo(document("downloadPackage", pathParameters(parameterWithName("packageId")
						.description("The generated universal id for the uploaded package"))));
	}

}

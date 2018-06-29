package org.kpmp.upload;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kpmp.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class UploadControllerIntegrationTest {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void uploadPackageInfo_documentation() throws Exception {
		PackageInformation packageInformation = new PackageInformation();
		packageInformation.setFirstName("jim");
		packageInformation.setLastName("bob");
		packageInformation.setPackageType("DNA Methylation");
		packageInformation.setInstitutionName("Indiana (IU/OSU TIS)");

		mockMvc.perform(RestDocumentationRequestBuilders.post("/upload/packageInfo")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(packageInformation)))
				.andExpect(status().isOk()).andDo(document("uploadPackageInfo"));
	}

	@Test
	public void upload_documentation() throws Exception {
		MockMultipartFile firstFile = new MockMultipartFile("qqfile", "file1.txt", "text/plain",
				"some data".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(firstFile)
				.param("fileMetadata", "This is a greate file").param("packageId", "1").param("submitterId", "1")
				.param("institutionId", "1").param("qqfilename", "filename")).andExpect(status().isOk())
				.andDo(document("upload"));

	}

}

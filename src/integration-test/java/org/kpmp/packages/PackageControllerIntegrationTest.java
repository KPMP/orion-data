package org.kpmp.packages;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kpmp.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
		Package uploadedPackage = new Package();
		uploadedPackage.setPackageType("mRNA");
		uploadedPackage.setSubmitter("John Doe");
		uploadedPackage.setInstitution("University of Colorado");
		uploadedPackage.setCreatedAt(new Date());
		packageRepository.save(uploadedPackage);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		mockMvc.perform(RestDocumentationRequestBuilders.get("/v1/packages")).andExpect(status().isOk())
				.andDo(document("packages", responseFields(
						fieldWithPath("[]")
								.description("The list of packages that have been uploaded to the data lake"),
						fieldWithPath("[].packageId").description("The generated unique id for the uploaded package"),
						fieldWithPath("[].packageType")
								.description("The type of data contained in the package, ex: Sub-segment RNAseq"),
						fieldWithPath("[].createdAt").description("The date this package was uploaded"),
						fieldWithPath("[].submitter")
								.description("The first and last name of the person who submitted this package"),
						fieldWithPath("[].institution")
								.description("The name of the institution where this data was generated"))));
	}

}

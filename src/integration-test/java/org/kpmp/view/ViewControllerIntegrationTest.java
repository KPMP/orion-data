package org.kpmp.view;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ViewControllerIntegrationTest {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void viewUploads() throws Exception {
		mockMvc.perform(RestDocumentationRequestBuilders.get("/uploader/viewFiles")).andExpect(status().isOk())
				.andDo(document("viewUploads", responseFields(
						fieldWithPath("[]").description("The list of files that have been uploaded to the data lake"),
						fieldWithPath("[].researcher").description(
								"The first name and last name of the researcher who uploaded this package"),
						fieldWithPath("[].institution")
								.description("The name of the institution where this package was generated"),
						fieldWithPath("[].packageType")
								.description("The package type (or package type other) provided by the uploader"),
						fieldWithPath("[].experimentDate")
								.description(
										"The date of the experiment associated with this package (or null if none provided)")
								.optional(),
						fieldWithPath("[].createdAt").description("The date/timestamp when this package was created"),
						fieldWithPath("[].subjectId")
								.description("The subjectId provided for this package (or null if none provided)")
								.optional(),
						fieldWithPath("[].experimentId")
								.description("The experimentId provided for this package (or null if none provided)")
								.optional(),
						fieldWithPath("[].filename").description("The name of the file uploaded"))));
	}

}

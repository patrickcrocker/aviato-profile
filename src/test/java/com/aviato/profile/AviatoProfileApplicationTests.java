package com.aviato.profile;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AviatoProfileApplication.class)
@WebAppConfiguration
public class AviatoProfileApplicationTests {

	@Rule
	public RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void peopleListExample() throws Exception {

		personRepository.deleteAll();

		createPerson("Rob", "Mee");

		//@formatter:off
		this.mockMvc.perform(get("/people"))
			.andExpect(status().isOk())
			.andDo(document("people-list-example",
				responseFields(
						fieldWithPath("_embedded.people").description("An array of <<resources-people, People resources>>"))));
		//@formatter:on
	}

	private void createPerson(String firstName, String lastName) {

		Person person = new Person();

		person.setFirstName(firstName);
		person.setLastName(lastName);

		personRepository.save(person);
	}

}

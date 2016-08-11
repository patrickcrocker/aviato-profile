/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aviato.profile;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AviatoProfileApplication.class)
public class AviatoProfileApplicationTests {
	
	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void errorExample() throws Exception {
		this.mockMvc
				.perform(get("/error")
						.requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
						.requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
								"/persons")
						.requestAttr(RequestDispatcher.ERROR_MESSAGE,
								"The person 'http://localhost:8080/persons/123' does not exist"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("error", is("Bad Request")))
				.andExpect(jsonPath("timestamp", is(notNullValue())))
				.andExpect(jsonPath("status", is(400)))
				.andExpect(jsonPath("path", is(notNullValue())))
				.andDo(document("error-example",
						responseFields(
								fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
								fieldWithPath("message").description("A description of the cause of the error"),
								fieldWithPath("path").description("The path to which the request was made"),
								fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
								fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))));
	}

	@Test
	public void indexExample() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andDo(document("index-example",
					links(
							linkWithRel("persons").description("The <<resources-persons,Persons resource>>"),
							linkWithRel("profile").description("The ALPS profile for the service")),
					responseFields(
							fieldWithPath("_links").description("<<resources-index-links,Links>> to other resources"))));

	}

	@Test
	public void personsListExample() throws Exception {
		this.personRepository.deleteAll();

		createPerson("Rob", "Mee");

		this.mockMvc.perform(get("/persons"))
			.andExpect(status().isOk())
			.andDo(document("persons-list-example",
					links(
							linkWithRel("self").description("Canonical link for this resource"),
							linkWithRel("profile").description("The ALPS profile for this resource")),
					responseFields(
							fieldWithPath("_embedded.persons").description("An array of <<resources-person, Person resources>>"),
							fieldWithPath("_links").description("<<resources-tags-list-links, Links>> to other resources"))));
	}

	@Test
	public void personsCreateExample() throws Exception {
		Map<String, Object> person = new HashMap<String, Object>();
		person.put("firstName", "Rob");
		person.put("lastName", "Mee");

		this.mockMvc.perform(
				post("/persons").contentType(MediaTypes.HAL_JSON).content(
						this.objectMapper.writeValueAsString(person))).andExpect(
				status().isCreated())
				.andDo(document("persons-create-example",
						requestFields(
									fieldWithPath("firstName").description("The first name of the person"),
									fieldWithPath("lastName").description("The last name of the person"))));
	}

	@Test
	public void personGetExample() throws Exception {
		Map<String, Object> person = new HashMap<String, Object>();
		person.put("firstName", "Rob");
		person.put("lastName", "Mee");

		String personLocation = this.mockMvc
				.perform(
						post("/persons").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(person)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		this.mockMvc.perform(get(personLocation))
			.andExpect(status().isOk())
			.andExpect(jsonPath("firstName", is(person.get("firstName"))))
			.andExpect(jsonPath("lastName", is(person.get("lastName"))))
			.andExpect(jsonPath("_links.self.href", is(personLocation)))
			.andDo(print())
			.andDo(document("person-get-example",
					links(
							linkWithRel("self").description("Canonical link for this <<resources-person,person>>"),
							linkWithRel("person").description("This <<resources-person,person>>")),
					responseFields(
							fieldWithPath("firstName").description("The first name of the person"),
							fieldWithPath("lastName").description("The last name of the person"),
							fieldWithPath("_links").description("<<resources-person-links,Links>> to other resources"))));
	}

	@Test
	public void personUpdateExample() throws Exception {
		Map<String, Object> person = new HashMap<String, Object>();
		person.put("firstName", "Rob");
		person.put("lastName", "Mee");

		String personLocation = this.mockMvc
				.perform(
						post("/persons").contentType(MediaTypes.HAL_JSON).content(
								this.objectMapper.writeValueAsString(person)))
				.andExpect(status().isCreated()).andReturn().getResponse()
				.getHeader("Location");

		this.mockMvc.perform(get(personLocation)).andExpect(status().isOk())
				.andExpect(jsonPath("firstName", is(person.get("firstName"))))
				.andExpect(jsonPath("lastName", is(person.get("lastName"))))
				.andExpect(jsonPath("_links.self.href", is(personLocation)));

		Map<String, Object> personUpdate = new HashMap<String, Object>();
		personUpdate.put("firstName", "Robert");

		this.mockMvc.perform(
				patch(personLocation).contentType(MediaTypes.HAL_JSON).content(
						this.objectMapper.writeValueAsString(personUpdate)))
				.andExpect(status().isNoContent())
				.andDo(document("person-update-example",
						requestFields(
								fieldWithPath("firstName").description("The firstName of the person").type(JsonFieldType.STRING).optional(),
								fieldWithPath("lastName").description("The lastName of the person").type(JsonFieldType.STRING).optional())));
	}

	private void createPerson(String firstName, String lastName) {
		Person note = new Person();
		note.setFirstName(firstName);
		note.setLastName(lastName);

		this.personRepository.save(note);
	}

}

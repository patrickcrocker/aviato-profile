package com.aviato.profile;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PROTECTED)
	private long id;

	private String firstName;

	private String lastName;

	private String birthDate;
}

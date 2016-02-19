package no.kitttn.testmatcher.model.events;

import no.kitttn.testmatcher.model.Person;

/**
 * @author kitttn
 */
public class PersonUpdatedEvent {
	private Person person;

	public PersonUpdatedEvent(Person person) {
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}
}

package no.kitttn.testmatcher.model.events;

import no.kitttn.testmatcher.model.Person;

/**
 * @author kitttn
 */
public class GotPersonEvent {
	private Person person;

	public GotPersonEvent(Person person) {
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}
}

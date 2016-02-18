package no.kitttn.testmatcher.model.events;

/**
 * @author kitttn
 */
public class PersonUpdatedEvent {
	private String person;
	public PersonUpdatedEvent(String jsonPerson) {
		this.person = jsonPerson;
	}

	public String getPerson() {
		return person;
	}
}

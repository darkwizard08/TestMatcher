package no.kitttn.testmatcher;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.EmptyListEvent;
import no.kitttn.testmatcher.model.events.MatchEvent;

/**
 * @author kitttn
 */
public class Matcher {
	private static final String TAG = "Matcher";
	protected UserGenerator generator;
	private EventBus bus;

	@Inject
	public Matcher(UserGenerator generator, EventBus bus) {
		this.generator = generator;
		this.bus = bus;
	}

	// TODO: make private
	public ArrayList<Person> likedPersons = new ArrayList<>();
	public ArrayList<Person> rejectedPersons = new ArrayList<>();

	public void getNextPerson() {
		if (generator.getPersonsLeft() == 0) {
			likedPersons = new ArrayList<>();
			rejectedPersons = new ArrayList<>();
			Log.i(TAG, "getNextPerson: List is empty :(");
			bus.post(new EmptyListEvent());
			return;
		}
		generator.getNextPerson();
	}

	private void markViewed(Person person) {
		generator.setPersonViewed(person);
	}

	public void markLiked(Person likedPerson) {
		Log.i(TAG, "markLiked: You liked " + likedPerson);
		markViewed(likedPerson);
		likedPersons.add(likedPerson);
		checkCompatibility(likedPerson);
	}

	public void markDisliked(Person dislikedPerson) {
		rejectedPersons.add(dislikedPerson);
		markViewed(dislikedPerson);
	}

	public boolean checkCompatibility(Person p) {
		boolean res = p.getStatus().equals("like") && likedPersons.contains(p);
		if (likedPersons.contains(p))
			System.out.println("You liked her");
		if (res) {
			System.out.println("She liked you");
			bus.post(new MatchEvent());
		}

		return res;
	}

	public void unsubscribe() {
		generator.unsubscribe();
	}

	public void close() {
		generator.realm.close();
	}
}

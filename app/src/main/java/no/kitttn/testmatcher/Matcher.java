package no.kitttn.testmatcher;

import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import no.kitttn.testmatcher.model.Person;

/**
 * @author kitttn
 */
public class Matcher {
	private static final String TAG = "Matcher";
	@Inject protected UserGenerator generator;

	// TODO: make private
	public ArrayList<Person> likedPersons = new ArrayList<>();
	public ArrayList<Person> rejectedPersons = new ArrayList<>();

	public Person getNextPerson() {
		if (generator.getPersonsLeft() == 0) {
			likedPersons = new ArrayList<>();
			rejectedPersons = new ArrayList<>();
		}
		return generator.getNextPerson();
	}

	public void markLiked(Person likedPerson) {
		Log.i(TAG, "markLiked: You liked " + likedPerson);
		likedPersons.add(likedPerson);
		if (checkCompatibility(likedPerson)) {
			// TODO: send Event here
		}
	}

	public void markDisliked(Person dislikedPerson) {
		rejectedPersons.add(dislikedPerson);
	}

	public boolean checkCompatibility(Person p) {
		return p.getStatus().equals("like")/* && likedPersons.contains(p)*/;
	}
}

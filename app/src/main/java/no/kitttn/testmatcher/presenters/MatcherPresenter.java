package no.kitttn.testmatcher.presenters;

import android.util.Log;

import javax.inject.Inject;

import no.kitttn.testmatcher.Matcher;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.views.MatcherView;

/**
 * @author kitttn
 */
public class MatcherPresenter {
	private static final String TAG = "MatcherPresenter";
	private MatcherView view;
	private Matcher matcher;

	@Inject
	public MatcherPresenter(Matcher matcher) {
		this.matcher = matcher;
	}

	public void setView(MatcherView view) {
		this.view = view;
		this.matcher.subscribe();
	}

	public void destroy() {
		matcher.unsubscribe();
	}

	public void getNextPerson() {
		Log.i(TAG, "getNextPerson: Loading person...");
		view.loading();
		Person p = matcher.getNextPerson();
		Log.i(TAG, "getNextPerson: Got person: " + p);
		view.showProfile(p);
		view.stopLoading();
	}

	public void likePerson(Person person) {
		Log.i(TAG, "likePerson: " + person);
		matcher.markLiked(person);
		getNextPerson();
	}

	public void dislikePerson(Person person) {
		Log.i(TAG, "dislikePerson: " + person);
		matcher.markDisliked(person);
		getNextPerson();
	}
}

package no.kitttn.testmatcher.presenters;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import no.kitttn.testmatcher.Matcher;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.GotPersonEvent;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;
import no.kitttn.testmatcher.views.MatcherView;

/**
 * @author kitttn
 */
public class MatcherPresenter {
	private static final String TAG = "MatcherPresenter";
	private MatcherView view;
	private Matcher matcher;
	private EventBus bus;

	@Inject
	public MatcherPresenter(Matcher matcher, EventBus bus) {
		this.matcher = matcher;
		this.bus = bus;
		bus.register(this);
	}

	public void setView(MatcherView view) {
		this.view = view;
	}

	public void destroy() {
		matcher.unsubscribe();
	}

	public void getNextPerson() {
		Log.i(TAG, "getNextPerson: Loading person...");
		view.loading();
		matcher.getNextPerson();
	}

	@Subscribe
	public void onGotPerson(GotPersonEvent evt) {
		Person p = evt.getPerson();
		Log.i(TAG, "getNextPerson: Got person: " + p);
		view.showProfile(p);
		view.stopLoading();
	}

	@Subscribe
	public void onPersonUpdated(PersonUpdatedEvent evt) {
		Person p = evt.getPerson();
		if (matcher.checkCompatibility(p))
			Log.i(TAG, "onPersonUpdated: Hoorray, it's a MATCH!");
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

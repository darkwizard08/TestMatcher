package no.kitttn.testmatcher.presenters;

import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import no.kitttn.testmatcher.Matcher;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.EmptyListEvent;
import no.kitttn.testmatcher.model.events.GotPersonEvent;
import no.kitttn.testmatcher.model.events.MatchEvent;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;
import no.kitttn.testmatcher.views.MapView;
import no.kitttn.testmatcher.views.MatcherView;

/**
 * @author kitttn
 */
public class MatcherPresenter {
	private static final String TAG = "MatcherPresenter";
	private MatcherView view;
	private MapView map;
	private Matcher matcher;
	private EventBus bus;

	private Person currentPerson;

	@Inject
	public MatcherPresenter(Matcher matcher, EventBus bus) {
		this.matcher = matcher;
		this.bus = bus;
		bus.register(this);
	}

	public void setView(MatcherView view) {
		this.view = view;
	}

	public void setMapView(MapView view) {
		this.map = view;
	}

	public void destroy() {
		Log.i(TAG, "destroy: Destroying matcher...");
		matcher.unsubscribe();
		matcher.close();
	}

	public void getNextPerson() {
		Log.i(TAG, "getNextPerson: Loading person...");
		view.loading();
		matcher.getNextPerson();
	}

	@Subscribe
	public void onGotPerson(GotPersonEvent evt) {
		currentPerson = evt.getPerson();
		Log.i(TAG, "getNextPerson: Got person: " + currentPerson);
		view.showProfile(currentPerson);
		showOnMap(currentPerson);
		view.stopLoading();
	}

	@Subscribe
	public void onMatch(MatchEvent event) {
		view.notice("Matched!");
		view.matchNotification();
	}

	@Subscribe
	public void onEmptyList(EmptyListEvent event) {
		view.notice("List ended, generate again, please!");
		view.end();
	}

	@Subscribe
	public void onPersonUpdated(PersonUpdatedEvent evt) {
		Person p = evt.getPerson();
		if (currentPerson != null && p.getId() == currentPerson.getId()) {
			if (p.getStatus().equals("removed")) {
				view.notice("Person blocked you :(");
				getNextPerson();
				return;
			}
			// maybe location updates?
			showOnMap(p);
		}

		// it's not me, maybe match?
		Log.i(TAG, "onPersonUpdated: New person: " + p);
		if (matcher.checkCompatibility(p))
			Log.i(TAG, "updatePerson: Hoorray, it's a MATCH!");
	}

	private void showOnMap(Person person) {
		if (map != null)
			map.showPerson(person);
	}

	public void likePerson() {
		Log.i(TAG, "likePerson: " + currentPerson);
		matcher.markLiked(currentPerson);
		getNextPerson();
	}

	public void dislikePerson() {
		Log.i(TAG, "dislikePerson: " + currentPerson);
		matcher.markDisliked(currentPerson);
		getNextPerson();
	}
}

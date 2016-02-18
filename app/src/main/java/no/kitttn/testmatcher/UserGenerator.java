package no.kitttn.testmatcher;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.PersonsExtendedCallback;

import java.util.ArrayList;

import javax.inject.Inject;

import io.realm.Realm;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;

/**
 * @author kitttn
 */
public class UserGenerator {
	class UserUpdater {
		private boolean isSubscribed = false;

		public void subscribe() {
			if (isSubscribed)
				return;
			isSubscribed = true;
			API.INSTANCE.subscribeUpdates(person -> bus.post(person));
			Log.w(TAG, "subscribe: Subscribed!");
		}

		public void unsubscribe() {
			isSubscribed = false;
			API.INSTANCE.unSubscribeUpdates();
			Log.w(TAG, "unsubscribe: Unsubscribed!");
		}

		public boolean isSubscribed() {
			return isSubscribed;
		}
	}

	private static final String TAG = "UserGenerator";
	private ArrayList<Person> userList = new ArrayList<>();
	private int pagesLoaded = 0;

	protected UserUpdater updater = new UserUpdater();
	protected EventBus bus;
	@Inject protected Realm realm;
	@Inject protected Context context;

	@Inject
	public UserGenerator(EventBus bus) {
		this.bus = bus;
		bus.register(this);
	}

	public void generate() {
		updater.unsubscribe();
		API.INSTANCE.init(context);
		API.INSTANCE.refreshPersons(this::loadPersons);
	}

	// ====== loading ======

	public void loadPersons() {
		loadPage(0);
	}

	public void loadPage(int pageNumber) {
		Log.i(TAG, "loadPage: Loading page " + pageNumber + "...");
		API.INSTANCE.getPersons(pageNumber, new PersonsExtendedCallback() {
			@Override
			public void onResult(String persons) {
				processPersonsList(pageNumber, persons);
			}

			@Override
			public void onFail(String reason) {
				Log.e(TAG, "onFail: Error:" + reason);
			}
		});
	}

	public boolean areAllPersonsProcessed(String personsList) {
		return personsList.length() == 2;
	}

	public void processingFinished() {
		Log.i(TAG, "processPersonsList: Finished processing, total pages: " + pagesLoaded);
		for (Person p : this.realm.where(Person.class).findAll())
			this.userList.add(p);
		// throw Event
		//updater.subscribe();
	}

	public void processPersonsList(int pageLoaded, String personsList) {
		if (areAllPersonsProcessed(personsList)) {
			this.pagesLoaded = pageLoaded;
			processingFinished();
			return;
		}
		loadPage(pageLoaded + 1);
	}

	// ======== updating ========

	@Subscribe
	public void updatePerson(String person) {
		Realm r = Realm.getInstance(context);
		r.beginTransaction();
		Person p = r.createOrUpdateObjectFromJson(Person.class, person);
		r.commitTransaction();

		Log.w(TAG, "updatePerson: Updating person with id=" + p.getId());
	}

	// ======== subscription ========

	public void subscribe() {
		updater.subscribe();
	}

	public void unsubscribe() {
		updater.unsubscribe();
	}

	public ArrayList<Person> getUserList() {
		return userList;
	}
}

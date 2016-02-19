package no.kitttn.testmatcher;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.PersonsExtendedCallback;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import io.realm.Realm;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.PersonListUpdatedEvent;
import rx.Observable;

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
	Observable<API> api = Observable.defer(() -> Observable.just(API.INSTANCE));

	private static final String TAG = "UserGenerator";
	private ArrayList<Person> userList = new ArrayList<>();
	private int pagesLoaded = 0;
	private Random random = new Random();

	protected UserUpdater updater = new UserUpdater();
	protected EventBus bus;
	protected Realm realm;
	protected Context context;

	@Inject
	public UserGenerator(EventBus bus, Realm realm, Context context) {
		this.bus = bus;
		bus.register(this);
		this.realm = realm;
		this.context = context;

		API.INSTANCE.init(this.context);
	}

	public void generate() {
		realm.beginTransaction();
		for (int i = 0; i < 100; ++i) {
			Person p = new Person();
			p.setId(i + 1);
			userList.add(p);
		}
		realm.commitTransaction();
		processingFinished();
		/*updater.unsubscribe();
		API.INSTANCE.init(context);
		API.INSTANCE.refreshPersons(this::updateList);*/
	}

	// ====== loading ======

	private void updateList() {
		loadPage(0);
	}

	private void loadPage(int pageNumber) {
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

	private boolean areAllPersonsProcessed(String personsList) {
		return personsList.length() == 2;
	}

	private void processingFinished() {
		Log.i(TAG, "processPersonsList: Finished processing, total pages: " + pagesLoaded);
		for (Person p : this.realm.where(Person.class).findAll())
			this.userList.add(p);

		bus.post(new PersonListUpdatedEvent());
		//updater.subscribe();
	}

	private void processPersonsList(int pageLoaded, String personsList) {
		if (areAllPersonsProcessed(personsList)) {
			this.pagesLoaded = pageLoaded;
			processingFinished();
			return;
		}
		loadPage(pageLoaded + 1);
	}

	// ======== updating ========

	@Subscribe
	public void updatePerson(Person person) {
		// creating new instance because Realm can't provide same instance on different threads
		Realm r = Realm.getInstance(context);
		r.beginTransaction();
		Person p = r.copyToRealmOrUpdate(person);
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

	// ========= users list =========

	public ArrayList<Person> getUserList() {
		return userList;
	}

	public int getPersonsLeft() {
		return userList.size();
	}

	public Person getNextPerson() {
		Log.i(TAG, "getNextPerson: Persons left:" + getPersonsLeft());
		if (getPersonsLeft() == 0) {
			Log.i(TAG, "getNextPerson: No persons left, starting from the beginning...");
			generate();
		}
		int index = random.nextInt(getPersonsLeft());
		return userList.remove(index);
	}
}

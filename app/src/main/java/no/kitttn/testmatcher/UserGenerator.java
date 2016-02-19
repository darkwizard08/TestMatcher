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
import no.kitttn.testmatcher.model.events.GotPersonEvent;
import no.kitttn.testmatcher.model.events.PersonListUpdatedEvent;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;
import rx.Observable;
import rx.Subscription;

/**
 * @author kitttn
 */
public class UserGenerator {
	private static final String TAG = "UserGenerator";
	private ArrayList<Person> userList = new ArrayList<>();
	private int pagesLoaded = 0;
	private Random random = new Random();
	private Subscription sub;

	protected EventBus bus;
	protected Realm realm;
	protected Context context;
	protected RxAPI api;

	@Inject
	public UserGenerator(EventBus bus, Realm realm, Context context, RxAPI api) {
		this.bus = bus;
		this.realm = realm;
		this.context = context;
		this.api = api;
		sub = this.api.update().subscribe(this::onPersonUpdated);
	}

	// ========= users list =========

	public void onPersonUpdated(Person p) {
		Log.i(TAG, "onPersonUpdated: " + p);
		realm.beginTransaction();
		realm.copyToRealmOrUpdate(p);
		realm.commitTransaction();

		Log.i(TAG, "onPersonUpdated: Sending person updated event...");
		bus.post(new PersonUpdatedEvent(p));
	}

	public ArrayList<Person> getUserList() {
		return userList;
	}

	public int getPersonsLeft() {
		return userList.size();
	}

	public void unsubscribe() {
		sub.unsubscribe();
	}

	public void yieldPerson() {
		int index = random.nextInt(getPersonsLeft());
		Person p = userList.remove(index);
		Log.i(TAG, "yieldPerson: " + getPersonsLeft() + " persons left");
		bus.post(new GotPersonEvent(p));
	}

	public void generate() {
		api.generate().subscribe(
				person -> {
					System.out.println("Person id: " + person.getId());
					userList.add(person);
				},
				Throwable::printStackTrace,
				() -> {
					bus.post(new PersonListUpdatedEvent());
					System.out.println("List loaded, size: " + userList.size());
				}
		);
	}

	public void getNextPerson() {
		Log.i(TAG, "getNextPerson: Persons left:" + getPersonsLeft());
		if (getPersonsLeft() == 0) {
			Log.i(TAG, "getNextPerson: No persons left, starting from the beginning...");
			generate();
		} else
			this.yieldPerson();
	}
}

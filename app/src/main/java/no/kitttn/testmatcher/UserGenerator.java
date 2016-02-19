package no.kitttn.testmatcher;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import io.realm.Realm;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.EmptyListEvent;
import no.kitttn.testmatcher.model.events.GotPersonEvent;
import no.kitttn.testmatcher.model.events.PersonListUpdatedEvent;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

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
	}

	public void updatePerson(Person p) {
		Log.i(TAG, "updatePerson: " + p);
		realm.beginTransaction();
		appendViewedStatus(p);
		realm.copyToRealmOrUpdate(p);
		realm.commitTransaction();

		checkRemovedStatus(p);

		Log.i(TAG, "updatePerson: Sending person updated event...");
		bus.post(new PersonUpdatedEvent(p));
	}

	public void initPerson(Person p) {
		userList.add(p);
		realm.beginTransaction();
		realm.copyToRealm(p);
		realm.commitTransaction();
	}

	private void appendViewedStatus(Person p) {
		Person check = realm.where(Person.class).equalTo("id", p.getId()).findFirst();
		if (check != null)
			p.setViewed(check.getViewed());
	}

	private void checkRemovedStatus(Person p) {
		if (p.getStatus().equals("removed")) {
			Log.i(TAG, "updatePerson: Person with id=" + p.getId() + " got \"removed\" status");
			userList.remove(p);
		}
	}

	private void clearDatabase() {
		realm.beginTransaction();
		realm.clear(Person.class);
		realm.commitTransaction();
	}

	private Person getValidPerson() {
		while (getPersonsLeft() > 0) {
			int index = random.nextInt(getPersonsLeft());
			Person p = userList.remove(index);
			if (!"removed".equals(p.getStatus()))
				return p;
		}

		return null;
	}

	public void setPersonViewed(Person personViewed) {
		Log.i(TAG, "setPersonViewed: Marked person");
		realm.beginTransaction();
		personViewed.setViewed(1);
		realm.commitTransaction();
	}

	public ArrayList<Person> getUserList() {
		return userList;
	}

	public int getPersonsLeft() {
		return userList.size();
	}

	public void subscribe() {
		if (sub == null)
			sub = this.api.update().subscribe(this::updatePerson);
	}

	public void unsubscribe() {
		if (sub != null && !sub.isUnsubscribed())
			sub.unsubscribe();
	}

	public void yieldPerson() {
		Person p = getValidPerson();
		if (p != null) {
			Log.i(TAG, "yieldPerson: " + getPersonsLeft() + " persons left");
			bus.post(new GotPersonEvent(p));
		} else
			bus.post(new EmptyListEvent());
	}

	public void generate() {
		clearDatabase();
		api.generate()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						person -> {
							System.out.println("Person id: " + person.getId());
							initPerson(person);
						},
						Throwable::printStackTrace,
						() -> {
							bus.post(new PersonListUpdatedEvent());
							subscribe();
							System.out.println("List loaded, size: " + userList.size());
						}
				);
	}

	public void getNextPerson() {
		subscribe();
		Log.i(TAG, "getNextPerson: Persons left:" + getPersonsLeft());
		if (getPersonsLeft() == 0) {
			Log.i(TAG, "getNextPerson: No persons left, starting from the beginning...");
			//generate();
		} else
			this.yieldPerson();
	}
}

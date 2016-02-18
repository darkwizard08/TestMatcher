package no.kitttn.testmatcher;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.PersonsExtendedCallback;

import io.realm.Realm;
import no.kitttn.testmatcher.dagger2.components.ApplicationComponent;
import no.kitttn.testmatcher.dagger2.components.DaggerApplicationComponent;
import no.kitttn.testmatcher.dagger2.modules.DatabaseModule;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;

/**
 * @author kitttn
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
	private static final String TAG = "ApplicationTest";
	private interface Executor {
		void execute();
	}
	// TODO:
	// Subscription == DONE
	// If unsubscribed and trying again -> omitting == DONE
	// DB injection == DONE
	// handle updates on service == DONE
	// Broadcasting events == DONE

	public ApplicationTest() {
		super(Application.class);
	}

	public void testInjectionInGenerator() {
		UserGenerator gen = buildComponent().getUserGenerator();
		assertNotNull(gen.bus);
		assertNotNull(gen.realm);
		assertNotNull(gen.context);
		assertNotNull(gen.updater);
	}

	public void _testInitAPIAndUserUpdate () {
		initAPI(this::userUpdate);
	}

	public void testAppDBInjection() {
		UserGenerator gen = buildComponent().getUserGenerator();
		assertNotNull(gen.realm);
	}

	public void testAppEventBusInjection() {
		EventBus bus = buildComponent().getEventBus();

		assertNotNull(bus);
	}

	public void testPersonModel() {
		UserGenerator gen = buildComponent().getUserGenerator();
		Realm realm = gen.realm;
		erasePersonsDatabase(realm);

		String person = "{\"id\":1,\"location\":\"38.735227,-9.109606\",\"photo\":\"http://cs313217.vk.me/v313217800/436c/DO1w-2mKStQ.jpg\",\"status\":\"none\"}";
		gen.updatePerson(person);

		Person p = realm.where(Person.class).findFirst();

		assertEquals(realm.where(Person.class).count(), 1);
		assertEquals(p.getId(), 1);
	}

	public void testPersonErasure() {
		UserGenerator gen = buildComponent().getUserGenerator();
		String person = "{\"id\": 1}";

		gen.updatePerson(person);

		long totalRecords = gen.realm.where(Person.class).equalTo("id", 1).count();
		Log.w(TAG, "testPersonErasure: totalRecors:" + totalRecords);
		assertEquals(totalRecords, 1);

		erasePersonsDatabase(gen.realm);

		totalRecords = gen.realm.where(Person.class).count();
		Log.w(TAG, "testPersonErasure: " + totalRecords);
		assertEquals(totalRecords, 0);
	}

	public void _testDBGeneration() {
		UserGenerator gen = buildComponent().getUserGenerator();
		gen.generate();

		assertNotNull(gen.getUserList());
		assertTrue(gen.getUserList().size() > 0);
	}

	public void testMatcherInjection() {
		Matcher m = buildComponent().getMatcher();
		assertNotNull(m.generator);
	}

	public void testMatcherYieldsPerson() {
		Matcher m = buildComponent().getMatcher();
		Person p = m.getNextPerson();

		assertNotNull(p);
		assertNotSame(p.getStatus(), "removed");
	}

	public void testMatcherLikedPerson() {
		Matcher m = buildComponent().getMatcher();
		Person p = new Person();
		m.markLiked(p);

		assertEquals(m.likedPersons.size(), 1);
	}

	public void testMatcherDislikedPerson() {
		Matcher m = buildComponent().getMatcher();
		Person p = new Person();
		m.markDisliked(p);

		assertEquals(m.rejectedPersons.size(), 1);
	}

	public void testMatcherLikePersonStatusTriggersMatch() {
		Matcher m = buildComponent().getMatcher();
		Person p = new Person();
		p.setStatus("like");
		m.markLiked(p);

		assertEquals(m.checkCompatibility(p), true);
	}

	// ============= Private methods ================

	private ApplicationComponent buildComponent() {
		return DaggerApplicationComponent.builder()
				.databaseModule(new DatabaseModule(getContext()))
				.build();
	}

	private void erasePersonsDatabase(Realm realm) {
		realm.beginTransaction();
		realm.clear(Person.class);
		realm.commitTransaction();
	}

	private void initAPI(Executor executeAfterAPIInitialized) {
		API.INSTANCE.init(getContext());
		API.INSTANCE.refreshPersons(() -> {
			Log.w(TAG, "initAPI: Initialized!");
			executeAfterAPIInitialized.execute();
		});
	}

	private void userUpdate() {
		UserGenerator gen = buildComponent().getUserGenerator();
		erasePersonsDatabase(gen.realm);

		gen.subscribe();
		sleep();
		gen.unsubscribe();

		gen.realm.refresh();
		long personsUpdated = gen.realm.where(Person.class).count();
		Log.w(TAG, "testNotificationManagerUserUpdate: Persons updated:" + personsUpdated);
		assertTrue(personsUpdated > 0);
	}

	private void sleep() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
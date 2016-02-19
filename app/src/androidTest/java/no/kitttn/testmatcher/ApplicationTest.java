package no.kitttn.testmatcher;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import no.kitttn.testmatcher.dagger2.components.ApplicationComponent;
import no.kitttn.testmatcher.dagger2.components.DaggerApplicationComponent;
import no.kitttn.testmatcher.dagger2.modules.PersonBaseModule;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.presenters.GeneratorPresenter;

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
	}

	public void _testUserUpdate () {
		RxAPI api = buildComponent().getAPI();
		api.update().subscribe(person -> System.out.println("Person changed: " + person));

		sleep(8000);
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
		Person p = buildComponent().getGson().fromJson(person, Person.class);
		gen.updatePerson(p);

		p = realm.where(Person.class).findFirst();

		assertEquals(realm.where(Person.class).count(), 1);
		assertEquals(p.getId(), 1);
	}

	public void testPersonErasure() {
		UserGenerator gen = buildComponent().getUserGenerator();
		gen.updatePerson(new Person());

		long totalRecords = gen.realm.where(Person.class).equalTo("id", 1).count();
		Log.w(TAG, "testPersonErasure: totalRecors:" + totalRecords);
		assertEquals(totalRecords, 1);

		erasePersonsDatabase(gen.realm);

		totalRecords = gen.realm.where(Person.class).count();
		Log.w(TAG, "testPersonErasure: " + totalRecords);
		assertEquals(totalRecords, 0);
	}

	public void _testDBGeneration() {
		RxAPI api = buildComponent().getAPI();
		api.generate().subscribe(
				System.out::println,
				Throwable::printStackTrace,
				() -> {
					assertTrue(api.total > 0);
					Log.i(TAG, "testDBGeneration: Got total:" + api.total);
				}
		);

		sleep(13000);
	}

	public void testGeneratorPresenterInjection() {
		GeneratorPresenter pres = buildComponent().getGenPresenter();
		assertNotNull(pres);
	}

	public void testMatcherInjection() {
		Matcher m = buildComponent().getMatcher();
		assertNotNull(m.generator);
	}

	public void testMatcherYieldsPerson() {
		Matcher m = buildComponent().getMatcher();
		m.getNextPerson();
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
				.personBaseModule(new PersonBaseModule(getContext()))
				.build();
	}

	private void erasePersonsDatabase(Realm realm) {
		realm.beginTransaction();
		realm.clear(Person.class);
		realm.commitTransaction();
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
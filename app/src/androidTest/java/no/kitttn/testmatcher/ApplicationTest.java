package no.kitttn.testmatcher;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;
import no.kitttn.testmatcher.dagger2.components.ApplicationComponent;
import no.kitttn.testmatcher.dagger2.components.DaggerApplicationComponent;
import no.kitttn.testmatcher.dagger2.modules.DatabaseModule;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.TestEvent;

/**
 * @author kitttn
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
	private static final String TAG = "ApplicationTest";
	// TODO:
	// Subscription == DONE
	// If unsubscribed and trying again -> omitting == DONE
	// DB injection == DONE
	// handle updates on service

	// Broadcasting events == DONE, but check in real app
	public ApplicationTest() {
		super(Application.class);
		EventBus bus = buildComponent().getEventBus();
		bus.register(this);
	}

	public void testNotificatorSubscription() {
		NotificationManager nf = buildComponent().getManager();
		nf.subscribe();
		assertTrue(nf.isSubscribed());

		nf.unsubscribe();
		nf.unsubscribe();
		assertFalse(nf.isSubscribed());
	}

	public void testAppDBInjection() {
		NotificationManager mgr = buildComponent().getManager();

		assertNotNull(mgr.getRealm());
	}

	public void testAppEventBusInjection() {
		EventBus bus = buildComponent().getEventBus();

		assertNotNull(bus);
	}

	private ApplicationComponent buildComponent() {
		return DaggerApplicationComponent.builder()
				.databaseModule(new DatabaseModule(getContext()))
				.build();
	}

	public void testPersonModel() {
		NotificationManager mgr = buildComponent().getManager();

		Realm db = mgr.getRealm();
		db.beginTransaction();
		db.clear(Person.class);
		db.createOrUpdateObjectFromJson(Person.class, "{\"id\":1,\"location\":\"38.735227,-9.109606\",\"photo\":\"http://cs313217.vk.me/v313217800/436c/DO1w-2mKStQ.jpg\",\"status\":\"none\"}");
		db.commitTransaction();

		Person p = db.where(Person.class).findFirst();

		assertEquals(db.where(Person.class).count(), 1);
		assertEquals(p.getId(), 1);
	}

	public void testSubscription() {
		EventBus bus = buildComponent().getEventBus();
		bus.post(new TestEvent());
	}

	@Subscribe
	public void onTestEvent(TestEvent evt) {
		Log.i(TAG, "onTestEvent: Got message!");
		assertTrue(true);
	}
}
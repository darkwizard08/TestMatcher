package no.kitttn.testmatcher;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.testpackage.test_sdk.android.testlib.API;

import javax.inject.Inject;

import io.realm.Realm;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;

/**
 * @author kitttn
 * class for handling DB updates, and alerting user about matches and persons' movements
 */
public class NotificationManager {
	private static final String TAG = "NotificationManager";
	private boolean isSubscribed = false;
	@Inject protected Realm realm;
	@Inject protected EventBus bus;

	public void subscribe() {
		if (isSubscribed)
			return;
		isSubscribed = true;
		API.INSTANCE.subscribeUpdates(
				person -> {
					realm.createOrUpdateObjectFromJson(Person.class, person);
					bus.post(new PersonUpdatedEvent());
				}
		);
		Log.i(TAG, "subscribe: Subscribed!");
	}

	public void unsubscribe() {
		isSubscribed = false;
		API.INSTANCE.unSubscribeUpdates();
		Log.i(TAG, "unsubscribe: Unsubscribed!");
	}

	public boolean isSubscribed() {
		return isSubscribed;
	}

	public Realm getRealm() {
		return realm;
	}

	@Inject
	public NotificationManager() {}
}

package no.kitttn.testmatcher;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.PersonsExtendedCallback;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import no.kitttn.testmatcher.model.Person;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author kitttn
 */
public class RxAPI {
	private static final String TAG = "RxAPI";
	private Observable<Integer> idxs = Observable.just(0, 1);
	private Context ctx;
	private Gson gson;

	private ArrayList<Person> persons = new ArrayList<>();
	public int total = 0;

	class PersonUpdateListener implements Observable.OnSubscribe<Person> {
		private Subscriber<Person> subscriber;
		@Override
		public void call(Subscriber<? super Person> subscriber) {
			this.subscriber = (Subscriber<Person>) subscriber;
			update();
		}

		public void update() {
			API.INSTANCE.subscribeUpdates(person -> {
				Person p = updatePerson(person);
				subscriber.onNext(p);
			});
		}

		public Person updatePerson(String personJson) {
			return gson.fromJson(personJson, Person.class);
		}
	}

	private void processList(String personsString) {
		System.out.println("Current thread: " + Thread.currentThread().getName());
		persons.clear();

		Person[] updated = gson.fromJson(personsString, Person[].class);
		persons.addAll(Arrays.asList(updated));

		total += updated.length;
		Log.i(TAG, "processList: New length:" + persons.size());
	}

	public Observable<Person> update() {
		return Observable.defer(() -> Observable.create(new PersonUpdateListener()))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	public Observable<Person> generate() {
		API api = API.INSTANCE;
		return idxs.doOnSubscribe(
				() -> {
					api.init(ctx);
					api.refreshPersons(() -> System.out.println("Refreshed DB!"));
				})
				//.doOnSubscribe(() -> API.INSTANCE.refreshPersons(() -> {}))
				.subscribeOn(Schedulers.io())
				.doOnNext(it -> API.INSTANCE.getPersons(it, new PersonsExtendedCallback() {
					@Override
					public void onResult(String persons) {
						processList(persons);
					}

					@Override
					public void onFail(String reason) {
						Log.i(TAG, "onFail: Error occured :( " + reason);
					}
				}))
				.flatMap(integer -> Observable.from(persons));
	}

	@Inject
	public RxAPI(Context ctx, Gson serializer) {
		this.ctx = ctx;
		this.gson = serializer;
	}
}

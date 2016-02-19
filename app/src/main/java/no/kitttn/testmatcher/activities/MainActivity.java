package no.kitttn.testmatcher.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import no.kitttn.testmatcher.App;
import no.kitttn.testmatcher.R;
import no.kitttn.testmatcher.UserGenerator;
import no.kitttn.testmatcher.fragments.GenerateFragment;
import no.kitttn.testmatcher.model.Person;

/**
 * @author kitttn
 */
public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	@Inject protected Realm realm;
	@Inject protected UserGenerator generator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((App) getApplication()).getComponent().inject(this);

		if (needToGenerateData())
			startGenerator();
		else startMatcher();
	}

	private boolean needToGenerateData() {
		RealmResults<Person> notViewed = realm
				.where(Person.class)
				.equalTo("viewed", 0)
				.notEqualTo("status", "removed")
				.findAll();

		generator.getUserList().clear();
		Log.i(TAG, "needToGenerateData: Not viewed:" + notViewed.size());
		if (notViewed.size() == 0)
			return true;

		// TODO: write a method aggregation?
		for (Person p : notViewed)
			generator.getUserList().add(p);

		return false;
	}

	private void startGenerator() {
		getFragmentManager().beginTransaction()
				.add(R.id.root, new GenerateFragment())
				.commit();
	}

	private void startMatcher() {
		Intent i = new Intent(this, MatcherActivity.class);
		startActivity(i);
		finish();
	}
}

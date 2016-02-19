package no.kitttn.testmatcher.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.test.mock.MockApplication;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMapOptions;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.kitttn.testmatcher.App;
import no.kitttn.testmatcher.R;
import no.kitttn.testmatcher.fragments.LocationFragment;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.presenters.MatcherPresenter;
import no.kitttn.testmatcher.utils.Util;
import no.kitttn.testmatcher.views.MatcherView;

/**
 * @author kitttn
 */
public class MatcherActivity extends Activity implements MatcherView {
	private static final String TAG = "MatcherActivity";

	@Inject MatcherPresenter presenter;

	@Bind(R.id.actMatcherPhotoImg)
	ImageView photoImg;
	@Bind(R.id.actMatcherLikeBtn)
	Button likeBtn;
	@Bind(R.id.actMatcherDislikeBtn)
	Button dislikeBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matcher);
		ButterKnife.bind(this);

		getFragmentManager()
				.beginTransaction()
				.add(R.id.actMatcherMapContainer, new LocationFragment())
				.commit();

		likeBtn.setOnClickListener(view -> like());
		dislikeBtn.setOnClickListener(view -> dislike());

		((App) getApplication()).getComponent().inject(this);
		presenter.setView(this);
		presenter.getNextPerson();
	}

	@Override
	protected void onDestroy() {
		presenter.destroy();
		super.onDestroy();
	}

	@Override
	public void loading() {
		likeBtn.setEnabled(false);
		likeBtn.setEnabled(false);
		Util.showLoading(this);
	}

	@Override
	public void stopLoading() {
		Util.hideLoading();
	}

	@Override
	public void showProfile(Person person) {
		Log.i(TAG, "showProfile: Loading " + person + "...");
		Picasso.with(this).load(person.getPhoto()).into(photoImg);
		likeBtn.setEnabled(true);
		dislikeBtn.setEnabled(true);
	}

	@Override
	public void like() {
		presenter.likePerson();
	}

	@Override
	public void dislike() {
		presenter.dislikePerson();
	}

	@Override
	public void notice(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void end() {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void matchNotification() {
		NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification not = new Notification.Builder(this)
				.setSmallIcon(R.drawable.placeholder)
				.setContentTitle("Wow, MATCH!")
				.setContentText("You've got a match!")
				.setVibrate(new long[] {100, 250, 100, 250})
				.build();

		mgr.notify(99, not);
	}
}

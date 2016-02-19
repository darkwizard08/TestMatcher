package no.kitttn.testmatcher.activities;

import android.app.Activity;
import android.os.Bundle;
import android.test.mock.MockApplication;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMapOptions;

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
	private Person activePerson;

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
				.add(R.id.actMatcherMapContainer, LocationFragment.newInstance(getMapOptions()))
				.commit();

		likeBtn.setOnClickListener(view -> like());
		dislikeBtn.setOnClickListener(view -> dislike());

		((App) getApplication()).getComponent().inject(this);
		presenter.setView(this);
		presenter.getNextPerson();
	}

	private GoogleMapOptions getMapOptions() {
		return new GoogleMapOptions()
				.liteMode(true)
				.mapToolbarEnabled(false);
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
		activePerson = person;
		likeBtn.setEnabled(true);
		dislikeBtn.setEnabled(true);
	}

	@Override
	public void like() {
		presenter.likePerson(activePerson);
	}

	@Override
	public void dislike() {
		presenter.dislikePerson(activePerson);
	}
}

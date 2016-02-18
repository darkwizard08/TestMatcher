package no.kitttn.testmatcher.activities;

import android.app.Activity;
import android.os.Bundle;

import no.kitttn.testmatcher.R;

/**
 * @author kitttn
 */
public class MatcherActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matcher);
	}
}

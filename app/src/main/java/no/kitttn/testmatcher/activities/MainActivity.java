package no.kitttn.testmatcher.activities;

import android.app.Activity;
import android.os.Bundle;

import no.kitttn.testmatcher.R;
import no.kitttn.testmatcher.fragments.GenerateFragment;

/**
 * @author kitttn
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getFragmentManager().beginTransaction()
				.add(R.id.root, new GenerateFragment())
				.commit();
	}
}

package no.kitttn.testmatcher.fragments;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * @author kitttn
 */
public class LocationFragment extends MapFragment implements OnMapReadyCallback {
	private static final String TAG = "LocationFragment";
	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.i(TAG, "onMapReady: Map loaded, ready to show locations!");
	}
}

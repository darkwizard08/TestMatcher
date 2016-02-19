package no.kitttn.testmatcher.fragments;

import android.os.Bundle;
import android.test.mock.MockApplication;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import no.kitttn.testmatcher.App;
import no.kitttn.testmatcher.R;
import no.kitttn.testmatcher.model.Person;
import no.kitttn.testmatcher.model.events.PersonUpdatedEvent;
import no.kitttn.testmatcher.presenters.MatcherPresenter;
import no.kitttn.testmatcher.views.MapView;

/**
 * @author kitttn
 */
public class LocationFragment extends MapFragment implements OnMapReadyCallback, MapView {
	private static final String TAG = "LocationFragment";
	private GoogleMap map;
	Marker marker;

	CameraPosition position = CameraPosition.fromLatLngZoom(new LatLng(39, 14), 2);
	@Inject protected MatcherPresenter presenter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((App) getActivity().getApplication()).getComponent().inject(this);
		getMapAsync(this);
		presenter.setMapView(this);
		Log.i(TAG, "onCreate: Injected!");
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		marker = map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapindicator))
				.anchor(0.0f, 1.0f)
				.position(new LatLng(39, 14)));
	}

	private LatLng fromString(String coords) {
		String[] latlong = coords.split(",");
		return new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
	}

	@Override
	public void showPerson(Person p) {
		if (map == null) {
			System.out.println("Map is null...");
			return;
		}

		Log.i(TAG, "onPersonUpdated: Person position:" + p.getLocation());
		LatLng coords = fromString(p.getLocation());
		position = CameraPosition.fromLatLngZoom(coords, 9);
		map.moveCamera(CameraUpdateFactory.newCameraPosition(position));

		marker.setPosition(coords);
	}
}

package no.kitttn.testmatcher;

import android.app.Application;

import io.realm.RealmConfiguration;
import no.kitttn.testmatcher.dagger2.components.ApplicationComponent;
import no.kitttn.testmatcher.dagger2.components.DaggerApplicationComponent;
import no.kitttn.testmatcher.dagger2.modules.PersonBaseModule;

/**
 * @author kitttn
 */
public class App extends Application {
	private ApplicationComponent component;

	@Override
	public void onCreate() {
		super.onCreate();

		component = DaggerApplicationComponent.builder()
				.personBaseModule(new PersonBaseModule(this))
				.build();
	}

	public ApplicationComponent getComponent() {
		return component;
	}
}

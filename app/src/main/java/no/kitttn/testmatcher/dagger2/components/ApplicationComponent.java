package no.kitttn.testmatcher.dagger2.components;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import no.kitttn.testmatcher.Matcher;
import no.kitttn.testmatcher.RxAPI;
import no.kitttn.testmatcher.UserGenerator;
import no.kitttn.testmatcher.activities.MainActivity;
import no.kitttn.testmatcher.activities.MatcherActivity;
import no.kitttn.testmatcher.dagger2.modules.PersonBaseModule;
import no.kitttn.testmatcher.dagger2.modules.EventBusModule;
import no.kitttn.testmatcher.dagger2.modules.PresentersModule;
import no.kitttn.testmatcher.fragments.GenerateFragment;
import no.kitttn.testmatcher.fragments.LocationFragment;
import no.kitttn.testmatcher.presenters.GeneratorPresenter;

/**
 * @author kitttn
 */
@Singleton
@Component(modules = {
		PersonBaseModule.class, EventBusModule.class, PresentersModule.class
})
public interface ApplicationComponent {
	UserGenerator getUserGenerator();
	Matcher getMatcher();
	EventBus getEventBus();
	GeneratorPresenter getGenPresenter();
	RxAPI getAPI();
	Gson getGson();

	void inject(LocationFragment fragment);
	void inject(GenerateFragment fragment);
	void inject(MatcherActivity activity);
	void inject(MainActivity activity);
}

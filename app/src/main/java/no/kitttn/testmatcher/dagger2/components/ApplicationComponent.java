package no.kitttn.testmatcher.dagger2.components;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import no.kitttn.testmatcher.Matcher;
import no.kitttn.testmatcher.UserGenerator;
import no.kitttn.testmatcher.dagger2.modules.PersonBaseModule;
import no.kitttn.testmatcher.dagger2.modules.EventBusModule;
import no.kitttn.testmatcher.dagger2.modules.PresentersModule;
import no.kitttn.testmatcher.fragments.GenerateFragment;
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

	void inject(UserGenerator generator);
	void inject(Matcher matcher);

	void inject(GeneratorPresenter presenter);
	void inject(GenerateFragment fragment);
}

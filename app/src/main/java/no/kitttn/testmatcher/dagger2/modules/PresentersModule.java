package no.kitttn.testmatcher.dagger2.modules;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import no.kitttn.testmatcher.UserGenerator;
import no.kitttn.testmatcher.presenters.GeneratorPresenter;

/**
 * @author kitttn
 */
@Module
public class PresentersModule {
	@Provides
	public GeneratorPresenter provideGeneratorPresenter(EventBus bus, UserGenerator generator) {
		return new GeneratorPresenter(bus, generator);
	}
}

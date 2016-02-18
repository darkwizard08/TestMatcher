package no.kitttn.testmatcher.dagger2.modules;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author kitttn
 */
@Module
public class EventBusModule {
	@Provides @Singleton
	public EventBus provideEventBus() {
		return new EventBus();
	}
}

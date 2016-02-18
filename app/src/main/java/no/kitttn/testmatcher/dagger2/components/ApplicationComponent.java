package no.kitttn.testmatcher.dagger2.components;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import no.kitttn.testmatcher.UserGenerator;
import no.kitttn.testmatcher.dagger2.modules.DatabaseModule;
import no.kitttn.testmatcher.dagger2.modules.EventBusModule;

/**
 * @author kitttn
 */
@Singleton
@Component(modules = {
		DatabaseModule.class, EventBusModule.class
})
public interface ApplicationComponent {
	UserGenerator getUserGenerator();
	EventBus getEventBus();
}

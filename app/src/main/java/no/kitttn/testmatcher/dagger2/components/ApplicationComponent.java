package no.kitttn.testmatcher.dagger2.components;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import no.kitttn.testmatcher.dagger2.modules.DatabaseModule;
import no.kitttn.testmatcher.NotificationManager;
import no.kitttn.testmatcher.dagger2.modules.EventBusModule;

/**
 * @author kitttn
 */
@Singleton
@Component(modules = {
		DatabaseModule.class, EventBusModule.class
})
public interface ApplicationComponent {
	NotificationManager getManager();
	EventBus getEventBus();
}

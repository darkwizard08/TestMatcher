package no.kitttn.testmatcher.dagger2.modules;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import no.kitttn.testmatcher.Matcher;
import no.kitttn.testmatcher.UserGenerator;

/**
 * @author kitttn
 */
@Module
public class PersonBaseModule {
	private Context ctx;
	public PersonBaseModule(Context ctx) {
		this.ctx = ctx;
	}

	@Provides
	public Realm provideRealm() {
		Realm r = Realm.getInstance(ctx);
		r.setAutoRefresh(true);
		return r;
	}

	@Provides
	public Context provideContext() {
		return ctx;
	}

	@Provides
	public UserGenerator provideUserGenerator(EventBus bus, Realm realm, Context ctx) {
		return new UserGenerator(bus, realm, ctx);
	}

	@Provides
	public Matcher provideMatcher() {
		return new Matcher();
	}
}

package no.kitttn.testmatcher.dagger2.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * @author kitttn
 */
@Module
public class DatabaseModule {
	private Context ctx;
	public DatabaseModule(Context ctx) {
		this.ctx = ctx;
	}

	@Provides
	public Realm provideRealm() {
		Realm r = Realm.getInstance(ctx);
		r.setAutoRefresh(true);
		return r;
	}

	@Provides
	public Context getContext() {
		return ctx;
	}
}

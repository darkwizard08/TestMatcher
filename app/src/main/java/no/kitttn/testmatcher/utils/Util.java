package no.kitttn.testmatcher.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author kitttn
 */
public class Util {
	private static ProgressDialog lastProgressDialog = null;
	public static void showLoading(Context context) {
		lastProgressDialog = ProgressDialog.show(context, "Loading", "Gathering data...", true);
	}

	public static void hideLoading() {
		if (lastProgressDialog != null)
			lastProgressDialog.dismiss();
	}
}

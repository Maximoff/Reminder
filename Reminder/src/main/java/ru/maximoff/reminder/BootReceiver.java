package ru.maximoff.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
			&& !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            return;
        }
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (preferences.getBoolean("enable_service", true)) {
			Intent service = new Intent(context, UnlockService.class);
			if (Build.VERSION.SDK_INT >= 26) {
				context.startForegroundService(service);
			} else {
				context.startService(service);
			}
		}
    }
}

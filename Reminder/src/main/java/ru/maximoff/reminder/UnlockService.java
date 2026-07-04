package ru.maximoff.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class UnlockService extends Service {
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "unlock_service";
    private BroadcastReceiver unlockReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundIfNeeded();
        unlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
				final Context ctx = getApplicationContext();
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
                if (Intent.ACTION_USER_PRESENT.equals(intent.getAction()) && canLaunch(preferences)) {
					String text = preferences.getString("remind_text", "");
					if (preferences.getBoolean("dialog_remind", true)) {
						if (Utils.canDrawOverlay(ctx)) {
							floatingWindow(text);
						} else {
							Utils.st(ctx, R.string.permission_need);
						}
					} else {
						Utils.st(ctx, text);
					}
					preferences.edit().putLong("remind_time", System.currentTimeMillis()).commit();
                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (unlockReceiver != null) {
            unregisterReceiver(unlockReceiver);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

	private void floatingWindow(String text) {
		final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		final ContextThemeWrapper ctx = new ContextThemeWrapper(this, R.style.OverlayDialogTheme);
		final View view = LayoutInflater.from(ctx).inflate(R.layout.dialog, null);
		TextView textView = view.findViewById(R.id.dialogTextView1);
		textView.setText(text);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams((int) (getResources().getDisplayMetrics().widthPixels * 0.85),
			WindowManager.LayoutParams.WRAP_CONTENT,
			Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			PixelFormat.TRANSLUCENT
        );
		params.gravity = Gravity.CENTER;
		wm.addView(view, params);
		Button settings = view.findViewById(R.id.dialogButton1);
		settings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent i = new Intent(UnlockService.this, MainActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(i);
						wm.removeView(view);
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			});
		Button close = view.findViewById(R.id.dialogButton2);
		close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						wm.removeView(view);
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			});
	}

    private void startForegroundIfNeeded() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getText(R.string.channel_name), NotificationManager.IMPORTANCE_LOW);
        nm.createNotificationChannel(channel);
		Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(android.net.Uri.parse("package:" + getPackageName()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
			.setContentTitle(getText(R.string.notification_title))
			.setContentText(getText(R.string.notification_text))
			.setSmallIcon(android.R.drawable.ic_lock_idle_lock)
			.setContentIntent(pendingIntent)
			.setOngoing(true)
			.build();
        startForeground(NOTIFICATION_ID, notification);
    }

	private boolean canLaunch(SharedPreferences preferences) {
		int hoursLimit = preferences.getInt("hours_limit", 0);
		long lastTime = preferences.getLong("remind_time", 0L);
		if (hoursLimit == 0 || lastTime == 0L) {
			return true;
		}
		long now = System.currentTimeMillis();
		long diff = now - lastTime;
		long limit = hoursLimit * 60L * 60L * 1000L;
		return diff >= limit;
	}
}

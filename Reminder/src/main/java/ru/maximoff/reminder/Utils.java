package ru.maximoff.reminder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ru.maximoff.reminder.R;

public class Utils {
	public static void setHyperlinkText(TextView tv, String text) {
		final SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(spannableString, TextView.BufferType.SPANNABLE);
		tv.setClickable(true);
	}

	public static boolean canDrawOverlay(Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		return Settings.canDrawOverlays(context);
	}

	public static void batteryOptimization(Context context) {
		if (Build.VERSION.SDK_INT < 23) {
			return;
		}
		final String packageName = context.getPackageName();
		final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (!pm.isIgnoringBatteryOptimizations(packageName)) {
			try {
				Intent intent = new Intent();
				intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + packageName));
				context.startActivity(intent);
			} catch (Exception e) {
				Utils.st(context, R.string.error);
			}
		}
	}

	public static int getColor(Context ctx, int color) {
		if (Build.VERSION.SDK_INT >= 23) {
			return ctx.getColor(color);
		}
		return ctx.getResources().getColor(color);
	}

	public static void st(Context ctx, String text, int duration) {
		try {
			final Drawable background = ctx.getResources().getDrawable(R.drawable.dark_background);
			final View layout = LayoutInflater.from(ctx).inflate(R.layout.toast, null);
			layout.setBackground(background);
			TextView toastMessage = layout.findViewById(R.id.toastTextView1);
			toastMessage.setTextColor(Utils.getColor(ctx, R.color.text_dark));
			toastMessage.setText(text);
			Toast toast = new Toast(ctx);
			toast.setDuration(duration);
			toast.setView(layout);
			toast.show();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void st(Context ctx, String text) {
		try {
			Utils.st(ctx, text, Toast.LENGTH_LONG);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void st(Context ctx, int res) {
		try {
			Utils.st(ctx, ctx.getString(res), Toast.LENGTH_LONG);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
}

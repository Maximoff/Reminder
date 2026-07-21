package ru.maximoff.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ru.maximoff.reminder.R;
import ru.maximoff.reminder.Utils;

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
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				int fontColor = preferences.getInt("font_color", Utils.getColor(context, R.color.text_dark));
				int bgColor = preferences.getInt("bg_color", Utils.getColor(context, R.color.bg_dark));
				boolean bold = preferences.getBoolean("font_bold", false);
				boolean cursive = preferences.getBoolean("font_cursive", false);
				Utils.st(context, R.string.error, fontColor, bgColor, bold, cursive);
			}
		}
	}

	public static int getColor(Context ctx, int color) {
		if (Build.VERSION.SDK_INT >= 23) {
			return ctx.getColor(color);
		}
		return ctx.getResources().getColor(color);
	}

	public static void st(Context ctx, String text, int duration, int fontColor, int bgColor, boolean bold, boolean cursive) {
		try {
			final LayerDrawable bgDrawable = (LayerDrawable) ctx.getResources().getDrawable(R.drawable.dark_background).mutate();
			GradientDrawable bg = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.content);
			bg.setColor(bgColor);
			final View layout = LayoutInflater.from(ctx).inflate(R.layout.toast, null);
			layout.setBackground(bgDrawable);
			TextView toastMessage = layout.findViewById(R.id.toastTextView1);
			toastMessage.setTextColor(fontColor);
			toastMessage.setText(text);
			int style;
			if (bold) {
				style = cursive ? Typeface.BOLD_ITALIC : Typeface.BOLD;
			} else {
				style = cursive ? Typeface.ITALIC : Typeface.NORMAL;
			}
			toastMessage.setTypeface(null, style);
			Toast toast = new Toast(ctx);
			toast.setDuration(duration);
			toast.setView(layout);
			toast.show();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void st(Context ctx, String text, int fontColor, int bgColor, boolean bold, boolean cursive) {
		try {
			Utils.st(ctx, text, Toast.LENGTH_LONG, fontColor, bgColor, bold, cursive);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static void st(Context ctx, int res, int fontColor, int bgColor, boolean bold, boolean cursive) {
		try {
			Utils.st(ctx, ctx.getString(res), Toast.LENGTH_LONG, fontColor, bgColor, bold, cursive);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static boolean isDarkColor(int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int brightness = (r * 299 + g * 587 + b * 114) / 1000;
		return brightness < 128;
	}

	public static int dp(Context context, int value) {
		return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	public static Drawable createButtonBackground(Context context, int bgColor) {
		boolean dark = isDarkColor(bgColor);
		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(dp(context, 5));
		if (dark) {
			drawable.setColor(Color.argb(45, 255, 255, 255));
			drawable.setStroke(dp(context, 1), Color.argb(80, 255, 255, 255));
		} else {
			drawable.setColor(Color.argb(35, 0, 0, 0));
			drawable.setStroke(dp(context, 1), Color.argb(70, 0, 0, 0));
		}
		return drawable;
	}
}

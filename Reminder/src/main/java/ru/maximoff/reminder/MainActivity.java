package ru.maximoff.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import ru.maximoff.color.Picker;
import ru.maximoff.reminder.R;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		initLayout();
    }

	private void initLayout() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final Picker colorPicker = new Picker(this);
		final Switch enableService = findViewById(R.id.mainSwitch1);
		final RadioButton radioDialog = findViewById(R.id.radioDialog);
		final RadioButton radioToast = findViewById(R.id.radioToast);
		final EditText editText = findViewById(R.id.mainEditText1);
		final Spinner hoursSpinner = findViewById(R.id.mainSpinner1);
		final Button saveButton = findViewById(R.id.mainButton1);
		final ImageView fontColorView = findViewById(R.id.colorPreview1);
		final ImageView bgColorView = findViewById(R.id.colorPreview2);
		final CheckBox boldFont = findViewById(R.id.mainCheckBox1);
		final CheckBox cursiveFont = findViewById(R.id.mainCheckBox2);
		final LinearLayout fontColorItem = findViewById(R.id.mainLinearLayout1);
		final LinearLayout bgColorItem = findViewById(R.id.mainLinearLayout2);
		final GradientDrawable fontColorPreview = (GradientDrawable) getDrawable(R.drawable.color_preview).mutate();
		final GradientDrawable bgColorPreview = (GradientDrawable) getDrawable(R.drawable.color_preview).mutate();
		boldFont.setChecked(preferences.getBoolean("font_bold", false));
		cursiveFont.setChecked(preferences.getBoolean("font_cursive", false));
		final int defaultFontColor = Utils.getColor(this, R.color.text_dark);
		final int defaultBgColor = Utils.getColor(this, R.color.bg_dark);
		final int[] fontColor = {preferences.getInt("font_color", defaultFontColor)};
		final int[] bgColor = {preferences.getInt("bg_color", defaultBgColor)};
		fontColorPreview.setColor(fontColor[0]);
		bgColorPreview.setColor(bgColor[0]);
		fontColorView.setImageDrawable(fontColorPreview);
		bgColorView.setImageDrawable(bgColorPreview);
		colorPicker.setOnColorSelect(new Picker.OnColorSelect() {
				@Override
				public void select(String hexColor, int resId) {
					// ignore
				}

				@Override
				public void select(int intColor, int resId) {
					switch (resId) {
						case R.id.mainLinearLayout1:
							fontColor[0] = intColor;
							fontColorPreview.setColor(fontColor[0]);
							fontColorView.setImageDrawable(fontColorPreview);
							break;
							
						case R.id.mainLinearLayout2:
							bgColor[0] = intColor;
							bgColorPreview.setColor(bgColor[0]);
							bgColorView.setImageDrawable(bgColorPreview);
							break;
					}
				}

				@Override
				public void selectSmali(String smaliColor, int resId) {
					// ignore
				}

				@Override
				public void cancel(int resId) {
					// ignore
				}
			});
		final OnClickListener colorClick = new OnClickListener() {
			@Override
			public void onClick(View p1) {
				String title = null;
				int selectedColor = Color.BLACK;
				switch (p1.getId()) {
					case R.id.mainLinearLayout1:
						selectedColor = fontColor[0];
						title = getString(R.string.font_color);
						break;

					case R.id.mainLinearLayout2:
						selectedColor = bgColor[0];
						title = getString(R.string.bg_color);
						break;
				}
				colorPicker.setColor(selectedColor);
				colorPicker.setTitle(title);
				colorPicker.show(p1.getId());
			}
		};
		final OnLongClickListener colorLongClick = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View p1) {
				switch (p1.getId()) {
					case R.id.mainLinearLayout1:
						fontColor[0] = defaultFontColor;
						fontColorPreview.setColor(fontColor[0]);
						fontColorView.setImageDrawable(fontColorPreview);
						return true;

					case R.id.mainLinearLayout2:
						bgColor[0] = defaultBgColor;
						bgColorPreview.setColor(bgColor[0]);
						bgColorView.setImageDrawable(bgColorPreview);
						return true;
				}
				return false;
			}
		};
		fontColorItem.setOnClickListener(colorClick);
		bgColorItem.setOnClickListener(colorClick);
		fontColorItem.setOnLongClickListener(colorLongClick);
		bgColorItem.setOnLongClickListener(colorLongClick);
		boolean enable = preferences.getBoolean("enable_service", true);
		if (enable) {
			startService();
		} else {
			stopService();
		}
		if (preferences.getBoolean("dialog_remind", true)) {
			radioDialog.setChecked(true);
		} else {
			radioToast.setChecked(true);
		}
		enableService.setChecked(enable);
		editText.setText(preferences.getString("remind_text", ""));
		final String[] hoursArray = new String[25];
		hoursArray[0] = "---";
		for (int i = 1; i < 25; i++) {
			hoursArray[i] = getResources().getQuantityString(R.plurals.hours, i, i);
		}
		ArrayAdapter<String> hoursAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, hoursArray);
		hoursSpinner.setAdapter(hoursAdapter);
		hoursSpinner.setSelection(preferences.getInt("hours_limit", 0));
		saveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					SharedPreferences.Editor editor = preferences.edit();
					boolean enable = enableService.isChecked();
					editor.putBoolean("enable_service", enable)
						.putBoolean("dialog_remind", radioDialog.isChecked())
						.putBoolean("font_bold", boldFont.isChecked())
						.putBoolean("font_cursive", cursiveFont.isChecked())
						.putString("remind_text", editText.getText().toString())
						.putInt("font_color", fontColor[0])
						.putInt("bg_color", bgColor[0])
						.putInt("hours_limit", hoursSpinner.getSelectedItemPosition())
						.commit();
					if (enable) {
						startService();
					} else {
						stopService();
					}
					MainActivity.this.finish();
				}
			});

		final TextView copyright = findViewById(R.id.mainTextView1);
		copyright.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					Intent open = new Intent(Intent.ACTION_VIEW);
					open.setData(Uri.parse("https://yoomoney.ru/to/410013008761175"));
					startActivity(open);
				}
			});
		Utils.setHyperlinkText(copyright, "© Maximoff, 2026");
		Utils.batteryOptimization(this);
		if (!Utils.canDrawOverlay(this)) {
			AlertDialog dialog = new AlertDialog.Builder(this)
				.setMessage(R.string.permission_need)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1, int p2) {
						p1.cancel();
						Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
						startActivity(intent);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1, int p2) {
						p1.cancel();
						finish();
					}
				})
				.setCancelable(false)
				.create();
			dialog.show();
		}
	}

	private void startService() {
		Intent service = new Intent(this, UnlockService.class);
		if (Build.VERSION.SDK_INT >= 26) {
			startForegroundService(service);
		} else {
			startService(service);
		}
	}

	private void stopService() {
		Intent service = new Intent(this, UnlockService.class);
		stopService(service);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}

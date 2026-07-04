package ru.maximoff.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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
		final Switch enableService = findViewById(R.id.mainSwitch1);
		final RadioButton radioDialog = findViewById(R.id.radioDialog);
		final RadioButton radioToast = findViewById(R.id.radioToast);
		final EditText editText = findViewById(R.id.mainEditText1);
		final Spinner hoursSpinner = findViewById(R.id.mainSpinner1);
		final Button saveButton = findViewById(R.id.mainButton1);
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
					editor.putBoolean("enable_service", enable).putBoolean("dialog_remind", radioDialog.isChecked()).putString("remind_text", editText.getText().toString()).putInt("hours_limit", hoursSpinner.getSelectedItemPosition()).commit();
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

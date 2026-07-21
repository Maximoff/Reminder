package ru.maximoff.color;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import ru.maximoff.reminder.R;

public class Picker {
	private OnColorSelect selectListener;
	private String title;
	private int resourceId;
	private int selectedColor;
	private String smaliColor;
	private boolean showSmali;
	private Context context;

	public Picker(Context ctx) {
		this.context = ctx;
		this.title = null;
		this.selectedColor = Color.BLACK;
		this.smaliColor = hexToSmali(String.format("#%08x", (0xFFFFFFFF & selectedColor)));
		this.showSmali = false;
	}

	public Picker setColor(String hexColor) {
		try {
			this.selectedColor = Color.parseColor(hexColor);
		} catch (Exception e) {}
		return this;
	}

	public Picker setColor(int color) {
		this.selectedColor = color;
		return this;
	}

	public Picker setOnColorSelect(OnColorSelect listener) {
		this.selectListener = listener;
		return this;
	}

	public Picker setTitle(String str) {
		this.title = str;
		return this;
	}

	public Picker showSmali() {
		this.showSmali = true;
		return this;
	}

	public String smaliToHex(String smali) {
		String pm = "";
		if (smali.startsWith("-")) {
			smali = smali.substring(1);
			pm = "-";
		}
		if (smali.startsWith("0x")) {
			smali = smali.substring(2);
		}
		int color = Integer.parseInt(pm + smali, 16);
		return String.format("#%08x", (0xFFFFFFFF & color));
    }

    public String hexToSmali(String hex) {
		if (!hex.startsWith("#")) {
			hex = "#" + hex;
		}
		int color = Color.parseColor(hex);
		int alpha = Color.alpha(color);
		String smali;
		if (alpha >= 128) {
			smali = "-0x" + Integer.toHexString(color * -1);
		} else {
			smali = "0x" + Integer.toHexString(color);
		}
		return smali;
    }

	public void show(int resId) {
		this.resourceId = resId;
		String hexDefault = String.format("#%08x", (0xFFFFFFFF & selectedColor));
		String smaliDefault = hexToSmali(hexDefault);
		View view = LayoutInflater.from(context).inflate(R.layout.maximoff_picker, null);
		final ImageView preview = view.findViewById(R.id.pickerImageView1);
		preview.setImageDrawable(new ColorDrawable(selectedColor));

		final SeekBar alphaBar = view.findViewById(R.id.pickerSeekBar1);
		final SeekBar redBar = view.findViewById(R.id.pickerSeekBar2);
		final SeekBar greenBar = view.findViewById(R.id.pickerSeekBar3);
		final SeekBar blueBar = view.findViewById(R.id.pickerSeekBar4);

		final TextView alphaView = view.findViewById(R.id.pickerTextView1);
		final TextView redView = view.findViewById(R.id.pickerTextView2);
		final TextView greenView = view.findViewById(R.id.pickerTextView3);
		final TextView blueView = view.findViewById(R.id.pickerTextView4);

		final EditText hexValue = view.findViewById(R.id.pickerEditText1);
		final EditText smaliValue = view.findViewById(R.id.pickerEditText2);
		smaliValue.setVisibility(showSmali ? View.VISIBLE : View.GONE);
		hexValue.setText(hexDefault);
		hexValue.setHint(hexDefault);
		hexValue.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

				}

				@Override
				public void afterTextChanged(Editable p1) {
					if (!hexValue.isFocused() || p1.length() == 0) {
						return;
					}
					try {
						String str = p1.toString();
						if (!str.startsWith("#")) {
							str = "#" + str;
						}
						selectedColor = Color.parseColor(str);
						smaliColor = hexToSmali(String.format("#%08x", (0xFFFFFFFF & selectedColor)));
						smaliValue.setText(smaliColor);
						alphaBar.setProgress(Color.alpha(selectedColor));
						redBar.setProgress(Color.red(selectedColor));
						greenBar.setProgress(Color.green(selectedColor));
						blueBar.setProgress(Color.blue(selectedColor));
					} catch (Exception e) {}
				}
			});

		smaliValue.setText(smaliDefault);
		smaliValue.setHint(smaliDefault);
		smaliValue.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

				}

				@Override
				public void afterTextChanged(Editable p1) {
					if (!smaliValue.isFocused() || p1.length() == 0) {
						return;
					}
					try {
						smaliColor = p1.toString();
						String hex = smaliToHex(smaliColor);
						selectedColor = Color.parseColor(hex);
						hexValue.setText(hex);
						alphaBar.setProgress(Color.alpha(selectedColor));
						redBar.setProgress(Color.red(selectedColor));
						greenBar.setProgress(Color.green(selectedColor));
						blueBar.setProgress(Color.blue(selectedColor));
					} catch (Exception e) {}
				}
			});

		final SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
			private boolean touched = false;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String tag = seekBar.getTag().toString().toUpperCase();
				switch (tag.charAt(0)) {
					case 'A':
						selectedColor = Color.argb(progress, Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor));
						alphaView.setText(String.valueOf(progress));
						break;

					case 'R':
						selectedColor = Color.argb(Color.alpha(selectedColor), progress, Color.green(selectedColor), Color.blue(selectedColor));
						redView.setText(String.valueOf(progress));
						break;

					case 'G':
						selectedColor = Color.argb(Color.alpha(selectedColor), Color.red(selectedColor), progress, Color.blue(selectedColor));
						greenView.setText(String.valueOf(progress));
						break;

					case 'B':
						selectedColor = Color.argb(Color.alpha(selectedColor), Color.red(selectedColor), Color.green(selectedColor), progress);
						blueView.setText(String.valueOf(progress));
						break;
				}
				smaliColor = hexToSmali(String.format("#%08x", (0xFFFFFFFF & selectedColor)));
				preview.setImageDrawable(new ColorDrawable(selectedColor));
				if (touched) {
					String hexDefault = String.format("#%08x", (0xFFFFFFFF & selectedColor));
					String smaliDefault = hexToSmali(hexDefault);
					hexValue.setText(hexDefault);
					hexValue.setHint(hexDefault);
					smaliValue.setText(smaliDefault);
					smaliValue.setHint(smaliDefault);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				touched = true;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				touched = false;
			}
		};
		OnClickListener preInput = new OnClickListener() {
			@Override
			public void onClick(View p1) {
				final SeekBar targetBar;
				String tag = p1.getTag().toString().toUpperCase();
				switch (tag.charAt(0)) {
					case 'A':
						targetBar = alphaBar;
						break;

					case 'R':
						targetBar = redBar;
						break;

					case 'G':
						targetBar = greenBar;
						break;

					case 'B':
						targetBar = blueBar;
						break;

					default:
						return;
				}
				final int progress = targetBar.getProgress();
				final EditText et = new EditText(context);
				et.setSingleLine(true);
				et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				et.setText(String.valueOf(progress));
				et.setHint(String.valueOf(progress));
				AlertDialog d = new AlertDialog.Builder(context)
					.setView(et)
					.setTitle("[" + tag.charAt(0) + "] 0 - 255")
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface p1, int p2) {
							try {
								int newProgress = Math.abs(Integer.parseInt(et.getText().toString()));
								if (newProgress > 255) {
									newProgress = progress;
								}
								seekListener.onStartTrackingTouch(targetBar);
								targetBar.setProgress(newProgress);
								seekListener.onStopTrackingTouch(targetBar);
							} catch (Exception e) {}
							p1.dismiss();
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.create();
				d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				d.setOnShowListener(new DialogInterface.OnShowListener() {
						@Override
						public void onShow(DialogInterface p1) {
							et.requestFocus();
							et.setSelection(String.valueOf(progress).length());
						}
					});
				d.show();
			}
		};
		alphaBar.setOnSeekBarChangeListener(seekListener);
		redBar.setOnSeekBarChangeListener(seekListener);
		greenBar.setOnSeekBarChangeListener(seekListener);
		blueBar.setOnSeekBarChangeListener(seekListener);

		alphaView.setOnClickListener(preInput);
		redView.setOnClickListener(preInput);
		greenView.setOnClickListener(preInput);
		blueView.setOnClickListener(preInput);

		alphaBar.setProgress(Color.alpha(selectedColor));
		redBar.setProgress(Color.red(selectedColor));
		greenBar.setProgress(Color.green(selectedColor));
		blueBar.setProgress(Color.blue(selectedColor));

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					if (selectListener != null) {
						selectListener.select(String.format("#%08x", (0xFFFFFFFF & selectedColor)), resourceId);
						selectListener.select(selectedColor, resourceId);
						selectListener.selectSmali(smaliColor, resourceId);
					}
					p1.dismiss();
				}
			});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {
					p1.cancel();
				}
			});
		AlertDialog dialog = builder.create();
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface p1) {
					if (selectListener != null) {
						selectListener.cancel(resourceId);
					}
					p1.dismiss();
				}
			});
		dialog.show();
	}

	public interface OnColorSelect {
		public void select(String hexColor, int resId);
		public void select(int intColor, int resId);
		public void selectSmali(String smaliColor, int resId);
		public void cancel(int resId);
	}
}

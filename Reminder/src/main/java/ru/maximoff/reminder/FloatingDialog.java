package ru.maximoff.reminder;

import android.view.View;
import android.view.WindowManager;

public final class FloatingDialog {
    private static View sView;
    private static WindowManager sWindowManager;

    private FloatingDialog() {}

    public static boolean isShown() {
        return (sView != null && sWindowManager != null);
    }

    public static void show(WindowManager wm, View view, WindowManager.LayoutParams params) {
        if (isShown()) {
            dismiss();
        }
		view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
				@Override
				public void onViewAttachedToWindow(View v) {}

				@Override
				public void onViewDetachedFromWindow(View v) {
					if (sView == v) {
						sView = null;
						sWindowManager = null;
					}
				}
			});
        wm.addView(view, params);
        sWindowManager = wm;
        sView = view;
    }

    public static void dismiss() {
        if (sView == null || sWindowManager == null) {
            return;
        }
        try {
            sWindowManager.removeView(sView);
        } catch (Exception ignored) {}
        sView = null;
        sWindowManager = null;
    }
}

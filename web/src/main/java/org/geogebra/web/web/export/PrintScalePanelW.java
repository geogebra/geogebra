package org.geogebra.web.web.export;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class PrintScalePanelW extends FlowPanel {

	private static final int maxFracDigits = 5;
	private TextBox tfScale1, tfScale2, tfScaleFixed;

	private EuclidianView ev;
	// private NumberFormat nf;

	private ListBox exportMode;
	private FlowPanel cmModePanel, fixedSizeModePanel;

	// private boolean noAction = false;
	HashMap<TextBox, Boolean> handlers = new HashMap<TextBox, Boolean>();

	public enum PrintScaleModes {
		SIZEINCM, FIXED_SIZE
	};

	private PrintScaleModes mode = PrintScaleModes.SIZEINCM;

	private String jcbItemScaleInCentimeter;
	private String jcbItemFixedSize;

	PrintScalePanelW(AppW app, EuclidianView eview) {
		ev = eview;
		Localization loc = app.getLocalization();
		// nf = NumberFormat.getInstance(Locale.ENGLISH);
		// nf.setMaximumFractionDigits(maxFracDigits);
		// nf.setGroupingUsed(false);

		this.addStyleName("printScalePanel");

		Runnable updateCm = new Runnable() {
			public void run() {
				fireTextFieldUpdate();
			}
		};

		Runnable updateFixedSize = new Runnable() {
			public void run() {
				fireFixedSizeTextFieldUpdate();
			}
		};
		
		tfScale1 = getNumberField(updateCm);
		tfScale2 = getNumberField(updateCm);
		tfScaleFixed = getNumberField(updateFixedSize);

		jcbItemScaleInCentimeter = loc.getPlain("ScaleInCentimeter")
				+ ":";
		jcbItemFixedSize = loc.getPlain("FixedSize") + ":";

		exportMode = new ListBox();
		exportMode.addItem(jcbItemScaleInCentimeter);
		exportMode.addItem(jcbItemFixedSize);

		add(exportMode);
		exportMode.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				switchMode();
			}

		});

		fixedSizeModePanel = new FlowPanel();
		Label aPixelsOnScreen = new Label(" "
				+ loc.getPlain("APixelsOnScreen", "100") + " = ");
		aPixelsOnScreen.addStyleName("aPixelsOnScreen");
		fixedSizeModePanel.add(aPixelsOnScreen);
		fixedSizeModePanel.add(tfScaleFixed);
		fixedSizeModePanel.add(new Label(" cm"));

		cmModePanel = new FlowPanel();
		cmModePanel.add(tfScale1);
		cmModePanel.add(new Label(" " + loc.getPlain("units") + " = "));
		cmModePanel.add(tfScale2);
		cmModePanel.add(new Label(" cm"));

		add(cmModePanel);

		updateScaleTextFields();

	}

	void switchMode() {
		Log.debug("switchMode: " + exportMode.getSelectedValue());

		if (exportMode.getSelectedValue().toString().equals(jcbItemFixedSize)) {
			mode = PrintScaleModes.FIXED_SIZE;
		} else {
			mode = PrintScaleModes.SIZEINCM;
		}

		switch (mode) {
		case SIZEINCM:
			remove(fixedSizeModePanel);
			add(cmModePanel);
			updateScaleTextFields();
			break;
		case FIXED_SIZE:
			remove(cmModePanel);
			add(fixedSizeModePanel);
			updateFixedSizeTextFields();
			break;
		}

	}

	private void updateFixedSizeTextFields() {
		double relScale = 100 * ev.getPrintingScale() / ev.getXscale();
		// setTextNoListener(tfScaleFixed, nf.format(relScale));
		setTextNoListener(tfScaleFixed, relScale + "");
	}

	private void updateScaleTextFields() {

		double scale = ev.getPrintingScale();
		if (scale <= 1) {
			setTextNoListener(tfScale2, "1");
			// setTextNoListener(tfScale1, nf.format(1 / scale));
			setTextNoListener(tfScale1, (1 / scale) + "");
		} else {
			// setTextNoListener(tfScale2, nf.format(scale));
			setTextNoListener(tfScale2, scale + "");
			setTextNoListener(tfScale1, "1");
		}
	}

	private void setTextNoListener(TextBox field, String s) {
		// handlers.put(field, true);
		field.setText(s);
	}

	void fireFixedSizeTextFieldUpdate() {
		// boolean viewChanged = false;

		try {
			double userScale = Double.parseDouble(tfScaleFixed.getText());
			if (!(Double.isInfinite(userScale) || Double.isNaN(userScale))) {
				double scale = userScale * ev.getXscale() / 100;
				ev.setPrintingScale(scale);
				// viewChanged = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		updateFixedSizeTextFields();

		// if (viewChanged) {
		// notifyListeners();
		// }
	}

	void fireTextFieldUpdate() {
		// boolean viewChanged = false;

		try {
			double numerator = Double.parseDouble(tfScale2.getText());
			double denominator = Double.parseDouble(tfScale1.getText());
			double scale = numerator / denominator;
			if (!(Double.isInfinite(scale) || Double.isNaN(scale))) {
				Log.debug("printing scale set: " + scale);
				ev.setPrintingScale(scale);
				// viewChanged = true;
			}
		} catch (Exception e) {
			// invalid numbers, continue editing
		}

		updateScaleTextFields();

		// if (viewChanged) {
		// notifyListeners();
		// }
	}

	private TextBox getNumberField(final Runnable run) {
		final TextBox ret = new TextBox();
		// ret.setColumns(maxFracDigits);
		// ret.setHorizontalAlignment(SwingConstants.RIGHT);

		ret.addDomHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				if (handlers.get(ret)) {
					handlers.put(ret, false);
					return;
				}
				run.run();
			}

		}, ChangeEvent.getType());

		handlers.put(ret, false);

		return ret;
	}

}

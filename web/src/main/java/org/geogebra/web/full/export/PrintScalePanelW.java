package org.geogebra.web.full.export;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Scale panel for print dialog
 *
 */
public class PrintScalePanelW extends FlowPanel {

	private TextBox tfScale1;
	private TextBox tfScale2;
	private TextBox tfScaleFixed;

	private EuclidianView ev;
	// private NumberFormat nf;

	private ListBox exportMode;
	private FlowPanel cmModePanel;
	private FlowPanel fixedSizeModePanel;

	// private boolean noAction = false;
	/** for each field remember if update handller is running */
	HashMap<TextBox, Boolean> handlers = new HashMap<>();

	/**
	 * Scale modes TODO move to common?
	 *
	 */
	public enum PrintScaleModes {
		/** scale using cm */
		SIZEINCM,
		/** fixed size in px */
		FIXED_SIZE
	}

	private PrintScaleModes mode = PrintScaleModes.SIZEINCM;

	private String jcbItemFixedSize;

	/**
	 * @param app
	 *            application
	 * @param eview
	 *            euclidian view
	 */
	PrintScalePanelW(AppW app, EuclidianView eview) {
		ev = eview;
		Localization loc = app.getLocalization();
		// nf = NumberFormat.getInstance(Locale.ENGLISH);
		// nf.setMaximumFractionDigits(maxFracDigits);
		// nf.setGroupingUsed(false);

		this.addStyleName("printScalePanel");

		Runnable updateCm = this::fireTextFieldUpdate;

		Runnable updateFixedSize = this::fireFixedSizeTextFieldUpdate;
		
		tfScale1 = getNumberField(updateCm);
		tfScale2 = getNumberField(updateCm);
		tfScaleFixed = getNumberField(updateFixedSize);

		String jcbItemScaleInCentimeter = loc.getMenu("ScaleInCentimeter")
				+ ":";
		jcbItemFixedSize = loc.getMenu("FixedSize") + ":";

		exportMode = new ListBox();
		exportMode.addItem(jcbItemScaleInCentimeter);
		exportMode.addItem(jcbItemFixedSize);

		add(exportMode);
		exportMode.addChangeHandler(event -> switchMode());

		fixedSizeModePanel = new FlowPanel();
		Label aPixelsOnScreen = new Label(" "
				+ loc.getPlain("APixelsOnScreen", "100") + " = ");
		aPixelsOnScreen.addStyleName("aPixelsOnScreen");
		fixedSizeModePanel.add(aPixelsOnScreen);
		fixedSizeModePanel.add(tfScaleFixed);
		fixedSizeModePanel.add(new Label(" cm"));

		cmModePanel = new FlowPanel();
		cmModePanel.add(tfScale1);
		cmModePanel.add(new Label(" " + loc.getMenu("units") + " = "));
		cmModePanel.add(tfScale2);
		cmModePanel.add(new Label(" cm"));

		add(cmModePanel);

		updateScaleTextFields();

	}

	/**
	 * Switch between fixed size and cm scale
	 */
	void switchMode() {
		Log.debug("switchMode: " + exportMode.getSelectedValue());

		if (exportMode.getSelectedValue().toString().equals(jcbItemFixedSize)) {
			mode = PrintScaleModes.FIXED_SIZE;
		} else {
			mode = PrintScaleModes.SIZEINCM;
		}

		if (mode == PrintScaleModes.SIZEINCM) {
			remove(fixedSizeModePanel);
			add(cmModePanel);
			updateScaleTextFields();
		} else if (mode == PrintScaleModes.FIXED_SIZE) {
			remove(cmModePanel);
			add(fixedSizeModePanel);
			updateFixedSizeTextFields();
		}

	}

	private void updateFixedSizeTextFields() {
		double relScale = DoubleUtil
				.checkInteger(100 * ev.getPrintingScale() / ev.getXscale());
		// setTextNoListener(tfScaleFixed, nf.format(relScale));
		setTextNoListener(tfScaleFixed, relScale + "");
	}

	private void updateScaleTextFields() {

		double scale = ev.getPrintingScale();
		if (scale <= 1) {
			setTextNoListener(tfScale2, "1");
			// setTextNoListener(tfScale1, nf.format(1 / scale));
			setTextNoListener(tfScale1, DoubleUtil.checkInteger(1 / scale) + "");
		} else {
			// setTextNoListener(tfScale2, nf.format(scale));
			setTextNoListener(tfScale2, DoubleUtil.checkInteger(scale) + "");
			setTextNoListener(tfScale1, "1");
		}
	}

	private static void setTextNoListener(TextBox field, String s) {
		// handlers.put(field, true);
		field.setText(s);
	}

	/**
	 * Fixed scale changed
	 */
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
			Log.debug(e);
		}

		updateFixedSizeTextFields();

		// if (viewChanged) {
		// notifyListeners();
		// }
	}

	/**
	 * x-scale or y-scale changed
	 */
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

		ret.addDomHandler(event -> {
			if (handlers.get(ret)) {
				handlers.put(ret, false);
				return;
			}
			run.run();
		}, ChangeEvent.getType());

		handlers.put(ret, false);

		return ret;
	}

}

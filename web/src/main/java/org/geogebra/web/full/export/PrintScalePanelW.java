package org.geogebra.web.full.export;

import java.util.Arrays;
import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.components.CompDropDown;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.TextBox;

/**
 * Scale panel for print dialog
 *
 */
public class PrintScalePanelW extends FlowPanel {

	private TextBox tfScale1;
	private TextBox tfScale2;
	private TextBox tfScaleFixed;
	private EuclidianView ev;
	private CompDropDown exportModeDropDown;
	private FlowPanel cmModePanel;
	private FlowPanel fixedSizeModePanel;

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

	/**
	 * @param app
	 *            application
	 * @param eview
	 *            euclidian view
	 */
	PrintScalePanelW(AppW app, EuclidianView eview) {
		ev = eview;
		Localization loc = app.getLocalization();

		this.addStyleName("printScalePanel");

		Runnable updateCm = this::fireTextFieldUpdate;

		Runnable updateFixedSize = this::fireFixedSizeTextFieldUpdate;
		
		tfScale1 = getNumberField(updateCm);
		tfScale2 = getNumberField(updateCm);
		tfScaleFixed = getNumberField(updateFixedSize);

		exportModeDropDown = new CompDropDown(app, null,
				Arrays.asList(loc.getMenu("ScaleInCentimeter"), loc.getMenu("FixedSize")),
				0);
		exportModeDropDown.setFullWidth(true);
		add(exportModeDropDown);
		exportModeDropDown.addChangeHandler(this::switchMode);

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
		if (exportModeDropDown.getSelectedIndex() == 1) {
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
		setTextNoListener(tfScaleFixed, relScale + "");
	}

	private void updateScaleTextFields() {
		double scale = ev.getPrintingScale();
		if (scale <= 1) {
			setTextNoListener(tfScale2, "1");
			setTextNoListener(tfScale1, DoubleUtil.checkInteger(1 / scale) + "");
		} else {
			setTextNoListener(tfScale2, DoubleUtil.checkInteger(scale) + "");
			setTextNoListener(tfScale1, "1");
		}
	}

	private static void setTextNoListener(TextBox field, String s) {
		field.setText(s);
	}

	/**
	 * Fixed scale changed
	 */
	void fireFixedSizeTextFieldUpdate() {
		try {
			double userScale = Double.parseDouble(tfScaleFixed.getText());
			if (!(Double.isInfinite(userScale) || Double.isNaN(userScale))) {
				double scale = userScale * ev.getXscale() / 100;
				ev.setPrintingScale(scale);
			}
		} catch (Exception e) {
			Log.debug(e);
		}

		updateFixedSizeTextFields();
	}

	/**
	 * x-scale or y-scale changed
	 */
	void fireTextFieldUpdate() {
		try {
			double numerator = Double.parseDouble(tfScale2.getText());
			double denominator = Double.parseDouble(tfScale1.getText());
			double scale = numerator / denominator;
			if (!(Double.isInfinite(scale) || Double.isNaN(scale))) {
				Log.debug("printing scale set: " + scale);
				ev.setPrintingScale(scale);
			}
		} catch (Exception e) {
			// invalid numbers, continue editing
		}

		updateScaleTextFields();
	}

	private TextBox getNumberField(final Runnable run) {
		final TextBox ret = new TextBox();

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

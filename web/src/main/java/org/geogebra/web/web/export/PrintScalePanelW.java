package org.geogebra.web.web.export;

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

	private TextBox tfScale1, tfScale2, tfScaleFixed;

	private EuclidianView ev;

	private ListBox exportMode;
	private FlowPanel cmModePanel, fixedSizeModePanel;

	public enum PrintScaleModes {
		SIZEINCM, FIXED_SIZE
	};

	private PrintScaleModes mode = PrintScaleModes.SIZEINCM;

	private String jcbItemScaleInCentimeter;
	private String jcbItemFixedSize;

	PrintScalePanelW(AppW app, EuclidianView eview) {
		ev = eview;
		Localization loc = app.getLocalization();

		this.addStyleName("printScalePanel");

		tfScale1 = new TextBox();
		tfScale2 = new TextBox();
		tfScaleFixed = new TextBox();

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
		fixedSizeModePanel.add(new Label(" "
				+ loc.getPlain("APixelsOnScreen", "100") + " = "));
		fixedSizeModePanel.add(tfScaleFixed);
		fixedSizeModePanel.add(new Label(" cm"));

		cmModePanel = new FlowPanel();
		cmModePanel.add(tfScale1);
		cmModePanel.add(new Label(" " + loc.getPlain("units") + " = "));
		cmModePanel.add(tfScale2);
		cmModePanel.add(new Label(" cm"));

		add(cmModePanel);

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
			break;
		case FIXED_SIZE:
			remove(cmModePanel);
			add(fixedSizeModePanel);
			break;
		}

	}
}

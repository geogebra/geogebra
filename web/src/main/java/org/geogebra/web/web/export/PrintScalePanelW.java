package org.geogebra.web.web.export;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
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

	PrintScalePanelW(AppW app, EuclidianView eview) {
		ev = eview;
		Localization loc = app.getLocalization();

		tfScale1 = new TextBox();
		tfScale2 = new TextBox();
		tfScaleFixed = new TextBox();

		String jcbItemScaleInCentimeter = loc.getPlain("ScaleInCentimeter")
				+ ":";
		String jcbItemFixedSize = loc.getPlain("FixedSize") + ":";

		exportMode = new ListBox();
		exportMode.addItem(jcbItemScaleInCentimeter);
		exportMode.addItem(jcbItemFixedSize);

		add(exportMode);
		exportMode.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				switchMode();
			}

		});

	}

	void switchMode() {
		Log.debug("switchMode: " + exportMode.getSelectedValue());
	}
}

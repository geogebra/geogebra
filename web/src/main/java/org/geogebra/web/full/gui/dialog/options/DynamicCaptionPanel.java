package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.DynamicCaptionModel;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.properties.ListBoxPanel;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Panel for setting text objects as captions.
 *
 * @author Laszlo
 */
public class DynamicCaptionPanel extends OptionPanel {
	private final CheckboxPanel enableDynamicCaption;
	private final ListBoxPanel captions;

	/**
	 *
	 * @param app the application
	 * @param captionField needs to be disabled/re-enabled
	 * 		  as dynamic caption is enabled/disabled.
	 */
	public DynamicCaptionPanel(App app, AutoCompleteTextFieldW captionField) {
		enableDynamicCaption = new EnableDynamicCaptionPanel(app, captionField);
		captions = new ListBoxPanel(app.getLocalization(), "");
		DynamicCaptionModel dynamicCaptionModel = new DynamicCaptionModel(app);
		captions.setModel(dynamicCaptionModel);
		dynamicCaptionModel.setListener(captions);
		FlowPanel main = new FlowPanel();
		main.add(enableDynamicCaption.getWidget());
		main.add(captions.getWidget());
		setWidget(main);
		captions.getWidget().setStyleName("listBoxPanel-noLabel");
	}

	@Override
	public void setLabels() {
		enableDynamicCaption.setLabels();
		captions.setLabels();
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		enableDynamicCaption.updatePanel(geos);
		captions.updatePanel(geos);
		return this;
	}
}

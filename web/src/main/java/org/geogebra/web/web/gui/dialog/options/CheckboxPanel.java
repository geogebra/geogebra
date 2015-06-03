package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel.IBooleanOptionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.web.gui.properties.OptionPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class CheckboxPanel extends OptionPanel implements
		IBooleanOptionListener {
	private final CheckBox checkbox;
	private final String titleId;
	private Localization loc;

	public CheckboxPanel(final String title, Localization loc) {
		this.loc = loc;
		checkbox = new CheckBox();
		checkbox.setStyleName("checkBoxPanel");
		setWidget(getCheckbox());
		this.titleId = title;

		getCheckbox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				((BooleanOptionModel) getModel()).applyChanges(getCheckbox()
						.getValue());
			}
		});

	}

	@Override
	public void updateCheckbox(boolean value) {
		getCheckbox().setValue(value);
	}

	@Override
	public void setLabels() {
		getCheckbox().setText(loc.getPlain(titleId));
	}

	public CheckBox getCheckbox() {
		return checkbox;
	}
}

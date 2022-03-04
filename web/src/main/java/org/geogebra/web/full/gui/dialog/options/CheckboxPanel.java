package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel.IBooleanOptionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.properties.OptionPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class CheckboxPanel extends OptionPanel implements
		IBooleanOptionListener, ClickHandler {
	private final CheckBox checkbox;
	private final String titleId;
	private Localization loc;

	/**
	 * @param title
	 *            title
	 * @param loc
	 *            localization
	 * @param m
	 *            model
	 */
	public CheckboxPanel(final String title, Localization loc,
			BooleanOptionModel m) {
		this(title, loc);
		setModel(m);
		m.setListener(this);
	}

	/**
	 * @param title
	 *            title
	 * @param loc
	 *            localization
	 */
	public CheckboxPanel(final String title, Localization loc) {
		this.loc = loc;
		checkbox = new CheckBox();
		checkbox.setStyleName("checkBoxPanel");
		setWidget(getCheckbox());
		this.titleId = title;

		getCheckbox().addClickHandler(this);
	}

	@Override
	public void updateCheckbox(boolean value) {
		getCheckbox().setValue(value);
	}

	@Override
	public void setLabels() {
		getCheckbox().setText(loc.getMenu(titleId));
	}

	public CheckBox getCheckbox() {
		return checkbox;
	}

	@Override
	public void onClick(ClickEvent event) {
		((BooleanOptionModel) getModel()).applyChanges(getCheckbox()
				.getValue());
		onChecked();
	}

	/** Override this to do stuff after setting checkbox */
	public void onChecked() {
		// Override and put your code here.
	}
}

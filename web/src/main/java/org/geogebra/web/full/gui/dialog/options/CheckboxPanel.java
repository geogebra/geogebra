package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel;
import org.geogebra.common.gui.dialog.options.model.BooleanOptionModel.IBooleanOptionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.properties.OptionPanel;

import com.google.gwt.user.client.ui.FlowPanel;

public class CheckboxPanel extends OptionPanel implements
		IBooleanOptionListener {
	private final ComponentCheckbox checkbox;

	/**
	 * @param title - title
	 * @param loc - localization
	 * @param m - model
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
		FlowPanel holderPanel = new FlowPanel();
		holderPanel.addStyleName("checkboxHolder");
		checkbox = new ComponentCheckbox(loc, false, title, this::onClick);
		holderPanel.add(checkbox);
		setWidget(holderPanel);
	}

	@Override
	public void updateCheckbox(boolean value) {
		getCheckbox().setSelected(value);
	}

	@Override
	public void setLabels() {
		getCheckbox().setLabels();
	}

	public ComponentCheckbox getCheckbox() {
		return checkbox;
	}

	private void onClick(boolean checked) {
		((BooleanOptionModel) getModel()).applyChanges(checked);
		onChecked();
	}

	/** Override this to do stuff after setting checkbox */
	public void onChecked() {
		// Override and put your code here.
	}
}

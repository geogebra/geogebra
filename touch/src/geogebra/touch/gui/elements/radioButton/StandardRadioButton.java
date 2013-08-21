package geogebra.touch.gui.elements.radioButton;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.StandardImage;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class StandardRadioButton extends FlowPanel {
	LookAndFeel laf = TouchEntryPoint.getLookAndFeel();

	private Label label;
	private StandardImage icon;
	private boolean value = false;

	private final StandardRadioGroup group;

	public StandardRadioButton(String label, StandardRadioGroup group) {
		this.setStyleName("radioButton");

		this.label = new Label(label);

		this.group = group;
		this.group.addRadioButton(this);

		this.icon = new StandardImage(this.laf.getIcons().radioButtonInactive());
		this.label.setText(label);

		this.add(this.icon);
		this.add(this.label);

		this.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				StandardRadioButton.this.onClick();
			}

		}, ClickEvent.getType());

	}

	void onClick() {
		if (this.value) {
			return;
		}
		this.group.deselectAll();
		this.setValue(true);
		this.group.fireRadioChanged(this);
	}

	public void setValue(boolean value) {
		if (value) {
			this.group.deselectAll();
		}
		this.value = value;
		if (value) {
			this.icon.setIcon(this.laf.getIcons().radioButtonActive());
		} else {
			this.icon.setIcon(this.laf.getIcons().radioButtonInactive());
		}
	}

	public boolean getValue() {
		return this.value;
	}

}

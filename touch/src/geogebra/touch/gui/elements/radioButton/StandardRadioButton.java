package geogebra.touch.gui.elements.radioButton;

import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.LookAndFeel;

public class StandardRadioButton extends StandardButton {

	protected static LookAndFeel laf = TouchEntryPoint.getLookAndFeel();
	
	private boolean activated = false;
	private final StandardRadioGroup group;

	public StandardRadioButton(final StandardRadioGroup group) {
		super(laf.getIcons().radioButtonInactive(), "");
		this.setStyleName("radioButton");

		this.group = group;
		this.group.addRadioButton(this);

		this.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				handleClick();
			}
		});
	}

	protected void handleClick() {
		if (this.activated) {
			return;
		}
		this.group.deselectAll();
		this.setActive(true);
		this.group.fireRadioChanged(this);
	}

	@Override
	public void setActive(final boolean value) {
		if (value) {
			this.group.deselectAll();
		}
		this.activated = value;
		if (value) {
			this.setIcon(laf.getIcons().radioButtonActive());
		} else {
			this.setIcon(laf.getIcons().radioButtonInactive());
		}
	}

	public boolean isActivated() {
		return this.activated;
	}

	public void setLabel() {

	}

	@Override
	public void onHoldPressDownStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHoldPressOffStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisablePressStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnablePressStyle() {
		// TODO Auto-generated method stub

	}
}

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Header for numbered rows
 */
public class AVItemHeaderScientific extends SimplePanel
		implements AlgebraItemHeader {

	private Label number;
	private NoDragImage warningImage;

	/**
	 * Create new number header
	 */
	public AVItemHeaderScientific() {
		setStyleName("avItemHeaderScientific");
		number = new Label();
		number.setStyleName("avItemNumber");
		setWidget(number);
	}

	@Override
	public void updateIcons(boolean warning) {
		setWidget(warning ? getWarningImage() : number);
	}

	private NoDragImage getWarningImage() {
		if (warningImage == null) {
			warningImage = new NoDragImage(MaterialDesignResources.INSTANCE.wrong_input(), 24);
			warningImage.addStyleName("avWarningScientific");
		}
		return warningImage;
	}

	@Override
	public void setLabels() {
		// no localization
	}

	@Override
	public void update() {
		// nothing to do
	}

	@Override
	public boolean isHit(int x, int y) {
		return false;
	}

	@Override
	public void setIndex(int itemCount) {
		number.setText(itemCount + ")");
	}

	@Override
	public void setError(String s) {
		// not implemented
	}

}

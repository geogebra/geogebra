package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.resources.client.ResourcePrototype;

public class AlgebraOutputFormatButton extends StandardButton {

	/**
	 * Default constructor
	 */
	public AlgebraOutputFormatButton() {
		super(24);
		addStyleName("symbolicButton");
	}

	/**
	 * Selects on the three different button states and updates the icon
	 * @param format Index
	 */
	public void select(AlgebraOutputFormat format) {
		setIcon(getIconFor(format));
		Dom.toggleClass(this, "show-fraction",
				format == AlgebraOutputFormat.FRACTION);
	}

	private ResourcePrototype getIconFor(AlgebraOutputFormat format) {
		MaterialDesignResources resources = MaterialDesignResources.INSTANCE;
		switch (format) {
		case FRACTION:
			return resources.fraction_white();
		case APPROXIMATION:
			return resources.modeToggleSymbolic();
		case ENGINEERING:
			return resources.engineering_notation_white();
		case EXACT:
		default:
			return resources.equal_sign_white();
		}
	}

}

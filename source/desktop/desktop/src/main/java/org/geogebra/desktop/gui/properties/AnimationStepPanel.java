package org.geogebra.desktop.gui.properties;

import org.geogebra.common.gui.dialog.options.model.TextPropertyModel;
import org.geogebra.desktop.gui.AngleTextField;
import org.geogebra.desktop.gui.dialog.TextPropertyPanel;
import org.geogebra.desktop.main.AppD;

/**
 * panel for animation step
 * 
 * @author Markus Hohenwarter
 */
public class AnimationStepPanel extends TextPropertyPanel {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 * @param m model
	 * @param app application
	 */
	public AnimationStepPanel(TextPropertyModel m, AppD app) {
		super(app, m, new AngleTextField(6, app));
	}
}
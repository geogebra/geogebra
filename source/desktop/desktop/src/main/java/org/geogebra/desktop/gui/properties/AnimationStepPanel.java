/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
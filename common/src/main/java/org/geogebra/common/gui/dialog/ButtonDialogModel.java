/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.gui.dialog;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;

public class ButtonDialogModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeoElement linkedGeo = null;
	private boolean textField = false;

	private App app;

	private GeoElement geoResult = null;
	private GeoButton button = null;
	private int x, y;

	public ButtonDialogModel(App app, int x, int y, boolean textField) {
		this.app = app;
		this.textField = textField;
		;
		this.x = x;
		this.y = y;

	}

	public GeoElement getResult(String labelText) {
		if (geoResult != null) {
			// set label of geoResult
			String strLabel;
			try {
				strLabel = app.getKernel().getAlgebraProcessor()
						.parseLabel(labelText);
			} catch (Exception e) {
				strLabel = null;
			}
			geoResult.setLabel(strLabel);
		}

		return geoResult;
	}

	public void apply(String caption, String scriptText) {
		Construction cons = app.getKernel().getConstruction();
		button = textField ? app.getKernel().getAlgoDispatcher()
				.textfield(null, linkedGeo) : GeoButton.getNewButton(cons);
		button.setEuclidianVisible(true);
		button.setAbsoluteScreenLoc(x, y);

		button.setLabel(null);
		// XXX See Remark 1 above
		Script script = app
				.createScript(ScriptType.GGBSCRIPT, scriptText, true);
		button.setClickScript(script);

		// set caption text
		String strCaption = caption.trim();
		if (strCaption.length() > 0) {
			button.setCaption(strCaption);
		}

		button.setEuclidianVisible(true);
		button.setLabelVisible(true);
		button.setFixed(true);
		button.updateRepaint();

		geoResult = button;

		app.storeUndoInfo();

	}

	public void cancel() {
		geoResult = null;
	}

	public String getTitle() {
		return textField ? app.getPlain("TextField") : app.getPlain("Button");
	}

	public String getInitString() {
		return button == null ? "" : button
				.getCaption(StringTemplate.defaultTemplate);
	}

	public boolean isTextField() {
		return textField;

	}

	public String getClickScript() {
		String result = "";
		Script clickScript = button == null ? null : button
				.getScript(EventType.CLICK);
		if (clickScript != null) {
			result = clickScript.getText();
		}
		return result;
	}

	public void setLinkedGeo(GeoElement geo) {
		linkedGeo = geo;

	}
}
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

package org.geogebra.common.gui.dialog;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;

/**
 * Model for Button / Input Box dialog.
 *
 */
public class ButtonDialogModel {

	private GeoElement linkedGeo = null;
	private boolean textField = false;

	private App app;

	private GeoButton button = null;
	private int x;
	private int y;

	/**
	 * @param app
	 *            app
	 * @param x
	 *            button screen x-coord
	 * @param y
	 *            button screen y-coord
	 * @param inputBox
	 *            whether output is input box
	 */
	public ButtonDialogModel(App app, int x, int y, boolean inputBox) {
		this.app = app;
		this.textField = inputBox;
		this.x = x;
		this.y = y;
	}

	/**
	 * @param caption
	 *            caption
	 * @param scriptText
	 *            script content
	 */
	public void apply(String caption, String scriptText) {
		Construction cons = app.getKernel().getConstruction();
		button = textField
				? app.getKernel().getAlgoDispatcher().textfield(null, linkedGeo)
				: GeoButton.getNewButton(cons);
		button.setEuclidianVisible(true);
		button.setAbsoluteScreenLoc(x, y);

		button.setLabel(null);

		ScriptType scriptType = ScriptType.GGBSCRIPT;
		if (scriptText.indexOf("ggbApplet.") > -1) {
			scriptType = ScriptType.JAVASCRIPT;
		}

		// XXX See Remark 1 above
		Script script = app.createScript(scriptType, scriptText,
				true);
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

		app.storeUndoInfo();
	}

	/**
	 * @return dialog title ("Button" or "Textfield")
	 */
	public String getTitle() {
		Localization loc = app.getLocalization();
		return textField ? loc.getMenu("TextField") : loc.getMenu("Button");
	}

	/**
	 * @return initial caption
	 */
	public String getInitString() {
		return button == null ? ""
				: button.getCaption(StringTemplate.defaultTemplate);
	}

	/**
	 * @return whether this is for input box
	 */
	public boolean isTextField() {
		return textField;
	}

	/**
	 * @return click script
	 */
	public String getClickScript() {
		String result = "";
		Script clickScript = button == null ? null
				: button.getScript(EventType.CLICK);
		if (clickScript != null) {
			result = clickScript.getText();
		}
		return result;
	}

	/**
	 * @param geo
	 *            linked geo for input box
	 */
	public void setLinkedGeo(GeoElement geo) {
		linkedGeo = geo;
	}

	/**
	 * @return objects that can be linked to an input box
	 */
	public ArrayList<GeoElement> getLinkableObjects() {
		ArrayList<GeoElement> options = new ArrayList<>();
		options.add(null);
		// combo box to link GeoElement to TextField
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();
		for (GeoElement geo : sortedSet) {
			if (GeoInputBox.isGeoLinkable(geo)) {
				options.add(geo);
			}
		}
		return options;
	}
}
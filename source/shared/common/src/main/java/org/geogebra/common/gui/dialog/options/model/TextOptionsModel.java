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

package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.gui.inputfield.DynamicTextProcessor;
import org.geogebra.common.gui.menubar.RoundingOptions;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

public class TextOptionsModel extends OptionsModel {
	private ITextOptionsListener listener;

	private boolean displayFontStyle;

	private Localization loc;
	private DynamicTextProcessor dTProcessor;
	private GeoText editGeo;
	private RoundingOptions roundingOptions;

	public interface ITextOptionsListener extends PropertyListener {

		@MissingDoc
		void selectSize(int index);

		@MissingDoc
		void selectFont(int index);

		@MissingDoc
		void selectDecimalPlaces(int index);

		@MissingDoc
		void updateWidgetVisibility();

		@MissingDoc
		void selectFontStyle(int style);

		@MissingDoc
		void setEditorText(ArrayList<DynamicTextElement> list);

		@MissingDoc
		void updatePreviewPanel();

		@MissingDoc
		void reinitEditor();

	}

	public TextOptionsModel(App app) {
		super(app);
		loc = app.getLocalization();
		dTProcessor = new DynamicTextProcessor(app);
		editGeo = null;
		roundingOptions = new RoundingOptions(loc);
	}

	@Override
	public boolean checkGeos() {
		if (!hasGeos()) {
			return false;
		}
		boolean geosOK = true;
		displayFontStyle = true;
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);

			if (geo instanceof GeoInputBox) {
				displayFontStyle = false;
			}

			GeoElement geoForProperties = geo.getGeoElementForPropertiesDialog();
			if (!(geoForProperties instanceof TextStyle)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	public TextProperties getTextPropertiesAt(int index) {
		Object objectAt = getObjectAt(index);
		return objectAt instanceof TextProperties ? (TextProperties) objectAt
				: null;
	}

	public GeoText getGeoTextAt(int index) {
		Object ret = getObjectAt(index);
		if (ret instanceof GeoText) {
			return (GeoText) ret;
		}

		return null;
	}

	@Override
	public void updateProperties() {
		GeoElement geo = getGeoAt(0);
		listener.updateWidgetVisibility();

		TextStyle textStyleGeo = (TextStyle) geo;
		listener.selectSize(GeoText
				.getFontSizeIndex(textStyleGeo.getFontSizeMultiplier()));
		TextProperties geo0 = getTextPropertiesAt(0);
		if (geo0 != null) {
			setEditGeo(getGeoTextAt(0));
			if (geo0 instanceof GeoInputBox) {
				listener.selectFont(((GeoInputBox) geo0).isSerifContent() ? 1 : 0);
			} else {
				listener.selectFont(geo0.isSerifFont() ? 1 : 0);
			}
			int selItem = -1;

			int decimals = geo0.getPrintDecimals();
			if (decimals > 0 && decimals < roundingOptions.decimalsLookupLength()
					&& !geo0.useSignificantFigures()) {
				selItem = roundingOptions.decimalsLookup(decimals);
			}

			int figures = geo0.getPrintFigures();
			if (figures > 0 && figures < roundingOptions.figuresLookupLength()
					&& geo0.useSignificantFigures()) {
				selItem = roundingOptions.figuresLookup(figures);
			}

			listener.selectDecimalPlaces(selItem);
		}

		Log.debug("UpdateText Properties Text");
		GeoText text0 = getGeoTextAt(0);
		if (text0 != null) {
			// Gives null for eg. table text
			ArrayList<DynamicTextElement> a = dTProcessor
					.buildDynamicTextList(text0);
			if (a != null) {
				listener.setEditorText(a);
			}
		}

		listener.selectFontStyle(EuclidianStyleBarStatic.getFontStyle(getGeosAsList()));
	}

	public void applyFontSizeFromString(String percentStr0) {
		double multiplier;
		if (percentStr0 == null) {
			// Cancel
			return;
		}
		String percentStr = percentStr0.replaceAll("%", "");

		try {
			multiplier = StringUtil.parseDouble(percentStr) / 100;

			if (multiplier < 0.01) {
				multiplier = 0.01;
			} else if (multiplier > 100) {
				multiplier = 100;
			}
		} catch (NumberFormatException e2) {
			app.showError(Errors.InvalidInput);
			return;
		}
		applyFontSize(multiplier);
	}

	public void applyFontSizeFromIndex(int index) {
		applyFontSize(GeoText.getRelativeFontSize(index));
	}

	public void applyFontSize(double value) {
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			if (text != null) {
				text.setFontSizeMultiplier(value);
				text.updateVisualStyleRepaint(GProperty.FONT);
			}
		}
		if (editGeo == null) {
			return;
		}

		editGeo.setFontSizeMultiplier(value);
		listener.updatePreviewPanel();
	}

	public String[] getFonts() {
		return new String[]{ loc.getMenu("SansSerif"), loc.getMenu("Serif") };
	}

	public String[] getFontSizes() {
		return loc.getFontSizeStrings();
	}

	public void applyFont(boolean isSerif) {
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			if (text != null) {
				if (text instanceof GeoInputBox) {
					((GeoInputBox) text).setSerifContent(isSerif);
				} else {
					text.setSerifFont(isSerif);
				}
				text.updateVisualStyleRepaint(GProperty.FONT);
			}
		}

		if (editGeo != null) {
			editGeo.setSerifFont(isSerif);
		}
		storeUndoInfo();
		listener.updatePreviewPanel();
	}

	public void applyDecimalPlaces(int decimals) {
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			if (decimals < 8) { // decimal places
				text.setPrintDecimals(roundingOptions.roundingMenuLookup(decimals),
						true);
			} else { // significant figures
				text.setPrintFigures(roundingOptions.roundingMenuLookup(decimals),
						true);
			}
			text.updateRepaint();
		}
		listener.updatePreviewPanel();

	}

	public static int getFontStyle(boolean isBold, boolean isItalic) {
		int style = 0;
		if (isBold) {
			style += GFont.BOLD;
		}
		if (isItalic) {
			style += GFont.ITALIC;
		}
		return style;
	}

	public void applyFontStyle(int mask, boolean add) {
		EuclidianStyleBarStatic.applyFontStyle(getGeosAsList(), mask, add);

		listener.updatePreviewPanel();
	}

	public String getGeoGebraString(ArrayList<DynamicTextElement> list,
			boolean isLatex) {
		return dTProcessor.buildGeoGebraString(list, isLatex);
	}

	public GeoText getEditGeo() {
		return editGeo;
	}

	public void setEditGeo(GeoText editGeo) {
		this.editGeo = editGeo;
	}

	public void applyEditedGeo(List<DynamicTextElement> text,
			final boolean isLatex, ErrorHandler handler) {
		GeoText geo0 = getGeoTextAt(0);
		dTProcessor.process(text, geo0, isLatex, handler, () -> editGeo = null);
		storeUndoInfo();
	}

	public void cancelEditGeo() {
		if (editGeo == null) {
			return;
		}

		editGeo = null;
		listener.updatePreviewPanel();
	}

	public void setLaTeX(boolean isLatex, boolean updateAlgo) {
		if (editGeo == null) {
			return;
		}

		editGeo.setLaTeX(isLatex, updateAlgo);
		editGeo.updateRepaint();
		listener.updatePreviewPanel();
		storeUndoInfo();
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTextEditable() {
		return getGeosLength() == 1 && getObjectAt(0) instanceof GeoText
				&& !getGeoTextAt(0).isTextCommand()
				&& !getGeoTextAt(0).isProtected(EventType.UPDATE);
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	public void setListener(ITextOptionsListener listener) {
		this.listener = listener;
	}

	public void reinitEditor() {
		if (listener != null) {
			listener.reinitEditor();
		}
	}

	public boolean hasFontStyle() {
		return hasGeos() && displayFontStyle;
	}

	/**
	 * @return whether rounding setting should be shown
	 */
	public boolean hasRounding() {
		if (!hasGeos()) {
			return false;
		}
		GeoElement geo = getGeoAt(0);
		return geo != null && !geo.isIndependent() && !geo.isGeoList();
	}
}

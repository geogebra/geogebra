package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;

import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.gui.inputfield.DynamicTextProcessor;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class TextOptionsModel extends OptionsModel {
	public interface ITextOptionsListener extends PropertyListener {

		void setWidgetsVisible(boolean showFontDetails, boolean isButton);

		void setFontSizeVisibleOnly();

		void selectSize(int index);

		void selectFont(int index);

		void selectDecimalPlaces(int index);

		void setSecondLineVisible(boolean noDecimals);

		void selectFontStyle(int style);

		void setEditorText(ArrayList<DynamicTextElement> list);
		void setEditorText(String text);

		void updatePreview();

		void reinitEditor();

	}

	private ITextOptionsListener listener;

	private boolean justDisplayFontSize;

	private String[] fonts = { "Sans Serif", "Serif" };
	private App app;
	private Localization loc;
	private DynamicTextProcessor dTProcessor; 
	private GeoText editGeo;
	private GeoText lastGeo;

	public TextOptionsModel(App app) {
		this.app = app;
		loc = app.getLocalization();
		dTProcessor = new DynamicTextProcessor(app);
		editGeo = null;
		lastGeo = null;
	}

	@Override
	public boolean checkGeos() { 
		if (!hasGeos()) {
			return false;
		}
		boolean geosOK = true; 
		justDisplayFontSize = true; 
		for (int i = 0; i < getGeosLength(); i++) { 
			GeoElement geo = getGeoAt(i); 

			if ((geo instanceof TextProperties && !((TextProperties) geo) 
					.justFontSize()) || geo.isGeoButton()) { 
				justDisplayFontSize = false; 
			} 


			if (!(geo.getGeoElementForPropertiesDialog().isGeoText())) {
				if (!geo.isGeoButton()) {
					geosOK = false;
					break;
				}
			}
		} 
		return geosOK; 
	} 


	public TextProperties getTextPropertiesAt(int index) {
		return (TextProperties) getObjectAt(index);
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
		if (geo.isGeoTextField()) {
			listener.setFontSizeVisibleOnly();
		} else {
			listener.setWidgetsVisible(!justDisplayFontSize, geo.isGeoButton());
		}

		TextProperties geo0 = getTextPropertiesAt(0);	

		setEditGeo(getGeoTextAt(0));

		listener.selectSize(GeoText.getFontSizeIndex(geo0
				.getFontSizeMultiplier())); // font
		// size
		// ranges
		// from
		// -6
		// to
		// 6,
		// transform
		// this
		// to
		// 0,1,..,6
		listener.selectFont(geo0.isSerifFont() ? 1 : 0);
		int selItem = -1;

		int decimals = geo0.getPrintDecimals();
		if (decimals > 0 && decimals < App.decimalsLookup.length
				&& !geo0.useSignificantFigures())
			selItem = App.decimalsLookup[decimals];

		int figures = geo0.getPrintFigures();
		if (figures > 0 && figures < App.figuresLookup.length
				&& geo0.useSignificantFigures())
			selItem = App.figuresLookup[figures];

		listener.selectDecimalPlaces(selItem);
		listener.setSecondLineVisible((getGeoAt(0).isIndependent() || (geo0 instanceof GeoList))
);

		if (geo.isGeoTextField()) {
			listener.setFontSizeVisibleOnly();
		}

		GeoText text0 = getGeoTextAt(0);
		if (text0 != null) {
			if (text0.getParentAlgorithm() instanceof AlgoDependentText) {
				listener.setEditorText(dTProcessor.buildDynamicTextList(text0));
			} else {
				listener.setEditorText(text0.getTextString());
			}
		}
		
		listener.selectFontStyle(geo0.getFontStyle());

	}
	public void applyFontSizeFromString(String percentStr) {
		double multiplier;
		if (percentStr == null) {
			// Cancel
			return
					;
		}
		percentStr = percentStr.replaceAll("%", "");

		try {
			multiplier = StringUtil.parseDouble(percentStr) / 100;

			if (multiplier < 0.01) {
				multiplier = 0.01;
			} else if (multiplier > 100) {
				multiplier = 100;
			}
		} catch (NumberFormatException e2) {
			app.showError("InvalidInput");
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
			text.setFontSizeMultiplier(value);
			getGeoAt(i).updateVisualStyleRepaint();
		}
		if (editGeo == null) {
			return;
		}

		((TextProperties)editGeo).setFontSizeMultiplier(value);
		listener.updatePreview();
	}

	public String[] getFonts() {
		return fonts;
	}

	public String[] getFontSizes() {
		return loc.getFontSizeStrings();
	}

	public void applyFont(boolean isSerif) { 
		if (editGeo == null) {
			return;
		}


		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			text.setSerifFont(isSerif);
			getGeoAt(i).updateVisualStyleRepaint();
		}

		((TextProperties)editGeo).setSerifFont(isSerif);
		listener.updatePreview();
	}

	public void applyDecimalPlaces(int decimals) {
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			if (decimals < 8) // decimal places
			{
				// Application.debug("decimals"+roundingMenuLookup[decimals]+"");
				text.setPrintDecimals(
						App.roundingMenuLookup[decimals], true);
			} else // significant figures
			{
				// Application.debug("figures"+roundingMenuLookup[decimals]+"");
				text.setPrintFigures(App.roundingMenuLookup[decimals],
						true);
			}
			((GeoElement) text).updateRepaint();
		}
		listener.updatePreview();

	}

	public static int getFontStyle(boolean isBold, boolean isItalic) {
		int style = 0;
		if (isBold)
			style += 1;
		if (isItalic)
			style += 2;
		return style;
	}

	public void applyFontStyle(boolean isBold, boolean isItalic) {
		int style = getFontStyle(isBold, isItalic);
		
		for (int i = 0; i < getGeosLength(); i++) {
			TextProperties text = getTextPropertiesAt(i);
			text.setFontStyle(style);
			((GeoElement) text).updateVisualStyleRepaint();
		}

		listener.updatePreview();

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
		lastGeo = editGeo;
	}

	public void setEditGeoText(String text) {
		if (editGeo == null) {
			return;
		}
	
		editGeo.setTextString(text);
	}

	public void applyEditedGeo(ArrayList<DynamicTextElement> text,
			boolean isLatex, boolean isSerif) {
		GeoText geo0 = getGeoTextAt(0);
		GeoElement geo1 = app
				.getKernel()
				.getAlgebraProcessor()
				.changeGeoElement(geo0,
						dTProcessor.buildGeoGebraString(text, isLatex), true,
						true);
		((GeoText) geo1).setSerifFont(isSerif);
		((GeoText) geo1).setLaTeX(isLatex, true);
		((GeoText) geo1).updateRepaint();
		app.getSelectionManager().addSelectedGeo(geo1);
		editGeo = null;
	}

	public void cancelEditGeo() {
		if (editGeo == null) {
			return;
		}

		editGeo = null;
		listener.updatePreview();
	}

	public void setLaTeX(boolean isLatex, boolean updateAlgo) {
		if (editGeo == null) {
			return;
		}

		editGeo.setLaTeX(isLatex, updateAlgo);
		listener.updatePreview();
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isTextEditable() {
		return getGeosLength() == 1 && getObjectAt(0) instanceof GeoText
				&& !getGeoTextAt(0).isTextCommand()
				&& !getGeoTextAt(0).isFixed();
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
}

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

public class ColorFunctionModel extends OptionsModel {
	private IColorFunctionListener listener;
	private Kernel kernel;

	public interface IColorFunctionListener extends PropertyListener {
		@MissingDoc
		void setRedText(String text);

		@MissingDoc
		void setGreenText(String text);

		@MissingDoc
		void setBlueText(String text);

		@MissingDoc
		void setAlphaText(String text);

		@MissingDoc
		void showAlpha(boolean value);

		@MissingDoc
		void setDefaultValues(GeoElement geo0);

		@MissingDoc
		void updateSelection(Object[] geos);
	}

	public ColorFunctionModel(App app, IColorFunctionListener listener) {
		super(app);
		this.kernel = app.getKernel();
		this.listener = listener;

	}

	@Override
	public void updateProperties() {
		// check for fillable geos in the current selection
		boolean someFillable = false;
		for (int i = 0; i < getGeosLength(); i++) {
			if (getGeoAt(i).isFillable()) {
				someFillable = true;
				continue;
			}
		}

		// if we have any fillables then show the opacity field
		listener.showAlpha(someFillable);

		GeoElement geo0 = getGeoAt(0);
		listener.setDefaultValues(geo0);

		// take condition of first geo
		String strRed = "";
		String strGreen = "";
		String strBlue = "";
		String strAlpha = "";

		GeoList colorList = geo0.getColorFunction();
		if (colorList != null) {
			strRed = colorList.get(0).getLabel(StringTemplate.editTemplate);
			strGreen = colorList.get(1).getLabel(StringTemplate.editTemplate);
			strBlue = colorList.get(2).getLabel(StringTemplate.editTemplate);
			if (colorList.size() == 4) {
				strAlpha = colorList.get(3)
						.getLabel(StringTemplate.editTemplate);
			}
		}

		// compare first geo with other selected geos
		// if difference exists in a color then null it out
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			GeoList colorListTemp = geo.getColorFunction();
			if (colorListTemp != null) {
				String strRedTemp = colorListTemp.get(0)
						.getLabel(StringTemplate.editTemplate);
				String strGreenTemp = colorListTemp.get(1)
						.getLabel(StringTemplate.editTemplate);
				String strBlueTemp = colorListTemp.get(2)
						.getLabel(StringTemplate.editTemplate);
				String strAlphaTemp = "";
				if (colorListTemp.size() == 4) {
					strAlphaTemp = colorListTemp.get(3)
							.getLabel(StringTemplate.editTemplate);
				}
				if (!strRed.equals(strRedTemp)) {
					strRed = "";
				}
				if (!strGreen.equals(strGreenTemp)) {
					strGreen = "";
				}
				if (!strBlue.equals(strBlueTemp)) {
					strBlue = "";
				}
				if (!strAlpha.equals(strAlphaTemp)) {
					strAlpha = "";
				}
			}
		}

		// set the color fields
		setListenerRGBA(strRed, strGreen, strBlue, strAlpha);

	}

	private void setListenerRGBA(final String strRed, final String strGreen,
			final String strBlue, final String strAlpha) {
		listener.setRedText(strRed);
		listener.setGreenText(strGreen);
		listener.setBlueText(strBlue);
		listener.setAlphaText(strAlpha);
	}

	public void applyChanges(String strRed0, String strGreen0, String strBlue0,
			String strAlpha0, int colorSpace, final String defaultRed,
			final String defaultGreen, final String defaultBlue,
			final String defaultAlpha) {
		GeoList list = null;
		GeoList listAlpha = null;
		String strRed = strRed0;
		String strGreen = strGreen0;
		String strBlue = strBlue0;
		String strAlpha = strAlpha0;
		int defaults = 0;

		if (StringUtil.emptyTrim(strRed)) {
			strRed = defaultRed;
			defaults++;
		}
		if (StringUtil.emptyTrim(strGreen)) {
			strGreen = defaultGreen;
			defaults++;
		}
		if (StringUtil.emptyTrim(strBlue)) {
			strBlue = defaultBlue;
			defaults++;
		}
		if (StringUtil.emptyTrim(strAlpha)) {
			strAlpha = defaultAlpha;
			defaults++;
		}
		if (defaults < 4) {
			list = kernel.getAlgebraProcessor().evaluateToList(
					"{" + strRed + "," + strGreen + "," + strBlue + "}");

			listAlpha = kernel.getAlgebraProcessor().evaluateToList("{" + strRed
					+ "," + strGreen + "," + strBlue + "," + strAlpha + "}");
		}

		// set condition
		// try {
		if (list != null) { //
			if ((list.get(0) instanceof NumberValue) && // bugfix, enter "x"
															// for a color
					(list.get(1) instanceof NumberValue) && //
					(list.get(2) instanceof NumberValue) && //
					(list.size() == 3 || list.get(3) instanceof NumberValue)) {
				for (int i = 0; i < getGeosLength(); i++) {

					GeoElement geo = getGeoAt(i);
					if (geo.isFillable() && listAlpha != null) {
						geo.setColorFunction(listAlpha);
						list = listAlpha; // to have correct update
					} else {
						geo.setColorFunction(list);
					}
					geo.setColorSpace(colorSpace);
				}
			}

			list.updateRepaint();

			// to update "showObject" as well
			listener.updateSelection(getGeos());
		} else {
			// put back faulty text (for editing)

			setListenerRGBA(strRed, strGreen, strBlue, strAlpha);
		}
		storeUndoInfo();

	}

	@Override
	public boolean checkGeos() {
		// Applicable for all geos
		return true;
	}

	public void removeAll() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.removeColorFunction();
			geo.setObjColor(geo.getObjectColor());
			geo.updateRepaint();
		}

		listener.setRedText("");
		listener.setGreenText("");
		listener.setBlueText("");
		listener.setAlphaText("");

	}

	@Override
	public boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;


public class ShowConditionModel extends OptionsModel {
	public interface IShowConditionListener {
		void setText(String text);
		void updateSelection(Object[] geos);

		Object update(Object[] geos2);
	}
	
	private Kernel kernel;
	private IShowConditionListener listener;
	
	public ShowConditionModel(App app, IShowConditionListener listener) {
		kernel = app.getKernel();
		this.listener = listener;
	}

	public void updateProperties() {
		
		// take condition of first geo
		String strCond = "";
		GeoElement geo0 = getGeoAt(0);
		GeoBoolean cond = geo0.getShowObjectCondition();
		if (cond != null) {
			strCond = cond.getLabel(StringTemplate.editTemplate);
		}

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			cond = geo.getShowObjectCondition();
			if (cond != null) {
				String strCondGeo = cond.getLabel(StringTemplate.editTemplate);
				if (!strCond.equals(strCondGeo))
					strCond = "";
			}
		}

		listener.setText(strCond);
		
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isEuclidianShowable();
	}

	public void applyChanges(String strCond) {
		//processed = true;
		GeoBoolean cond;
		if (strCond == null || strCond.trim().length() == 0) {
			cond = null;
		} else {

			strCond = replaceEqualsSigns(strCond);

			cond = kernel.getAlgebraProcessor()
					.evaluateToBoolean(strCond, true);
		}

		if (cond != null || strCond.trim().length() == 0) {
			// set condition
			try {
				for (int i = 0; i < getGeosLength(); i++) {
					GeoElement geo = getGeoAt(i);
					geo.setShowObjectCondition(cond);

					// make sure object shown when condition removed
					if (cond == null)
						geo.updateRepaint();
				}

			} catch (CircularDefinitionException e) {
				listener.setText("");
				kernel.getApplication().showError("CircularDefinition");
			}

			if (cond != null)
				cond.updateRepaint();

			// to update "showObject" as well
			listener.updateSelection(getGeos());
		} else {
			// put back faulty condition (for editing)
			listener.setText(strCond);
		}

	}


	/**
	 * allows using a single = in condition to show object and dynamic color
	 * 
	 * @param strCond
	 *            Condition to be processed
	 * @return processed condition
	 */
	public static String replaceEqualsSigns(String strCond) {
		// needed to make next replace easier
		strCond = strCond.replaceAll(">=",
				ExpressionNodeConstants.strGREATER_EQUAL);
		strCond = strCond.replaceAll("<=",
				ExpressionNodeConstants.strLESS_EQUAL);
		strCond = strCond.replaceAll("==",
				ExpressionNodeConstants.strEQUAL_BOOLEAN);
		strCond = strCond
				.replaceAll("!=", ExpressionNodeConstants.strNOT_EQUAL);

		// allow A=B as well as A==B
		// also stops A=B doing an assignment of B to A :)
		return strCond
				.replaceAll("=", ExpressionNodeConstants.strEQUAL_BOOLEAN);

	}

	@Override
	public boolean updateMPanel(Object[] geos2) {
		return listener.update(geos2) != null;
	}
}

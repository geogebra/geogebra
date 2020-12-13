package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.advanced.AlgoSlopeField;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.main.App;

public class DrawArrowsModel extends OptionsModel {
	private Boolean drawArrow;

	private DrawArrowsModel.IDrawArrowListener listener;

	public interface IDrawArrowListener extends IComboListener {
		void setDrawAsArrowVisible(boolean value);

		void setDrawAsArrowsCheckbox(boolean checked);
	}
	public void setListener(DrawArrowsModel.IDrawArrowListener listener) {
		this.listener = listener;
	}

	public DrawArrowsModel(App app) {
		super(app);
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void updateProperties() {
		if (drawArrow != null && listener != null) {
			listener.setDrawAsArrowsCheckbox(drawArrow);
		}
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!(getGeoAt(i) instanceof GeoLocus)) {
				geosOK = false;
				break;
			}
			GeoElement geo = getGeoAt(i);
			if (listener != null) {
				drawArrow = checkDrawArrow();
				listener.setDrawAsArrowVisible(geo instanceof GeoLocus && geo
						.getParentAlgorithm() instanceof AlgoSlopeField);
			}
		}
		return geosOK;
	}

	public void applyDrawArrow(boolean checked) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo instanceof GeoLocus) {
				((GeoLocus) geo).drawAsArrows(checked);
				geo.updateRepaint();
			}
		}
		storeUndoInfo();
	}

	public boolean checkDrawArrow() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo instanceof GeoLocus) {
				return ((GeoLocus) geo).checkDrawArrows();
			}
		}
		return false;
	}

}

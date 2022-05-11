package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.StringUtil;

public class ImageCornerModel extends MultipleGeosModel {
	private Kernel kernel;
	private int cornerIdx;

	public ImageCornerModel(App app) {
		super(app);
		kernel = app.getKernel();
	}

	@Override
	public void updateProperties() {
		GeoPoint p0 = getPointAt(0);
		boolean isEqual = true;

		for (int i = 0; i < getGeosLength(); i++) {
			if (p0 != getPointAt(i)) {
				isEqual = false;
				break;
			}

		}

		if (isEqual && p0 != null) {
			if (getListener() instanceof GeoComboListener) {
				((GeoComboListener) getListener()).setSelectedItem(
					p0.getLabel(StringTemplate.defaultTemplate));
			}
		} else {
			getListener().setSelectedIndex(-1);
		}

	}

	public int getCornerNumber() {
		return cornerIdx < 2 ? (cornerIdx + 1) : (cornerIdx + 2);
	}

	private GeoImage getGeoImageAt(int index) {
		return (GeoImage) getObjectAt(index);
	}

	/**
	 * 
	 * @return if this point is the center point.
	 */
	public boolean isCenter() {
		return cornerIdx == GeoImage.CENTER_INDEX;
	}
	@Override
	protected void apply(int index, int value) {
		// Not used

	}

	public void applyChanges(final String strLoc, ErrorHandler handler) {
		GeoPointND newLoc = null;
		handler.resetError();
		if (!StringUtil.emptyTrim(strLoc)) {
			newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc,
					handler, true);
		}
		if (newLoc == null
				&& (cornerIdx == 0 || !StringUtil.emptyTrim(strLoc))) {
			return;
		}
		for (int i = 0; i < getGeosLength(); i++) {
			GeoImage im = getGeoImageAt(i);
			im.setCorner(newLoc, cornerIdx);
			im.updateRepaint();
		}
		storeUndoInfo();
	}

	protected GeoPoint getPointAt(int index) {
		return getGeoImageAt(index).getCorner(cornerIdx);
	}

	@Override
	protected boolean isValidAt(int index) {
		Object geo = getObjectAt(index);
		if (geo instanceof GeoImage) {
			GeoImage img = (GeoImage) geo;
			return !img.isAbsoluteScreenLocActive()
					&& img.isIndependent()
					&& (img.isCentered() == isCenter());
		}

		return false;
	}

	public int getCornerIdx() {
		return cornerIdx;
	}

	public void setCornerIdx(int cornerIdx) {
		this.cornerIdx = cornerIdx;
	}

	@Override
	protected int getValueAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

}

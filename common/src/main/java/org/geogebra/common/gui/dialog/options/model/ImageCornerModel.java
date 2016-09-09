package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;

public class ImageCornerModel extends MultipleGeosModel {
	private List<String> choices;
	private Kernel kernel;
	private int cornerIdx;

	public ImageCornerModel(App app) {
		super(app);
		kernel = app.getKernel();
		choices = new ArrayList<String>();	
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
			getListener().setSelectedItem(p0.getLabel(StringTemplate.defaultTemplate));
		} else {
			getListener().setSelectedIndex(-1);
		}




	}

	private int indexOf(final String item) {
		return choices.indexOf(item);
	}
	
	public int getCornerNumber() {
		return cornerIdx < 2 ? (cornerIdx + 1) : (cornerIdx + 2);
	}

	private GeoImage getGeoImageAt(int index) {
		return (GeoImage)getObjectAt(index);
	}

	@Override
	protected void apply(int index, int value) {
		// Not used

	}
	
	public void applyChanges(final String strLoc, ErrorHandler handler) {
		GeoPointND newLoc = null;
		handler.showError(null);
		if (strLoc == null || strLoc.trim().length() == 0) {
			// newLoc = null;
		} else {
			newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc,
					handler, true);
		}

		for (int i=0; i < getGeosLength(); i++) {
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
			if (img.isAbsoluteScreenLocActive() || !img.isIndependent()) {
				return false;
			}
			
			return true;
			
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

package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;

public class ImageCornerModel extends MultipleOptionsModel {
	private List<String> choices;
	private Kernel kernel;
	private int cornerIdx;
	private App app;
	public ImageCornerModel(App app, IComboListener listener) {
		super(listener);
		this.app = app;
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

	@Override
	public List<String> getChoiches(Localization loc) {
		TreeSet<GeoElement> points = kernel.getConstruction()
				.getGeoSetLabelOrder(GeoClass.POINT);
		choices.clear();
		choices.add("");
		Iterator<GeoElement> it = points.iterator();
		int count = 0;
		while (it.hasNext() || ++count > MAX_CHOICES) {
			GeoPointND p = (GeoPointND) it.next();
			choices.add(p
					.getLabel(StringTemplate.defaultTemplate));
		}

		return choices;
	}

	private GeoImage getGeoImageAt(int index) {
		return (GeoImage)getObjectAt(index);
	}

	@Override
	protected void apply(int index, int value) {
		// Not used

	}
	
	public void applyChanges(final String strLoc) {
		GeoPointND newLoc = null;

		if (strLoc == null || strLoc.trim().length() == 0) {
			// newLoc = null;
		} else {
			newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc,
					true, true);
		}

		for (int i=0; i < getGeosLength(); i++) {
			GeoImage im = getGeoImageAt(i);
			im.setCorner((GeoPoint) newLoc, cornerIdx);
			im.updateRepaint();
		}
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

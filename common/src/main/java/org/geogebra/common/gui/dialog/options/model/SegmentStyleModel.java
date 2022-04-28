package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.common.main.App;

public class SegmentStyleModel extends NumberOptionsModel {
	private IComboListener listener;
	private boolean isStartStyle;

	public SegmentStyleModel(App app, boolean isStartStyle) {
		super(app);
		this.isStartStyle = isStartStyle;
	}

	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

	@Override
	protected boolean isValidAt(int index) {
		boolean isValid = false;
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoSegment) {
			isValid = true;
		}
		return isValid;
	}

	@Override
	public void updateProperties() {
		GeoElement geo = getGeoAt(0);
		if (geo instanceof GeoSegment) {
			SegmentStyle style = isStartStyle ? ((GeoSegment) geo).getStartStyle()
					: ((GeoSegment) geo).getEndStyle();
			listener.setSelectedIndex(style.ordinal());
		}
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	@Override
	protected void apply(int index, int value) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoSegment) {
			if (isStartStyle) {
				((GeoSegment) geo).setStartStyle(SegmentStyle.values()[value]);
			} else {
				((GeoSegment) geo).setEndStyle(SegmentStyle.values()[value]);
			}
			geo.updateVisualStyleRepaint(GProperty.COMBINED);
			app.updateStyleBars();
		}
	}

	@Override
	protected int getValueAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoSegment) {
			return isStartStyle ? ((GeoSegment) geo).getStartStyle().ordinal()
					: ((GeoSegment) geo).getEndStyle().ordinal();
		}
		return 0;
	}

	public boolean isStartStyle() {
		return isStartStyle;
	}

}

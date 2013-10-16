package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.PointProperties;


public class PointSizeModel extends OptionsModel {
	public interface IPointSizeListener {
		void setSliderValue(int value);
	}

	private IPointSizeListener listener;
	
	public PointSizeModel(IPointSizeListener listener) {
		this.listener = listener;
	}
	
	/**
	 * change listener implementation for slider
	 */
	public void applyChanges(int value) {
		
		for (int i = 0; i < getGeosLength(); i++) {
			PointProperties point = (PointProperties) getGeoAt(i);
			point.setPointSize(value);
			point.updateRepaint();
		}
	}



		@Override
		public void updateProperties() {
			PointProperties geo0 = (PointProperties) getGeoAt(0);
			listener.setSliderValue(geo0.getPointSize());

		}

		@Override
		public boolean checkGeos() {
			boolean geosOK = true;
			for (int i = 0; i < getGeosLength(); i++) {
				GeoElement geo = getGeoAt(i);
				if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
						&& (!(geo.isGeoList() && ((GeoList) geo)
								.showPointProperties()))) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

	}

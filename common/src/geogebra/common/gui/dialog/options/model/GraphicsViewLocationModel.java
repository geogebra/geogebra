package geogebra.common.gui.dialog.options.model;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

public class GraphicsViewLocationModel extends OptionsModel {
	public interface IGraphicsViewLocationListener {
		public void selectView(int index, boolean isSelected);
	}
	
	private IGraphicsViewLocationListener listener;
	private App app;
	public GraphicsViewLocationModel(App app, IGraphicsViewLocationListener listener) {
		this.app = app;
		this.listener = listener;
		
	}

	@Override
	public void updateProperties() {
		boolean isInEV = false;
		boolean isInEV2 = false;

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN))
				isInEV = true;
			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN2))
				isInEV2 = true;
		}

		listener.selectView(0, isInEV);
		listener.selectView(1, isInEV2);
		
	}

	public void applyToEuclidianView1(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (value) {
				app.addToEuclidianView(geo);
			} else {
				app.removeFromEuclidianView(geo);
			}
		}
	}
	
	public void applyToEuclidianView2(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			EuclidianView ev2 = app.getEuclidianView2();

			if (value) {
				geo.addView(App.VIEW_EUCLIDIAN2);
				ev2.add(geo);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN2);
				ev2.remove(geo);
			}

		}
	}
	
	@Override
	public boolean checkGeos() {
		return true;
	}

}

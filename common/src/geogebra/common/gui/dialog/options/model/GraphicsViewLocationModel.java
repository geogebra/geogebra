package geogebra.common.gui.dialog.options.model;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian3D.EuclidianView3DInterface;
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
		boolean isInEV3D = false;

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN))
				isInEV = true;
			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN2))
				isInEV2 = true;
			if (geo.isVisibleInView3D())
				isInEV3D = true;
		}

		listener.selectView(0, isInEV);
		listener.selectView(1, isInEV2);
		listener.selectView(2, isInEV3D);
		
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
			EuclidianView ev2 = app.getEuclidianView2(1);

			if (value) {
				geo.addView(App.VIEW_EUCLIDIAN2);
				ev2.add(geo);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN2);
				ev2.remove(geo);
			}

		}
	}
	
	public void applyToEuclidianView3D(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			EuclidianView3DInterface ev3D = app.getEuclidianView3D();

			if (value) {
				geo.addView(App.VIEW_EUCLIDIAN3D);
				ev3D.add(geo);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN3D);
				ev3D.remove(geo);
			}

		}
	}

	@Override
	public boolean checkGeos() {
		return true;
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}

}

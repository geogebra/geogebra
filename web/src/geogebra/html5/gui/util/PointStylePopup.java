package geogebra.html5.gui.util;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.dialog.options.model.PointStyleModel;
import geogebra.common.gui.util.SelectionTable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.ImageData;

public class PointStylePopup extends PopupMenuButton {

	private static final int DEFAULT_SIZE = 4;
	private static HashMap<Integer, Integer> pointStyleMap;
	private static int mode;
	private PointStyleModel model;
	
	public static PointStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider) {
		EuclidianStyleBarStatic.pointStyleArray = EuclidianView.getPointStyles();
		
		PointStylePopup.mode = mode;
		
		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++)
			pointStyleMap.put(EuclidianStyleBarStatic.pointStyleArray[i], i);

		final GDimensionW pointStyleIconSize = new GDimensionW(20, iconHeight);
		ImageData[] pointStyleIcons = new ImageData[EuclidianStyleBarStatic.pointStyleArray.length];
		for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++)
			pointStyleIcons[i] = GeoGebraIcon.createPointStyleIcon(
					EuclidianStyleBarStatic.pointStyleArray[i], 4, pointStyleIconSize, geogebra.common.awt.GColor.BLACK,
					null);

		return new PointStylePopup((AppW) app, pointStyleIcons, 2, -1,
				pointStyleIconSize, geogebra.common.gui.util.SelectionTable.MODE_ICON,
				true, hasSlider);
	}

	private GDimensionW iconSize;

	public PointStylePopup(AppW app, Object[] data, Integer rows,
            Integer columns, GDimensionW iconSize, SelectionTable mode,
            boolean hasTable, boolean hasSlider) {
	    super(app, data, rows, columns, iconSize, mode, hasTable, hasSlider);
	    this.iconSize = iconSize;
	    model = null;
		
    }

	public void setModel(PointStyleModel model) {
		this.model = model;
	}
	
	@Override
	public void update(Object[] geos) {
		GeoElement geo;
		if (geos == null) {
			return;
		}
		
		if (model != null) {
			model.setGeos(geos);
		}
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			geo = (GeoElement) geos[i];
			if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
					&& (!(geo.isGeoList() && ((GeoList) geo)
							.showPointProperties()))) {
				geosOK = false;
				break;
			}
		}
		this.setVisible(geosOK);

		if (geosOK) {
			// setFgColor(((GeoElement)geos[0]).getObjectColor());
			setFgColor((GColorW) geogebra.common.awt.GColor.black);

			// if geo is a matrix, this will return a GeoNumeric...
			geo = ((GeoElement) geos[0])
					.getGeoElementForPropertiesDialog();

			// ... so need to check
			if (geo instanceof PointProperties) {
	//			setSliderValue(((PointProperties) geo).getPointSize());
				int pointStyle = ((PointProperties) geo)
						.getPointStyle();
				if (pointStyle == -1) // global default point style
					pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
				setSelectedIndex(pointStyleMap.get(pointStyle));
				this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
			}
		}
	}

	@Override
	public void handlePopupActionEvent(){
		super.handlePopupActionEvent();
		if (model != null) {
			model.applyChanges(getSelectedIndex());
		}
	}
	
	@Override
	public ImageData getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIcon.createPointStyleIcon(
					EuclidianStyleBarStatic.pointStyleArray[this.getSelectedIndex()],
					this.getSliderValue(), iconSize,
					geogebra.common.awt.GColor.BLACK, null);
		}
		return GeoGebraIcon.createEmptyIcon(iconSize.getWidth(),
				iconSize.getHeight());
	}
	
	@Override 
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}

}

package geogebra.html5.gui.util;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.dialog.options.model.IComboListener;
import geogebra.common.gui.dialog.options.model.PointStyleModel;
import geogebra.common.gui.util.SelectionTable;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.ImageData;

public class PointStylePopup extends PopupMenuButton implements IComboListener {

	private static final int DEFAULT_SIZE = 4;
	private static HashMap<Integer, Integer> pointStyleMap;
	private static int mode;
	private PointStyleModel model;
	
	public static PointStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider, PointStyleModel model) {
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
				true, hasSlider, model);
	}

	private GDimensionW iconSize;

	public PointStylePopup(AppW app, Object[] data, Integer rows,
            Integer columns, GDimensionW iconSize, SelectionTable mode,
            boolean hasTable, boolean hasSlider, PointStyleModel model) {
	    super(app, data, rows, columns, iconSize, mode, hasTable, hasSlider);
	    this.iconSize = iconSize;
	    this.model = model;
		
    }

	public void setModel(PointStyleModel model) {
		this.model = model;
	}
	
	@Override
	public void update(Object[] geos) {
		model.setGeos(geos);
		
		if (!model.hasGeos()) {
			return;
		}
		
		boolean geosOK = model.checkGeos(); 
		
		this.setVisible(geosOK);

		if (geosOK) {
			setFgColor((GColorW) geogebra.common.awt.GColor.black);
			model.updateProperties();
			PointProperties geo0 = (PointProperties) model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getPointSize());
			}
			
			setSelectedIndex(pointStyleMap.get(geo0.getPointStyle()));
			
			this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
		}
	}
	

	//			setSliderValue(((PointProperties) geo).getPointSize());
	@Override
	public void handlePopupActionEvent(){
		super.handlePopupActionEvent();
 		model.applyChanges(getSelectedIndex());
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

	public void setSelectedIndex(int index) {
	    super.setSelectedIndex(index);		
;
	    
    }

	public void addItem(String item) {
	    // TODO Auto-generated method stub
	    
    }

	public void setSelectedItem(String item) {
	    // TODO Auto-generated method stub
	    
    }

}

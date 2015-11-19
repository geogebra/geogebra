package org.geogebra.web.web.gui.util;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.main.AppW;

public class PointStylePopup extends PopupMenuButton implements IComboListener {

	private static final int DEFAULT_SIZE = 4;
	private static HashMap<Integer, Integer> pointStyleMap;
	private static int mode;
	private PointStyleModel model;
	private boolean euclidian3D;

	public static PointStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider, PointStyleModel model) {
		EuclidianStyleBarStatic.pointStyleArray = EuclidianView.getPointStyles();
		
		PointStylePopup.mode = mode;
		
		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++)
			pointStyleMap.put(EuclidianStyleBarStatic.pointStyleArray[i], i);

		final GDimensionW pointStyleIconSize = new GDimensionW(20, iconHeight);
		ImageOrText[] pointStyleIcons = new ImageOrText[EuclidianStyleBarStatic.pointStyleArray.length];
		for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++)
			pointStyleIcons[i] = GeoGebraIcon
			        .createPointStyleIcon(EuclidianStyleBarStatic.pointStyleArray[i]);

		return new PointStylePopup(app, pointStyleIcons, 2, -1,
		        pointStyleIconSize,
		        org.geogebra.common.gui.util.SelectionTable.MODE_ICON, true,
		        hasSlider, model);
	}

	public static PointStylePopup create(AppW app, int mode,
	        PointStyleModel model) {
		return new PointStylePopup(app, null, 1, -1, null,
		        org.geogebra.common.gui.util.SelectionTable.MODE_ICON, false, true,
		        model);
	}

	private GDimensionW iconSize;

	public PointStylePopup(AppW app, ImageOrText[] data, Integer rows,
            Integer columns, GDimensionW iconSize, SelectionTable mode,
            boolean hasTable, boolean hasSlider, PointStyleModel model) {
		super(app, data, rows, columns, mode, hasTable, hasSlider);
	    this.iconSize = iconSize;
	    this.model = model;
		euclidian3D = false;
    }

	public void setModel(PointStyleModel model) {
		this.model = model;
	}
	
	@Override
	public void update(Object[] geos) {
		updatePanel(geos);
	}

	@Override
	public Object updatePanel(Object[] geos) {
		model.setGeos(geos);
		
		if (!model.hasGeos()) {
			this.setVisible(false);
			return null;
		}
		
		boolean geosOK = model.checkGeos(); 
		
		this.setVisible(geosOK);

		if (geosOK) {
			getMyTable().setVisible(!euclidian3D);

			model.updateProperties();

			PointProperties geo0 = (PointProperties) model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getPointSize());
			}
			
			
			setSelectedIndex(pointStyleMap.get(euclidian3D ? 0 : geo0
			        .getPointStyle()));

			this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
		}
		return this;
	}
	

	//			setSliderValue(((PointProperties) geo).getPointSize());
	@Override
	public void handlePopupActionEvent(){
		super.handlePopupActionEvent();
 		model.applyChanges(getSelectedIndex());
	}
	
	@Override
	public ImageOrText getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIcon
			        .createPointStyleIcon(EuclidianStyleBarStatic.pointStyleArray[this
			                .getSelectedIndex()]);
		}
		return new ImageOrText();
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

	public boolean isEuclidian3D() {
		return euclidian3D;
	}

	public void setEuclidian3D(boolean euclidian3d) {
		euclidian3D = euclidian3d;
	}

	public void clearItems() {
		// TODO Auto-generated method stub

	}

}

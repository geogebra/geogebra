package geogebra.html5.gui.util;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.gui.dialog.options.model.IComboListener;
import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.util.SelectionTable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.ImageData;

public class LineStylePopup extends PopupMenuButton implements IComboListener {

	private static final int DEFAULT_SIZE = 2;
	private static HashMap<Integer, Integer> lineStyleMap;
	private static int mode;
	private LineStyleModel model;
	
	public static LineStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider, LineStyleModel model) {
		
		LineStylePopup.mode = mode;
		final GDimensionW lineStyleIconSize = new GDimensionW(80, iconHeight);
		ImageData [] lineStyleIcons = new ImageData[EuclidianStyleBarStatic.lineStyleArray.length];
		for (int i = 0; i < EuclidianStyleBarStatic.lineStyleArray.length; i++)
			lineStyleIcons[i] = GeoGebraIcon.createLineStyleIcon(
					EuclidianStyleBarStatic.lineStyleArray[i], 2, lineStyleIconSize, geogebra.common.awt.GColor.BLACK, null);
		
		lineStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianStyleBarStatic.lineStyleArray.length; i++)
			lineStyleMap.put(EuclidianStyleBarStatic.lineStyleArray[i], i);

		return new LineStylePopup((AppW) app, lineStyleIcons, -1, 1,
				lineStyleIconSize, geogebra.common.gui.util.SelectionTable.MODE_ICON,
				true, hasSlider, model);
	}

	private GDimensionW iconSize;

	public LineStylePopup(AppW app, Object[] data, Integer rows,
            Integer columns, GDimensionW iconSize, SelectionTable mode,
            boolean hasTable, boolean hasSlider, LineStyleModel model) {
	    super(app, data, rows, columns, iconSize, mode, hasTable, hasSlider);
	    this.iconSize = iconSize;
	    this.model = model;
		
    }

	public void setModel(LineStyleModel model) {
		this.model = model;
	}
	
	@Override
	public void update(Object[] geos) {
		model.setGeos(geos);
		
		if (!model.hasGeos() ) {
			return;
		}
		
		boolean geosOK = model.checkGeos(); 
		
		this.setVisible(geosOK);

		if (geosOK) {
			setFgColor((GColorW) geogebra.common.awt.GColor.black);
			model.updateProperties();
			GeoElement geo0 = model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getLineThickness());
			}
			
			setSelectedIndex(geo0.getLineType());
			
			this.setKeepVisible(false);
			//mode == EuclidianConstants.MODE_MOVE);
		}
	}
	

	//			setSliderValue(((PointProperties) geo).getPointSize());
	@Override
	public void handlePopupActionEvent(){
		super.handlePopupActionEvent();
 		model.applyLineType(getSelectedIndex());
	}
	
	@Override
	public ImageData getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIcon.createLineStyleIcon(
					EuclidianStyleBarStatic.lineStyleArray[this.getSelectedIndex()],
					this.getSliderValue(), iconSize,
					geogebra.common.awt.GColor.BLACK, null);
		
		}
		
		return GeoGebraIcon.createEmptyIcon(iconSize.getWidth(),
				iconSize.getHeight());
	}
	
	public void apply() {
		model.applyLineType(getSelectedIndex());
		model.applyThickness(getSliderValue());
	}
	@Override 
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}

	public void setSelectedIndex(int index) {
	    super.setSelectedIndex(lineStyleMap.get(index));	
;
	    
    }

}

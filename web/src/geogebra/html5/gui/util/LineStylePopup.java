package geogebra.html5.gui.util;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import geogebra.common.gui.util.SelectionTable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.ImageData;

public class LineStylePopup extends PopupMenuButton implements ILineStyleListener {

	private static final int DEFAULT_SIZE = 2;
	private static HashMap<Integer, Integer> lineStyleMap;
	private static int mode;
	private LineStyleModel model;

	public static LineStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider, LineStyleModel model) {

		LineStylePopup.mode = mode;
		
		final GDimensionW lineStyleIconSize = new GDimensionW(80, iconHeight);
		
		Integer styleCount = LineStyleModel.getStyleCount();
		ImageData [] lineStyleIcons = new ImageData[styleCount];
		
		for (int i = 0; i < styleCount; i++)
			lineStyleIcons[i] = GeoGebraIcon.createLineStyleIcon(
					LineStyleModel.getStyleAt(i), 2, lineStyleIconSize, geogebra.common.awt.GColor.BLACK, null);
		
		lineStyleMap = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < styleCount; i++)
			lineStyleMap.put(LineStyleModel.getStyleAt(i), i);

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
		setKeepVisible(false);
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

			selectLineType(geo0.getLineType());
			this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
		}
	}
	
	public void selectLineType(int type) {
		setSelectedIndex(lineStyleMap.get(type));
	}

//	@Override
//	public void handlePopupActionEvent(){
//		super.handlePopupActionEvent();
//		Integer style = lineStyleMap.get(getSelectedIndex());
//		model.applyLineType(style);
//	}

	@Override
	public ImageData getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIcon.createLineStyleIcon(
					LineStyleModel.getStyleAt(getSelectedIndex()),
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

	public void setValue(int value) {
		getMySlider().setValue(value);

	}

	public void setMinimum(int minimum) {
		getMySlider().setMinimum(minimum);

	}

	public void selectCommonLineStyle(boolean equalStyle, int type) {

	}	    
}



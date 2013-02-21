package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.factories.AwtFactory;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.web.awt.GFontW;

import com.google.gwt.canvas.client.Canvas;

public abstract class EuclidianViewWeb extends EuclidianView {
	public geogebra.web.awt.GGraphics2DW g2p = null;
	private GGraphics2D g2dtemp;
	private geogebra.common.awt.GColor backgroundColor = geogebra.web.awt.GColorW.white;
	
	public EuclidianViewWeb(EuclidianController ec, EuclidianSettings settings) {
	    super(ec, settings);
    }
	
	@Override
	protected final void drawActionObjects(GGraphics2D g)
	{
		//not part of canvas, not needed
	}

	@Override
	protected final void setAntialiasing(GGraphics2D g2)
	{
		//always on
	}
	
	@Override
    public final GFont getFont() {
		return new GFontW((GFontW)g2p.getFont());
    }
	

    public final GColor getBackgroundCommon() {
	    return backgroundColor ;
    }
	@Override
    public final void setBackground(GColor bgColor) {
		if (bgColor != null)
			backgroundColor = AwtFactory.prototype.newColor(
			bgColor.getRed(),
			bgColor.getGreen(),
			bgColor.getBlue(),
			bgColor.getAlpha());
    }
	
	@Override
	public final GGraphics2D getTempGraphics2D(GFont fontForGraphics)
	{
		// TODO
		if (this.g2dtemp == null)
			this.g2dtemp = new geogebra.web.awt.GGraphics2DW(Canvas.createIfSupported());
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}
	
	@Override
    protected final MyZoomer newZoomer() {
	    return new MyZoomerW(this);
    }
	

	@Override
    public final void paintBackground(geogebra.common.awt.GGraphics2D g2) {
		((geogebra.web.awt.GGraphics2DW)g2).drawGraphics(
				(geogebra.web.awt.GGraphics2DW)bgGraphics, 0, 0, null);
	}
	
	

}

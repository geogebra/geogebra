package geogebra.web.gui.util;


import geogebra.common.awt.GColor;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GFontW;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.awt.GLine2DW;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.util.BasicIcons;
import geogebra.html5.openjdk.awt.geom.Polygon;
import geogebra.web.gui.images.StyleBarResources;

import java.util.HashMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class GeoGebraIcon extends BasicIcons{



	/** creates LineStyle icon
	 * @param dashStyle
	 * @param thickness
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return Canvas with icon drawn
	 */
	public static ImageOrText createLineStyleIcon(int dashStyle, int thickness, GColor fgColor, GColor bgColor) {
		ImageOrText ret = new ImageOrText();
		if(dashStyle >= lineStyleIcons.length){
			return new ImageOrText();
		}
		ret.url = lineStyleIcons[dashStyle].getSafeUri().asString();
		return ret;
    }
	
	public static ImageOrText createEmptyIcon(int width, int height){

		return new ImageOrText();//TODO
	}
	private static StyleBarResources LafIcons = StyleBarResources.INSTANCE;
	private static ImageResource[] pointStyleIcons =  {
		(LafIcons.point_full()),
		(LafIcons.point_cross_diag()),
		(LafIcons.point_empty()),
		(LafIcons.point_cross()),
		(LafIcons.point_diamond()),
		(LafIcons.point_diamond_empty()),
		(LafIcons.point_up()),
		(LafIcons.point_down()),
		(LafIcons.point_right()),
		(LafIcons.point_left())};
	private static ImageResource[] lineStyleIcons = {
		(LafIcons.line_solid()),
		(LafIcons.line_dashed_long()),
		(LafIcons.line_dashed_short()),
		(LafIcons.line_dotted()),
		(LafIcons.line_dash_dot()) };
	/**
	 * @param pointStyle
	 * @param pointSize
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return
	 */
	public static ImageOrText createPointStyleIcon(int pointStyle, int pointSize, GDimensionW iconSize, GColor fgColor, GColor bgColor) {
		ImageOrText ret = new ImageOrText();
		ret.url = pointStyleIcons[pointStyle].getSafeUri().asString();
		return ret;
    
    }
	
	public static ImageOrText createColorSwatchIcon(float alpha, GDimensionW iconSize, GColor fgColor, GColor bgColor){
		ImageOrText ret = new ImageOrText();
		float[] rgb = new float[3];
		if(fgColor!=null){
			fgColor.getRGBColorComponents(rgb);
			ret.fgColor = geogebra.common.factories.AwtFactory.prototype.newColor( rgb[0], rgb[1], rgb[2], alpha);
		}
		if(bgColor!=null){
			bgColor.getRGBColorComponents(rgb);
			ret.bgColor = geogebra.common.factories.AwtFactory.prototype.newColor( rgb[0], rgb[1], rgb[2], alpha);
		}
		return ret;
	}

	public static ImageOrText createTextSymbolIcon(String symbol,GFontW font, GDimensionW iconSize, GColor fgColor, GColor bgColor){
		ImageOrText ret = new ImageOrText();
		ret.text = symbol;
		ret.fgColor = fgColor;
		ret.bgColor = bgColor;
		return ret;
	}
	
	public static ImageOrText createNullSymbolIcon(int width, int height){

		Canvas c = getTmpCanvas(width, height);

		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(GColor.GRAY);
		// draw a rectangle with an x inside
		g2.drawRect(3, 3, width-6, height-6);
		int k = 7;
		g2.drawLine(k, k, width-k, height-k);
		g2.drawLine(k, height-k, width-k, k );
		return new ImageOrText();
	}

	public static ImageData createDownTriangleIcon() {
		int h = 18;
		int w = 12;
		
		Canvas icon = getTmpCanvas(w,h);
	    icon.setCoordinateSpaceHeight(h);
	    icon.setCoordinateSpaceWidth(w);
	    GGraphics2DW g2 = new GGraphics2DW(icon);
	    g2.setColor(GColor.WHITE);
	    g2.fillRect(0, 0, g2.getCanvas().getCoordinateSpaceWidth(), g2.getCanvas().getCoordinateSpaceHeight());
	    g2.setColor(GColor.GRAY);
	    
	    int midx = w/2;
		int midy = h/2;
		
		Polygon p = new Polygon();
		p.addPoint(midx-3,midy-3);
		p.addPoint(midx+3,midy-3);
		p.addPoint(midx,midy+3);

		g2.fillPolygon(p);
	    return g2.getImageData(0, 0, w, h);
    }

	public static ImageData createStringIcon(String str, GFontW font, boolean isBold, boolean isItalic, 
			boolean isCentered, GDimensionW iconSize, GColor fgColor, GColor bgColor){
		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas c = getTmpCanvas(w,h);
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null)
			g2.setBackground(bgColor);

		g2.setColor (fgColor);
		//font = font.deriveFont((h-6)*1.0f);
		if(isBold)
			font = (GFontW) font.deriveFont(GFontW.BOLD);
		if(isItalic)
			font = (GFontW) font.deriveFont(geogebra.common.awt.GFont.ITALIC);
		g2.setFont (font);


		//FontMetrics fm = g2.getFontMetrics ();
		TextMetrics fm = g2.getCanvas().getContext2d().measureText(str);
		double symbolWidth = fm.getWidth();
		//int ascent = fm.getMaxAscent ();
		//int descent= fm.getMaxDescent ();
		double x = (isCentered) ? w/2 - symbolWidth/2 : 1;
		double mid_y = 0; // there is not easy way to check the height of the text now h/2 - descent/2 + ascent/2 - 1;

		g2.drawString (str,(int) x, (int) mid_y);
		
		return g2.getImageData(0, 0, w, h);
    }


	public static ImageOrText createFileImageIcon(App app, String url, float alpha, GDimensionW iconSize){

		ImageOrText ret = new ImageOrText();
		ret.url = url;
		return ret;
	}
	
	public static ImageData ensureIconSize(ImageData icon, GDimensionW iconSize){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		int h2 = icon.getHeight();
		int w2 = icon.getWidth();
		if(h2 == h && w2 == w) 
			return icon;

		int wInset = (w - w2) > 0 ? (w-w2)/2 : 0;
		int hInset = (h - h2) > 0 ? (h-h2)/2 : 0;
		

		GGraphics2DW g2 = new GGraphics2DW(getTmpCanvas(w, h));
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		try {	
			if(icon !=null){
				g2.putImageData(icon, wInset, hInset);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return g2.getImageData(0, 0, w, h);
	}
	
	private static HashMap<String, ImageElement> rightIcons = new HashMap<String, ImageElement>();

	public static ImageData joinIcons(ImageData leftIcon,
            ImageResource rightIcon) {
		int w1 = leftIcon.getWidth();
		int w2 = rightIcon.getWidth();
		int h1 = leftIcon.getHeight();
		int h2 = rightIcon.getHeight();
		int h = Math.max(h1, h2);
		int mid = h/2;
		Canvas c = getTmpCanvas(w1 + w2, h);
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.putImageData(leftIcon, 0, mid - h1/2);
		String url = rightIcon.getSafeUri().asString();
		if (!rightIcons.containsKey(rightIcon.getSafeUri().asString())) {
			rightIcons.put(url,ImageElement.as(new Image(url).getElement()));
		}
		g2.getCanvas().getContext2d().drawImage(rightIcons.get(url), w1, mid - h2 / 2, w2, h2);

		return g2.getImageData(0, 0, w1 +  w2, h);
    }

	public static ImageOrText createResourceImageIcon(App app,
            ImageResource res, float alpha, GDimensionW dim) {
		ImageOrText ret = new ImageOrText();
		ret.url = res.getSafeUri().asString();
	    return ret;
    }

	private static void drawTick(GLine2DW tick, double angle){
		tick.setLine(13+37*Math.cos(angle),
				27-37*Math.sin(angle),
				13+43*Math.cos(angle),
				27-43*Math.sin(angle));
	}

	public static ImageOrText createDecorAngleIcon(int id, GDimensionW iconSize){
		ImageOrText ret = new ImageOrText();
		ImageResource url = null;
		switch(id){
			case GeoElement.DECORATION_ANGLE_TWO_ARCS:
				url =  GuiResources.INSTANCE.deco_angle_2lines();
			break;
			case GeoElement.DECORATION_ANGLE_THREE_ARCS:
				url =  GuiResources.INSTANCE.deco_angle_3lines();
			break;
			case GeoElement.DECORATION_ANGLE_ONE_TICK:
				url =  GuiResources.INSTANCE.deco_angle_1stroke();
			break;
			case GeoElement.DECORATION_ANGLE_TWO_TICKS:
				url =  GuiResources.INSTANCE.deco_angle_2strokes();
			break;
			case GeoElement.DECORATION_ANGLE_THREE_TICKS:
				url =  GuiResources.INSTANCE.deco_angle_3strokes();
			break;			
//			 Michael Borcherds 2007-11-19 BEGIN
			case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
				url =  GuiResources.INSTANCE.deco_angle_arrow_down();
			break;
			case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
				url =  GuiResources.INSTANCE.deco_angle_arrow_up();
			break;
			default:
				url =  GuiResources.INSTANCE.deco_angle_1line();
//			 Michael Borcherds 2007-11-19 END
		}
		ret.url = url.getSafeUri().asString();
		return ret;

	}

	public static ImageOrText createDecorSegmentIcon(int id, GDimensionW iconSize) {
		ImageOrText ret = new ImageOrText();
		ImageResource url = null;

		switch (id) {		
		case GeoElement.DECORATION_SEGMENT_ONE_TICK:
			url =  GuiResources.INSTANCE.deco_segment_1stroke();
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
			url =  GuiResources.INSTANCE.deco_segment_2strokes();
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
			url =  GuiResources.INSTANCE.deco_segment_3strokes();
			break;
		// Michael Borcherds 20071006 start
		case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
			url =  GuiResources.INSTANCE.deco_segment_1arrow();
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
			url =  GuiResources.INSTANCE.deco_segment_2arrows();
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
			url =  GuiResources.INSTANCE.deco_segment_3arrows();
			break;
		default:
			url =  GuiResources.INSTANCE.deco_segment_none();
			break;
		// Michael Borcherds 20071006 end
		}

		ret.url = url.getSafeUri().asString();
		return ret;

    }

	public static ImageOrText createAxesStyleIcon(int id, GDimensionW iconSize) {
		ImageOrText ret = new ImageOrText();
		ImageResource url = null;
		switch(id){
			case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW:
				url =  GuiResources.INSTANCE.deco_axes_arrow();
				break;
			case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS:
				url =  GuiResources.INSTANCE.deco_axes_arrows();
				break;
			case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED:
				url =  GuiResources.INSTANCE.deco_axes_arrow_filled();
				break;
			case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED:
				url =  GuiResources.INSTANCE.deco_axes_arrows_filled();
				break;
			default:
				url =  GuiResources.INSTANCE.deco_axes_none();
		}
		ret.url = url.getSafeUri().asString();
		return ret;
	}

	public static ImageOrText createStringIcon(String string) {
	    ImageOrText ret = new ImageOrText();
	    ret.text = string;
	    return ret;
    }
}

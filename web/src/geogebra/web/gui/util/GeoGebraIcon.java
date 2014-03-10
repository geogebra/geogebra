package geogebra.web.gui.util;


import geogebra.common.awt.GColor;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.html5.awt.GArc2DW;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GFontW;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.awt.GLine2DW;
import geogebra.html5.awt.GeneralPath;
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
		int w = iconSize.getWidth();
		int h = iconSize.getHeight();
		GLine2DW tick =new GLine2DW();
		GArc2DW arc =new GArc2DW();
	    GeneralPath polygon = new GeneralPath(); // Michael Borcherds 2007-10-28
		
		
		Canvas c = getTmpCanvas(w, h);
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.clearRect(0,0, w, h);
		g2.setColor(GColor.BLACK);
		g2.drawLine(13,27, 67, 27);
		g2.drawLine(13,27,67,3);
		arc.setArcByCenter(13,27,40,0,24,GArc2DW.OPEN);
		g2.draw(arc);
		switch(id){
			case GeoElement.DECORATION_ANGLE_TWO_ARCS:
				arc.setArcByCenter(13,27,35,0,24,GArc2DW.OPEN);
				g2.draw(arc);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_ARCS:
				arc.setArcByCenter(13,27,35,0,24,GArc2DW.OPEN);
				g2.draw(arc);
				arc.setArcByCenter(13,27,45,0,24,GArc2DW.OPEN);
				g2.draw(arc);
			break;
			case GeoElement.DECORATION_ANGLE_ONE_TICK:
				drawTick(tick, Math.toRadians(12));
				g2.draw(tick);
			break;
			case GeoElement.DECORATION_ANGLE_TWO_TICKS:
				drawTick(tick, Math.toRadians(9.6));
				g2.draw(tick);
				drawTick(tick, Math.toRadians(14.4));
				g2.draw(tick);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_TICKS:
				drawTick(tick, Math.toRadians(12));
				g2.draw(tick);
				drawTick(tick, Math.toRadians(7));
				g2.draw(tick);
				drawTick(tick, Math.toRadians(16));
				g2.draw(tick);
			break;			
//			 Michael Borcherds 2007-11-19 BEGIN
			case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
				polygon.reset();
			    polygon.moveTo(56,15);
			    polygon.lineTo(48,19);
			    polygon.lineTo(50,10);
			    polygon.lineTo(56,15);
			    polygon.closePath();
				g2.fill(polygon);
			break;
			case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
				polygon.reset();
			    polygon.moveTo(54,27);
			    polygon.lineTo(48,20);
			    polygon.lineTo(56,18);
			    polygon.lineTo(54,27);
			    polygon.closePath();
				g2.fill(polygon);
			break;
//			 Michael Borcherds 2007-11-19 END
		}
		//TODO
		ImageOrText ret = new ImageOrText();
		return ret;
	}

	public static ImageOrText createDecorSegmentIcon(int id, GDimensionW iconSize) {
		int width = iconSize.getWidth();
		int height = iconSize.getHeight();
		
		Canvas c = getTmpCanvas(width, height);
		GGraphics2DW g2 = new GGraphics2DW(c);
		
		g2.clearRect(0, 0, width, height);
		g2.setColor(GColor.BLACK);
		int mid = height / 2;
		g2.drawLine(0, mid, width, mid);

		switch (id) {
		case GeoElement.DECORATION_NONE:
			break;
		case GeoElement.DECORATION_SEGMENT_ONE_TICK:
			int quart = mid / 2;
			int mid_width = width / 2;
			g2.drawLine(mid_width, quart, mid_width, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
			quart = mid / 2;
			mid_width = width / 2;
			g2.drawLine(mid_width - 1, quart, mid_width - 1, mid + quart);
			g2.drawLine(mid_width + 2, quart, mid_width + 2, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
			quart = mid / 2;
			mid_width = width / 2;
			g2.drawLine(mid_width, quart, mid_width, mid + quart);
			g2.drawLine(mid_width + 3, quart, mid_width + 3, mid + quart);
			g2.drawLine(mid_width - 3, quart, mid_width - 3, mid + quart);
			break;
		// Michael Borcherds 20071006 start
		case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
			quart = mid / 2;
			mid_width = width / 2;
			g2.drawLine(mid_width, mid, mid_width - quart, mid - quart);
			g2.drawLine(mid_width, mid, mid_width - quart, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
			quart = mid / 2;
			mid_width = width / 2;
			g2.drawLine(mid_width - 3, mid, mid_width - quart - 3, mid - quart);
			g2.drawLine(mid_width - 3, mid, mid_width - quart - 3, mid + quart);
			g2.drawLine(mid_width + 3, mid, mid_width - quart + 3, mid - quart);
			g2.drawLine(mid_width + 3, mid, mid_width - quart + 3, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
			quart = mid / 2;
			mid_width = width / 2;
			g2.drawLine(mid_width, mid, mid_width - quart, mid - quart);
			g2.drawLine(mid_width, mid, mid_width - quart, mid + quart);
			g2.drawLine(mid_width + 6, mid, mid_width - quart + 6, mid - quart);
			g2.drawLine(mid_width + 6, mid, mid_width - quart + 6, mid + quart);
			g2.drawLine(mid_width - 6, mid, mid_width - quart - 6, mid - quart);
			g2.drawLine(mid_width - 6, mid, mid_width - quart - 6, mid + quart);
			break;
		// Michael Borcherds 20071006 end
		}

		return new ImageOrText();

    }

	public static ImageOrText createStringIcon(String string) {
	    ImageOrText ret = new ImageOrText();
	    ret.text = string;
	    return ret;
    }
}

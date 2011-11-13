package geogebra.euclidian;

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.PointProperties;
import geogebra.main.Application;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Class to hold style settings for the Euclidian stylebar
 * @author G. Sturr
 *
 */
public class MiniStyle{
	
	private Application app;
	
	final public static int MODE_PEN = 0;
	final public static int MODE_STANDARD = 1;
	
	public int lineStyle;
	public int lineSize;
	public int pointSize;
	public int pointStyle;
	public Color color;
	public int colorIndex;
	public float alpha;
	public boolean isBold = false;
	public boolean isItalic = false;

	private Color[] colorList;
	
	
	/************************************************
	 * Constructs MiniStyle
	 */
	public MiniStyle(Application app, int mode){	
		
		this.app = app;
		colorList  = createStyleBarColorList();
		if(mode == MODE_PEN)
			setPenDefaults();

		else if(mode == MODE_STANDARD)
			setStandardDefaults();
		
		
	}
	
	
	
	//==============================================
	// set defaults

	public void setPenDefaults(){	
		lineStyle = EuclidianView.LINE_TYPE_FULL;
		pointSize = 3;
		lineSize = 3;
		color = Color.black;
		colorIndex = 23;  // index for black
		alpha = 1.0f;
	}
	
	public void setStandardDefaults(){	
		lineStyle = EuclidianView.DEFAULT_LINE_TYPE;
		pointSize = EuclidianView.DEFAULT_POINT_SIZE;
		lineSize = EuclidianView.DEFAULT_LINE_THICKNESS;
		colorIndex = 0;  // index for red
		color = colorList[colorIndex];
		alpha = ConstructionDefaults.DEFAULT_POLYGON_ALPHA;
	}
	
	

	
	//==============================================
	// methods to apply styles to selected geos
	
	public void applyLineStyle() {

		ArrayList geos = app.getSelectedGeos();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);
			geo.setLineType(lineStyle);
			geo.updateRepaint();			
		}
	
	}
	
	public void applyPointStyle() {
		
		ArrayList geos = app.getSelectedGeos();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);

			if (geo instanceof PointProperties) {
				((PointProperties)geo).setPointSize(pointSize);
				((PointProperties)geo).setPointStyle(pointStyle);
				geo.updateRepaint();
				}
		}
	}


	public void applyLineSize() {

		ArrayList geos = app.getSelectedGeos();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);
			geo.setLineThickness(lineSize);
			geo.updateRepaint();
		}
	}


	
	public void applyColor() {
		
		ArrayList geos = app.getSelectedGeos();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);
			geo.setObjColor(color);
			geo.updateRepaint();
		}
	}

	public void applyAlpha() {

		ArrayList geos = app.getSelectedGeos();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);
			geo.setAlphaValue(alpha);
			geo.updateRepaint();
		}
	}

	public void applyBold() {

		int fontStyle = 0;
		if (isBold) fontStyle += 1;
		if (isItalic) fontStyle += 2;
		
		ArrayList geos = app.getSelectedGeos();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);
			if(geo.isGeoText()){
			((GeoText)geo).setFontStyle(fontStyle);
			geo.updateRepaint();
			}
		}
	}

	
	
	
	public void setAllProperties() {

		ArrayList geos = app.getSelectedGeos();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);
			if (geo instanceof PointProperties) {
				PointProperties p = (PointProperties)geo;
				p.setPointSize(pointSize);
			}

			geo.setLineThickness(lineSize);
			geo.setLineType(lineStyle);
			geo.setObjColor(color);
			geo.setAlphaValue(alpha);

			geo.update();
			
		}

	}
	
	
	
	
	
	
	//==============================================
	// colors
	
	public Color getStyleBarColor(int index){
		return colorList[index];
	}
	
	public Color[] getStyleBarColorList(int index){
		return colorList;
	}
	
	
	private Color[] createStyleBarColorList() {
		
		Color[]	primaryColors = new Color[] {		
				new Color(255, 0, 0), // Red
				new Color(255, 153, 0), // Orange
				new Color(255, 255, 0), // Yellow
				new Color(0, 255, 0), // Green 
				new Color(0, 255, 255), // Cyan 
				new Color(0, 0, 255), // Blue
				new Color(153, 0, 255), // Purple
				new Color(255, 0, 255) // Magenta 
		};
		
		Color[] c = new Color[24];
		for(int i = 0; i< 8; i++){
			
			// first row: primary colors
			c[i] = primaryColors[i];
			
			// second row: modified primary colors
			float[] hsb = Color.RGBtoHSB(c[i].getRed(), c[i].getGreen(), c[i].getBlue(), null); 
			int rgb = Color.HSBtoRGB((float) (.9*hsb[0]), (float) (.5*hsb[1]), (float) (1*hsb[2]));
			c[i+8] = new Color(rgb);
			
			// third row: gray scales (white ==> black)
			float p = 1.0f - i/7f;
			c[i+16] = new Color(p,p,p);
		}
			
		return c;
	
	}
	
	
	
	
	
	
}

package geogebra.euclidian;

import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.main.SelectionManager;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.main.AppD;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Class to hold style settings for the Euclidian stylebar
 * 
 * @author G. Sturr
 * 
 */
public class MiniStyle {

	private final AppD app;
	private final SelectionManager selection;

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
	public MiniStyle(AppD app, int mode) {

		this.app = app;
		this.selection = app.getSelectionManager();
		colorList = createStyleBarColorList();
		if (mode == MODE_PEN)
			setPenDefaults();

		else if (mode == MODE_STANDARD)
			setStandardDefaults();
	}

	// ==============================================
	// set defaults

	public void setPenDefaults() {
		lineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;
		pointSize = 3;
		lineSize = 3;
		color = Color.black;
		colorIndex = 23; // index for black
		alpha = 1.0f;
	}

	public void setStandardDefaults() {
		lineStyle = EuclidianStyleConstants.DEFAULT_LINE_TYPE;
		pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
		lineSize = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
		colorIndex = 0; // index for red
		color = colorList[colorIndex];
		alpha = ConstructionDefaults.DEFAULT_POLYGON_ALPHA;
	}

	// ==============================================
	// methods to apply styles to selected geos

	public void applyLineStyle() {

		ArrayList<GeoElement> geos = selection.getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setLineType(lineStyle);
			geo.updateRepaint();
		}
	}

	public void applyPointStyle() {

		ArrayList<GeoElement> geos = selection.getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);

			if (geo instanceof PointProperties) {
				((PointProperties) geo).setPointSize(pointSize);
				((PointProperties) geo).setPointStyle(pointStyle);
				geo.updateRepaint();
			}
		}
	}

	public void applyLineSize() {

		ArrayList<GeoElement> geos = selection.getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setLineThickness(lineSize);
			geo.updateRepaint();
		}
	}

	public void applyColor() {

		ArrayList<GeoElement> geos = selection.getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setObjColor(new geogebra.awt.GColorD(color));
			geo.updateRepaint();
		}
	}

	public void applyAlpha() {

		ArrayList<GeoElement> geos = selection.getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			geo.setAlphaValue(alpha);
			geo.updateRepaint();
		}
	}

	public void applyBold() {

		int fontStyle = 0;
		if (isBold)
			fontStyle += 1;
		if (isItalic)
			fontStyle += 2;

		ArrayList<GeoElement> geos = selection.getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo.isGeoText()) {
				((GeoText) geo).setFontStyle(fontStyle);
				geo.updateRepaint();
			}
		}
	}

	public void setAllProperties() {

		ArrayList<GeoElement> geos = selection.getSelectedGeos();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof PointProperties) {
				PointProperties p = (PointProperties) geo;
				p.setPointSize(pointSize);
			}

			geo.setLineThickness(lineSize);
			geo.setLineType(lineStyle);
			geo.setObjColor(new geogebra.awt.GColorD(color));
			geo.setAlphaValue(alpha);

			geo.updateVisualStyle();

		}
	}

	// ==============================================
	// colors

	public Color getStyleBarColor(int index) {
		return colorList[index];
	}

	private static Color[] createStyleBarColorList() {

		Color[] primaryColors = new Color[] { new Color(255, 0, 0), // Red
				new Color(255, 153, 0), // Orange
				new Color(255, 255, 0), // Yellow
				new Color(0, 255, 0), // Green
				new Color(0, 255, 255), // Cyan
				new Color(0, 0, 255), // Blue
				new Color(153, 0, 255), // Purple
				new Color(255, 0, 255) // Magenta
		};

		Color[] c = new Color[24];
		for (int i = 0; i < 8; i++) {

			// first row: primary colors
			c[i] = primaryColors[i];

			// second row: modified primary colors
			float[] hsb = Color.RGBtoHSB(c[i].getRed(), c[i].getGreen(),
					c[i].getBlue(), null);
			int rgb = Color.HSBtoRGB((float) (.9 * hsb[0]),
					(float) (.5 * hsb[1]), (1 * hsb[2]));
			c[i + 8] = new Color(rgb);

			// third row: gray scales (white ==> black)
			float p = 1.0f - i / 7f;
			c[i + 16] = new Color(p, p, p);
		}

		return c;

	}

}

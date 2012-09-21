package geogebra.mobile.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoText;

import java.util.ArrayList;

public class StyleBarStatic
{
	/**
	 * only repaint once (not once per geo)
	 * 
	 * @see EuclidianStyleBarStatic#applyLineStyle
	 * 
	 * @param geos
	 * @param lineStyleIndex
	 * @param lineSize
	 * @return
	 */
	public static boolean applyLineStyle(ArrayList<GeoElement> geos, int lineStyleIndex, int lineSize) {
		int lineStyle = EuclidianStyleBarStatic.lineStyleArray[lineStyleIndex];
		boolean needUndo = false;
		
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo.getLineType() != lineStyle
					|| geo.getLineThickness() != lineSize) {
				geo.setLineType(lineStyle);
				geo.setLineThickness(lineSize);
				if(i == geos.size() - 1){
					geo.updateRepaint();
				}
				else{
					geo.updateCascade(); 
				}				
				needUndo = true;
			}
		}
		
		return needUndo;
	}
	
	
	/**
	 * @see EuclidianStyleBarStatic#applyColor
	 * 
	 * @param geos
	 * @param color
	 * @param alpha
	 * @return
	 */
	public static boolean applyColor(ArrayList<GeoElement> geos, GColor color, float alpha)
	{
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++)
		{
			GeoElement geo = geos.get(i);
			// apply object color to all other geos except images or text
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoImage || geo.getGeoElementForPropertiesDialog() instanceof GeoText))
				if ((geo.getObjectColor() != color || geo.getAlphaValue() != alpha))
				{
					geo.setObjColor(color);
					// if we change alpha for functions, hit won't work properly
					if (geo.isFillable())
						geo.setAlphaValue(alpha);
					geo.updateVisualStyle();
					needUndo = true;
				}
		}

		return needUndo;
	}
}

package geogebra.mobile.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoText;

import java.util.ArrayList;
import java.util.List;

public class StyleBarStatic
{
	/**
	 * @see EuclidianStyleBarStatic#processSourceCommon(String, ArrayList,
	 *      EuclidianViewInterfaceCommon)
	 */
	public static void showAxes(EuclidianViewInterfaceCommon view)
	{
		view.setShowAxes(!(view.getShowXaxis() || view.getShowYaxis()), false);
		view.repaint();
	}

	/**
	 * @see EuclidianStyleBarStatic#processSourceCommon(String, ArrayList,
	 *      EuclidianViewInterfaceCommon)
	 */
	public static void showGrid(EuclidianViewInterfaceCommon view)
	{
		view.showGrid(!view.getShowGrid());
		view.repaint();
	}

	/**
	 * only repaint once (not once per geo); does not change LineSize
	 * 
	 * @see EuclidianStyleBarStatic#applyLineStyle
	 * 
	 * @param geos
	 * @param lineStyleIndex
	 * @param lineSize
	 * @return
	 */
	public static boolean applyLineStyle(ArrayList<GeoElement> geos, int lineStyleIndex)
	{
		int lineStyle = EuclidianStyleBarStatic.lineStyleArray[lineStyleIndex].intValue();
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++)
		{
			GeoElement geo = geos.get(i);
			if (geo.getLineType() != lineStyle)
			{
				geo.setLineType(lineStyle);
				geo.updateCascade();
				needUndo = true;
			}
			if (i == geos.size() - 1 && needUndo)
			{
				geo.updateRepaint();
			}
		}

		return needUndo;
	}

	/**
	 * only repaint once (not once per geo); does not change LineStyle
	 * 
	 * @see EuclidianStyleBarStatic#applyLineStyle
	 * 
	 * @param geos
	 * @param lineStyleIndex
	 * @param lineSize
	 * @return
	 */
	public static boolean applyLineSize(ArrayList<GeoElement> geos, int lineSize)
	{
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++)
		{
			GeoElement geo = geos.get(i);
			if (geo.getLineThickness() != lineSize)
			{
				geo.setLineThickness(lineSize);
				geo.updateCascade();
				needUndo = true;
			}
			if (i == geos.size() - 1 && needUndo)
			{
				geo.updateRepaint();
			}
		}

		return needUndo;
	}

	/**
	 * does not change alpha-value
	 * 
	 * @see EuclidianStyleBarStatic#applyColor
	 * 
	 * @param geos
	 * @param color
	 * @param alpha
	 * @return
	 */
	public static boolean applyColor(ArrayList<GeoElement> geos, GColor color)
	{
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++)
		{
			GeoElement geo = geos.get(i);
			// apply object color to all other geos except images or text
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoImage || geo.getGeoElementForPropertiesDialog() instanceof GeoText))
				if (geo.getObjectColor() != color)
				{
					geo.setObjColor(color);
					geo.updateVisualStyle();
					needUndo = true;
				}
		}

		return needUndo;
	}

	/**
	 * does not change color
	 * 
	 * @see EuclidianStyleBarStatic#applyColor
	 * 
	 * @param fillable
	 * @param color
	 * @param alpha
	 * @return
	 */
	public static boolean applyAlpha(List<GeoElement> fillable, float alpha)
	{
		boolean needUndo = false;
		for (int i = 0; i < fillable.size(); i++)
		{
			GeoElement geo = fillable.get(i);
			// apply object color to all other geos except images or text
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoImage || geo.getGeoElementForPropertiesDialog() instanceof GeoText))
				if (geo.getAlphaValue() != alpha)
				{
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

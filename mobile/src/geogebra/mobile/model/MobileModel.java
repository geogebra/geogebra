package geogebra.mobile.model;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class MobileModel
{

	ArrayList<GeoElement> selectedElements = new ArrayList<GeoElement>();

	public MobileModel()
	{
	}

	public void select(GeoElement geo)
	{
		geo.setSelected(true);
		this.selectedElements.add(geo);
	}

	public boolean select(Hits hits, Test geoclass, int max)
	{
		boolean success = false;
		Hits h = new Hits();
		hits.getHits(geoclass, h);
		for (int i = 0; i < max; i++)
		{
			if (i < h.size())
			{
				this.selectedElements.add(h.get(i));
				success = true;
			}
		}
		return success;
	}

	public void resetSelection()
	{
		for (GeoElement geo : this.selectedElements)
		{
			geo.setSelected(false);
		}
		this.selectedElements.clear();
	}

	/**
	 * 
	 * @param class1
	 *            required Class
	 * @return the first element of the given Class; null in case there is no
	 *         such element
	 */
	public GeoElement getElement(Class<? extends GeoElement> class1)
	{
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				return geo;
			}
		}
		return null;
	}

	public GeoElement getElement(Class<? extends GeoElement> class1, int i)
	{
		int count = 0;
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				if (i == count)
				{
					return geo;
				}
				count++;
			}
		}
		return null;
	}

	public ArrayList<GeoElement> getAll(Class<? extends GeoElement> class1)
	{
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				geos.add(geo);
			}
		}
		return geos;
	}

	public int getNumberOf(Class<? extends GeoElement> class1)
	{
		int count = 0;
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				count++;
			}
		}
		return count;
	}

	public GeoElement lastSelected()
	{
		return this.selectedElements.size() > 0 ? this.selectedElements
				.get(this.selectedElements.size() - 1) : null;
	}

}

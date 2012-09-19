package geogebra.mobile.model;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.Test;
import geogebra.mobile.utils.ToolBarCommand;

import java.awt.Point;
import java.util.ArrayList;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class MobileModel
{

	private GuiModel guiModel;
	private Kernel kernel;
	private ArrayList<GeoElement> selectedElements = new ArrayList<GeoElement>();
	private ToolBarCommand lastCmd = null;
	private boolean commandFinished = false;

	public MobileModel(GuiModel model, Kernel k)
	{
		this.guiModel = model;
		this.kernel = k;
	}

	public void select(GeoElement geo)
	{
		if (geo == null)
		{
			return;
		}
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
				select(h.get(i));
				success = true;
			}
		}
		return success;
	}

	/**
	 * selects one element of the given class (if there are elements of different
	 * classes, the first class that has elements in the hits will be used)
	 * 
	 * @param hits
	 *          the Hits to get the elements form
	 * @param geoclass
	 *          Array of possible classes
	 * @return success (false if there is no element of any of the given classes
	 */
	public boolean selectOutOf(Hits hits, Test[] geoclass)
	{
		Hits h = new Hits();
		for (int i = 0; i < geoclass.length; i++)
		{
			hits.getHits(geoclass[i], h);
			if (h.size() > 0)
			{
				select(h.get(0));
				return true;
			}
		}
		return false;
	}

	private void deselect(GeoElement geo)
	{
		geo.setSelected(false);
		this.selectedElements.remove(geo);
	}

	public void resetSelection()
	{
		for (GeoElement geo : this.selectedElements)
		{
			geo.setSelected(false);
		}
		this.selectedElements.clear();
	}

	public void checkCommand()
	{
		if (this.lastCmd != this.guiModel.getCommand())
		{
			this.lastCmd = this.guiModel.getCommand();
			resetSelection();
		}
		if (this.commandFinished)
		{
			resetSelection();
			this.commandFinished = false;
		}
	}

	public ArrayList<GeoElement> getSelectedGeos()
	{
		return this.selectedElements;
	}

	/**
	 * 
	 * @param class1
	 *          required Class
	 * @return the first element of the given Class; null in case there is no such
	 *         element
	 */
	public GeoElement getElement(Test geoclass)
	{
		for (GeoElement geo : this.selectedElements)
		{
			if (geoclass.check(geo))
			{
				return geo;
			}
		}
		return null;
	}

	public GeoElement getElement(Test geoclass, int i)
	{
		int count = 0;
		for (GeoElement geo : this.selectedElements)
		{
			if (geoclass.check(geo))
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

	private GeoElement getElementFrom(Test[] geoclass)
	{
		for (int i = 0; i < geoclass.length; i++)
		{
			for (GeoElement geo : this.selectedElements)
			{
				if (geoclass[i].check(geo))
				{
					return geo;
				}
			}
		}
		return null;
	}

	public ArrayList<GeoElement> getAll(Test geoclass)
	{
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		for (GeoElement geo : this.selectedElements)
		{
			if (geoclass.check(geo))
			{
				geos.add(geo);
			}
		}
		return geos;
	}

	public int getTotalNumber()
	{
		return this.selectedElements.size();
	}

	public int getNumberOf(Test geoclass)
	{
		int count = 0;
		for (GeoElement geo : this.selectedElements)
		{
			if (geoclass.check(geo))
			{
				count++;
			}
		}
		return count;
	}

	public GeoElement lastSelected()
	{
		return this.selectedElements.size() > 0 ? this.selectedElements.get(this.selectedElements.size() - 1) : null;
	}

	public void handleEvent(Hits hits)
	{
		boolean draw = false;

		switch (this.guiModel.getCommand())
		{
		// commands that need one point or a point and a Path or a Region
		case AttachDetachPoint:
			// TODO
			attachDetach(hits, null);
			break;

		// commands that need two points
		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
		case RayThroughTwoPoints:
		case VectorBetweenTwoPoints:
		case CircleWithCenterThroughPoint:
		case Semicircle:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need one point and one line
		case PerpendicularLine:
		case ParallelLine:
		case Parabola:
			selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE });
			draw = getNumberOf(Test.GEOPOINT) >= 1 && getNumberOf(Test.GEOLINE) >= 1;
			break;

		// commands that need two points or one segment
		case MidpointOrCenter:
		case PerpendicularBisector:
			if (!select(hits, Test.GEOPOINT, 1))
			{
				select(hits, Test.GEOSEGMENT, 1);
			}
			draw = getNumberOf(Test.GEOSEGMENT) >= 1 || getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need any two objects
		case IntersectTwoObjects:
			// TODO
			// intersect(hits);
			break;

		// commands that need tree points
		case CircleThroughThreePoints:
		case CircularArcWithCenterBetweenTwoPoints:
		case CircularSectorWithCenterBetweenTwoPoints:
		case CircumCirculuarArcThroughThreePoints:
		case CircumCircularSectorThroughThreePoints:
		case Ellipse:
		case Hyperbola:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) >= 3;
			break;

		// commands that need five points
		case ConicThroughFivePoints:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) >= 5;
			break;

		// commands that need an unknown number of points
		case PolylineBetweenPoints:
		case Polygon:
		case RigidPolygon:
		case VectorPolygon:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) > 2 && getElement(Test.GEOPOINT).equals(lastSelected());
			break;

		// special commands
		case Select:
			if (hits.size() == 0)
			{
				resetSelection();
			}

			for (GeoElement geo : hits)
			{
				if (geo.isSelected())
				{
					if (!hits.containsGeoPoint())
					{
						deselect(geo);
					}
					else if (geo instanceof GeoPoint)
					{
						deselect(geo);
					}
				}
				else
				{
					select(geo);
				}
			}
			break;

		default:
			break;
		}

		// draw anything other than a point
		if (draw)
		{
			GeoElement newElement = null;
			GeoElement[] newArray = new GeoElement[0];

			switch (this.guiModel.getCommand())
			{
			case LineThroughTwoPoints:
				newElement = this.kernel.getAlgoDispatcher().Line(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1));
				break;
			case SegmentBetweenTwoPoints:
				newElement = this.kernel.getAlgoDispatcher().Segment(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1));
				break;
			case RayThroughTwoPoints:
				newElement = this.kernel.getAlgoDispatcher().Ray(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1));
				break;
			case VectorBetweenTwoPoints:
				newElement = this.kernel.getAlgoDispatcher().Vector(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1));
				break;
			case CircleWithCenterThroughPoint:
				newElement = this.kernel.getAlgoDispatcher().Circle(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1));
				break;
			case Semicircle:
				newElement = this.kernel.getAlgoDispatcher().Semicircle(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1));
				break;
			case PerpendicularLine:
				newElement = this.kernel.getAlgoDispatcher().OrthogonalLine(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoLine) getElement(Test.GEOLINE));
				break;
			case ParallelLine:
				newElement = this.kernel.getAlgoDispatcher().Line(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoLine) getElement(Test.GEOLINE));
				break;
			case MidpointOrCenter:
				if (getNumberOf(Test.GEOSEGMENT) > 0)
				{
					newElement = this.kernel.getAlgoDispatcher().Midpoint(null, (GeoSegment) getElement(Test.GEOSEGMENT));
				}
				else if (getNumberOf(Test.GEOPOINT) >= 2)
				{
					newElement = this.kernel.getAlgoDispatcher().Midpoint(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1));
				}
				break;
			case PerpendicularBisector:
				if (getNumberOf(Test.GEOSEGMENT) > 0)
				{
					newElement = this.kernel.getAlgoDispatcher().LineBisector(null, (GeoSegment) getElement(Test.GEOSEGMENT));
				}
				else if (getNumberOf(Test.GEOPOINT) >= 2)
				{
					newElement = this.kernel.getAlgoDispatcher().LineBisector(null, (GeoPoint) getElement(Test.GEOPOINT),
					    (GeoPoint) getElement(Test.GEOPOINT, 1));
				}
				break;
			case Parabola:
				newElement = this.kernel.getAlgoDispatcher().Parabola(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoLine) getElement(Test.GEOLINE));
				break;
			case CircleThroughThreePoints:
				newElement = this.kernel.getAlgoDispatcher().Circle(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1),
				    (GeoPoint) getElement(Test.GEOPOINT, 2));
				break;
			case CircularArcWithCenterBetweenTwoPoints:
				newElement = this.kernel.getAlgoDispatcher().CircleArc(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1),
				    (GeoPoint) getElement(Test.GEOPOINT, 2));
				break;
			case CircularSectorWithCenterBetweenTwoPoints:
				newElement = this.kernel.getAlgoDispatcher().CircleSector(null, (GeoPoint) getElement(Test.GEOPOINT),
				    (GeoPoint) getElement(Test.GEOPOINT, 1), (GeoPoint) getElement(Test.GEOPOINT, 2));
				break;
			case CircumCirculuarArcThroughThreePoints:
				newElement = this.kernel.getAlgoDispatcher().CircumcircleArc(null, (GeoPoint) getElement(Test.GEOPOINT),
				    (GeoPoint) getElement(Test.GEOPOINT, 1), (GeoPoint) getElement(Test.GEOPOINT, 2));
				break;
			case CircumCircularSectorThroughThreePoints:
				newElement = this.kernel.getAlgoDispatcher().CircumcircleSector(null, (GeoPoint) getElement(Test.GEOPOINT),
				    (GeoPoint) getElement(Test.GEOPOINT, 1), (GeoPoint) getElement(Test.GEOPOINT, 2));
				break;
			case Ellipse:
				newElement = this.kernel.getAlgoDispatcher().Ellipse(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1),
				    (GeoPoint) getElement(Test.GEOPOINT, 2));
				break;
			case Hyperbola:
				newElement = this.kernel.getAlgoDispatcher().Hyperbola(null, (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1),
				    (GeoPoint) getElement(Test.GEOPOINT, 2));
				break;
			case ConicThroughFivePoints:
				newElement = this.kernel.getAlgoDispatcher().Conic(
				    null,
				    new GeoPoint[] { (GeoPoint) getElement(Test.GEOPOINT), (GeoPoint) getElement(Test.GEOPOINT, 1), (GeoPoint) getElement(Test.GEOPOINT, 2),
				        (GeoPoint) getElement(Test.GEOPOINT, 3), (GeoPoint) getElement(Test.GEOPOINT, 4), });
				break;
			case PolylineBetweenPoints:
				ArrayList<GeoElement> geos = getAll(Test.GEOPOINT);
				geos.remove(geos.size() - 1);
				newArray = this.kernel.PolyLineND(null, geos.toArray(new GeoPoint[geos.size()]));
				break;
			case Polygon:
				ArrayList<GeoElement> geos2 = getAll(Test.GEOPOINT);
				geos2.remove(geos2.size() - 1);
				newArray = this.kernel.Polygon(null, geos2.toArray(new GeoPoint[geos2.size() - 1]));
				break;
			case RigidPolygon:
				ArrayList<GeoElement> geos3 = getAll(Test.GEOPOINT);
				geos3.remove(geos3.size() - 1);
				newArray = this.kernel.RigidPolygon(null, geos3.toArray(new GeoPoint[geos3.size() - 1]));
				break;
			case VectorPolygon:
				ArrayList<GeoElement> geos4 = getAll(Test.GEOPOINT);
				geos4.remove(geos4.size() - 1);
				newArray = this.kernel.VectorPolygon(null, geos4.toArray(new GeoPoint[geos4.size() - 1]));
				break;
			default:
			}

			resetSelection();

			select(newElement);
			for (GeoElement geo : newArray)
			{
				select(geo);
			}

			this.commandFinished = true;
		}
	}

	public boolean handleEvent(GeoElement geo)
	{
		checkCommand();
		if (this.guiModel.getCommand() == ToolBarCommand.DeleteObject)
		{
			geo.remove();
			return false;
		}
		Hits hits = new Hits();
		hits.add(geo);
		handleEvent(hits);
		this.guiModel.updateStylingBar(this);
		return true;
	}

	public void attachDetach(Hits hits, Point c)
	{
		EuclidianViewInterfaceCommon view = this.kernel.getApplication().getActiveEuclidianView();

		selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.PATH, Test.GEOCONICND, Test.GEOFUNCTION, Test.GEOFUNCTIONNVAR, Test.REGION3D });
		GeoPoint point = (GeoPoint) getElement(Test.GEOPOINT);
		Path path = (Path) getElement(Test.PATH);
		Region region = (Region) getElementFrom(new Test[] { Test.GEOCONICND, Test.GEOFUNCTION, Test.GEOFUNCTIONNVAR, Test.REGION3D });
		if (point != null)
		{
			Point p = c != null ? c : new Point((int) point.getX(), (int) point.getY());

			if (!point.isIndependent())
			{
				this.kernel.getAlgoDispatcher().detach(point, view);
				this.commandFinished = true;
			}
			else if (region != null)
			{
				this.kernel.getAlgoDispatcher().attach(point, region, view, p.getX(), p.getY());
				this.commandFinished = true;
			}
			else if (path != null)
			{
				this.kernel.getAlgoDispatcher().attach(point, path, view, p.getX(), p.getY());
				this.commandFinished = true;
			}
		}
	}
}
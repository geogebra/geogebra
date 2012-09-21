package geogebra.mobile.model;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.Test;
import geogebra.common.main.MyError;
import geogebra.mobile.utils.ToolBarCommand;

import java.awt.Point;
import java.util.ArrayList;

import com.google.gwt.event.dom.client.KeyUpEvent;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class MobileModel
{

	private Kernel kernel;
	private GuiModel guiModel;
	private boolean commandFinished = false;
	private ToolBarCommand command;
	private ArrayList<GeoElement> selectedElements = new ArrayList<GeoElement>();

	public MobileModel(Kernel k)
	{
		this.kernel = k;
	}

	public void setGuiModel(GuiModel model)
	{
		this.guiModel = model;
	}

	public ToolBarCommand getCommand()
	{
		return this.command;
	}

	public void setCommand(ToolBarCommand cmd)
	{
		if (this.command != null && this.command.equals(cmd))
		{
			return;
		}
		resetSelection();
		this.command = cmd;
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

	public void deselect(GeoElement geo)
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

	/**
	 * 
	 * @return alpha value of the fillable that was last selected; -1 in case no
	 *         fillable geo that was selected
	 */
	public float getLastAlpha()
	{
		float alpha = -1f;
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.isFillable())
			{
				alpha = geo.getAlphaValue();
			}
		}
		return alpha;
	}

	public void handleEvent(Hits hits, Point point)
	{
		this.guiModel.closeOptions();

		boolean draw = false;
		if (this.commandFinished)
		{
			resetSelection();
			this.commandFinished = false;
		}

		switch (this.command)
		{
		// commands that only draw one point
		case NewPoint:
		case ComplexNumbers:
		case PointOnObject:
			resetSelection();
			select(hits, Test.GEOPOINT, 1);
			break;

		// commands that need one point or a point and a Path or a Region
		case AttachDetachPoint:
			attachDetach(hits, point);
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
		case Move_Mobile:
			for (GeoElement geo : hits)
			{
				select(geo);
			}
			break;

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
		case DeleteObject:
			for (GeoElement geo : hits)
			{
				geo.remove();
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

			switch (this.command)
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

		this.kernel.notifyRepaint();

		if (this.guiModel != null)
		{
			this.guiModel.updateStylingBar(this);
		}
	}

	private void attachDetach(Hits hits, Point c)
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
				resetSelection();
				select(point);
				this.commandFinished = true;
			}
			else if (region != null)
			{
				this.kernel.getAlgoDispatcher().attach(point, region, view, p.getX(), p.getY());
				resetSelection();
				select(point);
				this.commandFinished = true;
			}
			else if (path != null)
			{
				this.kernel.getAlgoDispatcher().attach(point, path, view, p.getX(), p.getY());
				resetSelection();
				select(point);
				this.commandFinished = true;
			}
		}
	}

	/**
	 * @see geogebra.web.gui.inputbar.AlgebraInputW#onKeyUp(KeyUpEvent event)
	 * 
	 * @param input
	 *          the new command
	 */
	public void newInput(String input)
	{
		try
		{
			this.kernel.clearJustCreatedGeosInViews();
			if (input == null || input.length() == 0)
			{
				return;
			}

			// this.app.setScrollToShow(true);
			GeoElement[] geos;
			try
			{
				if (input.startsWith("/"))
				{
					String cmd = input.substring(1);

					// TODO
					this.kernel.getApplication().getPythonBridge().eval(cmd);
					geos = new GeoElement[0];
				}
				else
				{
					geos = this.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(input, true, false, true);

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet)
					{
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			catch (MyError e)
			{
				e.printStackTrace();
				return;
			}

			// create texts in the middle of the visible view
			// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
			if (geos.length > 0 && geos[0] != null && geos[0].isGeoText())
			{
				GeoText text = (GeoText) geos[0];
				if (!text.isTextCommand() && text.getStartPoint() == null)
				{

					Construction cons = text.getConstruction();

					// TODO
					EuclidianViewInterfaceCommon ev = this.kernel.getApplication().getActiveEuclidianView();

					boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					GeoPoint p = new GeoPoint(text.getConstruction(), null, (ev.getXmin() + ev.getXmax()) / 2, (ev.getYmin() + ev.getYmax()) / 2, 1.0);
					cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

					try
					{
						text.setStartPoint(p);
						text.update();
					}
					catch (CircularDefinitionException e1)
					{
						e1.printStackTrace();
					}
				}
			}
			// this.app.setScrollToShow(false);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
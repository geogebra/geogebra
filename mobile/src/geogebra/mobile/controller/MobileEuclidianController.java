package geogebra.mobile.controller;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.mobile.euclidian.MouseEvent;
import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/**
 * Receives the events from the canvas and sends the orders to the kernel.
 * 
 * @author Thomas Krismayer
 * @see geogebra.common.euclidian.EuclidianController EuclidianController
 * 
 */
public class MobileEuclidianController extends EuclidianController implements
		TouchStartHandler, TouchEndHandler, TouchMoveHandler, MouseDownHandler,
		MouseMoveHandler, MouseUpHandler, ClickHandler
{

	private GuiModel guiModel;
	private ArrayList<GeoPointND> oldPoints = new ArrayList<GeoPointND>();
	private ArrayList<GeoLineND> oldLines = new ArrayList<GeoLineND>();
	private ToolBarCommand lastCmd;
	private GPoint origin;
	private boolean moving;

	public MobileEuclidianController()
	{
		this.mode = -1;
	}

	@Override
	protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complex)
	{
		return super.createNewPoint(hits, onPathPossible, inRegionPossible,
				intersectPossible, false, complex);
	}

	public void setView(EuclidianView euclidianView)
	{
		this.view = euclidianView;
	}

	@Override
	public void setKernel(Kernel k)
	{
		this.kernel = k;
		this.app = this.kernel.getApplication();
	}

	@Override
	public void setApplication(App app)
	{
	}

	@Override
	protected void initToolTipManager()
	{
	}

	@Override
	protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1)
	{
		return null;
	}

	@Override
	protected void resetToolTipManager()
	{
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		event.preventDefault();

	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		event.preventDefault();

	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		event.preventDefault();

	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		event.preventDefault();
		this.guiModel.closeOptions();

		this.origin = new GPoint(event.getX(), event.getY());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		event.preventDefault();
		if (this.moving
				|| (this.origin != null && (Math.abs(event.getX()
						- this.origin.getX()) > 10 || Math.abs(event.getY()
						- this.origin.getY()) > 10)))
		{

			this.mouseLoc = new GPoint(this.origin.getX(), this.origin.getY());

			MouseEvent mEvent = new MouseEvent(event.getX(), event.getY());

			if (!this.moving)
			{
				this.view.setHits(this.origin);
				if (this.view.getHits().isEmpty())
				{
					this.mode = EuclidianConstants.MODE_TRANSLATEVIEW;
				} else
				{
					this.mode = EuclidianConstants.MODE_MOVE;
				}

				this.moving = true;

				// get the mode and the object to move
				handleMousePressedForMoveMode(mEvent, false);

				// if added again, moved objects are not selected (while they
				// are moved)
				// -> moving objects becomes a lot slower
				// removeSelection();
			}

			this.startPoint = new GPoint2D.Double(
					this.view.toRealWorldCoordX(this.origin.getX()),
					this.view.toRealWorldCoordY(this.origin.getY()));

			wrapMouseDragged(mEvent);

			this.origin = new GPoint(event.getX(), event.getY());
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{

		event.preventDefault();

		this.origin = null;

		if (this.moving)
		{
			this.moving = false;
			this.mode = this.guiModel.getCommand().getMode();

			// object that was moved loses selection
			removeSelection();

			return;
		}

		ToolBarCommand cmd = this.guiModel.getCommand();

		if (this.lastCmd != cmd)
		{
			this.oldPoints = new ArrayList<GeoPointND>();
			this.oldLines = new ArrayList<GeoLineND>();
			this.lastCmd = cmd;
			resetSelection();
		}

		boolean draw = false;

		this.mouseLoc = new GPoint(event.getX(), event.getY());
		this.mode = this.guiModel.getCommand().getMode();

		// draw the new point
		switchModeForMousePressed(null);

		this.view.setHits(this.mouseLoc);
		Hits hits = this.view.getHits();

		switch (cmd)
		{
		// commands that need two points
		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
		case RayThroughTwoPoints:
		case VectorBetweenTwoPoints:
		case CircleWithCenterThroughPoint:
		case Semicircle:
			recordPoint(hits);
			draw = this.oldPoints.size() == 2;
			break;

		// commands that need one point and one line
		case PerpendicularLine:
		case ParallelLine:
		case Parabola:
			recordElement(hits);
			draw = this.oldPoints.size() >= 1 && this.oldLines.size() >= 1;
			break;

		// commands that need two points or one segment
		case MidpointOrCenter:
		case PerpendicularBisector:
			recordPoint(hits);
			if (hits.size() > 0 && hits.get(0) instanceof GeoSegment)
			{
				this.oldLines.add((GeoLineND) hits.get(0));
				draw = true;
			} else if (this.oldPoints.size() == 2)
			{
				draw = true;
			}
			break;

		// commands that need any two objects
		case IntersectTwoObjects:
			intersect(hits);
			break;

		// commands that need tree points
		case CircleThroughThreePoints:
		case CircularArcWithCenterBetweenTwoPoints:
		case CircularSectorWithCenterBetweenTwoPoints:
		case CircumCirculuarArcThroughThreePoints:
		case CircumCircularSectorThroughThreePoints:
		case Ellipse:
		case Hyperbola:
			recordPoint(hits);
			draw = this.oldPoints.size() == 3;
			break;

		// commands that need five points
		case ConicThroughFivePoints:
			recordPoint(hits);
			draw = this.oldPoints.size() == 5;
			break;

		// commands that need an unknown number of points
		// case PolylineBetweenPoints:
		// case Polygon:
		// recordPoint(hits);
		// draw = (this.oldPoints.size() > 1)
		// && hits.contains(this.oldPoints.get(0));
		// break;

		case PolylineBetweenPoints:
			polyline(hits);
			break;
		case Polygon:
			polygon(hits);
			break;

		// other commands
		case DeleteObject:
			for (int i = 0; i < hits.size(); i++)
			{
				hits.get(i).remove();
			}
			break;
		case Select:
			for (GeoElement geo : hits)
			{
				this.app.getSelectedGeos().add(geo);
				this.selectedGeos.add(geo);
				geo.setSelected(true);
			}

			this.kernel.notifyRepaint();
			break;

		// commands that need one point - nothing to do anymore
		default:
		}

		// draw anything other than a point
		if (draw)
		{
			switch (cmd)
			{
			case LineThroughTwoPoints:
				this.kernel.Line(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			case SegmentBetweenTwoPoints:
				this.kernel.Segment(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			case RayThroughTwoPoints:
				this.kernel.Ray(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			case VectorBetweenTwoPoints:
				this.kernel.Vector(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			case CircleWithCenterThroughPoint:
				this.kernel.Circle(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			case Semicircle:
				this.kernel.Semicircle(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			case PerpendicularLine:
				this.kernel.OrthogonalLine(null,
						(GeoPoint) this.oldPoints.get(0),
						(GeoLine) this.oldLines.get(0));
				break;
			case ParallelLine:
				this.kernel.Line(null, (GeoPoint) this.oldPoints
						.get(this.oldPoints.size() - 1),
						(GeoLine) this.oldLines.get(this.oldLines.size() - 1));
				break;
			case MidpointOrCenter:
				if (this.oldLines.size() > 0)
				{
					this.kernel.Midpoint(null,
							(GeoSegment) this.oldLines.get(0));
				} else if (this.oldPoints.size() >= 2)
				{
					this.kernel.Midpoint(null,
							(GeoPoint) this.oldPoints.get(0),
							(GeoPoint) this.oldPoints.get(1));
				}
				break;
			case PerpendicularBisector:
				if (this.oldLines.size() > 0)
				{
					this.kernel.LineBisector(null,
							(GeoSegment) this.oldLines.get(0));
				} else if (this.oldPoints.size() >= 2)
				{
					this.kernel.LineBisector(null,
							(GeoPoint) this.oldPoints.get(0),
							(GeoPoint) this.oldPoints.get(1));
				}
				break;
			case Parabola:
				this.kernel.Parabola(null, (GeoPoint) this.oldPoints.get(0),
						(GeoLine) this.oldLines.get(0));
				break;
			case CircleThroughThreePoints:
				this.kernel.Circle(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2));
				break;
			case CircularArcWithCenterBetweenTwoPoints:
				this.kernel.CircleArc(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2));
				break;
			case CircularSectorWithCenterBetweenTwoPoints:
				this.kernel.CircleSector(null,
						(GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2));
				break;
			case CircumCirculuarArcThroughThreePoints:
				this.kernel.CircumcircleArc(null,
						(GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2));
				break;
			case CircumCircularSectorThroughThreePoints:
				this.kernel.CircumcircleSector(null,
						(GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2));
				break;
			case Ellipse:
				this.kernel.Ellipse(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2));
				break;
			case Hyperbola:
				this.kernel.Hyperbola(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2));
				break;
			case ConicThroughFivePoints:
				this.kernel.Conic(null, new GeoPoint[] {
						(GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1),
						(GeoPoint) this.oldPoints.get(2),
						(GeoPoint) this.oldPoints.get(3),
						(GeoPoint) this.oldPoints.get(4) });
				break;
			// case PolylineBetweenPoints:
			// this.oldPoints.remove(this.oldPoints.size() - 1);
			// this.kernel.PolyLineND(null, this.oldPoints
			// .toArray(new GeoPointND[this.oldPoints.size() - 1]));
			// break;
			// case Polygon:
			// this.oldPoints.remove(this.oldPoints.size() - 1);
			// this.kernel.PolygonND(null, this.oldPoints
			// .toArray(new GeoPointND[this.oldPoints.size() - 1]));
			// break;
			default:
			}

			this.oldPoints = new ArrayList<GeoPointND>();
			this.oldLines = new ArrayList<GeoLineND>();
		}

	}

	@Override
	public void onClick(ClickEvent event)
	{
		event.preventDefault();
	}

	public void setGuiModel(GuiModel model)
	{
		this.guiModel = model;
	}

	private void recordPoint(Hits hits)
	{
		hits.removePolygons();
		if (hits.containsGeoPoint())
		{
			GeoPoint point = getNearestPoint(hits);
			this.oldPoints.add(point);
		} else
		{
			this.oldPoints.add((GeoPointND) this.movedGeoElement);
		}
	}

	/**
	 * 
	 * @param hits
	 * @return the hit element; null in case no element was hit
	 */
	private GeoElement recordElement(Hits hits)
	{
		if (hits.containsGeoPoint())
		{
			GeoPoint point = getNearestPoint(hits);
			this.oldPoints.add(point);
			return point;
		}

		// no points, but other objects
		if (hits.size() > 0)
		{
			for (int i = 0; i < hits.size(); i++)
			{
				if (hits.get(i) instanceof GeoLineND
						&& !this.oldLines.contains(hits.get(i)))
				{
					this.oldLines.add((GeoLineND) hits.get(i));
					return hits.get(i);
				}
			}
		}

		// no point and no line found
		this.oldPoints.add((GeoPointND) this.movedGeoElement);
		return null;
	}

	private GeoPoint getNearestPoint(Hits hits)
	{
		GeoPoint nearest = null;
		double distNearestSquare = 0.0;

		Iterator<GeoElement> iterator = hits.iterator();
		GeoElement e = iterator.next();

		while (e != null)
		{
			if (e instanceof GeoPointND)
			{
				double distanceSquare = Math.pow(
						(((GeoPoint) e).getX() - this.xRW), 2)
						+ Math.pow((((GeoPoint) e).getY() - this.yRW), 2);
				if (nearest == null || distanceSquare < distNearestSquare)
				{
					nearest = (GeoPoint) e;
					distNearestSquare = distanceSquare;
				}
			}
			e = iterator.hasNext() ? iterator.next() : null;
		}
		return nearest;
	}

	private void removeSelection()
	{
		boolean repaint = this.app.getSelectedGeos().size() > 0
				|| this.movedGeoPoint != null;

		for (GeoElement g : this.app.getSelectedGeos())
		{
			g.setSelected(false);
			g.setHighlighted(false);
		}

		if (this.movedGeoPoint != null)
		{
			((GeoElement) this.movedGeoPoint).setSelected(false);
			((GeoElement) this.movedGeoPoint).setHighlighted(false);
		}

		if (repaint)
		{
			this.app.setSelectedGeos(new ArrayList<GeoElement>());
			this.kernel.notifyRepaint();
		}
	}

	/**
	 * @see EuclidianView#clearSelections(boolean repaint, boolean
	 *      updateSelection)
	 * 
	 *      prevent repaint for every list
	 */
	private void resetSelection()
	{
		this.selectedGeos.clear();

		this.selectedNumbers.clear();
		this.selectedNumberValues.clear();
		this.selectedPoints.clear();
		this.selectedLines.clear();
		this.selectedSegments.clear();
		this.selectedConicsND.clear();
		this.selectedVectors.clear();
		this.selectedPolygons.clear();
		this.selectedGeos.clear();
		this.selectedFunctions.clear();
		this.selectedCurves.clear();
		this.selectedLists.clear();
		this.selectedPaths.clear();
		this.selectedRegions.clear();

		this.app.clearSelectedGeos(true, false);

		// if we clear selection and highlighting,
		// we may want to clear justCreatedGeos also
		clearJustCreatedGeos();

		// clear highlighting
		refreshHighlighting(null, null);
	}
}

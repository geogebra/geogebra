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
import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.gui.euclidian.MobileMouseEvent;
import geogebra.mobile.utils.Swipeables;
import geogebra.mobile.utils.ToolBarCommand;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Receives the events from the canvas and sends the orders to the kernel.
 * 
 * @author Thomas Krismayer
 * @see geogebra.common.euclidian.EuclidianController EuclidianController
 * 
 */
public class MobileEuclidianController extends EuclidianController
{

	private GuiModel guiModel;
	private ToolBarCommand lastCmd;
	private GPoint origin;
	private boolean moving;
	private boolean clicked = false;

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

	public void onTouchStart(int x, int y)
	{
		this.guiModel.closeOptions();

		this.origin = new GPoint(x, y);

		this.clicked = true;

		handleEvent(x, y);
	}

	public void onTouchMove(int x, int y)
	{
		if (this.clicked
				&& this.guiModel.getCommand() == ToolBarCommand.Move_Mobile)
		{

			this.mouseLoc = new GPoint(this.origin.getX(), this.origin.getY());
			MobileMouseEvent mEvent = new MobileMouseEvent(x, y);

			if (!this.moving)
			{
				this.view.setHits(this.origin);
				this.moving = true;

				// get the mode and the object to move
				// handleMousePressedForMoveMode(mEvent, false);

				// if added again, moved objects are not selected (while they
				// are moved)
				// -> moving objects becomes a lot slower
				// removeSelection();
			}

			this.startPoint = new GPoint2D.Double(
					this.view.toRealWorldCoordX(this.origin.getX()),
					this.view.toRealWorldCoordY(this.origin.getY()));
			wrapMouseDragged(mEvent);
			this.origin = new GPoint(x, y);
		}

	}

	public void onTouchEnd(int x, int y)
	{
		this.clicked = false;

		if (this.moving)
		{
			this.moving = false;
			this.mode = this.guiModel.getCommand().getMode();

			// object that was moved loses selection
			removeSelection();

			return;
		}

		if (Swipeables.isSwipeable(this.guiModel.getCommand())
				&& this.selectedPoints.size() == 1
				&& (Math.abs(this.origin.getX() - x) > 10 || Math
						.abs(this.origin.getY() - y) > 10))
		{
			handleEvent(x, y);
		}
	}

	private void handleEvent(int x, int y)
	{
		ToolBarCommand cmd = this.guiModel.getCommand();

		if (this.lastCmd != cmd)
		{
			this.lastCmd = cmd;
			resetSelection();
		}

		boolean draw = false;

		this.mouseLoc = new GPoint(x, y);
		this.mode = this.guiModel.getCommand().getMode();

		if (cmd == ToolBarCommand.Move_Mobile)
		{
			this.view.setHits(this.mouseLoc);
			Hits h = this.view.getHits();
			if (h.size() == 0)
			{
				this.mode = EuclidianConstants.MODE_TRANSLATEVIEW;
			}
		}

		// draw the new point
		switchModeForMousePressed(new MobileMouseEvent(x, y));

		this.view.setHits(this.mouseLoc);
		Hits hits = this.view.getHits();

		switch (cmd)
		{
		// commands that need one point or a point and an element
		case AttachDetachPoint:
			attachDetach(hits);
			break;

		// commands that need two points
		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
		case RayThroughTwoPoints:
		case VectorBetweenTwoPoints:
		case CircleWithCenterThroughPoint:
		case Semicircle:
			addSelectedPoint(hits, 2, false);
			draw = this.selectedPoints.size() >= 2;
			break;

		// commands that need one point and one line
		case PerpendicularLine:
		case ParallelLine:
		case Parabola:
			recordElement(hits);
			draw = this.selectedPoints.size() >= 1
					&& this.selectedLines.size() >= 1;
			break;

		// commands that need two points or one segment
		case MidpointOrCenter:
		case PerpendicularBisector:
			if (addSelectedPoint(hits, 2, false) == 0)
			{
				addSelectedSegment(hits, 1, false);
			}
			draw = this.selectedSegments.size() >= 1
					|| this.selectedPoints.size() >= 2;
			// if (hits.size() > 0 && hits.get(0) instanceof GeoSegment)
			// {
			// this.selectedLines.add((GeoLineND) hits.get(0));
			// draw = true;
			// } else if (this.selectedPoints.size() >= 2)
			// {
			// draw = true;
			// }
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
			addSelectedPoint(hits, 3, false);
			draw = this.selectedPoints.size() >= 3;
			break;

		// commands that need five points
		case ConicThroughFivePoints:
			addSelectedPoint(hits, 5, false);
			draw = this.selectedPoints.size() >= 5;
			break;

		// commands that need an unknown number of points
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
				this.kernel.getAlgoDispatcher().Line(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1));
				break;
			case SegmentBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().Segment(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1));
				break;
			case RayThroughTwoPoints:
				this.kernel.getAlgoDispatcher().Ray(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1));
				break;
			case VectorBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().Vector(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1));
				break;
			case CircleWithCenterThroughPoint:
				this.kernel.getAlgoDispatcher().Circle(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1));
				break;
			case Semicircle:
				this.kernel.getAlgoDispatcher().Semicircle(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1));
				break;
			case PerpendicularLine:
				this.kernel.getAlgoDispatcher().OrthogonalLine(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoLine) this.selectedLines.get(0));
				break;
			case ParallelLine:
				this.kernel.getAlgoDispatcher().Line(
						null,
						(GeoPoint) this.selectedPoints.get(this.selectedPoints
								.size() - 1),
						(GeoLine) this.selectedLines.get(this.selectedLines
								.size() - 1));
				break;
			case MidpointOrCenter:
				if (this.selectedSegments.size() > 0)
				{
					this.kernel.getAlgoDispatcher().Midpoint(null,
							this.selectedSegments.get(0));
				} else if (this.selectedPoints.size() >= 2)
				{
					this.kernel.getAlgoDispatcher().Midpoint(null,
							(GeoPoint) this.selectedPoints.get(0),
							(GeoPoint) this.selectedPoints.get(1));
				}
				break;
			case PerpendicularBisector:
				if (this.selectedSegments.size() > 0)
				{
					this.kernel.getAlgoDispatcher().LineBisector(null,
							this.selectedSegments.get(0));
				} else if (this.selectedPoints.size() >= 2)
				{
					this.kernel.getAlgoDispatcher().LineBisector(null,
							(GeoPoint) this.selectedPoints.get(0),
							(GeoPoint) this.selectedPoints.get(1));
				}
				break;
			case Parabola:
				this.kernel.getAlgoDispatcher().Parabola(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoLine) this.selectedLines.get(0));
				break;
			case CircleThroughThreePoints:
				this.kernel.getAlgoDispatcher().Circle(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1),
						(GeoPoint) this.selectedPoints.get(2));
				break;
			case CircularArcWithCenterBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().CircleArc(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1),
						(GeoPoint) this.selectedPoints.get(2));
				break;
			case CircularSectorWithCenterBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().CircleSector(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1),
						(GeoPoint) this.selectedPoints.get(2));
				break;
			case CircumCirculuarArcThroughThreePoints:
				this.kernel.getAlgoDispatcher().CircumcircleArc(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1),
						(GeoPoint) this.selectedPoints.get(2));
				break;
			case CircumCircularSectorThroughThreePoints:
				this.kernel.getAlgoDispatcher().CircumcircleSector(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1),
						(GeoPoint) this.selectedPoints.get(2));
				break;
			case Ellipse:
				this.kernel.getAlgoDispatcher().Ellipse(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1),
						(GeoPoint) this.selectedPoints.get(2));
				break;
			case Hyperbola:
				this.kernel.getAlgoDispatcher().Hyperbola(null,
						(GeoPoint) this.selectedPoints.get(0),
						(GeoPoint) this.selectedPoints.get(1),
						(GeoPoint) this.selectedPoints.get(2));
				break;
			case ConicThroughFivePoints:
				this.kernel.getAlgoDispatcher().Conic(
						null,
						new GeoPoint[] { (GeoPoint) this.selectedPoints.get(0),
								(GeoPoint) this.selectedPoints.get(1),
								(GeoPoint) this.selectedPoints.get(2),
								(GeoPoint) this.selectedPoints.get(3),
								(GeoPoint) this.selectedPoints.get(4) });
				break;
			default:
			}

			this.selectedPoints = new ArrayList<GeoPointND>();
			this.selectedLines = new ArrayList<GeoLineND>();
			this.selectedSegments = new ArrayList<GeoSegment>(); 

			removeSelection();
		}
	}

	public void setGuiModel(GuiModel model)
	{
		this.guiModel = model;
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
			this.selectedPoints.add(point);
			return point;
		}

		// no points, but other objects
		if (hits.size() > 0)
		{
			for (int i = 0; i < hits.size(); i++)
			{
				if (hits.get(i) instanceof GeoLineND
						&& !this.selectedLines.contains(hits.get(i)))
				{
					this.selectedLines.add((GeoLineND) hits.get(i));
					return hits.get(i);
				}
			}
		}

		// no point and no line found
		this.selectedPoints.add((GeoPointND) this.movedGeoElement);
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
			// includes repaint
			this.app.setSelectedGeos(new ArrayList<GeoElement>());
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

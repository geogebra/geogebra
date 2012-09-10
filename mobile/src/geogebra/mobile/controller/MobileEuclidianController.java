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
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.mobile.gui.euclidian.MobileMouseEvent;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.model.MobileModel;
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
	private MobileModel mobileModel;
	private ToolBarCommand lastCmd;
	private GPoint origin;
	private boolean moving;
	private boolean clicked = false;

	public MobileEuclidianController(MobileModel model, GuiModel guiModel)
	{
		this.mobileModel = model;
		this.guiModel = guiModel;
		this.mode = -1;
	}

	/**
	 * prevent redraw
	 */
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
				&& this.mobileModel.getNumberOf(GeoPoint.class) == 1
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
			if (this.view.getHits().size() == 0)
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
			// TODO
			attachDetach(hits);
			break;

		// commands that need two points
		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
		case RayThroughTwoPoints:
		case VectorBetweenTwoPoints:
		case CircleWithCenterThroughPoint:
		case Semicircle:
			this.mobileModel.select(hits, Test.GEOPOINT, 1);
			draw = this.mobileModel.getNumberOf(GeoPoint.class) >= 2;
			break;

		// commands that need one point and one line
		case PerpendicularLine:
		case ParallelLine:
		case Parabola:
			if (!this.mobileModel.select(hits, Test.GEOPOINT, 1))
			{
				this.mobileModel.select(hits, Test.GEOLINE, 1);
			}
			draw = this.mobileModel.getNumberOf(GeoPoint.class) >= 1
					&& this.mobileModel.getNumberOf(GeoLine.class) >= 1;
			break;

		// commands that need two points or one segment
		case MidpointOrCenter:
		case PerpendicularBisector:
			if (!this.mobileModel.select(hits, Test.GEOPOINT, 1))
			{
				this.mobileModel.select(hits, Test.GEOSEGMENT, 1);
			}
			draw = this.mobileModel.getNumberOf(GeoSegment.class) >= 1
					|| this.mobileModel.getNumberOf(GeoPoint.class) >= 2;
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
			// TODO
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
			this.mobileModel.select(hits, Test.GEOPOINT, 1);
			draw = this.mobileModel.getNumberOf(GeoPoint.class) >= 3;
			break;

		// commands that need five points
		case ConicThroughFivePoints:
			this.mobileModel.select(hits, Test.GEOPOINT, 1);
			draw = this.mobileModel.getNumberOf(GeoPoint.class) >= 5;
			break;

		// commands that need an unknown number of points
		case PolylineBetweenPoints:
		case Polygon:
			this.mobileModel.select(hits, Test.GEOPOINT, 1);
			draw = this.mobileModel.getNumberOf(GeoPoint.class) > 2
					&& this.mobileModel.getElement(GeoPoint.class).equals(
							this.mobileModel.lastSelected());
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
				this.mobileModel.select(geo);
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
				this.kernel.getAlgoDispatcher().Line(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1));
				break;
			case SegmentBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().Segment(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1));
				break;
			case RayThroughTwoPoints:
				this.kernel.getAlgoDispatcher().Ray(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1));
				break;
			case VectorBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().Vector(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1));
				break;
			case CircleWithCenterThroughPoint:
				this.kernel.getAlgoDispatcher().Circle(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1));
				break;
			case Semicircle:
				this.kernel.getAlgoDispatcher().Semicircle(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1));
				break;
			case PerpendicularLine:
				this.kernel.getAlgoDispatcher().OrthogonalLine(null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoLine) this.mobileModel.getElement(GeoLine.class));
				break;
			case ParallelLine:
				this.kernel.getAlgoDispatcher().Line(null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoLine) this.mobileModel.getElement(GeoLine.class));
				break;
			case MidpointOrCenter:
				if (this.mobileModel.getNumberOf(GeoSegment.class) > 0)
				{
					this.kernel.getAlgoDispatcher().Midpoint(
							null,
							(GeoSegment) this.mobileModel
									.getElement(GeoSegment.class));
				} else if (this.mobileModel.getNumberOf(GeoPoint.class) >= 2)
				{
					this.kernel.getAlgoDispatcher().Midpoint(
							null,
							(GeoPoint) this.mobileModel
									.getElement(GeoPoint.class),
							(GeoPoint) this.mobileModel.getElement(
									GeoPoint.class, 1));
				}
				break;
			case PerpendicularBisector:
				if (this.mobileModel.getNumberOf(GeoSegment.class) > 0)
				{
					this.kernel.getAlgoDispatcher().LineBisector(
							null,
							(GeoSegment) this.mobileModel
									.getElement(GeoSegment.class));
				} else if (this.mobileModel.getNumberOf(GeoPoint.class) >= 2)
				{
					this.kernel.getAlgoDispatcher().LineBisector(
							null,
							(GeoPoint) this.mobileModel
									.getElement(GeoPoint.class),
							(GeoPoint) this.mobileModel.getElement(
									GeoPoint.class, 1));
				}
				break;
			case Parabola:
				this.kernel.getAlgoDispatcher().Parabola(null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoLine) this.mobileModel.getElement(GeoLine.class));
				break;
			case CircleThroughThreePoints:
				this.kernel.getAlgoDispatcher().Circle(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								2));
				break;
			case CircularArcWithCenterBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().CircleArc(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								2));
				break;
			case CircularSectorWithCenterBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().CircleSector(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								2));
				break;
			case CircumCirculuarArcThroughThreePoints:
				this.kernel.getAlgoDispatcher().CircumcircleArc(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								2));
				break;
			case CircumCircularSectorThroughThreePoints:
				this.kernel.getAlgoDispatcher().CircumcircleSector(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								2));
				break;
			case Ellipse:
				this.kernel.getAlgoDispatcher().Ellipse(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								2));
				break;
			case Hyperbola:
				this.kernel.getAlgoDispatcher().Hyperbola(
						null,
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								1),
						(GeoPoint) this.mobileModel.getElement(GeoPoint.class,
								2));
				break;
			case ConicThroughFivePoints:
				this.kernel.getAlgoDispatcher().Conic(
						null,
						new GeoPoint[] {
								(GeoPoint) this.mobileModel
										.getElement(GeoPoint.class),
								(GeoPoint) this.mobileModel.getElement(
										GeoPoint.class, 1),
								(GeoPoint) this.mobileModel.getElement(
										GeoPoint.class, 2),
								(GeoPoint) this.mobileModel.getElement(
										GeoPoint.class, 3),
								(GeoPoint) this.mobileModel.getElement(
										GeoPoint.class, 4), });
				break;
			case PolylineBetweenPoints:
				ArrayList<GeoElement> geos = this.mobileModel
						.getAll(GeoPoint.class);
				this.kernel.PolyLineND(null,
						geos.toArray(new GeoPoint[geos.size() - 1]));
				break;
			case Polygon:
				ArrayList<GeoElement> geos2 = this.mobileModel
						.getAll(GeoPoint.class);
				this.kernel.Polygon(null,
						geos2.toArray(new GeoPoint[geos2.size() - 1]));
				break;
			default:
			}

			this.mobileModel.resetSelection();
			//
			// this.selectedPoints = new ArrayList<GeoPointND>();
			// this.selectedLines = new ArrayList<GeoLineND>();
			// this.selectedSegments = new ArrayList<GeoSegment>();
			//
			// removeSelection();
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

	public void onPinch(double scaleFactor)
	{
		// TODO Implement canvas scaling (simple rescaling the getContext2d
		// doesn't do the trick...
		// also mind all the other events that are fired beside pinch! (can we
		// consume them somehow, if a pinch occures?)
	}
}

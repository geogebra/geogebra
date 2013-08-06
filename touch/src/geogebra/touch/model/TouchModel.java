package geogebra.touch.model;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.algos.AlgoCirclePointRadius;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.algos.AlgoRadius;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdIntersect;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.utils.ToolBarCommand;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.google.gwt.event.dom.client.KeyUpEvent;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class TouchModel {

	private static void addAll(ArrayList<GeoElement> newGeoElements, GeoElement[] regularPolygon) {
		for (final GeoElement geo : regularPolygon) {
			newGeoElements.add(geo);
		}

	}

	Kernel kernel;
	GuiModel guiModel;
	private EuclidianView euclidianView;

	InputDialog inputDialog;
	private boolean commandFinished = false;
	private boolean changeColorAllowed = false;
	private boolean controlClicked = true;
	private boolean storeOnClose = false;
	private ToolBarCommand command;
	private final ArrayList<GeoElement> selectedElements = new ArrayList<GeoElement>();
	private CmdIntersect cmdIntersect;
	private GeoElement redefineGeo;

	private Point eventCoordinates = new Point(0, 0);
	private GeoNumeric redefineSlider;

	public TouchModel(Kernel k, TabletGUI tabletGUI) {
		this.kernel = k;
		this.guiModel = new GuiModel(this);
		this.inputDialog = new InputDialog((TouchApp) this.kernel.getApplication(), DialogType.NumberValue, tabletGUI, this.guiModel);
		this.inputDialog.setInputHandler(new InputHandler() {
			@Override
			public boolean processInput(String inputString) {
				return TouchModel.this.inputPanelClosed(inputString);
			}
		});
		this.cmdIntersect = new CmdIntersect(this.kernel);
	}

	private void attachDetach(Hits hits, Point c) {
		final EuclidianViewInterfaceCommon view = this.kernel.getApplication().getActiveEuclidianView();

		// a point and a path/line/conic/function/... or just a point
		if (!this.select(hits, Test.GEOPOINT, 1)) {
			this.selectOutOf(hits, new Test[] { Test.PATH, Test.GEOCONICND, Test.GEOFUNCTION, Test.GEOFUNCTIONNVAR, Test.REGION3D }, 1);
		}

		final GeoPoint point = (GeoPoint) this.getElement(Test.GEOPOINT);
		final Path path = (Path) this.getElement(Test.PATH);
		final Region region = (Region) this.getElementFrom(new Test[] { Test.GEOCONICND, Test.GEOFUNCTION, Test.GEOFUNCTIONNVAR, Test.REGION3D });
		if (point != null) {
			final Point p = c != null ? c : new Point((int) point.getX(), (int) point.getY());

			if (!point.isIndependent()) // detach
			{
				this.kernel.getAlgoDispatcher().detach(point, view);
				this.resetSelection();
				this.changeSelectionState(point);
				this.commandFinished = true;
			} else if (path != null) // attach to path
			{
				this.kernel.getAlgoDispatcher().attach(point, path, view, p.getX(), p.getY());
				this.resetSelection();
				this.changeSelectionState(point);
				this.commandFinished = true;
			} else if (region != null) // attach to region
			{
				this.kernel.getAlgoDispatcher().attach(point, region, view, p.getX(), p.getY());
				this.resetSelection();
				this.changeSelectionState(point);
				this.commandFinished = true;
			}
		}
	}

	/**
	 * selects the given element or deselects it in case it is selected
	 * 
	 * @param geo
	 *            the element to be selected or deselected
	 */
	public void changeSelectionState(GeoElement geo) {
		if (geo == null) {
			return;
		}

		if (this.selectedElements.indexOf(geo) != -1) {
			this.deselect(geo);
			return;
		}

		geo.setSelected(true);
		this.selectedElements.add(geo);
	}

	/**
	 * selects a number of elements of a given type
	 * 
	 * @param hits
	 *            elements that could be selected
	 * @param geoclass
	 *            the type of the element that should be selected
	 * @param max
	 *            maximum number of elements to be selected
	 * @return success
	 */
	public boolean changeSelectionState(Hits hits, Test geoclass, int max) {
		boolean success = false;
		final Hits h = new Hits();
		hits.getHits(geoclass, h);
		for (int i = 0; i < max; i++) {
			if (i < h.size()) {
				this.changeSelectionState(h.get(i));
				success = true;
			}
		}
		return success;
	}

	public boolean controlClicked() {
		final boolean ret = this.controlClicked;
		this.controlClicked = true;
		return ret;
	}

	private void createPreviewObject(boolean show) {
		if (this.euclidianView == null) {
			return;
		}
		Previewable prev = null;

		if (show) {
			switch (this.command) {
			case Polygon:
			case RegularPolygon:
			case RigidPolygon:
				final ArrayList<GeoPointND> list = new ArrayList<GeoPointND>();
				for (final GeoElement geo : this.selectedElements) {
					list.add((GeoPoint) geo);
				}
				prev = this.euclidianView.createPreviewPolygon(list);
				break;
			case PolylineBetweenPoints:
				final ArrayList<GeoPointND> list2 = new ArrayList<GeoPointND>();
				for (final GeoElement geo : this.selectedElements) {
					list2.add((GeoPoint) geo);
				}
				prev = this.euclidianView.createPreviewPolyLine(list2);
				break;
			default:
				break;
			}
		}

		this.euclidianView.setPreview(prev);
	}

	/**
	 * 
	 * @param geo
	 *            the element to be deselected
	 * @return
	 */
	public boolean deselect(GeoElement geo) {
		if (geo == null) {
			return false;
		}

		final boolean ret = geo.isSelected();
		geo.setSelected(false);
		this.selectedElements.remove(geo);
		return ret;
	}

	/**
	 * deselects all selected elements of the given type
	 * 
	 * @param geoclass
	 *            type of elements to be deselected
	 */
	public void deselectAll(Test geoclass) {
		for (final GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				this.selectedElements.remove(geo);
			}
		}
	}

	private boolean finishedPolygon(Hits hits) {
		return this.selectedElements.size() >= 3 && hits.indexOf(this.selectedElements.get(0)) != -1;
	}

	/**
	 * 
	 * @param geoclass
	 *            type to be returned
	 * @return all elements of the given type
	 */
	public ArrayList<GeoElement> getAll(Test geoclass) {
		final ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		for (final GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				geos.add(geo);
			}
		}
		return geos;
	}

	/**
	 * 
	 * @return the command that is actually executed
	 */
	public ToolBarCommand getCommand() {
		return this.command;
	}

	/**
	 * 
	 * @param class1
	 *            required Class
	 * @return the first element of the given Class; null in case there is no
	 *         such element
	 */
	public GeoElement getElement(Test geoclass) {
		for (final GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				return geo;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param geoclass
	 *            type of element looked for
	 * @param i
	 *            minimum index of the element
	 * @return the first element of the given class with an index larger or
	 *         equal than i; null in case there is no such element
	 */
	public GeoElement getElement(Test geoclass, int i) {
		int count = 0;
		for (final GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				if (i == count) {
					return geo;
				}
				count++;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param geoclass
	 *            array of possible types to be returned
	 * @return one element of the given classes; if there are matches for two
	 *         different types the element of the type with the lower index will
	 *         be returned
	 */
	public GeoElement getElementFrom(Test[] geoclass) {
		for (final Test geoclas : geoclass) {
			for (final GeoElement geo : this.selectedElements) {
				if (geoclas.check(geo)) {
					return geo;
				}
			}
		}
		return null;
	}

	public GuiModel getGuiModel() {
		return this.guiModel;
	}

	public Kernel getKernel() {
		return this.kernel;
	}

	/**
	 * 
	 * @return alpha value of the fillable that was last selected; -1 in case no
	 *         fillable geo that was selected
	 */
	public float getLastAlpha() {
		float alpha = -1f;
		for (final GeoElement geo : this.selectedElements) {
			if (geo.isFillable()) {
				alpha = geo.getAlphaValue();
			}
		}
		return alpha;
	}

	/**
	 * 
	 * @param geoclass
	 *            type to be counted
	 * @return number of selected elements of the given type
	 */
	public int getNumberOf(Test geoclass) {
		int count = 0;
		for (final GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @return all selected elemtents
	 */
	public ArrayList<GeoElement> getSelectedGeos() {
		return this.selectedElements;
	}

	/**
	 * 
	 * @return the number of all selected elements
	 */
	public int getTotalNumber() {
		return this.selectedElements.size();
	}

	private void handleDraw(Point2D pointRW, boolean singlePointForIntersection) {
		boolean draw = true;
		final ArrayList<GeoElement> newElements = new ArrayList<GeoElement>();

		switch (this.command) {
		case LineThroughTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Line(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			break;
		case SegmentBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Segment(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			break;
		case RayThroughTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Ray(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			break;
		case VectorBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Vector(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			break;
		case CircleWithCenterThroughPoint:
			newElements.add(this.kernel.getAlgoDispatcher().Circle(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			break;
		case Semicircle:
			newElements.add(this.kernel.getAlgoDispatcher().Semicircle(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			break;
		case Locus:
			newElements.add(this.kernel.getAlgoDispatcher().Locus(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			break;
		case PerpendicularLine:
			newElements.add(this.kernel.getAlgoDispatcher().OrthogonalLine(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoLine) this.getElement(Test.GEOLINE)));
			break;
		case ParallelLine:
			newElements.add(this.kernel.getAlgoDispatcher().Line(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoLine) this.getElement(Test.GEOLINE)));
			break;
		case MidpointOrCenter:
			if (this.getNumberOf(Test.GEOSEGMENT) > 0) {
				newElements.add(this.kernel.getAlgoDispatcher().Midpoint(null, (GeoSegment) this.getElement(Test.GEOSEGMENT)));
			} else if (this.getNumberOf(Test.GEOCONIC) > 0) {
				newElements.add(this.kernel.getAlgoDispatcher().Center(null, (GeoConic) this.getElement(Test.GEOCONIC)));
			} else if (this.getNumberOf(Test.GEOPOINT) >= 2) {
				newElements.add(this.kernel.getAlgoDispatcher().Midpoint(null, (GeoPoint) this.getElement(Test.GEOPOINT),
						(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			}
			break;
		case PerpendicularBisector:
			if (this.getNumberOf(Test.GEOSEGMENT) > 0) {
				newElements.add(this.kernel.getAlgoDispatcher().LineBisector(null, (GeoSegment) this.getElement(Test.GEOSEGMENT)));
			} else if (this.getNumberOf(Test.GEOPOINT) >= 2) {
				newElements.add(this.kernel.getAlgoDispatcher().LineBisector(null, (GeoPoint) this.getElement(Test.GEOPOINT),
						(GeoPoint) this.getElement(Test.GEOPOINT, 1)));
			}
			break;
		case IntersectTwoObjects:
			final GeoElement geoA = this.selectedElements.get(this.selectedElements.size() - 1);
			final GeoElement geoB = this.selectedElements.get(this.selectedElements.size() - 2);
			// intersection of two curves needs 4 params
			if (geoA instanceof GeoCurveCartesian && geoB instanceof GeoCurveCartesian && singlePointForIntersection && pointRW != null) {
				for (final GeoElement g : this.kernel.getAlgoDispatcher().IntersectCurveCurveSingle(new String[] { null }, (GeoCurveCartesian) geoA,
						(GeoCurveCartesian) geoB, pointRW.getX(), pointRW.getY())) {
					newElements.add(g);
				}
				break;
			}

			final Command c = new Command(this.kernel, "Intersect", draw);
			c.addArgument(geoA.wrap());
			c.addArgument(geoB.wrap());
			// intersection with specified initial point needs 3 params
			if (singlePointForIntersection && pointRW != null) {
				final GeoPoint p = new GeoPoint(this.kernel.getConstruction(), pointRW.getX(), pointRW.getY(), 1);
				c.addArgument(new ExpressionNode(this.kernel, p));
			}

			try {
				for (final GeoElement g : this.cmdIntersect.process(c)) {
					newElements.add(g);
				}
			} catch (final MyError e) {
				// in case there is a problem (f.e. intersecting is not
				// implemented for these object types),
				// continue selecting geos
				draw = false;
			}

			break;
		case Parabola:
			newElements.add(this.kernel.getAlgoDispatcher().Parabola(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoLine) this.getElement(Test.GEOLINE)));
			break;
		case DistanceOrLength:
			// TODO: EuclidianController.distance
			break;
		case ReflectObjectAboutLine:
			// get the line that was selected last
			final GeoLine line = this.getNumberOf(Test.GEOLINE) > 1 ? (GeoLine) this.getElement(Test.GEOLINE, 1) : (GeoLine) this
					.getElement(Test.GEOLINE);
			this.deselect(line);
			for (final GeoElement source : this.selectedElements) {
				addAll(newElements, this.kernel.getAlgoDispatcher().Mirror(null, source, line));
			}
			break;
		case ReflectObjectAboutCircle:
			// get the circle that was selected last
			final GeoConic circle = this.getNumberOf(Test.GEOCONIC) > 1 ? (GeoConic) this.getElement(Test.GEOCONIC, 1) : (GeoConic) this
					.getElement(Test.GEOCONIC);
			this.deselect(circle);
			for (final GeoElement source : this.selectedElements) {
				addAll(newElements, this.kernel.getAlgoDispatcher().Mirror(null, source, circle));
			}
			break;
		case ReflectObjectAboutPoint:
			// get the point that was selected last
			final GeoPoint mirrorPoint = this.getNumberOf(Test.GEOPOINT) > 1 ? (GeoPoint) this.getElement(Test.GEOPOINT, 1) : (GeoPoint) this
					.getElement(Test.GEOPOINT);
			this.deselect(mirrorPoint);
			for (final GeoElement source : this.selectedElements) {
				addAll(newElements, this.kernel.getAlgoDispatcher().Mirror(null, source, mirrorPoint));
			}
			break;
		case Dilate:
			this.inputDialog.redefine(DialogType.NumberValue);

			this.inputDialog.setMode("DilateFromPoint");
			this.inputDialog.show();
			// return instead of break, as everthing that follows is done by
			// the dialog!
			return;
		case RotateObjectByAngle:
			this.inputDialog.redefine(DialogType.Angle);

			this.inputDialog.setMode("RotateByAngle");
			this.inputDialog.setText("45\u00B0"); // 45°
			this.inputDialog.show();
			// return instead of break, as everthing that follows is done by
			// the dialog!
			return;
		case TranslateObjectByVector:
			// get the point that was selected last
			final GeoVector vector = this.getNumberOf(Test.GEOVECTOR) > 1 ? (GeoVector) this.getElement(Test.GEOVECTOR, 1) : (GeoVector) this
					.getElement(Test.GEOVECTOR);
			this.deselect(vector);
			for (final GeoElement source : this.selectedElements) {
				addAll(newElements, this.kernel.getAlgoDispatcher().Translate(null, source, vector));
			}
			break;
		case Tangents:
			GeoElement[] lines;
			if (this.getElement(Test.GEOPOINT) != null) {
				final GeoElement g = this.getElementFrom(new Test[] { Test.GEOCONIC, Test.GEOFUNCTION });
				if (g instanceof GeoConic) {
					// GeoPoint + GeoConic
					lines = this.kernel.getAlgoDispatcher().Tangent(null, (GeoPoint) this.getElement(Test.GEOPOINT), (GeoConic) g);
				} else {
					// GeoPoint + GeoFunction
					lines = new GeoElement[1];
					lines[0] = this.kernel.getAlgoDispatcher().Tangent(null, (GeoPoint) this.getElement(Test.GEOPOINT), (GeoFunction) g);
				}
			} else {
				// GeoLine + GeoConic
				lines = this.kernel.getAlgoDispatcher().Tangent(null, (GeoLine) this.getElement(Test.GEOLINE),
						(GeoConic) this.getElement(Test.GEOCONIC));
			}
			for (final GeoElement l : lines) {
				newElements.add(l);
			}
			break;
		case VectorFromPoint:
			final GeoPoint endPoint = (GeoPoint) this.kernel.getAlgoDispatcher().Translate(null, this.getElement(Test.GEOPOINT),
					(GeoVec3D) this.getElement(Test.GEOVECTOR))[0];
			newElements.add(this.kernel.getAlgoDispatcher().Vector(null, (GeoPoint) this.getElement(Test.GEOPOINT), endPoint));
			break;
		case CircleThroughThreePoints:
			newElements.add(this.kernel.getAlgoDispatcher().Circle(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			break;
		case CircularArcWithCenterBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().CircleArc(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			break;
		case CircularSectorWithCenterBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().CircleSector(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			break;
		case CircumCirculuarArcThroughThreePoints:
			newElements.add(this.kernel.getAlgoDispatcher().CircumcircleArc(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			break;
		case CircumCircularSectorThroughThreePoints:
			newElements.add(this.kernel.getAlgoDispatcher().CircumcircleSector(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			break;
		case Ellipse:
			newElements.add(this.kernel.getAlgoDispatcher().Ellipse(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			break;
		case Hyperbola:
			newElements.add(this.kernel.getAlgoDispatcher().Hyperbola(null, (GeoPoint) this.getElement(Test.GEOPOINT),
					(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			break;
		case Compasses:
			if (this.getNumberOf(Test.GEOPOINT) >= 3) {
				final AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(this.kernel.getConstruction(),
						(GeoPoint) this.getElement(Test.GEOPOINT), (GeoPoint) this.getElement(Test.GEOPOINT, 1), null);
				this.kernel.getConstruction().removeFromConstructionList(algoSegment);

				final AlgoCirclePointRadius algo = new AlgoCirclePointRadius(this.kernel.getConstruction(), null, (GeoPoint) this.getElement(
						Test.GEOPOINT, 2), algoSegment.getSegment(), true);
				final GeoConic compassCircle = algo.getCircle();
				compassCircle.setToSpecific();
				compassCircle.update();
				newElements.add(compassCircle);
			} else if (this.getNumberOf(Test.GEOCONIC) >= 1) {
				final AlgoRadius radius = new AlgoRadius(this.kernel.getConstruction(), (GeoConic) this.getElement(Test.GEOCONIC));
				this.kernel.getConstruction().removeFromConstructionList(radius);

				final AlgoCirclePointRadius algo = new AlgoCirclePointRadius(this.kernel.getConstruction(), null,
						(GeoPoint) this.getElement(Test.GEOPOINT), radius.getRadius());
				final GeoConic compassCircle2 = algo.getCircle();
				compassCircle2.setToSpecific();
				compassCircle2.update();
				newElements.add(compassCircle2);
			} else
			// segment
			{
				newElements.add(this.kernel.getAlgoDispatcher().Circle(null, (GeoPoint) this.getElement(Test.GEOPOINT),
						(GeoSegment) this.getElement(Test.GEOSEGMENT)));
			}

			break;
		case Angle:
			if (this.getNumberOf(Test.GEOPOINT) >= 3) {
				newElements.add(this.kernel.getAlgoDispatcher().Angle(null, (GeoPoint) this.getElement(Test.GEOPOINT),
						(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			} else if (this.getNumberOf(Test.GEOLINE) >= 2) {
				newElements.add(this.kernel.getAlgoDispatcher().Angle(null, (GeoLine) this.getElement(Test.GEOLINE),
						(GeoLine) this.getElement(Test.GEOLINE, 1)));
			} else {
				for (final GeoElement geo : this.kernel.getAlgoDispatcher().Angles(null, (GeoPolygon) this.getElement(Test.GEOPOLYGON))) {
					newElements.add(geo);
				}
			}
			break;
		case AngleBisector:
			if (this.getNumberOf(Test.GEOPOINT) >= 3) {
				newElements.add(this.kernel.getAlgoDispatcher().AngularBisector(null, (GeoPoint) this.getElement(Test.GEOPOINT),
						(GeoPoint) this.getElement(Test.GEOPOINT, 1), (GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			} else {
				for (final GeoElement e : this.kernel.getAlgoDispatcher().AngularBisector(null, (GeoLine) this.getElement(Test.GEOLINE),
						(GeoLine) this.getElement(Test.GEOLINE, 1))) {
					newElements.add(e);
				}
			}
			break;
		case ConicThroughFivePoints:
			newElements.add(this.kernel.getAlgoDispatcher().Conic(
					null,
					new GeoPoint[] { (GeoPoint) this.getElement(Test.GEOPOINT), (GeoPoint) this.getElement(Test.GEOPOINT, 1),
							(GeoPoint) this.getElement(Test.GEOPOINT, 2), (GeoPoint) this.getElement(Test.GEOPOINT, 3),
							(GeoPoint) this.getElement(Test.GEOPOINT, 4), }));
			break;
		case PolylineBetweenPoints:
			final ArrayList<GeoElement> geos = this.getAll(Test.GEOPOINT);
			final GeoElement[] geoArray = this.kernel.PolyLineND(null, geos.toArray(new GeoPoint[geos.size()]));
			for (final GeoElement geo : geoArray) {
				newElements.add(geo);
			}
			break;
		case RegularPolygon:
			if (this.inputDialog.getType() != DialogType.NumberValue) {
				this.inputDialog.redefine(DialogType.NumberValue);
			}
			this.inputDialog.setMode("RegularPolygon");
			this.inputDialog.show();

			this.controlClicked = false;
			this.commandFinished = true;
			return; // not break! no need to update or so before everything
			// is drawn
		case Polygon:
			final ArrayList<GeoElement> geos2 = this.getAll(Test.GEOPOINT);
			final GeoElement[] geoArray2 = this.kernel.Polygon(null, geos2.toArray(new GeoPoint[geos2.size()]));
			for (final GeoElement geo : geoArray2) {
				newElements.add(geo);
			}
			break;
		case RigidPolygon:
			final ArrayList<GeoElement> geos3 = this.getAll(Test.GEOPOINT);
			final GeoElement[] geoArray3 = this.kernel.RigidPolygon(null, geos3.toArray(new GeoPoint[geos3.size()]));
			for (final GeoElement geo : geoArray3) {
				newElements.add(geo);
			}
			break;
		case VectorPolygon:
			final ArrayList<GeoElement> geos4 = this.getAll(Test.GEOPOINT);
			final GeoElement[] geoArray4 = this.kernel.VectorPolygon(null, geos4.toArray(new GeoPoint[geos4.size()]));
			for (final GeoElement geo : geoArray4) {
				newElements.add(geo);
			}
			break;
		default:
		}

		if (draw) // set to false, if the command could not be finished
		{
			this.resetSelection();

			for (final GeoElement geo : newElements) {
				this.select(geo);
			}

			this.guiModel.appendStyle(newElements);

			this.commandFinished = true;
		}

	}

	public void handleEvent(Hits hits, Point point, Point2D pointRW) {
		this.kernel.setNotifyRepaintActive(false);

		this.eventCoordinates = point;
		boolean draw = false;
		if (this.commandFinished) {
			this.resetSelection();
			this.commandFinished = false;
		}
		this.changeColorAllowed = false;

		boolean singlePointForIntersection = false;

		switch (this.command) {
		// commands that only draw one point
		case NewPoint:
		case ComplexNumbers:
		case PointOnObject:
			this.resetSelection();
			this.changeSelectionState(hits, Test.GEOPOINT, 1);
			this.guiModel.appendStyle(this.selectedElements);
			this.changeColorAllowed = true;

			this.commandFinished = true;

			break;

		// special command: slider
		case Slider:
			// TODO
			if (this.inputDialog.getType() != DialogType.Slider) {
				this.inputDialog.redefine(DialogType.Slider);
			}
			this.inputDialog.setMode("Slider");
			this.inputDialog.show();
			break;

		// special command: attach/detach: needs a point (detach) or a point and
		// a region/path (attach)
		case AttachDetachPoint:
			this.attachDetach(hits, point);
			break;

		// special command: rotate around point: needs one point as center of
		// the roation and an other element to rotate
		case RotateAroundPoint:
			// deselect all elements except for the center point (index 0)
			// needed if there is another rotation
			while (this.selectedElements.size() > 1) {
				this.deselect(this.selectedElements.get(1));
			}

			if (this.getTotalNumber() > 0 && hits.contains(this.selectedElements.get(0))) {
				this.deselect(this.selectedElements.get(0));
			} else if (this.getNumberOf(Test.GEOPOINT) > 0) {
				// one Point is already selected!
				this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOCONIC, Test.GEOLINE, Test.GEOSEGMENTND, Test.GEORAY, Test.GEOVECTOR,
						Test.GEOPOLYGON, Test.GEOPOLYLINE }, 2);
			} else {
				this.select(hits, Test.GEOPOINT, 1);
			}

			// it should not be allowed to rotate the axes
			if (this.selectedElements.size() >= 2 && this.selectedElements.get(1) instanceof GeoAxis) {
				deselect(this.selectedElements.get(1));
			}

			break;

		// commands that need two points
		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
		case RayThroughTwoPoints:
		case VectorBetweenTwoPoints:
		case CircleWithCenterThroughPoint:
		case Semicircle:
		case Locus:
			this.changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = this.getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need one point and one line
		case PerpendicularLine:
		case ParallelLine:
		case Parabola:
			this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE }, new int[] { 1, 1 });
			draw = this.getNumberOf(Test.GEOPOINT) >= 1 && this.getNumberOf(Test.GEOLINE) >= 1;
			break;

		// commands that need two points or one point and one line or two lines
		// or one segment or a circle
		case DistanceOrLength: // TODO
			this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE, Test.GEOSEGMENT, Test.GEOCONIC }, new int[] { 2, 2, 1, 1 });
			draw = this.getNumberOf(Test.GEOPOINT) >= 2 || this.getNumberOf(Test.GEOPOINT) >= 1 && this.getNumberOf(Test.GEOLINE) >= 1
					|| this.getNumberOf(Test.GEOLINE) >= 2 || this.getNumberOf(Test.GEOSEGMENT) >= 1 || this.getNumberOf(Test.GEOCONIC) >= 1;
			break;

		// commands that need one line and any other object
		case ReflectObjectAboutLine:
			if (!this.changeSelectionState(hits, Test.GEOLINE, 1)) {
				this.changeSelectionState(hits.get(0));
			}
			draw = this.getNumberOf(Test.GEOLINE) >= 1 && this.getTotalNumber() >= 2;
			break;

		// commands that need one circle and any other object
		case ReflectObjectAboutCircle:
			if (!this.select(hits, Test.GEOCONIC, 2)) {
				this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE, Test.GEOSEGMENTND, Test.GEORAY, Test.GEOPOLYGON, Test.GEOPOLYLINE,
						Test.GEOCURVECARTESIAN, Test.GEOIMPLICITPOLY }, 1);
			}
			draw = this.getNumberOf(Test.GEOCONIC) >= 1 && this.getTotalNumber() >= 2;
			break;

		// commands that need one point and any other object
		case ReflectObjectAboutPoint:
		case Dilate:
		case RotateObjectByAngle:
			if (!this.changeSelectionState(hits, Test.GEOPOINT, 1) && hits.size() > 0) {
				this.changeSelectionState(hits.get(0));
			}
			draw = this.getNumberOf(Test.GEOPOINT) >= 1 && this.getTotalNumber() >= 2;
			break;

		// commands that need one vector and any other object
		case TranslateObjectByVector:
			if (!this.changeSelectionState(hits, Test.GEOVECTOR, 1) && hits.size() > 0) {
				this.changeSelectionState(hits.get(0));
			}
			draw = this.getNumberOf(Test.GEOVECTOR) >= 1 && this.getTotalNumber() >= 2;
			break;

		// commands that need one point or line and one circle or conic
		case Tangents:
			if (!this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE }, 1)) {
				this.selectOutOf(hits, new Test[] { Test.GEOCONIC, Test.GEOFUNCTION }, 1);
			}
			draw = this.getNumberOf(Test.GEOPOINT) + this.getNumberOf(Test.GEOLINE) >= 1
					&& this.getNumberOf(Test.GEOCONIC) + this.getNumberOf(Test.GEOFUNCTION) >= 1;
			break;

		// commands that need one point and one vector
		case VectorFromPoint:
			this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOVECTOR }, new int[] { 1, 1 });
			draw = this.getNumberOf(Test.GEOPOINT) >= 1 && this.getNumberOf(Test.GEOVECTOR) >= 1;
			break;

		// commands that need two points or one segment
		case MidpointOrCenter:
			if (!this.changeSelectionState(hits, Test.GEOPOINT, 1) && !this.changeSelectionState(hits, Test.GEOCONIC, 1)) {
				this.changeSelectionState(hits, Test.GEOSEGMENT, 1);
			}
			draw = this.getNumberOf(Test.GEOSEGMENT) >= 1 || this.getNumberOf(Test.GEOCONIC) >= 1 || this.getNumberOf(Test.GEOPOINT) >= 2;
			break;
		case PerpendicularBisector:
			if (!this.changeSelectionState(hits, Test.GEOPOINT, 1)) {
				this.changeSelectionState(hits, Test.GEOSEGMENT, 1);
			}
			draw = this.getNumberOf(Test.GEOSEGMENT) >= 1 || this.getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need any two objects
		case IntersectTwoObjects:
			// polygon needs to be the last element of the array
			final Test[] classes = new Test[] { Test.GEOLINE, Test.GEOCURVECARTESIAN, Test.GEOPOLYLINE, Test.GEOCONIC, Test.GEOFUNCTION,
					Test.GEOIMPLICITPOLY, Test.GEOPOLYGON };

			final boolean success = this.selectOutOf(hits, classes, 2);

			if (success && hits.size() >= 2) { // try to select another element
				// to prevent problems when
				// selecting the sides of the
				// polygon
				hits.removePolygons();
				hits.remove(this.selectedElements.get(this.selectedElements.size() - 1));
				if (this.selectOutOf(hits, classes, 2)) {
					singlePointForIntersection = true;
				}
			}

			draw = this.getTotalNumber() >= 2;

			break;

		// commands that need tree points
		case CircleThroughThreePoints:
		case CircularArcWithCenterBetweenTwoPoints:
		case CircularSectorWithCenterBetweenTwoPoints:
		case CircumCirculuarArcThroughThreePoints:
		case CircumCircularSectorThroughThreePoints:
		case Ellipse:
		case Hyperbola:
			this.changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = this.getNumberOf(Test.GEOPOINT) >= 3;
			break;

		// commands that need one point and two additional points or one circle
		// or
		// one segment
		case Compasses:
			this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOSEGMENT, Test.GEOCONIC }, new int[] { 3, 1, 1 });
			if (this.lastSelected() instanceof GeoConic && !((GeoConic) this.lastSelected()).isCircle()) {
				this.deselect(this.lastSelected());
			}
			draw = this.getNumberOf(Test.GEOPOINT) >= 3 || this.getNumberOf(Test.GEOPOINT) >= 1
					&& (this.getNumberOf(Test.GEOCONIC) >= 1 || this.getNumberOf(Test.GEOSEGMENT) >= 1);
			break;

		// commands that need three points or two lines
		case Angle:
			this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE, Test.GEOPOLYGON }, new int[] { 3, 2, 1 });
			draw = this.getNumberOf(Test.GEOPOINT) >= 3 || this.getNumberOf(Test.GEOLINE) >= 2 || this.getNumberOf(Test.GEOPOLYGON) >= 1;
			break;
		case AngleBisector:
			this.selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE }, new int[] { 3, 2 });
			draw = this.getNumberOf(Test.GEOPOINT) >= 3 || this.getNumberOf(Test.GEOLINE) >= 2;
			break;

		// commands that need five points
		case ConicThroughFivePoints:
			this.changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = this.getNumberOf(Test.GEOPOINT) >= 5;
			break;

		// commands that need two points and special input
		case RegularPolygon:
			this.changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = this.getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need an unknown number of points
		case PolylineBetweenPoints:
		case Polygon:
		case RigidPolygon:
		case VectorPolygon:
			// checking for draw prevents unintended deselecting of the
			// start-point
			draw = this.finishedPolygon(hits);
			if (!draw) {
				this.changeSelectionState(hits, Test.GEOPOINT, 1);
			}
			this.createPreviewObject(!draw);
			break;

		// special commands
		case Move_Mobile:
			for (final GeoElement geo : hits) {
				this.select(geo);
			}
			this.changeColorAllowed = true;
			break;

		case Select:
			if (hits.size() == 0) {
				this.resetSelection();
			}

			for (final GeoElement geo : hits) {
				if (geo.isSelected()) {
					if (!hits.containsGeoPoint()) {
						this.deselect(geo);
					} else if (geo instanceof GeoPoint) {
						this.deselect(geo);
					}
				} else {
					this.changeSelectionState(geo);
				}
			}
			this.changeColorAllowed = true;
			break;
		case DeleteObject:
			for (final GeoElement geo : hits) {
				// geo.remove();
				geo.removeOrSetUndefinedIfHasFixedDescendent();
			}
			this.commandFinished = true;
			break;

		default:
			break;
		}

		// draw anything other than a point
		if (draw) {
			this.handleDraw(pointRW, singlePointForIntersection);
		}

		this.kernel.setNotifyRepaintActive(true); // includes a repaint

		if (this.commandFinished) {
			this.kernel.getApplication().storeUndoInfo();
		}

		if (this.commandFinished || this.command == ToolBarCommand.Select || this.command == ToolBarCommand.Move_Mobile) {
			this.guiModel.updateStylingBar();
		}
	}

	public boolean inputPanelClosed(String input) {
		this.inputDialog.setText("");

		if (!this.inputDialog.isHandlingExpected(true)) {
			this.resetSelection();
			// still false! includes a repaint
			this.kernel.setNotifyRepaintActive(true);
			return true;
		}

		// redefine
		if (this.inputDialog.getType() == DialogType.RedefineSlider) {
			setSliderProperties(this.redefineSlider);
			this.redefineSlider.rename(input);
			this.redefineSlider.update();
			this.kernel.notifyRepaint();
			return true;
		} else if (this.inputDialog.getType() == DialogType.Redefine) {
			if (this.redefineGeo == null) {
				return false;
			}

			final boolean redefine = !this.redefineGeo.isPointOnPath();

			final GeoElement redefined = TouchModel.this.kernel.getAlgebraProcessor().changeGeoElement(this.redefineGeo, input, redefine, true);
			return redefined != null;
		}

		// avoid labeling of num
		final boolean oldVal = TouchModel.this.kernel.getConstruction().isSuppressLabelsActive();
		TouchModel.this.kernel.getConstruction().setSuppressLabelCreation(true);
		final String signedInput = this.inputDialog.isClockwise() ? "-(" + input + ")" : input;

		final ArrayList<GeoElement> newGeoElements = new ArrayList<GeoElement>();

		if (this.command == ToolBarCommand.Slider) {
			TouchModel.this.kernel.getConstruction().setSuppressLabelCreation(oldVal);

			final GeoNumeric slider = this.inputDialog.isNumber() ? new GeoNumeric(this.kernel.getConstruction()) : new GeoAngle(
					this.kernel.getConstruction());
			slider.setLabel(input.equals("") ? null : input);
			slider.setSliderLocation(this.eventCoordinates.x, this.eventCoordinates.y, true);
			setSliderProperties(slider);

			slider.setEuclidianVisible(true);
			slider.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			slider.setLabelVisible(true);
			slider.update();

		} else { // every command except for Slider and Redefine

			final GeoElement[] result = TouchModel.this.kernel.getAlgebraProcessor().processAlgebraCommand(signedInput, false);

			TouchModel.this.kernel.getConstruction().setSuppressLabelCreation(oldVal);

			if (result == null || result.length == 0 || !(result[0] instanceof NumberValue)) {
				// invalid input; nothing to do anymore.
				return false;
			}

			switch (this.command) {
			case RegularPolygon:
				addAll(newGeoElements,
						TouchModel.this.kernel.getAlgoDispatcher().RegularPolygon(null, (GeoPoint) this.getElement(Test.GEOPOINT),
								(GeoPoint) this.getElement(Test.GEOPOINT, 1), (NumberValue) result[0]));
				break;

			case Dilate:
				final GeoPoint start = (GeoPoint) this.getElement(Test.GEOPOINT);
				this.deselect(start);
				for (final GeoElement source : this.selectedElements) {
					addAll(newGeoElements, TouchModel.this.kernel.getAlgoDispatcher().Dilate(null, source, (NumberValue) result[0], start));
				}
				break;

			case RotateObjectByAngle:
				final GeoPoint center = this.lastSelected() instanceof GeoPoint ? (GeoPoint) this.lastSelected() : (GeoPoint) this
						.getElement(Test.GEOPOINT);
				this.deselect(center);
				for (final GeoElement source : this.selectedElements) {
					addAll(newGeoElements, TouchModel.this.kernel.getAlgoDispatcher().Rotate(null, source, (GeoNumberValue) result[0], center));
				}
				break;

			default:
				// should not happen. Therefore there is no repaint or anything
				// else.
				return false;
			}
		}

		// includes Slider again

		this.resetSelection();
		for (final GeoElement g : newGeoElements) {
			this.select(g);
		}

		// still false! includes a repaint
		this.kernel.setNotifyRepaintActive(true);
		this.guiModel.updateStylingBar();
		this.commandFinished = true;
		this.kernel.getApplication().storeUndoInfo();
		return true;
	}

	private void setSliderProperties(GeoNumeric slider) {
		slider.setIntervalMin(this.kernel.getAlgebraProcessor().evaluateToNumeric(this.inputDialog.getMin(), false));
		slider.setIntervalMax(this.kernel.getAlgebraProcessor().evaluateToNumeric(this.inputDialog.getMax(), false));
		slider.setAnimationStep(this.kernel.getAlgebraProcessor().evaluateToNumeric(this.inputDialog.getIncrement(), false));

	}

	public boolean isColorChangeAllowed() {
		return this.commandFinished || this.changeColorAllowed;
	}

	/**
	 * 
	 * @return the element that was selected last; null in case there is no
	 *         selected element
	 */
	public GeoElement lastSelected() {
		return this.selectedElements.size() > 0 ? this.selectedElements.get(this.selectedElements.size() - 1) : null;
	}

	/**
	 * @see geogebra.web.gui.inputbar.AlgebraInputW#onKeyUp(KeyUpEvent event)
	 * 
	 * @param input
	 *            the new command
	 * @return
	 */
	public boolean newInput(String input) {
		try {
			this.kernel.clearJustCreatedGeosInViews();
			if (input == null || input.length() == 0) {
				return true;
			}
			this.kernel.getApplication().getEuclidianView1().getEuclidianController().startCollectingMinorRepaints();
			// this.app.setScrollToShow(true);
			GeoElement[] geos;
			try {
				if (input.startsWith("/")) {
					final String cmd = input.substring(1);

					// TODO
					this.kernel.getApplication().getPythonBridge().eval(cmd);
					geos = new GeoElement[0];
				} else {
					geos = this.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(input, true, false, true);

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

				}
			} catch (final Exception e) {
				this.kernel.getApplication().showError(e, null);
				this.stopCollecting();
				e.printStackTrace();
				return false;
			} catch (final MyError e) {
				this.kernel.getApplication().showError(e);
				this.stopCollecting();
				e.printStackTrace();
				return false;
			}

			// create texts in the middle of the visible view
			// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
			if (geos.length > 0 && geos[0] != null && geos[0].isGeoText()) {
				final GeoText text = (GeoText) geos[0];
				if (!text.isTextCommand() && text.getStartPoint() == null) {

					final Construction cons = text.getConstruction();

					// TODO
					final EuclidianViewInterfaceCommon ev = this.kernel.getApplication().getActiveEuclidianView();

					final boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					final GeoPoint p = new GeoPoint(text.getConstruction(), null, (ev.getXmin() + ev.getXmax()) / 2,
							(ev.getYmin() + ev.getYmax()) / 2, 1.0);
					cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

					try {
						text.setStartPoint(p);
						text.update();
					} catch (final CircularDefinitionException e1) {
						e1.printStackTrace();
					}
				}
			}
			this.stopCollecting();
			// this.app.setScrollToShow(false);

		} catch (final Exception e) {
			this.stopCollecting();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void optionsClosed() {
		if (this.storeOnClose) {
			this.storeOnClose = false;
			this.kernel.getApplication().storeUndoInfo();
		}
	}

	public void redefine(final GeoElement geo) {
		if (geo.isGeoNumeric() && geo.isEuclidianVisible()) {
			this.redefineSlider = (GeoNumeric) geo;
			this.redefineGeo = null;
			this.inputDialog.setFromSlider(this.redefineSlider);
		} else {
			this.inputDialog.redefine(DialogType.Redefine);
			this.inputDialog.setText(geo.getDefinitionForInputBar());
			this.redefineGeo = geo;
			this.redefineSlider = null;
		}

		this.inputDialog.show();

	}

	public void repaint() {
		this.kernel.notifyRepaint();
	}

	/**
	 * deselect all elements
	 */
	public void resetSelection() {
		for (final GeoElement geo : this.selectedElements) {
			geo.setSelected(false);
			geo.setHighlighted(false);
		}
		this.selectedElements.clear();
	}

	/**
	 * selects the given element
	 * 
	 * @param geo
	 *            the element to be selected
	 */
	public void select(GeoElement geo) {
		if (geo == null || this.selectedElements.indexOf(geo) != -1) {
			return;
		}
		geo.setSelected(true);
		this.selectedElements.add(geo);
	}

	public boolean select(Hits hits, int max) {
		boolean selectAllowed = true;
		if (this.getTotalNumber() >= max) {
			selectAllowed = false;
		}

		boolean success = false;
		for (int i = 0; i < max; i++) {
			if (i < hits.size()) {
				if (selectAllowed) {
					this.select(hits.get(i));
					success = true;
				} else if (this.deselect(hits.get(i))) {
					return true;
				}
			}
		}
		return success;
	}

	public boolean select(Hits hits, Test geoclass, int max) {
		boolean selectAllowed = true;
		if (this.getNumberOf(geoclass) >= max) {
			selectAllowed = false;
		}

		boolean success = false;
		final Hits h = new Hits();
		hits.getHits(geoclass, h);
		for (int i = 0; i < max; i++) {
			if (i < h.size()) {
				if (selectAllowed) {
					this.select(h.get(i));
					success = true;
				} else if (this.deselect(h.get(i))) {
					return true;
				}
			}
		}
		return success;
	}

	/**
	 * selects one element of the given class (if there are elements of
	 * different classes, the first class that has elements in the hits will be
	 * used)
	 * 
	 * @param hits
	 *            the Hits to get the elements form
	 * @param geoclass
	 *            Array of possible classes
	 * @param max
	 *            maximum number of elements that might be selected
	 * @return success (false if there is no element of any of the given
	 *         classes)
	 */
	public boolean selectOutOf(Hits hits, Test[] geoclass, int max) {
		boolean selectAllowed = true;
		int sum = 0;
		for (final Test t : geoclass) {
			sum += this.getNumberOf(t);
		}
		if (sum >= max) {
			selectAllowed = false;
		}

		final Hits h = new Hits();
		for (final Test geoclas : geoclass) {
			hits.getHits(geoclas, h);
			if (h.size() > 0) {
				if (selectAllowed) {
					this.changeSelectionState(h.get(0));
					return true;
				} else if (this.deselect(h.get(0))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * selects one element of the given class (if there are elements of
	 * different classes, the first class that has elements in the hits will be
	 * used)
	 * 
	 * @param hits
	 *            the Hits to get the elements form
	 * @param geoclass
	 *            Array of possible classes
	 * @param max
	 *            maximum number of elements that might be selected (has to
	 *            include the same number of elements as geoclass)
	 * @return success (false if there is no element of any of the given
	 *         classes)
	 */
	public boolean selectOutOf(Hits hits, Test[] geoclass, int[] max) {
		if (geoclass.length != max.length) {
			return false;
		}

		final Hits h = new Hits();
		for (int i = 0; i < max.length; i++) {
			boolean selectAllowed = true;
			if (this.getNumberOf(geoclass[i]) >= max[i]) {
				selectAllowed = false;
			}
			hits.getHits(geoclass[i], h);
			if (h.size() > 0) {
				if (selectAllowed) {
					this.changeSelectionState(h.get(0));
					return true;
				} else if (this.deselect(h.get(0))) {
					return true;
				}
			}
		}
		return false;
	}

	public void setCaptionMode(int index) {
		this.guiModel.setCaptionMode(index);
		this.guiModel.closeOptions();
		this.kernel.getApplication().storeUndoInfo();
	}

	/**
	 * sets the command to be executed
	 * 
	 * @param cmd
	 *            the new command
	 */
	public void setCommand(ToolBarCommand cmd) {
		if (this.command != null && this.command.equals(cmd)) {
			return;
		}

		if (this.euclidianView == null) {
			this.euclidianView = this.kernel.getApplication().getEuclidianView1();
		}

		this.resetSelection();
		this.guiModel.resetStyle();
		this.command = cmd;
		this.createPreviewObject(false);
	}

	private void stopCollecting() {
		this.kernel.getApplication().getEuclidianView1().getEuclidianController().stopCollectingMinorRepaints();
	}

	public void storeOnClose() {
		this.storeOnClose = true;
	}

	public boolean wasCantorolClicked() {
		return this.controlClicked;
	}
}
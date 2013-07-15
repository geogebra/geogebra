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
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPoint;
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

	Kernel kernel;
	GuiModel guiModel;
	private EuclidianView euclidianView;
	InputDialog inputDialog;

	private boolean commandFinished = false;
	private boolean changeColorAllowed = false;
	private boolean controlClicked = true;
	private boolean storeOnClose = false;
	private ToolBarCommand command;
	private ArrayList<GeoElement> selectedElements = new ArrayList<GeoElement>();
	private CmdIntersect cmdIntersect;

	public TouchModel(Kernel k, TabletGUI tabletGUI) {
		this.kernel = k;
		this.guiModel = new GuiModel(this);
		this.inputDialog = new InputDialog(
				(TouchApp) this.kernel.getApplication(),
				DialogType.NumberValue, tabletGUI, this.guiModel);
		this.inputDialog.setInputHandler(new InputHandler()
		{

			@Override
			public boolean processInput(String inputString) {
				return inputPanelClosed(inputString);
			}
		});
		this.cmdIntersect = new CmdIntersect(this.kernel);
	}

	public GuiModel getGuiModel() {
		return this.guiModel;
	}

	/**
	 * 
	 * @return the command that is actually executed
	 */
	public ToolBarCommand getCommand() {
		return this.command;
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
			this.euclidianView = this.kernel.getApplication()
					.getEuclidianView1();
		}

		resetSelection();
		this.guiModel.resetStyle();
		this.command = cmd;
		createPreviewObject(false);
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
			deselect(geo);
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
		Hits h = new Hits();
		hits.getHits(geoclass, h);
		for (int i = 0; i < max; i++) {
			if (i < h.size()) {
				changeSelectionState(h.get(i));
				success = true;
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
	 * 						maximum number of elements that might be selected (has 
	 * 						to include the same number of elements as geoclass)
	 * @return success (false if there is no element of any of the given
	 *         classes)
	 */
	public boolean selectOutOf(Hits hits, Test[] geoclass, int[] max) {
		if(geoclass.length != max.length){
			return false;
		}
		
		Hits h = new Hits();
		for (int i = 0; i < max.length; i++) {
			boolean selectAllowed = true;
			if(getNumberOf(geoclass[i]) >= max[i]){
				selectAllowed = false;
			}
			hits.getHits(geoclass[i], h);
			if (h.size() > 0) {
				if(selectAllowed){
					changeSelectionState(h.get(0));
					return true;
				} else if(deselect(h.get(0))){
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
	 * 						maximum number of elements that might be selected
	 * @return success (false if there is no element of any of the given
	 *         classes)
	 */
	public boolean selectOutOf(Hits hits, Test[] geoclass, int max) {
		boolean selectAllowed = true;		
		int sum = 0; 
		for(Test t : geoclass){
			sum += getNumberOf(t);
		}
		if(sum >= max){
			selectAllowed = false;
		}
		
		Hits h = new Hits();
		for (int i = 0; i < geoclass.length; i++) {
			hits.getHits(geoclass[i], h);
			if (h.size() > 0) {
				if(selectAllowed){
					changeSelectionState(h.get(0));
					return true;
				} else if(deselect(h.get(0))){
					return true;
				}				
			}
		}
		return false;
	}

	public boolean select(Hits hits, Test geoclass, int max) {
		boolean selectAllowed = true;
		if(getNumberOf(geoclass) >= max){
			selectAllowed = false;
		}
		
		boolean success = false;
		Hits h = new Hits();
		hits.getHits(geoclass, h);
		for (int i = 0; i < max; i++) {
			if (i < h.size()) {
				if(selectAllowed){
					select(h.get(i));
					success = true;
				} else if(deselect(h.get(i))){
					return true;
				}
			}
		}
		return success;
	}
	
	public boolean select(Hits hits, int max) {
		boolean selectAllowed = true;
		if(getTotalNumber() >= max){
			selectAllowed = false;
		}
		
		boolean success = false;
		for (int i = 0; i < max; i++) {
			if (i < hits.size()) {
				if(selectAllowed){
					select(hits.get(i));
					success = true;
				} else if(deselect(hits.get(i))){
					return true;
				}
			}
		}
		return success;
	}
	
	/**
	 * 
	 * @param geo
	 *            the element to be deselected
	 * @return 
	 */
	public boolean deselect(GeoElement geo) {
		boolean ret = geo.isSelected();
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
		for (GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				this.selectedElements.remove(geo);
			}
		}
	}

	/**
	 * deselect all elements
	 */
	public void resetSelection() {
		for (GeoElement geo : this.selectedElements) {
			geo.setSelected(false);
			geo.setHighlighted(false);
		}
		this.selectedElements.clear();
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
	 * @param class1
	 *            required Class
	 * @return the first element of the given Class; null in case there is no
	 *         such element
	 */
	public GeoElement getElement(Test geoclass) {
		for (GeoElement geo : this.selectedElements) {
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
		for (GeoElement geo : this.selectedElements) {
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
		for (int i = 0; i < geoclass.length; i++) {
			for (GeoElement geo : this.selectedElements) {
				if (geoclass[i].check(geo)) {
					return geo;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param geoclass
	 *            type to be returned
	 * @return all elements of the given type
	 */
	public ArrayList<GeoElement> getAll(Test geoclass) {
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		for (GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				geos.add(geo);
			}
		}
		return geos;
	}

	/**
	 * 
	 * @return the number of all selected elements
	 */
	public int getTotalNumber() {
		return this.selectedElements.size();
	}

	/**
	 * 
	 * @param geoclass
	 *            type to be counted
	 * @return number of selected elements of the given type
	 */
	public int getNumberOf(Test geoclass) {
		int count = 0;
		for (GeoElement geo : this.selectedElements) {
			if (geoclass.check(geo)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @return the element that was selected last; null in case there is no
	 *         selected element
	 */
	public GeoElement lastSelected() {
		return this.selectedElements.size() > 0 ? this.selectedElements
				.get(this.selectedElements.size() - 1) : null;
	}

	/**
	 * 
	 * @return alpha value of the fillable that was last selected; -1 in case no
	 *         fillable geo that was selected
	 */
	public float getLastAlpha() {
		float alpha = -1f;
		for (GeoElement geo : this.selectedElements) {
			if (geo.isFillable()) {
				alpha = geo.getAlphaValue();
			}
		}
		return alpha;
	}

	public void handleEvent(Hits hits, Point point, Point2D pointRW) {
		this.kernel.setNotifyRepaintActive(false);

		boolean draw = false;
		if (this.commandFinished) {
			resetSelection();
			this.commandFinished = false;
		}
		this.changeColorAllowed = false;

		boolean singlePointForIntersection = false;

		switch (this.command) {
		// commands that only draw one point
		case NewPoint:
		case ComplexNumbers:
		case PointOnObject:
			resetSelection();
			changeSelectionState(hits, Test.GEOPOINT, 1);
			this.guiModel.appendStyle(this.selectedElements);
			this.changeColorAllowed = true;

			this.commandFinished = true;

			break;

		// special command: attach/detach: needs a point (detach) or a point and
		// a region/path (attach)
		case AttachDetachPoint:
			attachDetach(hits, point);
			break;

		// special command: rotate around point: needs one point as center of
		// the roation and a second point to rotate
		case RotateAroundPoint:
			if (this.getTotalNumber() > 0
					&& hits.contains(this.selectedElements.get(0))) {
				this.deselect(this.selectedElements.get(0));
			} else {
				select(hits, Test.GEOPOINT, 2);
			}

			// deselect all elements except for the center point (index 0)
			// needed if there is another rotation
			while (this.selectedElements.size() > 2) {
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
			changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need one point and one line
		case PerpendicularLine:
		case ParallelLine:
		case Parabola:
			selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE }, new int[] {1,1});
			draw = getNumberOf(Test.GEOPOINT) >= 1
					&& getNumberOf(Test.GEOLINE) >= 1;
			break;

		// commands that need two points or one point and one line or two lines
		// or one segment or a circle
		case DistanceOrLength: // TODO
			selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE,
					Test.GEOSEGMENT, Test.GEOCONIC }, new int[] {2,2,1,1});
			draw = getNumberOf(Test.GEOPOINT) >= 2
					|| (getNumberOf(Test.GEOPOINT) >= 1 && getNumberOf(Test.GEOLINE) >= 1)
					|| getNumberOf(Test.GEOLINE) >= 2
					|| getNumberOf(Test.GEOSEGMENT) >= 1
					|| getNumberOf(Test.GEOCONIC) >= 1;
			break;

		// commands that need one line and any other object
		case ReflectObjectAboutLine:
			if (!changeSelectionState(hits, Test.GEOLINE, 1)) {
				select(hits, 1 + getNumberOf(Test.GEOLINE));
			}
			draw = getNumberOf(Test.GEOLINE) >= 1 && getTotalNumber() >= 2;
			break;

		// commands that need one circle and any other object
		case ReflectObjectAboutCircle:
			if(!select(hits, Test.GEOCONIC, 2)){
				selectOutOf(hits, new Test[]{Test.GEOPOINT,
					Test.GEOPOLYGON, Test.GEOPOLYLINE, Test.GEOCURVECARTESIAN,
					Test.GEOIMPLICITPOLY }, 1);
			}
			draw = getNumberOf(Test.GEOCONIC) >= 1 && getTotalNumber() >= 2;
			break;

		// commands that need one point and any other object
		case ReflectObjectAboutPoint:
		case Dilate:
		case RotateObjectByAngle:
			if (!changeSelectionState(hits, Test.GEOPOINT, 1)
					&& hits.size() > 0) {
				changeSelectionState(hits.get(0));
			}
			draw = getNumberOf(Test.GEOPOINT) >= 1 && getTotalNumber() >= 2;
			break;

		// commands that need one vector and any other object
		case TranslateObjectByVector:
			if (!changeSelectionState(hits, Test.GEOVECTOR, 1)
					&& hits.size() > 0) {
				changeSelectionState(hits.get(0));
			}
			draw = getNumberOf(Test.GEOVECTOR) >= 1 && getTotalNumber() >= 2;
			break;

		// commands that need one point or line and one circle or conic
		case Tangents:
			if(!selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE}, 1)){
				selectOutOf(hits, new Test[] { Test.GEOCONIC, Test.GEOFUNCTION}, 1);				
			}
			draw = (getNumberOf(Test.GEOPOINT) + getNumberOf(Test.GEOLINE) >= 1)
					&& getNumberOf(Test.GEOCONIC) + getNumberOf(Test.GEOFUNCTION) >= 1;
			break;

		// commands that need one point and one vector
		case VectorFromPoint:
			selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOVECTOR }, new int[]{1,1});
			draw = getNumberOf(Test.GEOPOINT) >= 1
					&& getNumberOf(Test.GEOVECTOR) >= 1;
			break;

		// commands that need two points or one segment
		case MidpointOrCenter:
		case PerpendicularBisector:
			if (!changeSelectionState(hits, Test.GEOPOINT, 1)) {
				changeSelectionState(hits, Test.GEOSEGMENT, 1);
			}
			draw = getNumberOf(Test.GEOSEGMENT) >= 1
					|| getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need any two objects
		case IntersectTwoObjects:
			// polygon needs to be the last element of the array
			Test[] classes = new Test[] { Test.GEOLINE, Test.GEOCURVECARTESIAN,
					Test.GEOPOLYLINE, Test.GEOCONIC, Test.GEOFUNCTION,
					Test.GEOIMPLICITPOLY, Test.GEOPOLYGON };

			boolean success = selectOutOf(hits, classes, 2);

			if (success && hits.size() >= 2) { // try to select another element
												// to prevent problems when
												// selecting the sides of the
												// polygon
				hits.removePolygons();
				hits.remove(this.selectedElements.get(this.selectedElements
						.size() - 1));
				if (selectOutOf(hits, classes, 2)) {
					singlePointForIntersection = true;
				}
			}

			draw = getTotalNumber() >= 2;

			break;

		// commands that need tree points
		case CircleThroughThreePoints:
		case CircularArcWithCenterBetweenTwoPoints:
		case CircularSectorWithCenterBetweenTwoPoints:
		case CircumCirculuarArcThroughThreePoints:
		case CircumCircularSectorThroughThreePoints:
		case Ellipse:
		case Hyperbola:
			changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) >= 3;
			break;

		// commands that need one point and two additional points or one circle or one segment
		case Compasses: 
			selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOSEGMENT, Test.GEOCONIC}, new int[] {3, 1, 1});
			if(lastSelected() instanceof GeoConic && !((GeoConic) lastSelected()).isCircle()){
				deselect(lastSelected());
			}
			draw = getNumberOf(Test.GEOPOINT) >= 3
					|| getNumberOf(Test.GEOPOINT) >= 1
					&& (getNumberOf(Test.GEOCONIC) >= 1 || getNumberOf(Test.GEOSEGMENT) >= 1);
			break;

		// commands that need three points or two lines
		case Angle:
		case AngleBisector:
			selectOutOf(hits, new Test[] { Test.GEOPOINT, Test.GEOLINE }, new int[] {3, 2});
			draw = getNumberOf(Test.GEOPOINT) >= 3
					|| getNumberOf(Test.GEOLINE) >= 2;
			break;

		// commands that need five points
		case ConicThroughFivePoints:
			changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) >= 5;
			break;

		// commands that need two points and special input
		case RegularPolygon:
			changeSelectionState(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(Test.GEOPOINT) >= 2;
			break;

		// commands that need an unknown number of points
		case PolylineBetweenPoints:
		case Polygon:
		case RigidPolygon:
		case VectorPolygon:
			// checking for draw prevents unintended deselecting of the
			// start-point
			draw = finishedPolygon(hits);
			if (!draw) {
				changeSelectionState(hits, Test.GEOPOINT, 1);
			}
			createPreviewObject(!draw);
			break;

		// special commands
		case Move_Mobile:
			for (GeoElement geo : hits) {
				select(geo);
			}
			this.changeColorAllowed = true;
			break;

		case Select:
			if (hits.size() == 0) {
				resetSelection();
			}

			for (GeoElement geo : hits) {
				if (geo.isSelected()) {
					if (!hits.containsGeoPoint()) {
						deselect(geo);
					} else if (geo instanceof GeoPoint) {
						deselect(geo);
					}
				} else {
					changeSelectionState(geo);
				}
			}
			this.changeColorAllowed = true;
			break;
		case DeleteObject:
			for (GeoElement geo : hits) {
				// geo.remove();
				geo.removeOrSetUndefinedIfHasFixedDescendent();
			}
			this.commandFinished = true;
			break;

		default:
			break;
		}

		// draw anything other than a point
		if (draw) { handleDraw(pointRW,singlePointForIntersection);		}

		this.kernel.setNotifyRepaintActive(true); // includes a repaint

		if (this.commandFinished) {
			this.kernel.getApplication().storeUndoInfo();
		}

		if (this.commandFinished || this.command == ToolBarCommand.Select
				|| this.command == ToolBarCommand.Move_Mobile) {
			this.guiModel.updateStylingBar();
		}
	}

	private void handleDraw(Point2D pointRW, boolean singlePointForIntersection) {
		boolean draw = true;
		ArrayList<GeoElement> newElements = new ArrayList<GeoElement>();

		switch (this.command) {
		case LineThroughTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Line(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1)));
			break;
		case SegmentBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Segment(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1)));
			break;
		case RayThroughTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Ray(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1)));
			break;
		case VectorBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().Vector(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1)));
			break;
		case CircleWithCenterThroughPoint:
			newElements.add(this.kernel.getAlgoDispatcher().Circle(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1)));
			break;
		case Semicircle:
			newElements.add(this.kernel.getAlgoDispatcher().Semicircle(
					null, (GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1)));
			break;
		case Locus:
			newElements.add(this.kernel.getAlgoDispatcher().Locus(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1)));
			break;
		case PerpendicularLine:
			newElements.add(this.kernel.getAlgoDispatcher().OrthogonalLine(
					null, (GeoPoint) getElement(Test.GEOPOINT),
					(GeoLine) getElement(Test.GEOLINE)));
			break;
		case ParallelLine:
			newElements.add(this.kernel.getAlgoDispatcher().Line(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoLine) getElement(Test.GEOLINE)));
			break;
		case MidpointOrCenter:
			if (getNumberOf(Test.GEOSEGMENT) > 0) {
				newElements.add(this.kernel.getAlgoDispatcher().Midpoint(
						null, (GeoSegment) getElement(Test.GEOSEGMENT)));
			} else if (getNumberOf(Test.GEOPOINT) >= 2) {
				newElements.add(this.kernel.getAlgoDispatcher().Midpoint(
						null, (GeoPoint) getElement(Test.GEOPOINT),
						(GeoPoint) getElement(Test.GEOPOINT, 1)));
			}
			break;
		case PerpendicularBisector:
			if (getNumberOf(Test.GEOSEGMENT) > 0) {
				newElements.add(this.kernel.getAlgoDispatcher()
						.LineBisector(null,
								(GeoSegment) getElement(Test.GEOSEGMENT)));
			} else if (getNumberOf(Test.GEOPOINT) >= 2) {
				newElements.add(this.kernel.getAlgoDispatcher()
						.LineBisector(null,
								(GeoPoint) getElement(Test.GEOPOINT),
								(GeoPoint) getElement(Test.GEOPOINT, 1)));
			}
			break;
		case IntersectTwoObjects:
			GeoElement geoA = this.selectedElements
					.get(this.selectedElements.size() - 1);
			GeoElement geoB = this.selectedElements
					.get(this.selectedElements.size() - 2);
			//intersection of two curves needs 4 params
			if (geoA instanceof GeoCurveCartesian
					&& geoB instanceof GeoCurveCartesian
					&& singlePointForIntersection && pointRW != null) {
				for (GeoElement g : this.kernel.getAlgoDispatcher().IntersectCurveCurveSingle(
						new String[] { null }, (GeoCurveCartesian) geoA,
						(GeoCurveCartesian) geoB, pointRW.getX(),
						pointRW.getY())){
					newElements.add(g);
				}
				break;
			}
			
			Command c = new Command(this.kernel, "Intersect", draw);
			c.addArgument(geoA.wrap());
			c.addArgument(geoB.wrap());
			//intersection with specified initial point needs 3 params
			if (singlePointForIntersection && pointRW != null) {
				GeoPoint p = new GeoPoint(this.kernel.getConstruction(),
						pointRW.getX(), pointRW.getY(), 1);
				c.addArgument(new ExpressionNode(this.kernel, p));
			}

			try {
				for (GeoElement g : this.cmdIntersect.process(c)){
					newElements.add(g);
				}
			} catch (MyError e) {
				// in case there is a problem (f.e. intersecting is not
				// implemented for these object types),
				// continue selecting geos
				draw = false;
			}

			break;
		case Parabola:
			newElements.add(this.kernel.getAlgoDispatcher().Parabola(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoLine) getElement(Test.GEOLINE)));
			break;
		case DistanceOrLength:
			// TODO: EuclidianController.distance
			break;
		case ReflectObjectAboutLine:
			// get the line that was selected last
			GeoLine line = getNumberOf(Test.GEOLINE) > 1 ? (GeoLine) getElement(
					Test.GEOLINE, 1) : (GeoLine) getElement(Test.GEOLINE);
			deselect(line);
			for (GeoElement e : this.kernel.getAlgoDispatcher().Mirror(
					null, this.selectedElements.get(0), line)) {
				newElements.add(e);
			}
			break;
		case ReflectObjectAboutCircle:
			// get the circle that was selected last
			GeoConic circle = getNumberOf(Test.GEOCONIC) > 1 ? (GeoConic) getElement(
					Test.GEOCONIC, 1)
					: (GeoConic) getElement(Test.GEOCONIC);
			deselect(circle);
			for (GeoElement e : this.kernel.getAlgoDispatcher().Mirror(
					null, this.selectedElements.get(0), circle)) {
				newElements.add(e);
			}
			break;
		case ReflectObjectAboutPoint:
			// get the point that was selected last
			GeoPoint mirrorPoint = getNumberOf(Test.GEOPOINT) > 1 ? (GeoPoint) getElement(
					Test.GEOPOINT, 1)
					: (GeoPoint) getElement(Test.GEOPOINT);
			deselect(mirrorPoint);
			for (GeoElement e : this.kernel.getAlgoDispatcher().Mirror(
					null, this.selectedElements.get(0), mirrorPoint)) {
				newElements.add(e);
			}
			break;
		case Dilate:
			if(this.inputDialog.getType() != DialogType.NumberValue)
			{
				this.inputDialog.redefine(DialogType.NumberValue);
			}
			this.inputDialog.setMode("DilateFromPoint");
			this.inputDialog.show();
			// return instead of break, as everthing that follows is done by
			// the dialog!
			return;
		case RotateObjectByAngle: 
			if(this.inputDialog.getType() != DialogType.Angle)
			{
				this.inputDialog.redefine(DialogType.Angle);
			}
			this.inputDialog.setMode("RotateByAngle");
			this.inputDialog.setText("45\u00B0"); // 45°
			this.inputDialog.show();
			// return instead of break, as everthing that follows is done by
			// the dialog!
			return;
		case TranslateObjectByVector:
			// get the point that was selected last
			GeoVector vector = getNumberOf(Test.GEOVECTOR) > 1 ? (GeoVector) getElement(
					Test.GEOVECTOR, 1)
					: (GeoVector) getElement(Test.GEOVECTOR);
			deselect(vector);
			for (GeoElement e : this.kernel.getAlgoDispatcher().Translate(
					null, this.selectedElements.get(0), vector)) {
				newElements.add(e);
			}
			break;
		case Tangents:
			GeoElement[] lines;
			if (this.getElement(Test.GEOPOINT) != null) {
				GeoElement g = getElementFrom(new Test[]{Test.GEOCONIC, Test.GEOFUNCTION});
				if(g instanceof GeoConic){
					//GeoPoint + GeoConic
					lines = this.kernel.getAlgoDispatcher().Tangent(null,
							(GeoPoint) this.getElement(Test.GEOPOINT),
							(GeoConic) g);
				} else {
					//GeoPoint + GeoFunction
					lines = new GeoElement[1];
					lines[0] = this.kernel.getAlgoDispatcher().Tangent(null,
							(GeoPoint) this.getElement(Test.GEOPOINT),
							(GeoFunction) g);
				}
			} else {
				//GeoLine + GeoConic
				lines = this.kernel.getAlgoDispatcher().Tangent(null,
						(GeoLine) this.getElement(Test.GEOLINE),
						(GeoConic) this.getElement(Test.GEOCONIC));
			}
			for (GeoElement l : lines) {
				newElements.add(l);
			}
			break;
		case VectorFromPoint:
			GeoPoint endPoint = (GeoPoint) this.kernel.getAlgoDispatcher()
					.Translate(null, getElement(Test.GEOPOINT),
							(GeoVec3D) getElement(Test.GEOVECTOR))[0];
			newElements.add(this.kernel.getAlgoDispatcher().Vector(null,
					(GeoPoint) getElement(Test.GEOPOINT), endPoint));
			break;
		case CircleThroughThreePoints:
			newElements.add(this.kernel.getAlgoDispatcher().Circle(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1),
					(GeoPoint) getElement(Test.GEOPOINT, 2)));
			break;
		case CircularArcWithCenterBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().CircleArc(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1),
					(GeoPoint) getElement(Test.GEOPOINT, 2)));
			break;
		case CircularSectorWithCenterBetweenTwoPoints:
			newElements.add(this.kernel.getAlgoDispatcher().CircleSector(
					null, (GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1),
					(GeoPoint) getElement(Test.GEOPOINT, 2)));
			break;
		case CircumCirculuarArcThroughThreePoints:
			newElements.add(this.kernel.getAlgoDispatcher()
					.CircumcircleArc(null,
							(GeoPoint) getElement(Test.GEOPOINT),
							(GeoPoint) getElement(Test.GEOPOINT, 1),
							(GeoPoint) getElement(Test.GEOPOINT, 2)));
			break;
		case CircumCircularSectorThroughThreePoints:
			newElements.add(this.kernel.getAlgoDispatcher()
					.CircumcircleSector(null,
							(GeoPoint) getElement(Test.GEOPOINT),
							(GeoPoint) getElement(Test.GEOPOINT, 1),
							(GeoPoint) getElement(Test.GEOPOINT, 2)));
			break;
		case Ellipse:
			newElements.add(this.kernel.getAlgoDispatcher().Ellipse(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1),
					(GeoPoint) getElement(Test.GEOPOINT, 2)));
			break;
		case Hyperbola:
			newElements.add(this.kernel.getAlgoDispatcher().Hyperbola(null,
					(GeoPoint) getElement(Test.GEOPOINT),
					(GeoPoint) getElement(Test.GEOPOINT, 1),
					(GeoPoint) getElement(Test.GEOPOINT, 2)));
			break;
		case Compasses:
			if (getNumberOf(Test.GEOPOINT) >= 3) {
				AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(
						this.kernel.getConstruction(),
						(GeoPoint) getElement(Test.GEOPOINT),
						(GeoPoint) getElement(Test.GEOPOINT, 1), null);
				this.kernel.getConstruction().removeFromConstructionList(
						algoSegment);

				AlgoCirclePointRadius algo = new AlgoCirclePointRadius(
						this.kernel.getConstruction(), null,
						(GeoPoint) getElement(Test.GEOPOINT, 2),
						algoSegment.getSegment(), true);
				GeoConic compassCircle = algo.getCircle();
				compassCircle.setToSpecific();
				compassCircle.update();
				newElements.add(compassCircle);
			} else if (getNumberOf(Test.GEOCONIC) >= 1) {
				AlgoRadius radius = new AlgoRadius(
						this.kernel.getConstruction(),
						(GeoConic) getElement(Test.GEOCONIC));
				this.kernel.getConstruction().removeFromConstructionList(
						radius);

				AlgoCirclePointRadius algo = new AlgoCirclePointRadius(
						this.kernel.getConstruction(), null,
						(GeoPoint) getElement(Test.GEOPOINT),
						radius.getRadius());
				GeoConic compassCircle2 = algo.getCircle();
				compassCircle2.setToSpecific();
				compassCircle2.update();
				newElements.add(compassCircle2);
			} else
			// segment
			{
				newElements.add(this.kernel.getAlgoDispatcher().Circle(
						null, (GeoPoint) getElement(Test.GEOPOINT),
						(GeoSegment) getElement(Test.GEOSEGMENT)));
			}

			break;
		case Angle:
			if (this.getNumberOf(Test.GEOPOINT) >= 3) {
				newElements.add(this.kernel.getAlgoDispatcher().Angle(null,
						(GeoPoint) this.getElement(Test.GEOPOINT),
						(GeoPoint) this.getElement(Test.GEOPOINT, 1),
						(GeoPoint) this.getElement(Test.GEOPOINT, 2)));
			} else {
				newElements.add(this.kernel.getAlgoDispatcher().Angle(null,
						(GeoLine) this.getElement(Test.GEOLINE),
						(GeoLine) this.getElement(Test.GEOLINE, 1)));
			}
			break;
		case AngleBisector:
			if (getNumberOf(Test.GEOPOINT) >= 3) {
				newElements.add(this.kernel.getAlgoDispatcher()
						.AngularBisector(null,
								(GeoPoint) getElement(Test.GEOPOINT),
								(GeoPoint) getElement(Test.GEOPOINT, 1),
								(GeoPoint) getElement(Test.GEOPOINT, 2)));
			} else {
				for (GeoElement e : this.kernel.getAlgoDispatcher()
						.AngularBisector(null,
								(GeoLine) getElement(Test.GEOLINE),
								(GeoLine) getElement(Test.GEOLINE, 1))) {
					newElements.add(e);
				}
			}
			break;
		case ConicThroughFivePoints:
			newElements.add(this.kernel.getAlgoDispatcher().Conic(
					null,
					new GeoPoint[] { (GeoPoint) getElement(Test.GEOPOINT),
							(GeoPoint) getElement(Test.GEOPOINT, 1),
							(GeoPoint) getElement(Test.GEOPOINT, 2),
							(GeoPoint) getElement(Test.GEOPOINT, 3),
							(GeoPoint) getElement(Test.GEOPOINT, 4), }));
			break;
		case PolylineBetweenPoints:
			ArrayList<GeoElement> geos = getAll(Test.GEOPOINT);
			GeoElement[] geoArray = this.kernel.PolyLineND(null,
					geos.toArray(new GeoPoint[geos.size()]));
			for (GeoElement geo : geoArray) {
				newElements.add(geo);
			}
			break;
		case RegularPolygon:
			if(this.inputDialog.getType() != DialogType.NumberValue)
			{
				this.inputDialog.redefine(DialogType.NumberValue);
			}
			this.inputDialog.setMode("RegularPolygon");
			this.inputDialog.show();

			this.controlClicked = false;
			this.commandFinished = true;
			return; // not break! no need to update or so before everything
			// is drawn
		case Polygon:
			ArrayList<GeoElement> geos2 = getAll(Test.GEOPOINT);
			GeoElement[] geoArray2 = this.kernel.Polygon(null,
					geos2.toArray(new GeoPoint[geos2.size()]));
			for (GeoElement geo : geoArray2) {
				newElements.add(geo);
			}
			break;
		case RigidPolygon:
			ArrayList<GeoElement> geos3 = getAll(Test.GEOPOINT);
			GeoElement[] geoArray3 = this.kernel.RigidPolygon(null,
					geos3.toArray(new GeoPoint[geos3.size()]));
			for (GeoElement geo : geoArray3) {
				newElements.add(geo);
			}
			break;
		case VectorPolygon:
			ArrayList<GeoElement> geos4 = getAll(Test.GEOPOINT);
			GeoElement[] geoArray4 = this.kernel.VectorPolygon(null,
					geos4.toArray(new GeoPoint[geos4.size()]));
			for (GeoElement geo : geoArray4) {
				newElements.add(geo);
			}
			break;
		default:
		}

		if (draw) // set to false, if the command could not be finished
		{
			resetSelection();

			for (GeoElement geo : newElements) {
				select(geo);
			}

			this.guiModel.appendStyle(newElements);

			this.commandFinished = true;
		}

		
	}

	private boolean finishedPolygon(Hits hits) {
		return this.selectedElements.size() >= 3
				&& hits.indexOf(this.selectedElements.get(0)) != -1;
	}

	public boolean wasCantorolClicked() {
		return this.controlClicked;
	}

	public boolean controlClicked() {
		boolean ret = this.controlClicked;
		this.controlClicked = true;
		return ret;
	}

	private void attachDetach(Hits hits, Point c) {
		EuclidianViewInterfaceCommon view = this.kernel.getApplication()
				.getActiveEuclidianView();

		// a point and a path/line/conic/function/... or just a point
		if(!select(hits,  Test.GEOPOINT, 1)){
			selectOutOf(hits, new Test[] { Test.PATH, Test.GEOCONICND, Test.GEOFUNCTION, Test.GEOFUNCTIONNVAR,
				Test.REGION3D }, 1);			
		}

		GeoPoint point = (GeoPoint) getElement(Test.GEOPOINT);
		Path path = (Path) getElement(Test.PATH);
		Region region = (Region) getElementFrom(new Test[] { Test.GEOCONICND,
				Test.GEOFUNCTION, Test.GEOFUNCTIONNVAR, Test.REGION3D });
		if (point != null) {
			Point p = c != null ? c : new Point((int) point.getX(),
					(int) point.getY());

			if (!point.isIndependent()) // detach
			{
				this.kernel.getAlgoDispatcher().detach(point, view);
				resetSelection();
				changeSelectionState(point);
				this.commandFinished = true;
			} else if (region != null) // attach to region
			{
				this.kernel.getAlgoDispatcher().attach(point, region, view,
						p.getX(), p.getY());
				resetSelection();
				changeSelectionState(point);
				this.commandFinished = true;
			} else if (path != null) // attach to path
			{
				this.kernel.getAlgoDispatcher().attach(point, path, view,
						p.getX(), p.getY());
				resetSelection();
				changeSelectionState(point);
				this.commandFinished = true;
			}
		}
	}

	private void stopCollecting() {
		this.kernel.getApplication().getEuclidianView1()
				.getEuclidianController().stopCollectingMinorRepaints();
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
			this.kernel.getApplication().getEuclidianView1()
					.getEuclidianController().startCollectingMinorRepaints();
			// this.app.setScrollToShow(true);
			GeoElement[] geos;
			try {
				if (input.startsWith("/")) {
					String cmd = input.substring(1);

					// TODO
					this.kernel.getApplication().getPythonBridge().eval(cmd);
					geos = new GeoElement[0];
				} else {
					geos = this.kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(input,
									true, false, true);

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

				}
			} catch (Exception e) {
				this.kernel.getApplication().showError(e,null);
				stopCollecting();
				e.printStackTrace();
				return false;
			} catch (MyError e) {
				this.kernel.getApplication().showError(e);
				stopCollecting();
				e.printStackTrace();
				return false;
			}

			// create texts in the middle of the visible view
			// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
			if (geos.length > 0 && geos[0] != null && geos[0].isGeoText()) {
				GeoText text = (GeoText) geos[0];
				if (!text.isTextCommand() && text.getStartPoint() == null) {

					Construction cons = text.getConstruction();

					// TODO
					EuclidianViewInterfaceCommon ev = this.kernel
							.getApplication().getActiveEuclidianView();

					boolean oldSuppressLabelsStatus = cons
							.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					GeoPoint p = new GeoPoint(text.getConstruction(), null,
							(ev.getXmin() + ev.getXmax()) / 2,
							(ev.getYmin() + ev.getYmax()) / 2, 1.0);
					cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

					try {
						text.setStartPoint(p);
						text.update();
					} catch (CircularDefinitionException e1) {
						e1.printStackTrace();
					}
				}
			}
			stopCollecting();
			// this.app.setScrollToShow(false);

		} catch (Exception e) {
			stopCollecting();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isColorChangeAllowed() {
		return this.commandFinished || this.changeColorAllowed;
	}

	public void repaint() {
		this.kernel.notifyRepaint();
	}

	public void setCaptionMode(int index) {
		this.guiModel.setCaptionMode(index);
		this.guiModel.closeOptions();
		this.kernel.getApplication().storeUndoInfo();
	}

	public void storeOnClose() {
		this.storeOnClose = true;
	}

	public void optionsClosed() {
		if (this.storeOnClose) {
			this.storeOnClose = false;
			this.kernel.getApplication().storeUndoInfo();
		}
	}

	private void createPreviewObject(boolean show) {
		if (this.euclidianView == null) {
			return;
		}
		Previewable prev = null;

		if(show){
			switch (this.command) {
			case Polygon:
			case RegularPolygon:
			case RigidPolygon:
				ArrayList<GeoPointND> list = new ArrayList<GeoPointND>();
				for (GeoElement geo : this.selectedElements) {
					list.add((GeoPoint) geo);
				}
				prev = this.euclidianView.createPreviewPolygon(list);
				break;
			case PolylineBetweenPoints:
				ArrayList<GeoPointND> list2 = new ArrayList<GeoPointND>();
				for (GeoElement geo : this.selectedElements) {
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

	public Kernel getKernel() {
		return this.kernel;
	}

	public boolean inputPanelClosed(String input) {
		this.inputDialog.setText("");
		
		if(!this.inputDialog.isHandlingExpected(true))
		{
			resetSelection();
			// still false! includes a repaint
			this.kernel.setNotifyRepaintActive(true);
			return true;
		}
		
		boolean oldVal = TouchModel.this.kernel.getConstruction()
				.isSuppressLabelsActive();
		TouchModel.this.kernel.getConstruction().setSuppressLabelCreation(true);
		String signedInput =this.inputDialog.clockwise() ?
			"-(" + input + ")" : input;
		
		GeoElement[] result = TouchModel.this.kernel.getAlgebraProcessor()
				.processAlgebraCommand(signedInput, false);

		TouchModel.this.kernel.getConstruction().setSuppressLabelCreation(
				oldVal);

		if (result == null || result.length == 0
				|| !(result[0] instanceof NumberValue)) {
			// invalid input; nothing to do anymore.
			return false;
		}

		GeoElement[] newGeoElements;

		switch (this.command) {
		case RegularPolygon:
			// avoid labeling of num

			newGeoElements = TouchModel.this.kernel.getAlgoDispatcher()
					.RegularPolygon(null, (GeoPoint) getElement(Test.GEOPOINT),
							(GeoPoint) getElement(Test.GEOPOINT, 1),
							(NumberValue) result[0]);
			resetSelection();

			break;
		case Dilate:
			GeoPoint start = (GeoPoint) getElement(Test.GEOPOINT);
			GeoElement geoDil = this.selectedElements.get(0) == start ? this.selectedElements
					.get(1) : this.selectedElements.get(0);
			newGeoElements = TouchModel.this.kernel.getAlgoDispatcher().Dilate(
					null, geoDil, (NumberValue) result[0], start);
			resetSelection();

			break;
		case RotateObjectByAngle: 
			GeoPoint center = lastSelected() instanceof GeoPoint ? (GeoPoint) lastSelected() : (GeoPoint) getElement(Test.GEOPOINT);
			GeoElement geoRotate = this.selectedElements.get(0) == center ? this.selectedElements
					.get(1) : this.selectedElements.get(0);
			newGeoElements = TouchModel.this.kernel.getAlgoDispatcher().Rotate(
					null, geoRotate, (GeoNumberValue) result[0], center);
			resetSelection();

			break;
		default:
			// should not happen. Therefore there is no repaint or anything
			// else.
			return false;
		}

		for (GeoElement g : newGeoElements) {
			select(g);
		}

		// still false! includes a repaint
		this.kernel.setNotifyRepaintActive(true);
		this.guiModel.updateStylingBar();
		this.commandFinished = true;
		this.kernel.getApplication().storeUndoInfo();
		return true;
	}

	public void redefine(final GeoElement geo) {
		if(this.inputDialog.getType() != DialogType.Redefine)
		{
			this.inputDialog.redefine(DialogType.Redefine);
		}
		this.inputDialog.setText(geo.getDefinitionForInputBar());
		
		this.inputDialog.setInputHandler(new InputHandler(){
			@Override
			public boolean processInput(String input){
				boolean redefine = !geo.isPointOnPath();

				TouchModel.this.kernel.getAlgebraProcessor().changeGeoElement(
					geo, input, redefine, true);
				return true;
			}
		});
		this.inputDialog.show();
		
	}
}
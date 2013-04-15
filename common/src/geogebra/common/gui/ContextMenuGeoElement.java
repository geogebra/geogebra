package geogebra.common.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoUserInputElement;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.main.App;
import geogebra.common.main.OptionType;

import java.util.ArrayList;

/**
 * @author gabor
 * 
 * Superclass for ContextMenuGeoElements in Web and Desktop
 *
 */
public abstract class ContextMenuGeoElement {

	protected static double [] zoomFactors = {4.0, 2.0, 1.5, 1.25, 1.0/1.25, 1.0/1.5, 0.5, 0.25};
	protected static double [] axesRatios = {1.0/1000.0, 1.0/500.0, 1.0/200.0, 1.0/100.0, 1.0/50.0, 1.0/20.0, 1.0/10.0, 1.0/5.0, 1.0/2.0,
	    		1, 2, 5, 10, 20, 50, 100, 200, 500, 1000};
	/** selected elements*/
	protected ArrayList<GeoElement> geos;
	/** current element */
	protected GeoElement geo;
	/** application */
	public App app;
	protected boolean justOneGeo = false;

	/**
	 * 
	 * @param geo geo
	 * @return description
	 */
	protected String getDescription(GeoElement geo, boolean addHTMLtag) {
		String title = geo.getLongDescriptionHTML(false, addHTMLtag);
		if (title.length() > 80)
			title = geo.getNameDescriptionHTML(false, addHTMLtag);
		return title;
	}

	public void cartesianCoordsCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoPoint) {
				GeoPoint point1 = (GeoPoint)geo1;
				point1.setMode(Kernel.COORD_CARTESIAN);
				point1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void polarCoorsCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoPoint) {
				GeoPoint point1 = (GeoPoint)geo1;
				point1.setMode(Kernel.COORD_POLAR);
				point1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}
	

	public void equationImplicitEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine)geo1;
				line1.setMode(GeoLine.EQUATION_IMPLICIT);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void equationExplicitEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine)geo1;
				line1.setMode(GeoLine.EQUATION_EXPLICIT);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void parametricFormCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine)geo1;
				line1.setMode(GeoLine.PARAMETRIC);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void cartesianCoordsForVectorItemsCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoVector) {
				GeoVector vector1 = (GeoVector)geo1;
				vector1.setMode(Kernel.COORD_CARTESIAN);
				vector1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void polarCoordsForVectorItemsCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoVector) {
				GeoVector vector1 = (GeoVector)geo1;
				vector1.setMode(Kernel.COORD_POLAR);
				vector1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void implicitConicEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic)geo1;
				conic1.setToImplicit();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void equationConicEqnCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic)geo1;
				conic1.setToSpecific();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void equationExplicitConicEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic)geo1;
				conic1.setToExplicit();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void extendedFormCmd(final GeoUserInputElement inputElement) {
		inputElement.setExtendedForm();
		inputElement.updateRepaint();
		app.storeUndoInfo();
	}

	public void deleteCmd() {
		
		ArrayList<GeoElement> geos2 = checkOneGeo();
		
		//geo.remove();
		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			geo1.removeOrSetUndefinedIfHasFixedDescendent();
		}
		app.storeUndoInfo();
	}

	private ArrayList<GeoElement> checkOneGeo() {
		if (justOneGeo) {
			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			ret.add(geo);
			return ret;
		}
		
		return geos;
	}

	public void editCmd() {
		app.getDialogManager().showTextDialog((GeoText) geo);
	}

	public void renameCmd() {
		app.getDialogManager().showRenameDialog(geo, true, geo.getLabelSimple(), true);
	}

	public void fixObjectNumericCmd(final GeoNumeric num) {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isGeoNumeric()) {
				((GeoNumeric)geo1).setSliderFixed(!num.isSliderFixed());
				geo1.updateRepaint();
			} else {
				geo1.setFixed(!num.isSliderFixed());
			}
			
		}
		app.storeUndoInfo();
	}

	public void fixObjectCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isGeoNumeric()) {
				((GeoNumeric)geo1).setSliderFixed(!geo1.isFixed());
				geo1.updateRepaint();
			} else {
				if (geo1.isFixable()) {
					geo1.setFixed(!geo1.isFixed());
					geo1.updateRepaint();
				}
			}
			
		}
		app.storeUndoInfo();
	}

	public void showLabelCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			geo1.setLabelVisible(!geo1.isLabelVisible());
			geo1.updateRepaint();
			
		}
		app.storeUndoInfo();
	}

	public void showObjectCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			geo1.setEuclidianVisible(!geo1.isSetEuclidianVisible());
			geo1.updateRepaint();
			
		}
		app.storeUndoInfo();
	}

	public void showObjectAuxiliaryCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isAlgebraShowable()) {
				geo1.setAuxiliaryObject(!geo1.isAuxiliaryObject());
				geo1.updateRepaint();
			}
			
		}
		app.storeUndoInfo();
	}

	public void openPropertiesDialogCmd() {
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, checkOneGeo());
	}

	public void inputFormCmd(final GeoUserInputElement inputElement) {
		inputElement.setInputForm();
		inputElement.updateRepaint();
		app.storeUndoInfo();
	}

	public void traceCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isTraceable()) {
				((Traceable) geo1).setTrace(!((Traceable) geo1).getTrace());
				geo1.updateRepaint();
			}
			
		}
		app.storeUndoInfo();
	}

	public void animationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isAnimatable()) {
	    		geo1.setAnimating(!(geo1.isAnimating() && 
	    				app.getKernel().getAnimatonManager().isRunning()));
				geo1.updateRepaint();
			}
			
		}
		app.storeUndoInfo();
	    app.getActiveEuclidianView().repaint();
	
		// automatically start animation when animating was turned on
		if (geo.isAnimating())
			geo.getKernel().getAnimatonManager().startAnimation();
	}

	public void pinCmd(boolean isSelected) {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof AbsoluteScreenLocateable && !geo1.isGeoList()) {
				AbsoluteScreenLocateable geoText = (AbsoluteScreenLocateable)geo1;
				boolean flag = !geoText.isAbsoluteScreenLocActive();
				if (flag) {
					// convert real world to screen coords
					int x = app.getActiveEuclidianView().toScreenCoordX(geoText.getRealWorldLocX());
					int y = app.getActiveEuclidianView().toScreenCoordY(geoText.getRealWorldLocY());
					geoText.setAbsoluteScreenLoc(x, y);							
				} else {
					// convert screen coords to real world 
					double x = app.getActiveEuclidianView().toRealWorldCoordX(geoText.getAbsoluteScreenLocX());
					double y = app.getActiveEuclidianView().toRealWorldCoordY(geoText.getAbsoluteScreenLocY());
					geoText.setRealWorldLoc(x, y);
				}
				geoText.setAbsoluteScreenLocActive(flag);            		
				geoText.updateRepaint();
			} else if (geo.isPinnable()) {
				EuclidianStyleBarStatic.applyFixPosition(geos2, isSelected, app.getActiveEuclidianView());
			}
		}
		app.storeUndoInfo();
	}

	public void geoActionCmd(GeoElement cmdGeo, ArrayList<GeoElement> sGeos, ArrayList<GeoElement> gs,
			EuclidianView v, GPoint l) {
				//AbstractApplication.debug(geo.getLabelSimple());
				app.getSelectionManager().clearSelectedGeos(false); //repaint done next step
				app.getSelectionManager().addSelectedGeo(cmdGeo);
				
				// update the geo lists and show the popup again with the new selection
				sGeos.clear();
				sGeos.add(cmdGeo);
				app.getGuiManager().showPopupChooseGeo(sGeos, gs, v, l);
			}

}

package geogebra.common.gui;

import java.util.ArrayList;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.gui.view.properties.PropertiesView.OptionType;
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

/**
 * @author gabor
 * 
 * Superclass for ContextMenuGeoElements in Web and Desktop
 *
 */
public abstract class ContextMenuGeoElement {

	/** selected elements*/
	protected ArrayList<GeoElement> geos;
	/** current element */
	protected GeoElement geo;
	/** application */
	public App app;

	/**
	 * 
	 * @param geo geo
	 * @return description
	 */
	protected String getDescription(GeoElement geo) {
		String title = geo.getLongDescriptionHTML(false, true);
		if (title.length() > 80)
			title = geo.getNameDescriptionHTML(false, true);
		return title;
	}

	public void cartesianCoordsCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1 instanceof GeoPoint) {
				GeoPoint point1 = (GeoPoint)geo1;
				point1.setMode(Kernel.COORD_CARTESIAN);
				point1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void polarCoorsCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1 instanceof GeoPoint) {
				GeoPoint point1 = (GeoPoint)geo1;
				point1.setMode(Kernel.COORD_POLAR);
				point1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void equationImplicitEquationCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine)geo1;
				line1.setMode(GeoLine.EQUATION_IMPLICIT);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void equationExplicitEquationCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine)geo1;
				line1.setMode(GeoLine.EQUATION_EXPLICIT);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void parametricFormCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine)geo1;
				line1.setMode(GeoLine.PARAMETRIC);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void cartesianCoordsForVectorItemsCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1 instanceof GeoVector) {
				GeoVector vector1 = (GeoVector)geo1;
				vector1.setMode(Kernel.COORD_CARTESIAN);
				vector1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void polarCoordsForVectorItemsCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1 instanceof GeoVector) {
				GeoVector vector1 = (GeoVector)geo1;
				vector1.setMode(Kernel.COORD_POLAR);
				vector1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void implicitConicEquationCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic)geo1;
				conic1.setToImplicit();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void equationConicEqnCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic)geo1;
				conic1.setToSpecific();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	public void equationExplicitConicEquationCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
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
		//geo.remove();
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			geo1.removeOrSetUndefinedIfHasFixedDescendent();
		}
		app.storeUndoInfo();
	}

	public void editCmd() {
		app.getDialogManager().showTextDialog((GeoText) geo);
	}

	public void renameCmd() {
		app.getDialogManager().showRenameDialog(geo, true, geo.getLabelSimple(), true);
	}

	public void fixObjectNumericCmd(final GeoNumeric num) {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
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
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
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
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			geo1.setLabelVisible(!geo1.isLabelVisible());
			geo1.updateRepaint();
			
		}
		app.storeUndoInfo();
	}

	public void showObjectCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			geo1.setEuclidianVisible(!geo1.isSetEuclidianVisible());
			geo1.updateRepaint();
			
		}
		app.storeUndoInfo();
	}

	public void showObjectAuxiliaryCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1.isAlgebraShowable()) {
				geo1.setAuxiliaryObject(!geo1.isAuxiliaryObject());
				geo1.updateRepaint();
			}
			
		}
		app.storeUndoInfo();
	}

	public void openPropertiesDialogCmd() {
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, geos);
	}

	public void inputFormCmd(final GeoUserInputElement inputElement) {
		inputElement.setInputForm();
		inputElement.updateRepaint();
		app.storeUndoInfo();
	}

	public void traceCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
			if (geo1.isTraceable()) {
				((Traceable) geo1).setTrace(!((Traceable) geo1).getTrace());
				geo1.updateRepaint();
			}
			
		}
		app.storeUndoInfo();
	}

	public void animationCmd() {
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
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
		for (int i = geos.size() - 1 ; i >= 0 ; i--) {
			GeoElement geo1 = geos.get(i);
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
				EuclidianStyleBarStatic.applyFixPosition(geos, isSelected, app.getActiveEuclidianView());
			}
		}
		app.storeUndoInfo();
	}

}

package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class StartPointModel extends MultipleOptionsModel {

	private Kernel kernel;
	private App app;
	private List<String> choices;
	
	public StartPointModel(App app, IComboListener listener) {
		super(listener);
		this.app = app;
		this.kernel = app.getKernel();
		choices = new ArrayList();
	}

	public Locateable getLocateableAt(int index) {
		return (Locateable)getObjectAt(index);
	}
	
	@Override
	public void updateProperties() {

		// repopulate model with names of points from the geoList's model
		// take all points from construction
		// TreeSet points =
		// kernel.getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);

		// check if properties have same values
		Locateable temp, geo0 = getLocateableAt(0);
		
		boolean equalLocation = true;

		for (int i = 0; i < getGeosLength(); i++) {
			if (geo0.getStartPoint() != getLocateableAt(i).getStartPoint()) {
				equalLocation = false;
				break;
			}

		}

		// set location textfield
		GeoElement p = (GeoElement) geo0.getStartPoint();
		if (equalLocation && p != null) {
			getListener().setSelectedIndex(0);
		} else
			getListener().setSelectedIndex(-1);

	}
	@Override
	public List<String> getChoiches(Localization loc) {
		TreeSet<GeoElement> points = kernel.getPointSet();
		choices.clear();
//		choices.add("");
		Iterator<GeoElement> it = points.iterator();
		int count = 0;
		while (it.hasNext() || ++count > MAX_CHOICES) {
			GeoElement p = it.next();
			choices.add(p.getLabel(StringTemplate.editTemplate));
		}
		return choices;
	}

	private int indexOf(final String item) {
		return choices.indexOf(item);
	}
	
	public void applyChanges(final String strLoc) {
		GeoPointND newLoc = null;

		if (strLoc == null || strLoc.trim().length() == 0) {
			// newLoc = null;
		} else {
			newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc,
					true, true);
		}

		for (int i=0; i < getGeosLength(); i++) {
			Locateable l = getLocateableAt(i);
			try {
				l.setStartPoint(newLoc);
				l.toGeoElement().updateRepaint();
			} catch (CircularDefinitionException e) {
				app.showError("CircularDefinition");
			}			
		}
	}
	
 	@Override
	protected void apply(int index, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getValueAt(int index) {
		// not used
		return 0;
	}

	@Override
	protected boolean isValidAt(int index) {
		boolean valid = true;
		GeoElement geo = getGeoAt(index);
		if (!(geo instanceof Locateable && !((Locateable) geo)
				.isAlwaysFixed()) || geo.isGeoImage()) {
			valid = false;
		}
		return valid;
	}
}

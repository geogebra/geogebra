package geogebra.web.kernel;

import geogebra.common.awt.ColorAdapter;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.cas.GeoGebraCasInterfaceSlim;
import geogebra.common.kernel.commands.AbstractAlgebraProcessor;
import geogebra.common.kernel.geos.AbstractGeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoListInterface;
import geogebra.common.kernel.geos.GeoNumericInterface;
import geogebra.common.kernel.geos.GeoPointInterface;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.web.main.Application;

public class Kernel extends AbstractKernel {

	public Kernel(Application application) {
	    
    }

	@Override
	public ColorAdapter getColorAdapter(int red, int green, int blue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberFormatAdapter getNumberFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScientificFormatAdapter getScientificFormat(int a, int b, boolean c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void notifyEuclidianViewCE() {
		// TODO Auto-generated method stub

	}

	@Override
	public LaTeXCache newLaTeXCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoElementInterface lookupLabel(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoElementInterface lookupLabel(String label, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractConstruction getConstruction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractApplication getApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyRepaint() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initUndoInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public GeoGebraCasInterfaceSlim newGeoGebraCAS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getConstructionStep() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void storeUndoInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public double[] getViewBoundsForGeo(GeoElementInterface geo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyUpdate(GeoElementInterface geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractAnimationManager getAnimatonManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyRename(GeoElementInterface geoElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyRemove(GeoElementInterface geoElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyUpdateVisualStyle(GeoElementInterface geoElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyUpdateAuxiliaryObject(GeoElementInterface geoElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAdd(GeoElementInterface geoElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractAlgebraProcessor getAlgebraProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoNumericInterface newNumeric(AbstractConstruction cons) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoListInterface newList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoElementInterface lookupCasCellLabel(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoElementInterface lookupCasRowReference(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoElementInterface Semicircle(String label,
	        GeoPointInterface geoPoint, GeoPointInterface geoPoint2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractGeoElementSpreadsheet getGeoElementSpreadsheet() {
		// TODO Auto-generated method stub
		return null;
	}

}

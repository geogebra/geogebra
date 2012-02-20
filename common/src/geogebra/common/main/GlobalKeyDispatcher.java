package geogebra.common.main;

import java.util.ArrayList;
import java.util.TreeSet;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;

public abstract class GlobalKeyDispatcher {

	public abstract void handleFunctionKeyForAlgebraInput(int i, GeoElement geo);
	
	protected AbstractApplication app;
	
	protected TreeSet<AlgoElementInterface> tempSet;

	protected TreeSet<AlgoElementInterface> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElementInterface>();
		}
		return tempSet;
	}

	private Coords tempVec;

	/**
	 * Tries to move the given objects after pressing an arrow key on the
	 * keyboard.
	 * 
	 * @param keyCode
	 *            VK_UP, VK_DOWN, VK_RIGHT, VK_LEFT
	 * @return whether any object was moved
	 */
	protected boolean handleArrowKeyMovement(ArrayList<GeoElement> geos,
			double xdiff, double ydiff, double zdiff) {
		GeoElement geo = geos.get(0);

		boolean allSliders = true;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geoi = geos.get(i);
			if (!geoi.isGeoNumeric() || !geoi.isChangeable()) {
				allSliders = false;
				continue;
			}
		}

		// don't move sliders, they will be handled later
		if (allSliders) {
			return false;
		}

		// set translation vector
		if (tempVec == null)
			tempVec = new Coords(4); // 4 coords for 3D
		double xd = geo.getAnimationStep() * xdiff;
		double yd = geo.getAnimationStep() * ydiff;
		double zd = geo.getAnimationStep() * zdiff;
		tempVec.setX(xd);
		tempVec.setY(yd);
		tempVec.setZ(zd);

		// move objects
		boolean moved = GeoElement.moveObjects(geos, tempVec, null, null);

		// nothing moved
		if (!moved) {
			for (int i = 0; i < geos.size(); i++) {
				geo = geos.get(i);
				// toggle boolean value
				if (geo.isChangeable() && geo.isGeoBoolean()) {
					GeoBoolean bool = (GeoBoolean) geo;
					bool.setValue(!bool.getBoolean());
					bool.updateCascade();
					moved = true;
				}
			}
		}

		if (moved)
			app.getKernel().notifyRepaint();

		return moved;
	}

}

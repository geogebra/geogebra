package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.euclidian3D.EuclidianView3DInterface;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoDrawingPadCorner;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

/**
 * Extends AlgoDrawingPadCorner for 3D view
 * 
 * @author mathieu
 *
 */
public class AlgoDrawingPadCorner3D extends AlgoDrawingPadCorner {

	/**
	 * 
	 * @param cons
	 *            cons
	 * @param label
	 *            label
	 * @param number
	 *            corner id
	 * @param evNum
	 *            euclidian view id
	 */
	public AlgoDrawingPadCorner3D(Construction cons, String label,
			NumberValue number, NumberValue evNum) {
		super(cons, label, number, evNum, 9);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return new point
	 */
	@Override
	protected GeoPointND newGeoPoint(Construction cons) {

		return new GeoPoint3D(cons);

	}

	/**
	 * 
	 * @param nv
	 *            number value
	 * @return true if nv has a value for 3D view (at this very moment)
	 */
	static final public boolean is3D(NumberValue nv) {
		return Kernel.isEqual(nv.getDouble(), -1);
	}

	@Override
	public final void compute() {

		App app = cons.getApplication();

		if (evNum != null && is3D(evNum)) {
			if (!app.hasEuclidianView3D() || !corner.isGeoElement3D()) {
				corner.setUndefined();
				return;
			}
			EuclidianView3DInterface ev = app.getEuclidianView3D();

			switch ((int) number.getDouble()) {
			case 1:
				corner.setCoords(ev.getXmin(), ev.getYmin(), ev.getZmin(), 1.0);
				break;
			case 2:
				corner.setCoords(ev.getXmax(), ev.getYmin(), ev.getZmin(), 1.0);
				break;
			case 3:
				corner.setCoords(ev.getXmax(), ev.getYmax(), ev.getZmin(), 1.0);
				break;
			case 4:
				corner.setCoords(ev.getXmin(), ev.getYmax(), ev.getZmin(), 1.0);
				break;

			case 5:
				corner.setCoords(ev.getXmin(), ev.getYmin(), ev.getZmax(), 1.0);
				break;
			case 6:
				corner.setCoords(ev.getXmax(), ev.getYmin(), ev.getZmax(), 1.0);
				break;
			case 7:
				corner.setCoords(ev.getXmax(), ev.getYmax(), ev.getZmax(), 1.0);
				break;
			case 8:
				corner.setCoords(ev.getXmin(), ev.getYmax(), ev.getZmax(), 1.0);
				break;

			case 9: // return size of Graphics View in pixels
				corner.setCoords(ev.getWidth(), ev.getHeight(), 1.0);
				break;
			case 10: // return size of Window in pixels
				// (to help with sizing for export to applet)
				// doesn't work very well as it receives updates only when
				// EuclidianView is changed
				corner.setCoords(app.getWidth(), app.getHeight(), 1.0);

				break;
				
			case 11: // return view direction
				Coords eye = ev.getEyePosition();
				corner.setCoords(eye.getX(), eye.getY(), eye.getZ(), 1.0);
				break;
				
			default:
				corner.setUndefined();
				break;
			}
			

		} else { // ev1 or ev2
			super.compute();
		}

	}

	@Override
	public Commands getClassName() {
		if (corner.isGeoElement3D()
				&& (kernel.isSaving() || kernel.isGettingUndo() || getConstruction()
						.isGettingXMLForReplace())) {
			return Commands.CornerThreeD; // save in XML (and undo XML) this
											// specific token to distinguish
											// from 2D corner command
		}

		return super.getClassName();
	}
}

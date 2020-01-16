package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDrawingPadCorner;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;

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
			GeoNumberValue number, GeoNumberValue evNum) {
		super(cons, label, number, evNum, 9);
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return new point
	 */
	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

	/**
	 * 
	 * @param nv
	 *            number value
	 * @return true if nv has a value for 3D view (at this very moment)
	 */
	static final public boolean is3D(NumberValue nv) {
		return DoubleUtil.isEqual(nv.getDouble(), -1);
	}

	@Override
	public final void compute() {

		App app = cons.getApplication();

		if (evNum != null && is3D(evNum)) {
			if (!app.isEuclidianView3Dinited() || !corner.isGeoElement3D()) {
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

			case CORNER_VIEW_DIRECTION: // return view direction
				Coords eye = ev.getEyePosition();
				corner.setCoords(eye.getX(), eye.getY(), eye.getZ(), 1.0);
				break;

			case CORNER_SCREEN_RIGHT: // return screen left-to-right direction
				Coords vx = ev.getToSceneMatrix().getVx();
				corner.setCoords(vx.getX(), vx.getY(), vx.getZ(), 1.0);
				break;

			case CORNER_AXES_SCALE:
				corner.setCoords(ev.getXscale(), ev.getYscale(),
						ev.getZscale(), 1.0);
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
	public String getDefinitionName(StringTemplate tpl) {
		if (tpl.getStringType() == StringType.GEOGEBRA_XML) {
			return Commands.CornerThreeD.name();
		}
		return super.getDefinitionName(tpl);
	}
}

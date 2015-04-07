package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.plugin.GeoClass;

public class GeoAxis3D extends GeoLine3D implements GeoAxisND {

	private int type;

	// for numbers and ticks
	private int ticksize = 5; // TODO

	public GeoAxis3D(Construction cons) {
		super(cons);
	}

	public int getType() {
		return type;
	}

	public GeoAxis3D(Construction c, int type) {
		this(c);

		this.type = type;

		switch (type) {
		case X_AXIS_3D:
			setCoord(Coords.O, Coords.VX);
			label = "xAxis3D";
			setObjColor(GColor.RED);
			break;

		case Y_AXIS_3D:
			setCoord(Coords.O, Coords.VY);
			label = "yAxis3D";
			// setObjColor(Color.GREEN);
			setObjColor(GColor.darkGreen);// (new
											// geogebra.awt.GColorD(0,0.5f,0));
			break;

		case Z_AXIS_3D:
			setCoord(Coords.O, Coords.VZ);
			label = "zAxis";
			setObjColor(GColor.BLUE);
			break;
		}

		labelSet = true;
		setFixed(true);
		setLabelVisible(false);
	}

	@Override
	public boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the axes available
		// in empty constructions too (for step == -1)
		return true;
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.AXIS3D;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return label;
	}

	/**
	 * overrides GeoElement method : this is a "constant" element, so the label
	 * is set
	 */
	@Override
	public boolean isLabelSet() {
		return true;
	}

	public String getUnitLabel() {
		// TODO Auto-generated method stub
		return "";
	}

	public int getTickStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getShowNumbers() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @return tick size
	 */
	public int getTickSize() {
		return ticksize;
	}

	@Override
	public Coords getDirectionInD3() {
		return new Coords(0, 0, 1, 0);
	}

	@Override
	public boolean isAxis() {
		return true;
	}

	@Override
	public String getLabel(StringTemplate tpl) {
		if (tpl.isPrintLocalizedCommandNames()) {
			return getLoc().getPlain(label);
		}
		return label;

	}

	@Override
	public boolean isTraceable() {
		return false;
	}

	@Override
	final protected void getCoordsXML(StringBuilder sb) {
		// not needed here
	}

	@Override
	public boolean isRenameable() {
		return false;
	}
}

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;

/**
 * Coordinate axis for 3D view
 *
 */
public class GeoAxis3D extends GeoLine3D implements GeoAxisND {

	private int type;

	// for numbers and ticks
	private int ticksize = 5; // TODO

	/** color used when axis is colored (not black) in 3D view */
	private GColor coloredColorFor3D;

	/**
	 * @param cons
	 *            construction
	 */
	public GeoAxis3D(Construction cons) {
		super(cons);
	}

	@Override
	public int getType() {
		return type;
	}

	/**
	 * @param c
	 *            construction
	 * @param type
	 *            0, 1, 2 for x, y, z axis
	 */
	public GeoAxis3D(Construction c, int type) {
		this(c);

		this.type = type;

		switch (type) {
		default:
		case X_AXIS_3D:
			setCoord(Coords.O, Coords.VX);
			label = "xAxis3D";
			coloredColorFor3D = GColor.RED;
			break;

		case Y_AXIS_3D:
			setCoord(Coords.O, Coords.VY);
			label = "yAxis3D";
			coloredColorFor3D = GColor.DARK_GREEN;
			break;

		case Z_AXIS_3D:
			setCoord(Coords.O, Coords.VZ);
			label = "zAxis";
			coloredColorFor3D = GColor.BLUE;
			break;
		}

		setLabelSet(true);
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

	@Override
	public String getUnitLabel() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public int getTickStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getShowNumbers() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @return tick size
	 */
	@Override
	public int getTickSize() {
		return ticksize;
	}

	@Override
	public Coords getDirectionInD3() {
		switch (type) {
		default:
		case X_AXIS_3D:
			return Coords.VX;
		case Y_AXIS_3D:
			return Coords.VY;
		case Z_AXIS_3D:
			return Coords.VZ;
		}
	}

	@Override
	public boolean isAxis() {
		return true;
	}

	@Override
	public String getLabel(StringTemplate tpl) {
		if (tpl.isPrintLocalizedCommandNames()) {
			return getLoc().getMenu(label);
		}
		return label;

	}

	@Override
	public boolean isTraceable() {
		return false;
	}

	@Override
	final protected void getCoordsXML(XMLStringBuilder sb) {
		// not needed here
	}

	@Override
	public boolean isRenameable() {
		return false;
	}

	@Override
	public final boolean isSelectionAllowed(EuclidianViewInterfaceSlim ev) {

		EuclidianSettings settings = ev == null
				? kernel.getApplication().getActiveEuclidianView().getSettings()
				: ev.getSettings();

		if (settings != null) {
			return settings.isSelectionAllowed(type);
		}

		return true;
	}
	
	@Override
	public int getLineThickness() {
		return EuclidianStyleConstants.AXES_THICKNESS;
	}

	@Override
	public boolean isProtected(EventType eventType) {
		return true;
	}

	@Override
	public void setColoredFor3D(boolean colored) {
		if (colored) {
			setObjColor(coloredColorFor3D);
		} else {
			setObjColor(GColor.BLACK);
		}
	}
}

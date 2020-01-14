package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.properties.Auxiliary;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for cylinder/cone from a conic and a height
 * 
 * @author mathieu
 *
 */
public abstract class AlgoQuadricLimitedConicHeight extends AlgoElement3D {

	// input
	private GeoConicND bottom;
	private NumberValue height;

	// output
	private GeoQuadric3DPart side;
	protected GeoConic3D top;
	private GeoQuadric3DLimited quadric;

	private AlgoQuadricSide algoSide;
	private AlgoQuadricEndTop algoTop;

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels
	 * @param bottom
	 *            bottom side
	 * @param height
	 *            height
	 * @param type
	 *            type (cylinder/cone)
	 */
	public AlgoQuadricLimitedConicHeight(Construction c, String[] labels,
			GeoConicND bottom, GeoNumberValue height, int type) {
		super(c);

		this.bottom = bottom;
		this.height = height;

		quadric = new GeoQuadric3DLimited(c, type);

		input = new GeoElement[] { bottom, (GeoElement) height };

		bottom.addAlgorithm(this);
		((GeoElement) height).addAlgorithm(this);

		// parent of output
		quadric.setParentAlgorithm(this);
		cons.addToAlgorithmList(this);

		setQuadric();

		algoSide = new AlgoQuadricSide(cons, quadric, true, bottom);
		side = (GeoQuadric3DPart) algoSide.getQuadric();
		side.setParentAlgorithm(this);
		side.setAuxiliaryObject(Auxiliary.YES_DEFAULT);
		quadric.setSide(side);

		createTop();

		quadric.setBottomTop(bottom, top);

		// output = new GeoElement[] {quadric,bottom,top,side};
		setOutput();

		quadric.initLabelsNoBottom(labels);
		quadric.updatePartsVisualStyle();

		if (height instanceof GeoNumeric) {
			if (((GeoNumeric) height).isIndependent()) {
				side.setChangeableParent((GeoNumeric) height, bottom,
						new ExtrudeConverter());
				top.setChangeableParent((GeoNumeric) height, bottom,
						new ExtrudeConverter());
			}
		}

	}

	/**
	 * create the top side
	 */
	final protected void createTop() {
		algoTop = new AlgoQuadricEndTop(cons, getQuadric());
		top = algoTop.getSection();
		top.setAuxiliaryObject(Auxiliary.YES_DEFAULT);
		top.setParentAlgorithm(this);

	}

	/**
	 * sets the output
	 */
	final protected void setOutput() {
		setOutput(new GeoElement[] { getQuadric(), getQuadric().getTop(),
				getQuadric().getSide() });
	}

	private void setQuadric() {

		Coords o = bottom.getMidpoint3D();

		// TODO cylinder with other conics (than circles)
		double r = bottom.getHalfAxis(0);
		double r2 = bottom.getHalfAxis(1);

		double altitude = height.getDouble();

		Coords d = bottom.getMainDirection().normalize();

		Coords o2 = o.add(d.mul(altitude));

		quadric.setDefined();

		setQuadric(o, o2, d, bottom.getEigenvec3D(0), r, r2, 0, altitude);
	}

	@Override
	public void compute() {

		setQuadric();

		// must compute side first for midpoint
		algoSide.compute();
		algoTop.compute();

		quadric.calcVolume();

	}

	abstract protected void setQuadric(Coords o1, Coords o2, Coords d,
			Coords eigen, double r, double r2, double min, double max);

	/**
	 * @return resulting limited quadric
	 */
	public GeoQuadric3DLimited getQuadric() {
		return quadric;
	}

	// @Override
	// public void update() {
	//
	// if (stopUpdateCascade) {
	// return;
	// }
	//
	// compute();
	// quadric.update();
	//
	// if (!getQuadric().isLabelSet()) { // geo is in sequence/list : update
	// // top and side
	// getQuadric().getTop().getParentAlgorithm().update();
	// getQuadric().getSide().getParentAlgorithm().update();
	// }
	//
	// }

	@Override
	protected void getOutputXML(StringBuilder sb) {
		super.getOutputXML(sb);

		// append XML for bottom once more, to avoid override of specific
		// properties
		if (bottom.isLabelSet()) {
			bottom.getXML(false, sb);
		}
	}

	// /////////////////////////////////////////////////////
	// FOR PREVIEWABLE
	// /////////////////////////////////////////////////////

	public void setOutputPointsEuclidianVisible(boolean b) {
		//
	}

	public void notifyUpdateOutputPoints() {
		//
	}

	public GeoElement getTopFace() {
		return top;
	}

	public GeoConicND getBottomFace() {
		return bottom;
	}

	/**
	 * @param b
	 *            whether side and top should be visible
	 */
	public void setOutputOtherEuclidianVisible(boolean b) {
		side.setEuclidianVisible(b);
		top.setEuclidianVisible(b);
	}

	/**
	 * Notify kernel about side & top update.
	 */
	public void notifyUpdateOutputOther() {
		getKernel().notifyUpdate(side);
		getKernel().notifyUpdate(top);
	}

}

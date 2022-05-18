package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for finding intersect points for 3D polygons
 * 
 * @author thilina
 *
 */
public class AlgoIntersectPolygons3D extends AlgoElement3D {
	// input
	protected GeoPolygon polyA;
	protected GeoPolygon polyB;
	// output
	protected OutputHandler<GeoElement> outputPoints;
	// intersections
	protected ArrayList<Coords> intersectingCoords;

	// temporary lines
	private GeoSegmentND[] segA;
	private GeoSegmentND[] segB;

	/**
	 * constructor with labels
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param polyA
	 *            first polygon
	 * @param polyB
	 *            second polygon
	 */
	public AlgoIntersectPolygons3D(Construction c, String[] labels,
			GeoPolygon polyA, GeoPolygon polyB) {
		super(c);
		this.polyA = polyA;
		this.polyB = polyB;
		this.outputPoints = createOutputPoints();

		segA = polyA.getSegments();
		segB = polyB.getSegments();

		this.intersectingCoords = new ArrayList<>();

		compute();
		setInputOutput();
		outputPoints.setLabelsMulti(labels);
		update();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = this.polyA;
		input[1] = this.polyB;

		setDependencies();
	}

	/**
	 * @return output point handler
	 */
	protected OutputHandler<GeoElement> createOutputPoints() {

		return new OutputHandler<>(new ElementFactory<GeoElement>() {
			@Override
			public GeoPoint3D newElement() {
				GeoPoint3D p = new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectPolygons3D.this);
				return p;
			}
		});
	}

	@Override
	public void compute() {

		// clears the point map
		this.intersectingCoords.clear();

		Coords o1, d1, o2, d2;
		Coords[] project;

		for (int i = 0; i < segA.length; i++) {

			o1 = segA[i].getPointInD(3, 0).getInhomCoordsInSameDimension();
			d1 = segA[i].getPointInD(3, 1).getInhomCoordsInSameDimension()
					.sub(o1);

			for (int k = 0; k < segB.length; k++) {

				o2 = segB[k].getPointInD(3, 0).getInhomCoordsInSameDimension();
				d2 = segB[k].getPointInD(3, 1).getInhomCoordsInSameDimension()
						.sub(o2);

				project = CoordMatrixUtil.nearestPointsFromTwoLines(o1, d1, o2,
						d2);

				if (project != null && !Double.isNaN(project[2].get(1))
						&& project[0].equalsForKernel(project[1],
								Kernel.STANDARD_PRECISION)) {
					double t1 = project[2].get(1); // parameter on line 1
					double t2 = project[2].get(2); // parameter on line 2

					if (t1 > segA[i].getMinParameter()
							- Kernel.STANDARD_PRECISION
							&& t1 < segA[i].getMaxParameter()
									+ Kernel.STANDARD_PRECISION
							&& t2 > segB[k].getMinParameter()
									- Kernel.STANDARD_PRECISION
							&& t2 < segB[k].getMaxParameter()
									+ Kernel.STANDARD_PRECISION) {
						intersectingCoords.add(new Coords(project[0]));
					}
				}
			}
		}

		// update and/or create points
		this.outputPoints.adjustOutputSize(this.intersectingCoords.size() > 0
				? this.intersectingCoords.size() : 1);

		// affect new computed points
		int index = 0;
		for (; index < this.intersectingCoords.size(); index++) {
			Coords coords = this.intersectingCoords.get(index);
			GeoPoint3D point = (GeoPoint3D) this.outputPoints.getElement(index);
			point.setCoords(coords);
			point.updateCoords();
		}

		// other points are undefined
		for (; index < this.outputPoints.size(); index++) {
			this.outputPoints.getElement(index).setUndefined();
		}

		outputPoints.updateLabels();
	}

	@Override
	public GetCommand getClassName() {

		return Commands.Intersect;
	}
}

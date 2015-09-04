package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Algo for finding intersect points for 3D polygons
 * 
 * @author thilina
 *
 */
public class AlgoIntersectPolygons3D extends AlgoElement3D {
	// input
	protected GeoPolygon3D polyA, polyB;
	// output
	protected OutputHandler<GeoElement> outputPoints;
	// intersections
	protected ArrayList<Coords> intersectingCoords;

	// temporary lines
	private GeoSegmentND[] segA, segB;
		
	/**
	 * constructor with labels
	 * 
	 * @param c
	 * @param labels
	 * @param polyA
	 * @param polyB
	 */
	public AlgoIntersectPolygons3D(Construction c, String[] labels,
			GeoPolygon3D polyA, GeoPolygon3D polyB) {
		super(c);
		this.polyA = polyA;
		this.polyB = polyB;
		this.outputPoints = createOutputPoints();

		segA = polyA.getSegments();
		segB = polyB.getSegments();

		this.intersectingCoords = new ArrayList<Coords>();

		compute();
		setInputOutput();
		setLabels(labels);
		update();
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param polyA
	 * @param polyB
	 */
	public AlgoIntersectPolygons3D(Construction c, GeoPolygon3D polyA,
			GeoPolygon3D polyB) {
		this(c, null, polyA, polyB);

	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = this.polyA;
		input[1] = this.polyB;

		setDependencies();
	}

	/**
	 * @return
	 */
	protected OutputHandler<GeoElement> createOutputPoints() {

		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p = new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectPolygons3D.this);
				return p;
			}
		});
	}

	/**
	 * if only one label (e.g. "A") for more than one output, new labels will be
	 * A_1, A_2, ...
	 * 
	 * @param labels
	 */
	protected void setLabels(String[] labels) {
		// if only one label (e.g. "A") for more than one output, new labels
		// will be A_1, A_2, ...
		if (labels != null && labels.length == 1 &&
		// outputPoints.size() > 1 &&
				labels[0] != null && !labels[0].equals("")) {
			this.outputPoints.setIndexLabels(labels[0]);
		} else {

			this.outputPoints.setLabels(labels);
			this.outputPoints.setIndexLabels(this.outputPoints.getElement(0)
					.getLabel(StringTemplate.defaultTemplate));
		}
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
				
				if (project != null
						&& !Double.isNaN(project[2].get(1))
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
		this.outputPoints
				.adjustOutputSize(this.intersectingCoords.size() > 0 ? this.intersectingCoords
						.size() : 1);

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

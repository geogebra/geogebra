package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.scripting.CmdSetSpinSpeed;
import org.geogebra.common.geogebra3D.kernel3D.scripting.CmdSetViewDirection;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.main.App;

/**
 * Command dispatcher for 3D
 * 
 * @author matthieu
 * 
 */
public class CommandDispatcher3D extends CommandDispatcher {

	/**
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CommandDispatcher3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandProcessor commandTableSwitch(Command c) {
		String cmdName = c.getName();
		try {
			switch (Commands.valueOf(cmdName)) {

			case Segment:
				return new CmdSegment3D(kernel);
			case Line:
				return new CmdLine3D(kernel);
			case Ray:
				return new CmdRay3D(kernel);
			case Vector:
				return new CmdVector3D(kernel);
			case Polygon:
				return new CmdPolygon3D(kernel);
			case Area:
				return new CmdArea3D(kernel);
			case PolyLine:
				return new CmdPolyLine3D(kernel);
			case Point:
				return new CmdPoint3D(kernel);
			case Midpoint:
			case Center:
				return new CmdMidpoint3D(kernel);

			case Tangent:
				return new CmdTangent3D(kernel);

			case Polar:
				return new CmdPolar3D(kernel);

			case Diameter:
			case ConjugateDiameter:
				return new CmdDiameter3D(kernel);

			case Circle:
				return new CmdCircle3D(kernel);

			case Ellipse:
				return new CmdEllipseHyperbola3D(kernel,
						GeoConicNDConstants.CONIC_ELLIPSE);
			case Hyperbola:
				return new CmdEllipseHyperbola3D(kernel,
						GeoConicNDConstants.CONIC_HYPERBOLA);
			case Conic:
				return new CmdConic3D(kernel);

			case CircumcircleSector:
			case CircumcircularSector:
				return new CmdCircumcircleSector3D(kernel);

			case CircumcircleArc:
			case CircumcircularArc:
				return new CmdCircumcircleArc3D(kernel);

			case Arc:
				return new CmdArcSector3D(kernel,
						GeoConicNDConstants.CONIC_PART_ARC);
			case Sector:
				return new CmdArcSector3D(kernel,
						GeoConicNDConstants.CONIC_PART_SECTOR);

			case CircleArc:
			case CircularArc:
				return new CmdCircleArcSector3D(kernel,
						GeoConicNDConstants.CONIC_PART_ARC);

			case CircleSector:
			case CircularSector:
				return new CmdCircleArcSector3D(kernel,
						GeoConicNDConstants.CONIC_PART_SECTOR);

			case Semicircle:
				return new CmdSemicircle3D(kernel);

			case Parabola:
				return new CmdParabola3D(kernel);

			case Corner:
				return new CmdCorner3D(kernel);

			case CornerThreeD:
				return new CmdVertexForce3D(kernel);

			case Locus:
				return new CmdLocus3D(kernel);

			case Vertex:
				return new CmdVertex3D(kernel);
			case FirstAxis:
				return new CmdFirstAxis3D(kernel);
			case Focus:
				return new CmdFocus3D(kernel);

			case OrthogonalLine:
				return new CmdOrthogonalLine3D(kernel);

			case LineBisector:
			case PerpendicularBisector:
				return new CmdLineBisector3D(kernel);

			case AngleBisector:
			case AngularBisector:
				return new CmdAngularBisector3D(kernel);

			case OrthogonalVector:
				return new CmdOrthogonalVector3D(kernel);

			case UnitOrthogonalVector:
				return new CmdUnitOrthogonalVector3D(kernel);

			case UnitVector:
				return new CmdUnitVector3D(kernel);

			case Curve:
			case CurveCartesian:
				return new CmdCurveCartesian3D(kernel);

			case Plane:
				return new CmdPlane(kernel);
			case PerpendicularPlane:
				return new CmdOrthogonalPlane(kernel);
			case OrthogonalPlane:
				return new CmdOrthogonalPlane(kernel); // old name

			case PlaneBisector:
				return new CmdPlaneBisector(kernel);

				// case Polyhedron: return new CmdPolyhedron(kernel);

			case Prism:
				return new CmdPrism(kernel);
			case Pyramid:
				return new CmdPyramid(kernel);

			case Tetrahedron:
				return new CmdArchimedeanSolid(kernel, Commands.Tetrahedron);
			case Cube:
				return new CmdArchimedeanSolid(kernel, Commands.Cube);
			case Octahedron:
				return new CmdArchimedeanSolid(kernel, Commands.Octahedron);
			case Dodecahedron:
				return new CmdArchimedeanSolid(kernel, Commands.Dodecahedron);
			case Icosahedron:
				return new CmdArchimedeanSolid(kernel, Commands.Icosahedron);

			case Net:
				return new CmdPolyhedronNet(kernel);

				/*
				 * case Polyhedron: return new CmdPolyhedronConvex(kernel);
				 */

			case PointIn:
				return new CmdPointIn3D(kernel);

			case Distance:
				return new CmdDistance3D(kernel);

			case ClosestPoint:
				return new CmdClosestPoint3D(kernel);

			case ClosestPointRegion:
				return new CmdClosestPointRegion(kernel);

			case Intersect:
				return new CmdIntersect3D(kernel);

			case IntersectPath:
			case IntersectionPaths: // deprecated
			case IntersectRegion: // deprecated
				return new CmdIntersectPath3D(kernel);

			case IntersectCircle:
			case IntersectConic:
				return new CmdIntersectConic(kernel);

			case Sphere:
				return new CmdSphere3D(kernel);

			case Cone:
				return new CmdCone(kernel);
			case InfiniteCone:
				return new CmdConeInfinite(kernel);
			case ConeInfinite:
				return new CmdConeInfinite(kernel); // removed for release
													// candidate

			case Cylinder:
				return new CmdCylinder(kernel);
			case InfiniteCylinder:
				return new CmdCylinderInfinite(kernel);
			case CylinderInfinite:
				return new CmdCylinderInfinite(kernel); // removed for release
														// candidate
			case Side:
			case QuadricSide:
				return new CmdQuadricSide(kernel);
			case Bottom:
				return new CmdBottom(kernel);
			case Top:
				return new CmdTop(kernel);
			case Ends:
				return new CmdEnds(kernel);

			case Function:
				return new CmdFunction2Var(kernel);

			case Surface:
				return new CmdSurfaceCartesian3D(kernel);

			case Angle:
				return new CmdAngle3D(kernel);

			case Translate:
				return new CmdTranslate3D(kernel);

			case Rotate:
				return new CmdRotate3D(kernel);
			case Reflect:
			case Mirror:
				return new CmdMirror3D(kernel);

			case Dilate:
				return new CmdDilate3D(kernel);

			case Length:
				return new CmdLength3D(kernel);

			case Volume:
				return new CmdVolume(kernel);

			case Height:
				return new CmdHeight(kernel);

			case Axes:
				return new CmdAxes3D(kernel);

				// scripting : 3D
			case SetViewDirection:
				return new CmdSetViewDirection(kernel);

			case SetSpinSpeed:
				return new CmdSetSpinSpeed(kernel);

				// polygon operations
			case Difference:
				return new CmdDifference3D(kernel);

			case Union:
				return new CmdUnion3D(kernel);

			default:
				return super.commandTableSwitch(c);
			}
		} catch (Exception e) {
			App.debug("command not found / CAS command called");
		}
		return null;
	}

}

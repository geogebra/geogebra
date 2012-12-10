package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.main.App;

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
	public CommandProcessor commandTableSwitch(String cmdName) {
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
			case PolyLine:
				return new CmdPolyLine3D(kernel);
			case Point:
				return new CmdPoint3D(kernel);
			case Midpoint:
				return new CmdMidpoint3D(kernel);
			case Circle:
				return new CmdCircle3D(kernel);

				
			case OrthogonalLine:
				return new CmdOrthogonalLine3D(kernel);
				
			case OrthogonalVector:
				return new CmdOrthogonalVector3D(kernel);

			case UnitOrthogonalVector:
				return new CmdUnitOrthogonalVector3D(kernel);

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
				return new CmdArchimedeanSolid(kernel, "Tetrahedron");
			case Cube:
				return new CmdArchimedeanSolid(kernel, "Cube");
			case Octahedron:
				return new CmdArchimedeanSolid(kernel, "Octahedron");
			case Dodecahedron:
				return new CmdArchimedeanSolid(kernel, "Dodecahedron");
			case Icosahedron:
				return new CmdArchimedeanSolid(kernel, "Icosahedron");

			case PointIn:
				return new CmdPointIn3D(kernel);

			case Distance:
				return new CmdDistance3D(kernel);
				
			case ClosestPoint:
				return new CmdClosestPoint3D(kernel);
				
			case Intersect:
				return new CmdIntersect3D(kernel);
			case Intersection:
				return new CmdIntersect3D(kernel);
				// case IntersectionPaths: return new
				// CmdIntersectionPaths(kernel);
			case IntersectionPaths:
				return new CmdIntersectionPaths3D(kernel);

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

			case QuadricSide:
				return new CmdQuadricSide(kernel);
			case Bottom:
				return new CmdBottom(kernel);
			case Top:
				return new CmdTop(kernel);

			case Function:
				return new CmdFunction2Var(kernel);

			case Surface:
			case SurfaceCartesian:
				return new CmdSurfaceCartesian3D(kernel);

			case Angle:
				return new CmdAngle3D(kernel);

			case Translate:
				return new CmdTranslate3D(kernel);
				
			case Rotate:
				return new CmdRotate3D(kernel);

			case Length:
				return new CmdLength3D(kernel);
			default:
				return super.commandTableSwitch(cmdName);
			}
		} catch (Exception e) {
			App.debug("command not found / CAS command called");
		}
		return null;
	}

}

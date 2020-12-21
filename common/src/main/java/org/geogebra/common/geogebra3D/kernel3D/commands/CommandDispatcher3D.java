package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.scripting.CmdSetSpinSpeed;
import org.geogebra.common.geogebra3D.kernel3D.scripting.CmdSetViewDirection;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.util.debug.Log;

/**
 * Command dispatcher for 3D
 *
 * @author Mathieu
 *
 */
public abstract class CommandDispatcher3D extends CommandDispatcher {

	/** dispatcher for 3D commands */
	protected static CommandDispatcherInterface commands3DDispatcher = null;

	private CommandDispatcher commandDispatcher;

	/**
	 * @param kernel
	 *            kernel
	 */
	public CommandDispatcher3D(Kernel kernel) {
		super(kernel);
		commandDispatcher = kernel.getApplication().newCommandDispatcher(kernel);
	}

	@Override
	public CommandProcessor commandTableSwitch(Command c) {
		String cmdName = c.getName();
		try {
			Commands command = Commands.valueOf(cmdName);
			if (!isAllowedByNameFilter(command)) {
				Log.info("The command is not allowed by the command filter");
				return null;
			}
			switch (command) {

			case Segment:
				return new CmdSegment3D(kernel);
			case Line:
				return new CmdLine3D(kernel);
			case Ray:
				return new CmdRay3D(kernel);
			case Polygon:
				return new CmdPolygon3D(kernel);
			case Area:
				return new CmdArea3D(kernel);
			case Polyline:
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
				return new CmdAxis3D(kernel, 0);
			case SecondAxis:
				return new CmdAxis3D(kernel, 1);
			case Focus:
				return new CmdFocus3D(kernel);

			case PerpendicularLine:
			case OrthogonalLine:
				return new CmdOrthogonalLine3D(kernel);

			case LineBisector:
			case PerpendicularBisector:
				return new CmdLineBisector3D(kernel);

			case AngleBisector:
			case AngularBisector:
				return new CmdAngularBisector3D(kernel);

			case PerpendicularVector:
			case OrthogonalVector:
				return new CmdOrthogonalVector3D(kernel);

			case UnitPerpendicularVector:
			case UnitOrthogonalVector:
				return new CmdUnitOrthogonalVector3D(kernel);

			case Direction:
				return new CmdUnitVector3D(kernel, false);
			case UnitVector:
				return new CmdUnitVector3D(kernel, true);

			case Curve:
			case CurveCartesian:
				return new CmdCurveCartesian3D(kernel);

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

			case Angle:
				return new CmdAngle3D(kernel);

			case InteriorAngles:
				return new CmdInteriorAngles3D(kernel);

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

			// 3D commands dispatcher
			case Plane:
			case PerpendicularPlane:
			case OrthogonalPlane:
			case PlaneBisector:
			case Prism:
			case Pyramid:
			case Tetrahedron:
			case Cube:
			case Octahedron:
			case Dodecahedron:
			case Icosahedron:
			case Polyhedron:
			case Net:
			case Sphere:
			case Cone:
			case InfiniteCone:
			case ConeInfinite:
			case Cylinder:
			case InfiniteCylinder:
			case CylinderInfinite:
			case Side:
			case QuadricSide:
			case Bottom:
			case Top:
			case Ends:
				return get3DDispatcher().dispatch(command, kernel);
			default:
				return super.commandTableSwitch(c);
			}
		} catch (RuntimeException e) {
			Log.debug("command not found / CAS command called: " + cmdName);
		}
		return null;
	}

	// a hacky solution to avoid code repetition
	@Override
	public CommandDispatcherInterface getDiscreteDispatcher() {
		return commandDispatcher.getDiscreteDispatcher();
	}

	@Override
	public CommandDispatcherInterface getScriptingDispatcher() {
		return commandDispatcher.getScriptingDispatcher();
	}

	@Override
	public CommandDispatcherInterface getAdvancedDispatcher() {
		return commandDispatcher.getAdvancedDispatcher();
	}

	@Override
	public CommandDispatcherInterface getCASDispatcher() {
		return commandDispatcher.getCASDispatcher();
	}

	@Override
	public CommandDispatcherInterface getStatsDispatcher() {
		return commandDispatcher.getStatsDispatcher();
	}

	@Override
	public CommandDispatcherInterface getStepsDispatcher() {
		return commandDispatcher.getStepsDispatcher();
	}

	@Override
	public CommandDispatcherInterface getProverDispatcher() {
		return commandDispatcher.getProverDispatcher();
	}
}

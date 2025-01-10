package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.scripting.CmdSetSpinSpeed;
import org.geogebra.common.geogebra3D.kernel3D.scripting.CmdSetViewDirection;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;
import org.geogebra.common.kernel.commands.Commands;

/**
 * Factory for command processors that are only relevant in 3D
 */
public class SpatialCommandProcessorFactory implements CommandProcessorFactory {
	@Override
	public CommandProcessor getProcessor(Commands command, Kernel kernel) {

		if (!kernel.getApplication().areCommands3DEnabled()) {
			return null;
		}

		switch (command) {
			case Plane:
				return new CmdPlane(kernel);
			// English for scripting
			case PerpendicularPlane:
				// internal name
			case OrthogonalPlane:
				return new CmdOrthogonalPlane(kernel);
			case PlaneBisector:
				return new CmdPlaneBisector(kernel);

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

			case Sphere:
				return new CmdSphere3D(kernel);

			case Cone:
				return new CmdCone(kernel);
			case InfiniteCone:
			case ConeInfinite:
				return new CmdConeInfinite(kernel);
			case Cylinder:
				return new CmdCylinder(kernel);
			case InfiniteCylinder:
			case CylinderInfinite:
				return new CmdCylinderInfinite(kernel);
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
			case Volume:
				return new CmdVolume(kernel);
			case Height:
				return new CmdHeight(kernel);
			case SetViewDirection:
				return new CmdSetViewDirection(kernel);

			case SetSpinSpeed:
				return new CmdSetSpinSpeed(kernel);
			case ClosestPointRegion:
				return new CmdClosestPointRegion(kernel);
			case IntersectCircle:
			case IntersectConic:
				return new CmdIntersectConic(kernel);
			case CornerThreeD:
				return new CmdVertexForce3D(kernel);
			default:
				break;
		}
		return null;
	}
}

package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet
 * loading)
 *
 */
public class CommandDispatcherCommands3D implements CommandDispatcherInterface {
	@Override
	public CommandProcessor dispatch(Commands c, Kernel kernel) {

		if (!kernel.getApplication().areCommands3DEnabled()) {
			return null;
		}

		switch (c) {
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

			default:
				break;
		}
		return null;
	}
}

package geogebra3D.kernel3D.commands;


import geogebra.kernel.Kernel;
import geogebra.kernel.commands.CmdIntersect;
import geogebra.kernel.commands.CmdIntersectionPaths;
import geogebra.kernel.commands.CmdMidpoint;
import geogebra.kernel.commands.CmdUnitOrthogonalVector;
import geogebra.kernel.commands.CommandDispatcher;

public class CommandDispatcher3D extends CommandDispatcher {
	

	public CommandDispatcher3D(Kernel kernel) {
		super(kernel);
	}

	protected void initCmdTable() {  
		super.initCmdTable();
		//Application.debug("CommandDispatcher3D.initCmdTable()");
		cmdTable.put("Segment", new CmdSegment3D(kernel));
		cmdTable.put("Line", new CmdLine3D(kernel));
		cmdTable.put("Ray", new CmdRay3D(kernel));
		cmdTable.put("Vector", new CmdVector3D(kernel));
		cmdTable.put("Polygon", new CmdPolygon3D(kernel));
		cmdTable.put("PolyLine", new CmdPolyLine3D(kernel));
		cmdTable.put("Point", new CmdPoint3D(kernel));
		cmdTable.put("Midpoint", new CmdMidpoint3D(kernel));	
		
		cmdTable.put("Circle", new CmdCircle3D(kernel));
		
		
		cmdTable.put("OrthogonalLine", new CmdOrthogonalLine3D(kernel));
    	cmdTable.put("OrthogonalVector", new CmdOrthogonalVector3D(kernel));
		
    	cmdTable.put("UnitOrthogonalVector", new CmdUnitOrthogonalVector3D(kernel));	
		
		
		cmdTable.put("CurveCartesian", new CmdCurveCartesian3D(kernel));
		
		
		cmdTable.put("Plane", new CmdPlane(kernel));
		cmdTable.put("OrthogonalPlane", new CmdOrthogonalPlane(kernel));
		cmdTable.put("PlaneBisector", new CmdPlaneBisector(kernel));
		
		//cmdTable.put("Polyhedron", new CmdPolyhedron(kernel));
		
		
		cmdTable.put("Prism", new CmdPrism(kernel));
		cmdTable.put("Pyramid", new CmdPyramid(kernel));
		
		
		cmdTable.put("PointIn", new CmdPointIn3D(kernel));   
		  
		
    	cmdTable.put("Intersect", new CmdIntersect3D(kernel));	
    	cmdTable.put("Intersection", new CmdIntersect3D(kernel)); 
    	//cmdTable.put("IntersectionPaths", new CmdIntersectionPaths(kernel));
    	cmdTable.put("IntersectionPaths", new CmdIntersectionPaths3D(kernel));
    	
    	cmdTable.put("Sphere", new CmdSphere3D(kernel));
    	
    	cmdTable.put("Cone", new CmdCone(kernel));	
    	cmdTable.put("ConeInfinite", new CmdConeInfinite(kernel));	
    	
     	cmdTable.put("Cylinder", new CmdCylinder(kernel));	
     	cmdTable.put("CylinderInfinite", new CmdCylinderInfinite(kernel));	
	       	
     	cmdTable.put("QuadricSide", new CmdQuadricSide(kernel));	
     	cmdTable.put("Bottom", new CmdBottom(kernel));	
     	cmdTable.put("Top", new CmdTop(kernel));	

		cmdTable.put("Function", new CmdFunction2Var(kernel));
		
		cmdTable.put("SurfaceCartesian", new CmdSurfaceCartesian3D(kernel));
    	    	
	}
	
	
	
}

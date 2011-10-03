/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package geogebra3D;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Region;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;
import geogebra.plugin.GgbAPI;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoConic3D;
import geogebra3D.kernel3D.GeoCurveCartesian3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.GeoPolyhedron;
import geogebra3D.kernel3D.GeoQuadric3D;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * @author  ggb3D
 */
public class Test3D{
	
	private void test(){

		//testLoad("test");
		//kernel3D.setResolveUnkownVarsAsDummyGeos(true);
		//testQuadric();
		
		
	}
	
	
	
	
	
	Construction cons;
	Kernel3D kernel3D;
	EuclidianView view2D;
	Application3D app;
	
	
	GeoPlane3D xOyPlane;


	public Test3D(Kernel3D kernel3D, EuclidianView view2D, EuclidianView3D view3D, Application3D app){
		
		this.kernel3D=kernel3D;
		cons=kernel3D.getConstruction();
		this.view2D = view2D;
		this.app = app;
		
		app.setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY);

		//view2D.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		//view2D.showAxes(true, true);
		view2D.setCoordSystem(100,view2D.getYZero(),view2D.getXscale(),view2D.getYscale());
		
		
        //init 3D view
        view3D.setZZero(-0.0);
        view3D.setYZero(-117);
        view3D.setRotXYinDegrees(-60,20);
        //view3D.setRotXYinDegrees(0,0,true);

        test();
        
        //testList();
        //testLoad("plane");
        //testAxis();
		
		
		//testTetrahedron();
		
		//testSpring();
		
        //demos();
        //testCube(true);//testSave("polyhedron3d");
        //testLoad("polyhedron3d");
        //testLoad("tetrahedron-and-plane");
        //testLoad("viewInFrontOf");
        //testLoad("vectors");
        
        //testNumerous(400, Math.PI/48, 0.01);
        //testNumerous2(400, Math.PI/6, 0.01);
        
        //testLine();
        //testLineAndPlane();
        //testPlaneThrough();
        
        //testPlaneOrtho();
        
        //testPoint(1,1,1);testSave("point3d");
        //testSegment();
        //testPolygon();testSave("polygon3d");
	
        //testLoad("polygon3d");
        
        //testLoad("test");
        
		//testRegion();
		
		
		
		//testConic3D();
		//testPolygon();
		//testPlane();


		//testQuadric();
    	
		//testRay3D();
		//testVector3D();
		//testAlgoPyramide();
        //testAlgoPolyhedron();
		
		//testPolyhedron();
		//testTetrahedron();
		
		//testIntersectLinePlane();
		//testIntersectLineLine();
		//testIntersectParallelLines();
        
        //testFuntion2Var();
        
        //testCurve3D();
	}

	

	
}

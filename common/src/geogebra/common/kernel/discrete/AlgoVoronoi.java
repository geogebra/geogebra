/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.discrete;

import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.discrete.delauney.Pnt;
import geogebra.common.kernel.discrete.delauney.Triangle;
import geogebra.common.kernel.discrete.delauney.Triangulation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


/**
 * Mode of a list. Adapted from AlgoMode
 * @author Michael Borcherds
 * @version 
 */

public class AlgoVoronoi extends AlgoElement implements GraphAlgo {

	
	private GeoList inputList; //input
    private GeoLocus locus; // output   
    private ArrayList<MyPoint> al;
    private int size;

    public AlgoVoronoi(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        locus = new GeoLocus(cons);

        setInputOutput();
        compute();
        locus.setLabel(label);
    }

    public Algos getClassName() {
        return Algos.AlgoVoronoi;
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        setOnlyOutput(locus);
        setDependencies(); // done by AlgoElement
    }

    public GeoLocus getResult() {
        return locus;
    }

    public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		locus.setUndefined();
    		return;
    	} 
       
    	
		Triangulation dt;                   // Delaunay triangulation
		Triangle initialTriangle;           // Initial triangle


		double initialSize = 10;     // Size of initial triangle
		double inhom[] = new double[2];
		
		
		// make sure initial triangle is large enough to contain all the points
		for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				if (Math.abs(inhom[0]) > initialSize) initialSize = Math.abs(inhom[0]);
				if (Math.abs(inhom[1]) > initialSize) initialSize = Math.abs(inhom[1]);
			}
		}
		
		initialSize *= 3;


		initialTriangle = new Triangle(
				new Pnt(-initialSize, -initialSize),
				new Pnt( initialSize, -initialSize),
				new Pnt(           0,  initialSize));
		dt = new Triangulation(initialTriangle);
		

		for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				dt.delaunayPlace(new Pnt(inhom[0], inhom[1]));
			}
		}
		
		
		//System.out.println("\n\n\n");
		
		//boolean oldState = cons.isSuppressLabelsActive();
		//cons.setSuppressLabelCreation(true);
		
		
		int index = 0;
		
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
        
        TreeSet<MyLine> tree = new TreeSet<MyLine>(AlgoDelauneyTriangulation.getComparator());

		
        // Keep track of sites done; no drawing for initial triangles sites
        HashSet<Pnt> done = new HashSet<Pnt>(initialTriangle);
        for (Triangle triangle : dt)
            for (Pnt site: triangle) {
                if (done.contains(site)) continue;
                done.add(site);
                List<Triangle> list = dt.surroundingTriangles(site, triangle);
				//Pnt[] vertices = new Pnt[list.size()];
                int i = 0;
                Pnt firstPoint = null;
                Pnt prevPoint = null;
                for (Triangle tri: list) {
                    Pnt p = tri.getCircumcenter();
                    //al.add(new MyPoint(p.coord(0), p.coord(1), i != 0));

                    if (prevPoint != null)
                    	tree.add(new MyLine(AwtFactory.prototype.newPoint2D(prevPoint.coord(0) , prevPoint.coord(1)), AwtFactory.prototype.newPoint2D(p.coord(0) , p.coord(1))));

                    
                    if (i == 0) firstPoint = p;
                    prevPoint = p;
                    i++;
                    //vertices[i++] = new GeoPoint(cons, null, p.coord(0), p.coord(1), 1);
                    //Application.debug(p.coord(0)+" "+p.coord(1));
                }
                
                // close curve
                //al.add(new MyPoint(firstPoint.coord(0), firstPoint.coord(1), true));
            	tree.add(new MyLine(AwtFactory.prototype.newPoint2D(prevPoint.coord(0) , prevPoint.coord(1)), AwtFactory.prototype.newPoint2D(firstPoint.coord(0) , firstPoint.coord(1))));

                //draw(vertices, withFill? getColor(site) : null);
                //if (withSites) draw(site);
                
                //GeoPolygon poly = new GeoPolygon(cons, (GeoPoint2[])vertices);
                //outputList.add(poly);
                //setListElement(index ++, vertices);
                
            }
        
        Iterator<MyLine> it = tree.iterator();
        
        while (it.hasNext()) {
        	MyLine line = it.next();
        	al.add(new MyPoint(line.p1.getX() , line.p1.getY(), false));
        	al.add(new MyPoint(line.p2.getX() , line.p2.getY(), true));
        }

        
		//cons.setSuppressLabelCreation(oldState);
		locus.setPoints(al);
		locus.setDefined(true);

        
        //outputList.setDefined(true);
       
    }

	// TODO Consider locusequability
    

    

}

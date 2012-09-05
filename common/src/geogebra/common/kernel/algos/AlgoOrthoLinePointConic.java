/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoLinePointLine.java
 *
 * line through P orthogonal to l
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.advanced.AlgoPointOnPath;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoOrthoLinePointConic extends AlgoElement {

    private GeoPoint P; // input
    private GeoConic l; // input
    private GeoLine[] g; // output      
    
    private GeoNumeric[] n;
    private AlgoPointOnPath[] algoPoint;
    private AlgoClosestPoint closestPoint;

    /** Creates new AlgoOrthoLinePointLine 
     * @param cons 
     * @param label 
     * @param P 
     * @param l */
    public AlgoOrthoLinePointConic(
        Construction cons,
        String label,
        GeoPoint P,
        GeoConic l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine[4];
        n = new GeoNumeric[4];
        algoPoint = new AlgoPointOnPath[4];
        closestPoint = new AlgoClosestPoint(cons,l,P);
        for(int i=0; i<4; i++){
        	g[i] = new GeoLine(cons);
        	g[i].setStartPoint(P);
        	n[i] = new GeoNumeric(cons);
        	algoPoint[i] = new AlgoPointOnPath(cons,l,0,0,n[i]);
        	cons.removeFromConstructionList(algoPoint[i]);
        	//algoPoint[i].remove();
        }
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();
        for(int i=0;i<4;i++)
        	g[0].setLabel(label);
        
        addIncidence();
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoOrthoLinePointLine;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ORTHOGONAL;
    }
    
    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
    	//for (int i=0; i<4; i++)
    		P.addIncidence(g[0]);
	}

    
    
    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l.toGeoElement();
        
        setOutputLength(4);
        for(int i=0;i<4;i++)
        	setOutput(i,g[i]);
        
        setDependencies(); // done by AlgoElement
    }
    
    GeoConic getC(){
    	return l;
    }
    
    GeoLine[] getLines() {
        return g;
    }
    
    GeoPoint getP() {
        return P;
    }
    
    /**
     *  calc the line g through P and normal to l   
     */
    @Override
	public final void compute() {
    	/*
        if(l.getType()==GeoConic.CONIC_ELLIPSE){
        double[] params = l.getPerpendicularParams(P.getCoords());
        for(int i=0;i<4;i++){
        	n[i].setValue(PathNormalizer.toNormalizedPathParameter(Math.asin(params[i]),l.getMinParameter(),l.getMaxParameter()));
        	n[i].updateCascade();        	    	
        	//algoPoint[i].update();
        	GeoVec3D.lineThroughPoints(P, algoPoint[i].getP(), g[i]);
        	Application.debug(g[i]);
        	g[i].setLabel("r_"+i);
        }
        }
        else{    */    	
        	GeoVec3D.lineThroughPoints(P, closestPoint.getP(), g[0]);        	
       /* }*/
    }

    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineThroughAPerpendicularToB",P.getLabel(tpl),l.toGeoElement().getLabel(tpl));
    }

	// TODO Consider locusequability
}

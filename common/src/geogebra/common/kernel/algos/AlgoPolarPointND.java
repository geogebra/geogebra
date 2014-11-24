/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoPolarLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Michael
 * @version 
 */
public abstract class AlgoPolarPointND extends AlgoElement {
    
    protected GeoConicND c;  // input
    protected GeoLineND line;     // input
    protected GeoPointND polar;  // output
        
    /** Creates new AlgoPolarLine */
    public AlgoPolarPointND(Construction cons, String label, GeoConicND c, GeoLineND line) {
        super(cons);
        this.line = line;
        this.c = c;                
        polar = newGeoPoint(cons);
        
        setInputOutput(); // for AlgoElement
                
        compute();              
        polar.setLabel(label);
    }   
    
    /**
     * 
     * @param cons construction
     * @return new geo line
     */
    abstract protected GeoPointND newGeoPoint(Construction cons);
    
    @Override
	public Commands getClassName() {
		return Commands.Polar;
	}
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POLAR_DIAMETER;
    }
        
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) line;
        input[1] = c;
        
        super.setOutputLength(1);
        super.setOutput(0, (GeoElement) polar);
        setDependencies(); // done by AlgoElement
    }    
    
    // Made public for LocusEqu
    public GeoPointND getPoint() { return polar; }
    // Made public for LocusEqu
    public GeoConicND getConic() { return c; }
    public GeoLineND getLine() { return line; }
    
     
    @Override
	final public String toString(StringTemplate tpl) {
        return loc.getPlain("PoleOfLineARelativeToB", line.getLabel(tpl), c.getLabel(tpl));

    }


}

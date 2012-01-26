/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Denis M. Kishenko
 */
// This file was later modified by GeoGebra Inc.

package geogebra.web.awt;

import geogebra.common.awt.Point2D;
import geogebra.common.awt.Shape;
import geogebra.common.main.AbstractApplication;

public class AffineTransform implements geogebra.common.awt.AffineTransform {

	private geogebra.web.openjdk.awt.geom.AffineTransform at;
	
	public AffineTransform() {
		at = new geogebra.web.openjdk.awt.geom.AffineTransform();
	}
	
	public AffineTransform(geogebra.web.openjdk.awt.geom.AffineTransform at) {
		this.at = at;
	}
	
	public AffineTransform(double m00, double m10, double m01, double m11,
            double m02, double m12) {
		this();
		at.setTransform(m00, m10, m01, m11, m02, m12);
	}
	
	
    public void setTransform(geogebra.common.awt.AffineTransform a) {
	    at.setTransform(getGawtAffineTransform(a));    
    }

	
    public void setTransform(double m00, double m10, double m01, double m11,
            double m02, double m12) {
	    at.setTransform(m00, m10, m01, m11, m02, m12);	    
    }

	
    public void concatenate(geogebra.common.awt.AffineTransform a) {
	  at.concatenate(getGawtAffineTransform(a));
    }

	
    public double getScaleX() {
	    return at.getScaleX();
    }

	
    public double getScaleY() {
	    return at.getScaleY();
    }

	
    public double getShearX() {
	    return at.getShearX();
    }

	
    public double getShearY() {
	    return at.getShearY();
    }

    public double getTranslateX(){
    	return at.getTranslateX();
    }
	
    public double getTranslateY(){
    	return at.getTranslateY();
    }
    
    public Shape createTransformedShape(geogebra.common.awt.Shape shape) {
		geogebra.web.openjdk.awt.geom.Shape ret = null;
		ret = at.createTransformedShape(geogebra.web.awt.GenericShape.getGawtShape(shape));
		if(ret==null) AbstractApplication.debug("type of shape is: "+shape.getClass());
		return new geogebra.web.awt.GenericShape(ret);
    }

	/**
	 * @param at2
	 * @return
	 */
	public static geogebra.web.openjdk.awt.geom.AffineTransform getGawtAffineTransform(
            geogebra.common.awt.AffineTransform at2) {
	    if(!(at2 instanceof AffineTransform))
	    	return null;
	    return ((AffineTransform)at2).at;
    }

	/*
	public void transform(PathPoint p, PathPoint p2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }
    */

	public void transform(Point2D p, Point2D p2) {
		geogebra.web.openjdk.awt.geom.Point2D point = geogebra.web.awt.Point2D.getGawtPoint2D(p);
		geogebra.web.openjdk.awt.geom.Point2D point2 = geogebra.web.awt.Point2D.getGawtPoint2D(p2); 
		at.transform(point, point2);
		p2.setX(point2.getX());
		p2.setY(point2.getY());    
    }

	public void transform(double[] labelCoords, int i, double[] labelCoords2,
            int j, int k) {
		at.transform(labelCoords, i, labelCoords2, j, k);
	    
    }

	public geogebra.common.awt.AffineTransform createInverse() throws Exception{
	    // TODO Auto-generated method stub
	    return new geogebra.web.awt.AffineTransform(at.createInverse());
    }

	public void scale(double xscale, double d) {
	    at.scale(xscale, d);
	    
    }

	public void translate(double ax, double ay) {
	    at.translate(ax, ay);
	    
    }
	
	
	
}

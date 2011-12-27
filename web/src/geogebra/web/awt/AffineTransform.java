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

import geogebra.common.awt.Shape;

public class AffineTransform implements geogebra.common.awt.AffineTransform {

	private geogebra.web.kernel.gawt.AffineTransform at;
	
	public AffineTransform() {
		at = new geogebra.web.kernel.gawt.AffineTransform();
	}
	
	
	
	@Override
    public void setTransform(geogebra.common.awt.AffineTransform a) {
	    at.setTransform((geogebra.web.kernel.gawt.AffineTransform) a);    
    }

	@Override
    public void setTransform(double m00, double m10, double m01, double m11,
            double m02, double m12) {
	    at.setTransform(m00, m10, m01, m11, m02, m12);	    
    }

	@Override
    public void concatenate(geogebra.common.awt.AffineTransform a) {
	  at.concatenate((geogebra.web.kernel.gawt.AffineTransform) a);
    }

	@Override
    public double getScaleX() {
	    return at.getScaleX();
    }

	@Override
    public double getScaleY() {
	    return at.getScaleX();
    }

	@Override
    public double getShearX() {
	    return at.getShearX();
    }

	@Override
    public double getShearY() {
	    return at.getShearY();
    }

	@Override
    public Shape createTransformedShape(Object shape) {
	    return (Shape) at.createTransformedShape((geogebra.web.kernel.gawt.Shape) shape);
    }
	
	
	
}

/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.ar;

import org.geogebra.xr.XRMotionEvent;

abstract public class ARMotionEvent implements XRMotionEvent {

    public abstract float getX();

    public abstract float getY();

    public abstract float getX(int index);

    public abstract float getY(int index);

    public abstract int getPointerCount();

    public abstract int getAction();

    public abstract void setLocation(int x, int y);

    public Object getXREvent(){
        return null;
    }
}

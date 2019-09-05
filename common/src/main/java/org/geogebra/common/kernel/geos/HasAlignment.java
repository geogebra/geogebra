package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.geos.properties.AlignmentType;

public interface HasAlignment {

    /**
     *  sets the text alignment
     */
    public void setAlignment(AlignmentType alignment);

    /**
     *
     * @return the text alignment
     */
    public AlignmentType getAlignment();
}

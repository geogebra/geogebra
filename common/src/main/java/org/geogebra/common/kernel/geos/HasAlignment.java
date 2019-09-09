package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.geos.properties.TextAlignment;

public interface HasAlignment {

    /**
     *  sets the text alignment
     */
    public void setAlignment(TextAlignment alignment);

    /**
     *
     * @return the text alignment
     */
    public TextAlignment getAlignment();
}

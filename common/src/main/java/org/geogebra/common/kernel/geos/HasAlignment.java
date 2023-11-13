package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;

public interface HasAlignment {

    /**
     *  sets the text alignment
     * @param alignment horizontal alignment
     */
	void setAlignment(HorizontalAlignment alignment);

    /**
     *
     * @return the text alignment
     */
	HorizontalAlignment getAlignment();
}

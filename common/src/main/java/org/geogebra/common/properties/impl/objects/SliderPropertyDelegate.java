package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

class SliderPropertyDelegate {

    private AbstractGeoElementProperty property;

    SliderPropertyDelegate(AbstractGeoElementProperty property) {
        this.property = property;
    }

    boolean isSlider(GeoElement element) {
        if (property.isTextOrInput(element)) {
            return false;
        }
        if (element instanceof GeoList) {
            return property.isApplicableTo(element);
        }
        return hasSliderProperties(element);
    }

    /**
     * @param geo
     *         the GeoElement to check if it is a slider or not
     * @return if the passed geo if slider or not
     */
    private boolean hasSliderProperties(GeoElement geo) {
        return geo instanceof GeoNumeric
                && ((GeoNumeric) geo).getIntervalMinObject() != null
                && geo.isIndependent();
    }
}

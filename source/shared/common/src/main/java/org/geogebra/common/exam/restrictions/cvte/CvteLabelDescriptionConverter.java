package org.geogebra.common.exam.restrictions.cvte;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.App;
import org.geogebra.common.util.ToStringConverter;

/**
 * "Label description converter" for APPS-5926 (don't try to adapt to other use cases!).
 *
 * @apiNote The class name is not ideal, but we're sticking with the naming of the existing
 * {@link org.geogebra.common.kernel.geos.description.DefaultLabelDescriptionConverter DefaultLabelDescriptionConverter} /
 * {@link org.geogebra.common.kernel.geos.description.ProtectiveLabelDescriptionConverter ProtectiveLabelDescriptionConverter}.
 *
 * @implNote The idea with this (decorator) was to not having to touch/change the existing
 * code around {@link App#getLabelDescriptionConverter()}. When the CvTE exam is active, new
 * behaviour is added on top of what was there before (by wrapping it), using the same
 * structure as before, without having to touch the existing
 * {@link org.geogebra.common.kernel.geos.description.DefaultLabelDescriptionConverter DefaultLabelDescriptionConverter} /
 * {@link org.geogebra.common.kernel.geos.description.ProtectiveLabelDescriptionConverter ProtectiveLabelDescriptionConverter}.
 */
public final class CvteLabelDescriptionConverter implements ToStringConverter<GeoElement> {

    private final @Nullable ToStringConverter<GeoElement> wrappedConverter;

    public CvteLabelDescriptionConverter(@Nullable ToStringConverter<GeoElement> wrappedConverter) {
        this.wrappedConverter = wrappedConverter;
    }

    @Override
    public String convert(GeoElement element) {
        if (element == null) {
            return null;
        }
        if (!Cvte.isCalculatedEquationAllowed(element)) {
            return getRestrictedLabelDescription(element);
        }
        if (wrappedConverter != null) {
            return wrappedConverter.convert(element);
        }
        return null;
    }

    private String getRestrictedLabelDescription(GeoElement element) {
        String label;
        switch (element.getLabelMode()) {
            case LABEL_VALUE:
            case LABEL_NAME_VALUE:
                label = element.getDefinition(element.getLabelStringTemplate());
                break;
            default:
                label = wrappedConverter.convert(element);
        }
        return label.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : label;
    }
}

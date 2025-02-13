package org.geogebra.common.exam.restrictions.cvte;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.ToStringConverter;

/**
 * GeoElement "value converter" for APPS-5926 (don't try to adapt to other use cases!).
 *
 * @apiNote The class name is not ideal, but we're sticking with the naming of the existing
 * {@link org.geogebra.common.gui.view.algebra.GeoElementValueConverter GeoElementValueConverter} /
 * {@link org.geogebra.common.gui.view.algebra.ProtectiveGeoElementValueConverter ProtectiveGeoElementValueConverter}.
 *
 * @implNote The idea with this (decorator) was to not having to touch/change the existing
 * code around {@link App#getGeoElementValueConverter()}. When the CvTE exam is active, new behaviour
 * is added on top of what was there before (by wrapping it), using the same structure as before,
 * without having to touch the existing
 * {@link org.geogebra.common.gui.view.algebra.GeoElementValueConverter GeoElementValueConverter} /
 * {@link org.geogebra.common.gui.view.algebra.ProtectiveGeoElementValueConverter ProtectiveGeoElementValueConverter}.
 */
public final class CvteValueConverter implements ToStringConverter {

    private final @Nonnull ToStringConverter wrappedConverter;

    public CvteValueConverter(@Nonnull ToStringConverter wrappedConverter) {
        this.wrappedConverter = wrappedConverter;
    }

    @Override
    public @Nonnull String toOutputValueString(GeoElement element, StringTemplate template) {
        if (!Cvte.isCalculatedEquationAllowed(element)) {
            return element.getDefinition(template);
        }

        return wrappedConverter.toOutputValueString(element, template);
    }

    @Override
    public @Nonnull String toValueString(GeoElement element, StringTemplate template) {
        if (!Cvte.isCalculatedEquationAllowed(element)) {
            return element.getDefinition(template);
        }
        return wrappedConverter.toValueString(element, template);
    }

    @Override
    public @Nonnull String toLabelAndDescription(GeoElement element, StringTemplate template) {
        if (!Cvte.isCalculatedEquationAllowed(element)) {
            return ToStringConverter.getRestrictedLabelDescription(
                    element, template, wrappedConverter);
        }
        return wrappedConverter.toLabelAndDescription(element, template);
    }
}

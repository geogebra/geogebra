package org.geogebra.common.exam.restrictions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.properties.EnumeratedProperty;
import org.geogebra.common.properties.Property;

/**
 * TODO DOCUMENTATION
 */
public class PropertyRestriction {
    private final boolean shouldFreeze;
    private final ValueFilter valueFilter;

    public PropertyRestriction(boolean shouldFreeze, @Nullable ValueFilter valueFilter) {
        this.shouldFreeze = shouldFreeze;
        this.valueFilter = valueFilter;
    }

    public void applyTo(@Nonnull Property property) {
        if (shouldFreeze) {
            freeze(property);
        }
        if (property instanceof EnumeratedProperty<?> && valueFilter != null) {
            ((EnumeratedProperty<?>) property).addValueFilter(valueFilter);
        }
    }

    public void removeFrom(@Nonnull Property property) {
        if (shouldFreeze) {
            unfreeze(property);
        }
        if (property instanceof EnumeratedProperty<?> && valueFilter != null) {
            ((EnumeratedProperty<?>) property).removeValueFilter(valueFilter);
        }
    }

    /**
     * "Freeze" a property (i.e. prevent changing the value, or triggering the action)
     * at the start of the exam.
     * @param property A property.
     */
    protected void freeze(@Nonnull Property property) {
        property.setFrozen(true);
    }

    /**
     * "Unfreeze" a property at the end of the exam.
     * @param property A property.
     */
    protected void unfreeze(@Nonnull Property property) {
        property.setFrozen(false);
    }
}

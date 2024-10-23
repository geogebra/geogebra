package org.geogebra.common.exam.restrictions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.properties.EnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.ValueFilter;

/**
 * A set of restrictions that can be applied to a {@link Property} during exam mode.
 * The restrictions may include freezing a property or applying a {@link ValueFilter}
 * to an enumerated property.
 */
public class PropertyRestriction {
    private final boolean shouldFreeze;
    private final ValueFilter valueFilter;

    /**
     * @param shouldFreeze whether the property should be frozen
     * @param valueFilter an optional {@link ValueFilter} to be applied to an enumerated property
     */
    public PropertyRestriction(boolean shouldFreeze, @Nullable ValueFilter valueFilter) {
        this.shouldFreeze = shouldFreeze;
        this.valueFilter = valueFilter;
    }

    /**
     * Applies the restriction to the specified {@link Property}.
     * If {@code shouldFreeze} is {@code true}, the property is frozen.
     * If the property is an instance of {@link EnumeratedProperty} and {@code valueFilter}
     * is not {@code null}, the value filter is added to the property.
     *
     * @param property the {@link Property} to which the restriction will be applied
     */
    public void applyTo(@Nonnull Property property) {
        if (shouldFreeze) {
            freeze(property);
        }
        if (property instanceof EnumeratedProperty<?> && valueFilter != null) {
            ((EnumeratedProperty<?>) property).addValueFilter(valueFilter);
        }
    }

    /**
     * Removes the restriction from the specified {@link Property},
     * undoing the effects applied by {@link PropertyRestriction#applyTo}.
     *
     * @param property the {@link Property} from which the restriction will be removed
     */
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

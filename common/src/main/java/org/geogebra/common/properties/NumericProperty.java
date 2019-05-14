package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;

public interface NumericProperty  extends Property {
    int getMin();

    int getMax();

    Integer getValue(); // use boxed type to be compatible with ElementProperty

    void setValue(Integer value);
}

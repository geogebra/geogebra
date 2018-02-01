package org.geogebra.common.util;

import java.util.Locale;

public interface TimeFormatAdapter {

    String format(Locale locale, String pattern, long timeMs);

}

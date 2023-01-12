package org.geogebra.common.util;

public interface TimeFormatAdapter {

    String format(String localeStr, String pattern, long timeMs);

}

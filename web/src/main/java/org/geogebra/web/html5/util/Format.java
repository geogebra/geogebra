package org.geogebra.web.html5.util;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.NumberConstants;

public abstract class Format {

	protected static final NumberConstants localizedNumberConstants = LocaleInfo
			.getCurrentLocale().getNumberConstants();

	protected MyNumberFormat nf = MyNumberFormat.getDecimalFormat();

}

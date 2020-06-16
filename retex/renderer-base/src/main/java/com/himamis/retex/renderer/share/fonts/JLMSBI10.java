package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMSBI10 extends FontInfo {

	JLMSBI10(final String ttfPath) {
		super(0, ttfPath, 0, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 734, 454, 0, 46);

		setMetrics(36, 734, 454, 0, 4);

		setMetrics(34, 489, 454, 0, 34);

		setMetrics(35, 489, 454, 0, -12);

		setMetrics(37, 1330, 751, 0, 46);

		setMetrics(38, 1826, 751, 0, 46);

	}
}

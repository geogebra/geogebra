package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMSB10 extends FontInfo {

	JLMSB10(final String ttfPath) {
		super(0, ttfPath, 0, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 734, 455);

		setMetrics(36, 734, 455);

		setMetrics(34, 489, 455);

		setMetrics(35, 489, 455);

		setMetrics(37, 1330, 752);

		setMetrics(38, 1826, 752);

	}
}

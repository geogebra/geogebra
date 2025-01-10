package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMBI10 extends FontInfo {

	JLMBI10(final String ttfPath) {
		super(0, ttfPath, 0, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 634, 472, 0, 90);

		setMetrics(36, 634, 472, 0, -26);

		setMetrics(34, 507, 472, 0, 30);

		setMetrics(35, 507, 472, 0, -26);

		setMetrics(37, 1284, 752, 0, 22);

		setMetrics(38, 1704, 752, 0, 22);

	}
}

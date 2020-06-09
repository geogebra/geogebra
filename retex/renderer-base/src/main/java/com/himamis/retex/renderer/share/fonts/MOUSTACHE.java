package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class MOUSTACHE extends FontInfo {

	MOUSTACHE(final String ttfPath) {
		super(0, ttfPath, 431, 0, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(56, 889, 0, 900);

		setMetrics(57, 889, 0, 900);

		setMetrics(58, 889, 0, 900);

		setMetrics(59, 889, 0, 900);

		setMetrics(62, 889, 0, 300);
		setExtension(0, 0, 62, 0);

		setMetrics(64, 458, 0);
		setExtension(56, 0, 62, 59);

		setMetrics(65, 875, 0);
		setExtension(57, 0, 62, 58);

	}
}

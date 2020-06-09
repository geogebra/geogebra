package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMSI10 extends FontInfo {

	JLMSI10(final String ttfPath) {
		super(0, ttfPath, 0, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 662, 448, 0, 58);

		setMetrics(36, 662, 448);

		setMetrics(34, 441, 448, 0, 48);

		setMetrics(35, 441, 448, 0, -2);

		setMetrics(37, 1159, 751, 0, 50);

		setMetrics(38, 1533, 751, 0, 50);

	}
}

package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.Configuration;
import com.himamis.retex.renderer.share.FontInfo;

final class CMMI10_UNCHANGED extends FontInfo {

	CMMI10_UNCHANGED(final String ttfPath) {
		super(0, ttfPath, 431, 0, 1000, 196);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 622, 431, 0, 36);

		setMetrics(34, 466, 431);
		setKern(196, 83);

		setMetrics(35, 591, 694);
		setKern(196, 83);

		setMetrics(36, 828, 431, 0, 28);

		setMetrics(37, 517, 431, 194);
		setKern(196, 83);

		setMetrics(38, 363, 431, 97, 80);
		setKern(196, 83);

		setMetrics(39, 654, 431, 194);
		setKern(196, 83);

		setMetrics(40, 1000, 367, -133);

		setMetrics(41, 1000, 367, -133);

		setMetrics(42, 1000, 367, -133);

		setMetrics(43, 1000, 367, -133);

		setMetrics(44, 278, 464, -36);

		setMetrics(45, 278, 464, -36);

		setMetrics(46, 500, 465, -35);

		setMetrics(47, 500, 465, -35);

		setMetrics(58, 278, 106);

		setMetrics(59, 278, 106, 194);

		setMetrics(60, 778, 539, 39);
		setNextLarger(Configuration.getFonts().cmex10, 171);

		setMetrics(61, 500, 750, 250);
		setNextLarger(Configuration.getFonts().cmex10, 177);

		setMetrics(62, 778, 539, 39);
		setNextLarger(Configuration.getFonts().cmex10, 174);

		setMetrics(63, 500, 465, -35);

		setMetrics(64, 531, 694, 0, 56);
		setKern(196, 83);

		setMetrics(91, 389, 750);

		setMetrics(92, 389, 694, 194);

		setMetrics(93, 389, 694, 194);

		setMetrics(94, 1000, 358, -142);

		setMetrics(95, 1000, 358, -142);

		setMetrics(96, 417, 694);
		setKern(196, 111);

		setMetrics(101, 466, 431);
		setKern(196, 56);

		setMetrics(111, 485, 431);
		setKern(196, 56);

		setMetrics(174, 640, 431, 0, 4);
		setKern(196, 28);

		setMetrics(175, 566, 694, 194, 53);
		setKern(196, 83);

		setMetrics(176, 518, 431, 194, 56);

		setMetrics(177, 444, 694, 0, 38);
		setKern(59, -56, 58, -56, 196, 56);

		setMetrics(178, 406, 431);
		setKern(196, 56);

		setMetrics(179, 438, 694, 194, 74);
		setKern(196, 83);

		setMetrics(180, 497, 431, 194, 36);
		setKern(196, 56);

		setMetrics(181, 469, 694, 0, 28);
		setKern(196, 83);

		setMetrics(182, 354, 431);
		setKern(196, 56);

		setMetrics(183, 576, 431);

		setMetrics(184, 583, 694);

		setMetrics(185, 603, 431, 194);
		setKern(196, 28);

		setMetrics(186, 494, 431, 0, 64);
		setKern(59, -56, 58, -56, 196, 28);

		setMetrics(187, 438, 694, 194, 46);
		setKern(196, 111);

		setMetrics(188, 570, 431, 0, 36);

		setMetrics(189, 517, 431, 194);
		setKern(196, 83);

		setMetrics(190, 571, 431, 0, 36);
		setKern(59, -56, 58, -56);

		setMetrics(191, 437, 431, 0, 113);
		setKern(59, -56, 58, -56, 196, 28);

		setMetrics(192, 540, 431, 0, 36);
		setKern(196, 28);

		setMetrics(193, 596, 694, 194);
		setKern(196, 83);

		setMetrics(194, 626, 431, 194);
		setKern(196, 56);

		setMetrics(195, 651, 694, 194, 36);
		setKern(196, 111);

		setMetrics(123, 322, 431);
		setKern(196, 28);

		setMetrics(124, 384, 431, 194);
		setKern(196, 83);

		setMetrics(125, 636, 431, 194);
		setKern(196, 111);

		setMetrics(126, 500, 714, 0, 154);

		setMetrics(196, 278, 694, 0, 399);

	}
}

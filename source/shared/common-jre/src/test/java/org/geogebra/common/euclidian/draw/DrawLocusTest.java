/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GBufferedImageCommon;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class DrawLocusTest extends BaseAppTestSetup {

	@ParameterizedTest
	@ValueSource(strings = {"Segment((0,0),(10,1))", "Segment((0,0),(10,0))"})
	@Issue("APPS-6329")
	public void locusShouldAppearInGraphics(String line) {
		setupApp(SuiteSubApp.G3D);
		evaluate("A=Point(" + line + ")");
		evaluate("B=A-(0,0)");
		evaluate("loc=Locus(B,A)");
		DrawLocus locus = (DrawLocus) getApp().getActiveEuclidianView()
				.getDrawableFor(getKernel().lookupLabel("loc"));
		GGraphics2D graphics = mock(GGraphics2D.class);
		assertNotNull(locus);
		locus.draw(graphics);
		Mockito.verify(graphics).draw(any());
	}

	@ParameterizedTest
	@CsvSource(value = {"PenStroke((0,0),(10,1)):1", "PenStroke((1000,0),(1000,1)):0"},
			delimiterString = ":")
	public void strokesOnlyShownWhenOnScreen(String stroke, int images) {
		setupApp(SuiteSubApp.G3D);
		evaluate("stroke=" + stroke);
		DrawLocus locus = (DrawLocus) getApp().getActiveEuclidianView()
				.getDrawableFor(getKernel().lookupLabel("stroke"));
		GGraphics2D graphics = mock(GGraphics2D.class);
		assertNotNull(locus);
		locus.draw(graphics);
		// strokes should use bitmap buffer, no paths should be drawn
		Mockito.verify(graphics, never()).draw(any());
		// check that we've drawn the buffered stoke
		Mockito.verify(graphics, times(images))
				.drawImage(Mockito.<GBufferedImage>any(), anyInt(), anyInt());
	}

	@Test
	public void strokeNonEmpty() {
		setupApp(SuiteSubApp.G3D);
		GGraphics2D cachedGraphics = mock(GGraphics2D.class);
		GBufferedImageCommon mock = new GBufferedImageCommon(100, 100) {
			@Override
			public GGraphics2D createGraphics() {
				return cachedGraphics;
			}
		};
		AwtFactoryCommon.setImageFactory((width, height) -> mock);
		evaluate("stroke=PenStroke((1,1),(1,1))");
		DrawLocus locus = (DrawLocus) getApp().getActiveEuclidianView()
				.getDrawableFor(getKernel().lookupLabel("stroke"));
		GGraphics2D graphics = mock(GGraphics2D.class);
		assertNotNull(locus);
		locus.draw(graphics);
		// strokes should use bitmap buffer, no paths should be drawn
		Mockito.verify(graphics, never()).draw(any());
		Mockito.verify(cachedGraphics, times(1)).draw(
				ArgumentMatchers.argThat(p -> p instanceof GGeneralPath && isStroke(p)));
		// check that we've drawn the buffered stoke
		Mockito.verify(graphics, times(1))
				.drawImage(Mockito.<GBufferedImage>any(), anyInt(), anyInt());
	}

	@AfterEach
	public void cleanup() {
		AwtFactoryCommon.setImageFactory(GBufferedImageCommon::new);
	}

	private boolean isStroke(GShape p) {
		double[] seg1 = new double[2];
		double[] seg2 = new double[2];
		GPathIterator pathIterator = p.getPathIterator(null);
		assertEquals(GPathIterator.SEG_MOVETO, pathIterator.currentSegment(seg2));
		pathIterator.next();
		assertEquals(GPathIterator.SEG_LINETO, pathIterator.currentSegment(seg1));

		return seg1[0] != seg2[0];
	}
}

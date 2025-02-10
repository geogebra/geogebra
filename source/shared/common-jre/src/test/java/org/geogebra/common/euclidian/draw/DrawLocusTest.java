package org.geogebra.common.euclidian.draw;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

public class DrawLocusTest {

	private final AppCommon app = AppCommonFactory.create3D();

	@ParameterizedTest
	@ValueSource(strings = {"Segment((0,0),(10,1))", "Segment((0,0),(10,0))"})
	@Issue("APPS-6329")
	public void locusShouldAppearInGraphics(String line) {
		add("A=Point(" + line + ")");
		add("B=A-(0,0)");
		add("loc=Locus(B,A)");
		DrawLocus locus = (DrawLocus) app.getActiveEuclidianView()
				.getDrawableFor(app.getKernel().lookupLabel("loc"));
		GGraphics2D graphics = mock(GGraphics2D.class);
		assertNotNull(locus);
		locus.draw(graphics);
		Mockito.verify(graphics).draw(any());
	}

	@ParameterizedTest
	@CsvSource(value = {"PenStroke((0,0),(10,1)):1", "PenStroke((1000,0),(1000,1)):0"},
			delimiterString = ":")
	public void strokesOnlyShownWhenOnScreen(String stroke, int images) {
		add("stroke=" + stroke);
		DrawLocus locus = (DrawLocus) app.getActiveEuclidianView()
				.getDrawableFor(app.getKernel().lookupLabel("stroke"));
		GGraphics2D graphics = mock(GGraphics2D.class);
		assertNotNull(locus);
		locus.draw(graphics);
		// strokes should use bitmap buffer, no paths should be drawn
		Mockito.verify(graphics, never()).draw(any());
		// check that we've drawn the buffered stoke
		Mockito.verify(graphics, times(images))
				.drawImage(Mockito.<GBufferedImage>any(), anyInt(), anyInt());
	}

	private void add(String command) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(command, false);
	}
}

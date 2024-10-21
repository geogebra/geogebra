package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class DrawImageTest extends BaseEuclidianControllerTest {

	@Test
	public void translateShouldNotFixateCorners() {
		add("ZoomIn(0,0,800,600)");
		GeoImage img = createImage();
		GeoPoint pt = add("(100,100)");
		img.setCorner(pt, 0);
		img.setLabel("pic1");
		GeoImage translated = add("Translate(pic1, (1,0))");
		DrawImage drawImage = (DrawImage) getDrawable(img);
		DrawImage drawTranslated = (DrawImage) getDrawable(translated);
		assertEquals(1.0, drawImage.getTransform().getScaleX(), 0.1);
		assertEquals(1.0, drawTranslated.getTransform().getScaleX(), 0.1);
		add("ZoomIn(0,0,1600,1200)");
		// only one corner fixed, scale stays the same
		assertEquals(1.0, drawImage.getTransform().getScaleX(), 0.1);
		assertEquals(1.0, drawTranslated.getTransform().getScaleX(), 0.1);
	}

	@Test
	public void translateShouldChangeCenter() {
		add("ZoomIn(0,0,800,600)");
		GeoImage img = createImage();
		GeoPoint pt = add("(100,100)");
		img.setCorner(pt, 0);
		img.setCentered(true);
		img.setLabel("pic1");
		GeoImage translated = add("Translate(pic1, (100,0))");
		DrawImage drawImage = (DrawImage) getDrawable(img);
		DrawImage drawTranslated = (DrawImage) getDrawable(translated);
		// width 50px, centered at 100 -> x from 75 to 125
		assertEquals(75, drawImage.getTransform().getTranslateX(), 0.1);
		// translated by 100px
		assertEquals(175, drawTranslated.getTransform().getTranslateX(), 0.1);
		add("ZoomIn(0,0,1600,1200)");
		// width 50px, centered at 50 -> x from 25 to 75
		assertEquals(25, drawImage.getTransform().getTranslateX(), 0.1);
		// translated by 50px
		assertEquals(75, drawTranslated.getTransform().getTranslateX(), 0.1);
	}

	@Test
	public void translateShouldTransformCorners() {
		add("ZoomIn(0,0,800,600)");
		GeoImage img = createImage();
		GeoPoint pt = (GeoPoint) add("(100,100)");
		GeoPoint pt2 = (GeoPoint) add("(200,100)");
		img.setCorner(pt, 0);
		img.setCorner(pt2, 1);
		img.setLabel("pic1");
		GeoImage translated = add("Translate(pic1, (1,0))");
		DrawImage drawImage = (DrawImage) getDrawable(img);
		DrawImage drawTranslated = (DrawImage) getDrawable(translated);
		assertEquals(2.0, drawImage.getTransform().getScaleX(), 0.1);
		assertEquals(2.0, drawTranslated.getTransform().getScaleX(), 0.1);
		add("ZoomIn(0,0,1600,1200)");
		assertEquals(1.0, drawImage.getTransform().getScaleX(), 0.1);
		assertEquals(1.0, drawTranslated.getTransform().getScaleX(), 0.1);
	}
}

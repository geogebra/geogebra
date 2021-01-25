package org.geogebra.web.full.main;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListController;
import org.geogebra.web.full.gui.pagecontrolpanel.PagePreviewCard;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.geogebra.web.test.ViewWMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for Undo with multiple slides
 * 
 * @author Zbynek
 *
 */
@RunWith(GgbMockitoTestRunner.class)
public class NotesUndoTest {
	private static AppWFull app;

	/**
	 * Undo / redo with a single slide.
	 */
	@Test
	public void undoSingle() {
		addObject("x");
		addObject("-x");
		shouldHaveUndoPoints(2);

		app.getGgbApi().undo();
		shouldHaveUndoPoints(1);
		app.getAppletFrame().initPageControlPanel(app);
		slideShouldHaveObjects(0, 1);

		app.getGgbApi().redo();
		shouldHaveUndoPoints(2);
		slideShouldHaveObjects(0, 2);
	}

	/**
	 * Create two pages with some objects, reorder, undo, redo
	 */
	@Test
	public void undoReorder() {
		addObject("x");
		addObject("-x");
		shouldHaveUndoPoints(2);

		app.getAppletFrame().initPageControlPanel(app);
		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		shouldHaveUndoPoints(3);
		addObject("x");
		objectsPerSlideShouldBe(2, 1);

		app.getPageController().reorder(0, 1);
		app.getAppletFrame().getPageControlPanel().update();
		objectsPerSlideShouldBe(1, 2);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(2, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 2);
	}

	/**
	 * Duplicate a slide with one object, add object to each copy, undo all,
	 * redo all
	 */
	@Test
	public void undoDuplicate() {
		addObject("x");
		shouldHaveUndoPoints(1);

		app.getAppletFrame().initPageControlPanel(app);
		duplicate(0);
		shouldHaveSlides(2);
		objectsPerSlideShouldBe(1, 1);

		selectPage(0);
		addObject("2x");
		objectsPerSlideShouldBe(2, 1);

		selectPage(1);
		addObject("2x");
		objectsPerSlideShouldBe(2, 2);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(2, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(2, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(2, 2);
	}

	private void duplicate(int page) {
		app.getPageController().refreshSlide(page);
		PagePreviewCard card = ((PageListController) app.getPageController()).getCard(page);
		String content = ViewWMock.toJson(card.getFile());
		app.getAppletFrame().getPageControlPanel().pastePage(card, content);
	}

	/**
	 * Make duplicate of a duplicate, add objects to all slides, undo & redo
	 */
	@Test
	public void undoDuplicateChain() {
		addObject("x");
		shouldHaveUndoPoints(1);

		app.getAppletFrame().initPageControlPanel(app);
		duplicate(0);
		objectsPerSlideShouldBe(1, 1);

		duplicate(1);
		objectsPerSlideShouldBe(1, 1, 1);

		selectPage(0);
		addObject("2x");
		objectsPerSlideShouldBe(2, 1, 1);

		selectPage(1);
		addObject("2x");
		objectsPerSlideShouldBe(2, 2, 1);

		selectPage(2);
		addObject("2x");
		objectsPerSlideShouldBe(2, 2, 2);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(2, 2, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(2, 1, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(2, 1, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(2, 2, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(2, 2, 2);
	}
	/**
	 * Undo and redo removing the last page.
	 */
	@Test
	public void undoRedo() {
		addObject("x");
		addObject("-x");
		shouldHaveUndoPoints(2);

		app.getAppletFrame().initPageControlPanel(app);
		shouldHaveSlides(1);
		slideShouldHaveObjects(0, 2);

		app.getAppletFrame().getPageControlPanel().removePage(0);

		shouldHaveSlides(1);
		slideShouldHaveObjects(0, 0);

		app.getGgbApi().undo();
		shouldHaveSlides(1);
		slideShouldHaveObjects(0, 2);

		app.getGgbApi().redo();
		shouldHaveSlides(1);
		slideShouldHaveObjects(0, 0);
	}

	@Test
	public void undoCut() {
		app.getAppletFrame().initPageControlPanel(app);
		addPenStroke();
		objectsPerSlideShouldBe(1);
		app.getAppletFrame().getPageControlPanel().removePage(0);
		objectsPerSlideShouldBe(0);
		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1);
	}

	private void addPenStroke() {
		app.setMode(EuclidianConstants.MODE_PEN);
		app.getEuclidianView1().getEuclidianController().wrapMousePressed(evt(50,50));
		app.getEuclidianView1().getEuclidianController().wrapMouseReleased(evt(150,150));
	}

	private AbstractEvent evt(int x, int y) {
		return new PointerEvent(x,y, PointerEventType.MOUSE, new ZeroOffset());
	}

	/**
	 * Make sure asserts don't kill the tests
	 */
	@Before
	public void init() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
		app = AppMocker
				.mockApplet(new AppletParameters("notes"));
	}

	/**
	 * Create objects on slide 1, 2, 1, 2, undo all, redo all.
	 */
	@Test
	public void pageSwitch() {
		addObject("x");

		app.getAppletFrame().initPageControlPanel(app);
		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		addObject("-x");

		shouldHaveUndoPoints(3);
		shouldHaveSlides(2);
		slideShouldHaveObjects(0, 1);
		slideShouldHaveObjects(1, 1);
		selectPage(0);
		addObject("2x");
		selectPage(1);
		addObject("-2x");
		app.getPageController().saveSelected();
		shouldHaveUndoPoints(5);
		objectsPerSlideShouldBe(2, 2);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(2, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 0);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(0);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 0);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 1);
	}

	private static void selectPage(int i) {
		app.getPageController().saveSelected();
		app.getPageController().clickPage(i, true);
	}

	/**
	 * Create four pages with object each, undo all, red all
	 */
	@Test
	public void switchFourSlides() {
		addObject("x");

		app.getAppletFrame().initPageControlPanel(app);
		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		addObject("2x");

		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		addObject("3x");

		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		addObject("4x");

		shouldHaveUndoPoints(7);
		objectsPerSlideShouldBe(1, 1, 1, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1, 1, 0);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1, 0);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 0);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 0);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 1, 0);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 1, 1);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 1, 1, 0);

		app.getGgbApi().redo();
		objectsPerSlideShouldBe(1, 1, 1, 1);
	}

	/**
	 * Create three slides, then one object on each, undo all, redo all.
	 */
	@Test
	public void singleObjectPerSlide() {
		app.getAppletFrame().initPageControlPanel(app);
		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		app.getAppletFrame().getPageControlPanel().loadNewPage(false);
		selectPage(0);
		addObject("x");
		selectPage(1);
		addObject("2x");
		selectPage(2);
		addObject("3x");

		shouldHaveUndoPoints(5);
		objectsPerSlideShouldBe(1, 1, 1);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 1, 0);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(1, 0, 0);

		app.getGgbApi().undo();
		objectsPerSlideShouldBe(0, 0, 0);
		app.getGgbApi().undo();
		objectsPerSlideShouldBe(0, 0);
		app.getGgbApi().undo();
		objectsPerSlideShouldBe(0);
	}

	private static void addObject(String string) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				true);

	}

	private static void objectsPerSlideShouldBe(int... counts) {
		shouldHaveSlides(counts.length);
		for (int i = 0; i < counts.length; i++) {
			slideShouldHaveObjects(i, counts[i]);
		}
	}

	private static void slideShouldHaveObjects(int slide, int expectedCount) {
		app.getPageController().refreshSlide(slide);
		String xml = app.getPageController().getSlide(slide)
				.get("geogebra.xml");
		int start = 0;
		int count = 0;
		while (xml.indexOf("<element", start) > 0) {
			count++;
			start = xml.indexOf("<element", start) + 1;
		}
		Assert.assertEquals(slide + ":" + expectedCount, slide + ":" + count);
	}

	private static void shouldHaveSlides(int expected) {
		Assert.assertEquals(expected, app.getPageController().getSlideCount());

	}

	private static void shouldHaveUndoPoints(int expected) {
		Assert.assertEquals(expected, app.getKernel().getConstruction()
				.getUndoManager().getHistorySize());
		
	}
}

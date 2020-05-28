package org.geogebra.web.full.main;

import org.geogebra.web.full.gui.pagecontrolpanel.PageListController;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.test.AppMocker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

/**
 * Tests for Undo with multiple slides
 * 
 * @author Zbynek
 *
 */
@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class NotesUndoTest {
	private static AppWFull app;

	/**
	 * Undo / redo with a single slide.
	 */
	@Test
	public void undoSingle() {
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes")
						.attr("vendor", "mebis"));
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
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes"));
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
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes"));
		addObject("x");
		shouldHaveUndoPoints(1);

		app.getAppletFrame().initPageControlPanel(app);
		app.getAppletFrame().getPageControlPanel().duplicatePage(
				((PageListController) app.getPageController()).getCard(0));
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

	/**
	 * Make duplicate of a duplicate, add objects to all slides, undo & redo
	 */
	@Test
	public void undoDuplicateChain() {
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes"));
		addObject("x");
		shouldHaveUndoPoints(1);

		app.getAppletFrame().initPageControlPanel(app);
		app.getAppletFrame().getPageControlPanel().duplicatePage(
				((PageListController) app.getPageController()).getCard(0));
		objectsPerSlideShouldBe(1, 1);

		app.getAppletFrame().getPageControlPanel().duplicatePage(
				((PageListController) app.getPageController()).getCard(1));
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
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes"));
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


	/**
	 * Make sure asserts don't kill the tests
	 */
	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	/**
	 * Create objects on slide 1, 2, 1, 2, undo all, redo all.
	 */
	@Test
	public void pageSwitch() {
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes"));
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
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes"));
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
		app = AppMocker
				.mockApplet(new TestArticleElement("canary", "notes"));


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

package org.geogebra.web.web.gui.pagecontrolpanel;

import java.util.ArrayList;

import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.PageListControllerInterface;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;

/**
 * controller for page actions, such as delete or add slide
 * 
 * @author csilla
 *
 */
public class PageControlPanelController implements PageListControllerInterface {

	/**
	 * application {@link AppW}
	 */
	protected AppW app;
	/**
	 * list of slides (pages)
	 */
	protected ArrayList<GgbFile> slides = null;
	private int activeSlide = -1;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public PageControlPanelController(AppW app) {
		this.app = app;
	}

	/**
	 * loads the slide with index i from the list
	 * 
	 * @param i
	 *            index of the slide to load
	 */
	public void loadSlide(int i) {
		if (slides == null) {
			return;
		}
		slides.set(activeSlide >= 0 ? activeSlide : 0,
				app.getGgbApi().createArchiveContent(false));
		activeSlide = i;

		try {
			if (slides.get(i).isEmpty()) {
				app.fileNew();
			} else {
				app.resetPerspectiveParam();
				app.loadGgbFile(slides.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds a new slide to the list
	 * 
	 * @return index of the added slide
	 */
	public int addSlide() {
		if (slides == null) {
			slides = new ArrayList<>();
			activeSlide = 0;
		}
		slides.add(new GgbFile());
		return slides.size() - 1;
	}

	/**
	 * removes the slide with given index from the list
	 * 
	 * @param index
	 *            of the slide to be removed
	 */
	public void removeSlide(int index) {
		if (slides == null || index >= slides.size()) {
			return;
		}
		if (activeSlide >= index) {
			activeSlide--;
		}
		slides.remove(index);
	}

	/**
	 * gets the number of slides in the list
	 * 
	 * @return number of slides
	 */
	public int getSlidesAmount() {
		return slides.size();
	}

	@Override
	public void resetPageControl() {
		if (!app.has(Feature.MOW_MULTI_PAGE)) {
			return;
		}
		slides = null;
		((GeoGebraFrameBoth) app.getAppletFrame()).getPageControlPanel()
				.reset();
	}
}

package org.geogebra.web.web.gui.pagecontrolpanel;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.PageListControllerInterface;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.pagecontrolpanel.DragController.Cards;
import org.geogebra.web.web.main.AppWFull;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/**
 * controller for page actions, such as delete or add slide
 * 
 * @author csilla
 *
 */
public class PageListController implements PageListControllerInterface,
		MouseDownHandler, MouseMoveHandler, MouseUpHandler, TouchStartHandler,
		TouchMoveHandler, TouchEndHandler, Cards {
	/**
	 * application {@link AppW}
	 */
	protected AppW app;
	/**
	 * list of slides (pages)
	 */
	ArrayList<PagePreviewCard> slides;
	private PagePreviewCard selectedCard;

	private DragController dragCtrl;
	private CardListInterface listener;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param listener
	 *            the card listener.
	 */
	public PageListController(AppW app, CardListInterface listener) {
		this.app = app;
		slides = new ArrayList<>();
		this.listener = listener;
		dragCtrl = new DragController(this, app);
	}

	/**
	 * @return list of slides; never null
	 */
	public ArrayList<PagePreviewCard> getCards() {
		return slides != null ? slides : new ArrayList<PagePreviewCard>();
	}

	public GgbFile getSlide(int index) {
		if(slides == null){
			return null;
		}
		if(selectedCard == slides.get(index)){
			return app.getGgbApi().createArchiveContent(true);
		}
		return slides.get(index).getFile();
	}

	/**
	 * loads the slide with index i from the list
	 * 
	 * @param curSelCard
	 *            currently selected card
	 * 
	 * @param i
	 *            index of the slide to load
	 * @param newPage
	 *            true if slide is new slide
	 */
	public void loadSlide(PagePreviewCard curSelCard, int i, boolean newPage) {
		if (slides == null) {
			return;
		}
		// save file status of currently selected card
		savePreviewCard(curSelCard);
		try {
			if (newPage) {
				// new file
				((AppWFull) app).loadEmptySlide();
				app.getKernel().getConstruction().getUndoManager()
						.storeAction(EventType.ADD_SLIDE, new String[0]);
			} else {
				// load last status of file
				app.resetPerspectiveParam();
				app.loadGgbFile(slides.get(i).getFile(), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save current file to selected card
	 * 
	 * @param card
	 *            selected card
	 */
	public void savePreviewCard(PagePreviewCard card) {
		if (card != null) {
			card.setFile(app.getGgbApi().createArchiveContent(true));
		}
	}
	
	/**
	 * @param dest
	 *            slide to load
	 */
	public void changeSlide(PagePreviewCard dest) {
		try {
			app.resetPerspectiveParam();
			app.loadGgbFile(dest.getFile(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Duplicates slide
	 * 
	 * @param sourceCard
	 *            to duplicate.
	 * @return the new, duplicated card.
	 */
	public PagePreviewCard duplicateSlideStoreUndo(PagePreviewCard sourceCard) {
		PagePreviewCard ret = duplicateSlide(sourceCard);
		app.getKernel().getConstruction().getUndoManager().storeAction(
				EventType.DUPLICATE_SLIDE, sourceCard.getPageIndex() + "");
		return ret;
	}

	/**
	 * Duplicates slide
	 * 
	 * @param sourceCard
	 *            to duplicate.
	 * @return the new, duplicated card.
	 */
	private PagePreviewCard duplicateSlide(PagePreviewCard sourceCard) {

		savePreviewCard(selectedCard);
		PagePreviewCard dup = PagePreviewCard.duplicate(sourceCard);
		int dupIdx = dup.getPageIndex();
		slides.add(dupIdx, dup);
		setCardSelected(dup);
		changeSlide(dup);

		if (dupIdx != slides.size()) {
			updatePageIndexes(dupIdx);
		}
		return dup;
	}

	/**
	 * adds a new slide to the list
	 * 
	 * @return index of the added slide
	 */
	private PagePreviewCard addSlide(int index, GgbFile ggbFile) {
		if (slides == null) {
			slides = new ArrayList<>();
		}
		PagePreviewCard previewCard = new PagePreviewCard(
				app, index, ggbFile);
		slides.add(index, previewCard);
		return previewCard;
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
		slides.remove(index);
	}

	/**
	 * gets the number of slides in the list
	 * 
	 * @return number of slides
	 */
	public int getSlideCount() {
		return slides.size();
	}

	@Override
	public void resetPageControl() {
		if (!app.has(Feature.MOW_MULTI_PAGE)) {
			return;
		}
		// clear preview card list
		slides = new ArrayList<>();
		// clear gui
		((GeoGebraFrameBoth) app.getAppletFrame()).getPageControlPanel()
				.reset();
	}
	
	private void updatePageIndexes(int masterIdx) {
		for (int i = masterIdx; i < slides.size(); i++) {
			slides.get(i).setPageIndex(i);
		}
	}

	public String getStructureJSON() {
		try {
			JSONObject book = new JSONObject();
			JSONObject chapter = new JSONObject();
			JSONArray pages = new JSONArray();
			if (slides != null) {
				for (int i = 0; i < slides.size(); i++) {
					JSONArray elements = new JSONArray();
					elements.put(new JSONObject().put("id",
							GgbFile.SLIDE_PREFIX + i));
					pages.put(new JSONObject().put("elements", elements));
				}
			}
			chapter.put("pages", pages);
			book.put("chapters", new JSONArray().put(chapter));
			return book.toString();
		} catch (JSONException e) {
			Log.warn("can't save slides:" + e.getMessage());
		}
		return "{}";
	}

	public boolean loadSlides(GgbFile archive) {
		if (!archive.containsKey(GgbFile.STRUCTURE_JSON)) {
			return false;
		}
		String structure = archive.remove(GgbFile.STRUCTURE_JSON);
		slides.clear();
		Log.debug(structure);
		try {
			JSONObject response = new JSONObject(new JSONTokener(structure));
			JSONArray pages = response.getJSONArray("chapters").getJSONObject(0)
					.getJSONArray("pages");
			for (int i = 0; i < pages.length(); i++) {
				slides.add(new PagePreviewCard(app, i, filter(archive,
						pages.getJSONObject(i).getJSONArray("elements")
								.getJSONObject(0).getString("id"))));
			}
			app.loadGgbFile(slides.get(0).getFile(), true);
			/// TODO this breaks MVC
			((GeoGebraFrameBoth) app.getAppletFrame()).getPageControlPanel()
					.update();
			setCardSelected(slides.get(0));
		} catch (Exception e) {
			Log.debug(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Sets the selected page visible and highlights the preview card
	 * 
	 * @param previewCard
	 *            selected preview card
	 */
	protected void setCardSelected(PagePreviewCard previewCard) {
		if (selectedCard != null) {
			// deselect old selected card
			selectedCard.removeStyleName("selected");
		}
		// select new card
		previewCard.addStyleName("selected");
		selectedCard = previewCard;
	}

	private static GgbFile filter(GgbFile archive, String prefix) {
		GgbFile ret = new GgbFile();
		for (Entry<String, String> e : archive.entrySet()) {
			if (e.getKey().startsWith(prefix + "/")
					|| e.getKey().startsWith(GgbFile.SHARED_PREFIX)) {
				ret.put(e.getKey().substring(prefix.length() + 1),
						e.getValue());
			}
		}
		return ret;
	}

	/**
	 * @param srcIdx
	 *            source index
	 * @param destIdx
	 *            destination index
	 */
	public void reorder(int srcIdx, int destIdx) {
		doReorder(srcIdx, destIdx);
		app.getKernel().getConstruction().getUndoManager()
				.storeAction(EventType.MOVE_SLIDE, srcIdx + "", destIdx + "");
	}


	private void doReorder(int srcIdx, int destIdx) {
		PagePreviewCard src = slides.get(srcIdx);
		slides.remove(srcIdx);
		slides.add(destIdx, src);
		updatePageIndexes(Math.min(srcIdx, destIdx));
	}

	/**
	 * Add style to a given card, removes from all other ones.
	 * 
	 * @param pageIndex
	 *            the card index to add style to.
	 * @param name
	 *            the style name.
	 */
	public void styleCard(int pageIndex, String name) {
		for (PagePreviewCard card : slides) {
			if (card.getPageIndex() == pageIndex) {
				card.addStyleName(name);
			} else {
				card.removeStyleName(name);
			}
		}
	}

	/**
	 * Updates the preview image of the active preview card
	 */
	public void updatePreviewImage() {
		if (selectedCard != null) {
			selectedCard.updatePreviewImage();
		}
	}

	/**
	 * load existing page
	 * 
	 * @param index
	 *            index of page to load
	 * @param newPage
	 *            true if slide is new page
	 */
	public void loadPage(int index, boolean newPage) {
		loadSlide(selectedCard, index, newPage);
		setCardSelected(getCards().get(index));
	}


	@Override
	public void clickPage(int pageIdx) {
		loadPage(pageIdx, false);
	}

	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		event.stopPropagation();
		CancelEventTimer.dragCanStart();
	}

	public void onMouseMove(MouseMoveEvent event) {
		dragCtrl.move(event.getClientX(), event.getClientY());
	}

	public void onMouseUp(MouseUpEvent event) {
		dragCtrl.stopDrag(event.getClientX(), event.getClientY());
	}

	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		event.stopPropagation();
		CancelEventTimer.dragCanStart();
	}

	public void onTouchMove(TouchMoveEvent event) {
		Touch t = event.getTargetTouches().get(0);
		dragCtrl.move(t.getClientX(), t.getClientY());
	}

	public void onTouchEnd(TouchEndEvent event) {
		Touch t = event.getTargetTouches().get(0);
		if (t == null) {
			t = event.getChangedTouches().get(0);
		}
		dragCtrl.stopDrag(t.getClientX(), t.getClientY());
	}

	@Override
	public PagePreviewCard cardAt(int index) {
		return slides.get(index);
	}
	
	@Override
	public int getCardCount() {
		return getSlideCount();
	}

	@Override
	public CardListInterface getListener() {
		return listener;
	}


	/**
	 * @param selected
	 *            whether to select
	 * @param index
	 *            position to insert
	 * @param file
	 *            GeoGebra file (single slide)
	 */
	public void addNewPreviewCard(boolean selected, int index, GgbFile file) {
		final PagePreviewCard card = addSlide(index, file);
		if (selected) {
			setCardSelected(card);
		}
	}

	@Override
	public String getSlideID() {
		return selectedCard.getPageIndex() + "";
	}

	public void executeAction(EventType action, String[] args) {
		if (action == EventType.ADD_SLIDE) {
			int idx = args.length > 0 ? Integer.parseInt(args[0])
					: getSlideCount();
			GgbFile file = new GgbFile();
			if (args.length > 1) {
				file.put("geogebra.xml", args[1]);
			}
			String perspXML = app.getGgbApi().getPerspectiveXML();
			addNewPreviewCard(false, idx, file);
			loadSlide(null, idx, false);
			setCardSelected(getCards().get(idx));
			updatePreviewImage();
			app.getGgbApi().setPerspective(perspXML);
		} else if (action == EventType.REMOVE_SLIDE) {

			removeSlide(getSlideCount() - 1);

		} else if (action == EventType.DUPLICATE_SLIDE) {
			duplicateSlide(slides.get(Integer.parseInt(args[0])));
		} else if (action == EventType.MOVE_SLIDE) {
			doReorder(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		}
		((AppWapplet) app).getAppletFrame().getPageControlPanel().update();

	}

}

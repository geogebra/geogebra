package org.geogebra.web.web.gui.pagecontrolpanel;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.PageListControllerInterface;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;

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
		TouchMoveHandler, TouchEndHandler {
	/**
	 * application {@link AppW}
	 */
	protected AppW app;
	/**
	 * list of slides (pages)
	 */
	ArrayList<PagePreviewCard> slides;
	private PagePreviewCard selectedCard;

	private DragInfo drag = new DragInfo();
	private CardListInterface listener;
	private int startSpaceIdx;

	private class DragInfo {
		PagePreviewCard card = null;
		PagePreviewCard lastTarget = null;

		DragInfo() {
			reset();
		}

		private void reset() {
			card = null;
			lastTarget = null;
		}

		void setIndex(int idx) {
			if (idx >= 0 && idx < getSlideCount()) {
				card = slides.get(idx);
				card.addStyleName("dragged");
			} else {
				reset();
			}
		}

		int index() {
			return isValid() ? card.getPageIndex() : -1;
		}

		int dropToIndex() {
			return lastTarget != null ? lastTarget.getPageIndex() : -1;
		}

		void setPosition(int x, int y) {
			card.setDragPosition(x, y);
		}

		public void cancel() {
			CancelEventTimer.resetDrag();
			if (isValid()) {
				card.removeStyleName("dragged");
			}
			reset();
		}

		public boolean getDirection(int y) {
			return card.getDragDirection(y);
		}

		boolean isAnimated() {
			return app.has(Feature.MOW_DRAG_AND_DROP_ANIMATION);
		}

		boolean isValid() {
			return card != null;
		}
	}

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
				app.fileNew();
			} else {
				// load last status of file
				app.resetPerspectiveParam();
				app.loadGgbFile(slides.get(i).getFile());
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
			app.loadGgbFile(dest.getFile());
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
	public PagePreviewCard duplicateSlide(PagePreviewCard sourceCard) {
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
	public PagePreviewCard addSlide() {
		if (slides == null) {
			slides = new ArrayList<>();
		}
		PagePreviewCard previewCard = new PagePreviewCard(
				app, slides.size(), new GgbFile());
		slides.add(previewCard);
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
			app.loadGgbFile(slides.get(0).getFile());
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
		PagePreviewCard src = slides.get(srcIdx);
		slides.remove(srcIdx);
		slides.add(destIdx, src);
		updatePageIndexes(Math.min(srcIdx, destIdx));
	}

	private int cardIndexAt(int x, int y) {
		int result = -1;
		for (PagePreviewCard card: slides) {
			if ((!drag.isValid() || card != drag.card)
					&& card.isHit(x, y)) {
				result = card.getPageIndex();
			}
		}
		return result;
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
	protected void loadPage(int index, boolean newPage) {
		loadSlide(selectedCard, index, newPage);
		setCardSelected(getCards().get(index));
	}

	private void clearSpaces() {
		clearSpacesBut(-1);
	}

	private void clearSpacesBut(int index) {
		for (PagePreviewCard card: slides) {
			if (index != card.getPageIndex()) {
				removeSpaceStyles(card);
			}
		}
	}

	private static void removeSpaceStyles(PagePreviewCard card) {
		card.removeStyleName("spaceBefore");
		card.removeStyleName("spaceAfter");
		card.removeStyleName("spaceBeforeAnimated");
		card.removeStyleName("spaceAfterAnimated");
	}
	private void startDrag(int x, int y) {
		drag.setIndex(cardIndexAt(x, y));

		if (drag.isValid() && drag.isAnimated()) {
			if (drag.index() < getSlideCount() - 1) {
				startSpaceIdx = drag.index() + 1;
				slides.get(startSpaceIdx).addStyleName("spaceBeforeAnimated");
			}
		}
	}

	private int doDrag(int y) {
		if (!drag.isValid()) {
			return -1;
		}

		boolean down = drag.getDirection(y);

		drag.setPosition(0, y);

		int idx = cardIndexAt(
				drag.card.getAbsoluteLeft() + drag.card.getOffsetWidth() / 2,
				down ? drag.card.getBottom() : drag.card.getAbsoluteTop());
		if (idx == -1) {
			return -1;
		}

		PagePreviewCard target = slides.get(idx);
		if (target == null) {
			return -1;
		}

		int targetIdx = target.getPageIndex();

		boolean bellowMiddle = target
				.isBellowMiddle(drag.card.getAbsoluteTop());

		if (drag.isAnimated()) {
			int treshold = target.getOffsetHeight() / 5;
			Log.debug("[DND] target is " + targetIdx);

			if (down) {
				dragDown(target, treshold);
			} else {
				dragUp(target, treshold);
			}
		}
		drag.lastTarget = target;
		return bellowMiddle ? targetIdx + 1 : targetIdx;
	}

	private void dragDown(PagePreviewCard target, int treshold) {
		int beforeIdx = target.getPageIndex() - 1;
		if (beforeIdx > 0) {
			for (int i = 0; i < beforeIdx; i++) {
				removeSpaceStyles(slides.get(i));
			}
		}

		Log.debug("[DND] dragDown");
		boolean hit = target.getAbsoluteTop()
				- drag.card.getBottom() < treshold;
		if (hit) {
			addSpaceAfter(target);
		} else {
			addSpaceBefore(target);
		}
	}

	private void dragUp(PagePreviewCard target, int treshold) {
		int afterIdx = target.getPageIndex() + 1;
		PagePreviewCard afterCard = afterIdx < getSlideCount()
				? slides.get(afterIdx)
				: null;
		int diff = drag.card.getAbsoluteTop() - target.getAbsoluteTop();
		if (diff < treshold) {
			Log.debug("[DND] hit");
			addSpaceBefore(target);
			if (afterCard != null) {
				afterCard.removeStyleName("spaceBeforeAnimated");
			}
		}
	}

	private static void addSpaceBefore(PagePreviewCard target) {
		target.removeStyleName("spaceAfterAnimated");
		target.addStyleName("spaceBeforeAnimated");
	}

	private static void addSpaceAfter(PagePreviewCard target) {
		target.removeStyleName("spaceBeforeAnimated");
		target.addStyleName("spaceAfterAnimated");
	}

	private void loadPageAt(int x, int y) {
		int idx = cardIndexAt(x, y);
		if (idx != -1) {
			loadPage(idx, false);
		}
	}

	private void drag(int x, int y) {
		if (CancelEventTimer.isDragStarted()) {
			startDrag(x, y);
		} else if (CancelEventTimer.isDragging()) {
			int targetIdx = doDrag(y);
			if (targetIdx != -1 && !drag.isAnimated()) {
				listener.insertDivider(targetIdx);
			}
		}
	}

	private void stopDrag(int x, int y) {
		if (CancelEventTimer.isDragging()) {
			if (dropCard()) {
				listener.update();
			}
		} else {
			loadPageAt(x, y);
		}
		cancelDrag();
	}

	private boolean dropCard() {
		if (!drag.isValid()) {
			return false;
		}

		int srcIdx = drag.index();
		int destIdx = drag.dropToIndex();

		if (srcIdx != -1 && destIdx != -1) {
			Log.debug("drag: " + srcIdx + " drop to " + destIdx);

			reorder(srcIdx, destIdx);
			return true;
		}
		return false;
	}

	private void cancelDrag() {
		CancelEventTimer.resetDrag();
		drag.cancel();
		clearSpaces();
		listener.removeDivider();
	}

	public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		event.stopPropagation();
		CancelEventTimer.dragCanStart();
	}

	public void onMouseMove(MouseMoveEvent event) {
		drag(event.getClientX(), event.getClientY());
	}

	public void onMouseUp(MouseUpEvent event) {
		stopDrag(event.getClientX(), event.getClientY());
	}

	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		event.stopPropagation();
		CancelEventTimer.dragCanStart();
	}

	public void onTouchMove(TouchMoveEvent event) {
		Touch t = event.getTargetTouches().get(0);
		drag(t.getClientX(), t.getClientY());
	}

	public void onTouchEnd(TouchEndEvent event) {
		Touch t = event.getTargetTouches().get(0);
		if (t == null) {
			t = event.getChangedTouches().get(0);
		}
		stopDrag(t.getClientX(), t.getClientY());
	}
}

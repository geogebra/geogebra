package org.geogebra.web.full.gui.pagecontrolpanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.io.ObjectLabelHandler;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.undo.AppState;
import org.geogebra.common.main.undo.UndoCommand;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.pagecontrolpanel.DragController.Cards;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.export.Canvas2Pdf;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.PageContent;
import org.geogebra.web.html5.main.PageListControllerInterface;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.PDFEncoderW;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.client.Touch;
import org.gwtproject.event.dom.client.HumanInputEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseOutHandler;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchEndHandler;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchMoveHandler;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.event.dom.client.TouchStartHandler;

import elemental2.dom.DomGlobal;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * controller for page actions, such as delete or add slide
 */
public class PageListController implements PageListControllerInterface,
		MouseDownHandler, MouseMoveHandler, MouseUpHandler, TouchStartHandler, MouseOutHandler,
		TouchMoveHandler, TouchEndHandler, Cards, EventListener, EventRenderable {
	/**
	 * application {@link AppW}
	 */
	protected AppWFull app;
	/**
	 * list of slides (pages)
	 */
	final ArrayList<PagePreviewCard> slides;
	private PagePreviewCard selectedCard;

	private final DragController dragCtrl;
	private final CardListInterface listener;
	private Material activeMaterial = null;
	private final UndoManager undoManager;
	private boolean selectedCardChangedAfterLoad;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param listener
	 *            the card listener.
	 */
	public PageListController(AppWFull app, CardListInterface listener) {
		this.app = app;
		slides = new ArrayList<>();
		this.listener = listener;
		dragCtrl = new DragController(this, app);
		undoManager = app.getUndoManager();
		undoManager.addActionExecutor(this);
		app.getEventDispatcher().addEventListener(this);
		if (app.getLoginOperation() != null) {
			app.getLoginOperation().getView().add(this);
		}
	}

	/**
	 * @return list of slides; never null
	 */
	@Override
	public ArrayList<PagePreviewCard> getCards() {
		return slides;
	}

	/**
	 * @param i
	 *            index
	 * @return preview card at given index
	 */
	public PagePreviewCard getCard(int i) {
		return slides.get(i);
	}

	@Override
	public void refreshSlide(int index) {
		refreshSlide(slides.get(index));
	}

	protected void refreshSlide(PagePreviewCard card) {
		if (selectedCard == card) {
			app.getGgbApi().createArchiveContent(true,
					card.getFile());
		}
	}

	@Override
	public GgbFile getSlide(int index) {
		return slides.get(index).getFile();
	}

	/**
	 * loads the slide with index i from the list
	 * 
	 * @param i
	 *            index of the slide to load
	 */
	private void loadSlide(int i) {
		if (slides.get(i).getFile().isEmpty()) {
			app.loadEmptySlide();
		} else {
			try {
				// load last status of file
				saveMaterialProperties();
				app.resetPerspectiveParam();
				// in case page was added through API, thumbnail may be outdated
				app.registerOpenFileListener(() -> {
					DomGlobal.requestAnimationFrame(ignore -> slides.get(i).updatePreviewImage());
					return true;
				});
				app.loadGgbFile(slides.get(i).getFile(), true);
				restoreMaterialProperties();
				// to clear ruler and protractor selection
				app.setMode(app.getMode());
			} catch (Exception e) {
				Log.debug(e);
			}
		}
	}

	private String getTubeId() {
		return activeMaterial != null ? activeMaterial.getSharingKeySafe() : null;
	}

	private void saveMaterialProperties() {
		activeMaterial = app.getActiveMaterial();
	}

	private void restoreMaterialProperties() {
		if (activeMaterial == null) {
			return;
		}
		app.setTubeId(getTubeId());
		app.getKernel().getConstruction().setTitle(activeMaterial.getTitle());
	}

	/**
	 * Save all slides as PDF.
	 */
	@Override
	public String exportPDF() {
		// export scale
		double scale = 1;

		EuclidianViewW ev = app.getEuclidianView1();

		// assume height/width same for all slides
		int width = (int) Math.floor(ev.getExportWidth() * scale);
		int height = (int) Math.floor(ev.getExportHeight() * scale);

		int currentIndex = selectedCard.getPageIndex();
		savePreviewCard(selectedCard);

		Canvas2Pdf.PdfContext ctx = PDFEncoderW.getContext(width, height);

		if (ctx == null) {
			Log.debug("canvas2PDF not found");
			return "";
		}

		app.setExporting(ExportType.PDF_HTML5, scale);

		GGraphics2DW g4copy = new GGraphics2DW(ctx);

		int n = slides.size();

		for (int i = 0; i < n; i++) {

			try {
				loadSlide(i);

				ev = app.getEuclidianView1();

				if (i > 0) {
					ctx.addPage();
				}
				ev.exportPaintPre(g4copy, scale, false);
				ev.drawObjects(g4copy);

			} catch (Exception e) {
				Log.error(
						"problem exporting slide " + i + " " + e.getMessage());
			}
		}

		app.setExporting(ExportType.NONE, 1);
		loadSlide(currentIndex);

		return ctx.getPDFbase64();
	}

	/**
	 * Save current file to selected card
	 * 
	 * @param card
	 *            selected card
	 */
	public void savePreviewCard(PagePreviewCard card) {
		if (card != null) {
			app.getGgbApi().createArchiveContent(true, card.getFile());
			EmbedManager embedManager = app.getEmbedManager();
			if (embedManager != null) {
				embedManager.persist();
			}
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
			Log.debug(e);
		}
	}

	/**
	 * Duplicates slide
	 * 
	 * @param sourceCard
	 *            to duplicate.
	 * @return the new, duplicated card.
	 */
	public PagePreviewCard pasteSlideStoreUndo(PagePreviewCard sourceCard, String targetID,
			String json) {
		PagePreviewCard ret = pasteSlide(sourceCard, targetID, json);
		undoManager.storeAction(
				ActionType.PASTE_PAGE, new String[]{sourceCard.getPageIndex() + "",
				ret.getFile().getID(), json}, ActionType.REMOVE_PAGE,
				(sourceCard.getPageIndex() + 1) + "");
		return ret;
	}

	/**
	 * Duplicates slide
	 * 
	 * @param sourceCard
	 *            to duplicate.
	 * @return the new, duplicated card.
	 */
	private PagePreviewCard pasteSlide(PagePreviewCard sourceCard,
			@Nonnull String targetID, String json) {
		savePreviewCard(selectedCard);
		PagePreviewCard dup = PagePreviewCard.pasteAfter(sourceCard, targetID, json);
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
	 * @param index
	 *            insertion position
	 * @param ggbFile
	 *            file content
	 * @return index of the added slide
	 */
	private PagePreviewCard addSlide(int index, GgbFile ggbFile) {
		PagePreviewCard previewCard = new PagePreviewCard(app, index, ggbFile);
		slides.add(index, previewCard);
		resetCardPositions();
		return previewCard;
	}

	/**
	 * removes the slide with given index from the list
	 * 
	 * @param index
	 *            of the slide to be removed
	 */
	public void removeSlide(int index) {
		if (index >= slides.size()) {
			return;
		}
		slides.remove(index);
	}

	/**
	 * gets the number of slides in the list
	 * 
	 * @return number of slides
	 */
	@Override
	public int getSlideCount() {
		return slides.size();
	}

	@Override
	public void resetPageControl() {
		// clear preview card list
		slides.clear();
		// clear gui
		listener.reset();
	}
	
	private void updatePageIndexes(int masterIdx) {
		for (int i = masterIdx; i < slides.size(); i++) {
			slides.get(i).setPageIndex(i);
		}
	}

	@Override
	public String getStructureJSON() {
		try {
			JSONObject book = new JSONObject();
			JSONObject chapter = new JSONObject();
			JSONArray pages = new JSONArray();

			for (int i = 0; i < slides.size(); i++) {
				JSONArray elements = new JSONArray();
				JSONObject page = new JSONObject();
				page.put("id", GgbFile.SLIDE_PREFIX + i);
				String title = getCard(i).getCardTitle();
				if (!StringUtil.empty(title)) {
					page.put("title", title);
				}

				elements.put(page);
				pages.put(new JSONObject().put("elements", elements));
			}

			chapter.put("pages", pages);
			book.put("chapters", new JSONArray().put(chapter));
			return book.toString();
		} catch (JSONException e) {
			Log.warn("can't save slides:" + e.getMessage());
		}
		return "{}";
	}

	@Override
	public boolean loadSlides(GgbFile archive) {
		if (!archive.containsKey(GgbFile.STRUCTURE_JSON)) {
			return false;
		}
		String structure = archive.remove(GgbFile.STRUCTURE_JSON).string;
		slides.clear();

		try {
			JSONObject response = new JSONObject(new JSONTokener(structure));
			JSONArray pages = response.getJSONArray("chapters").getJSONObject(0)
					.getJSONArray("pages");

			for (int i = 0; i < pages.length(); i++) {
				slides.add(createCardFromArchive(archive, pages, i));
			}
			// select card first to make sure we have the correct slide ID
			setCardSelected(0);
			app.loadGgbFile(slides.get(0).getFile(), false);
			listener.update();
		} catch (Exception e) {
			Log.debug(e);
			Log.debug(e);
		}
		return true;
	}

	private PagePreviewCard createCardFromArchive(GgbFile archive, JSONArray pages, int cardIndex)
			throws JSONException {
		JSONObject page = pages.getJSONObject(cardIndex).getJSONArray("elements")
				.getJSONObject(0);
		PagePreviewCard card = new PagePreviewCard(app, cardIndex, filter(archive,
				page.getString("id")));

		if (page.has("title")) {
			card.setCardTitle(page.getString("title"));
		}

		return card;
	}

	/**
	 * Sets the selected page visible and highlights the preview card
	 * 
	 * @param previewCard
	 *            selected preview card
	 */
	protected void setCardSelected(PagePreviewCard previewCard) {
		if (selectedCard == previewCard) {
			return;
		}
		if (selectedCard != null) {
			// deselect old selected card
			selectedCard.removeStyleName("selected");
		}
		// select new card
		previewCard.addStyleName("selected");
		selectedCard = previewCard;
		selectedCardChangedAfterLoad = true;
	}

	private static GgbFile filter(GgbFile archive, String prefix) {
		GgbFile ret = new GgbFile(prefix);
		for (Entry<String, ArchiveEntry> e : archive.entrySet()) {
			if (e.getKey().startsWith(prefix + "/")
					|| e.getKey().startsWith(GgbFile.SHARED_PREFIX)) {
				String fileName = e.getKey().substring(prefix.length() + 1);
				ArchiveEntry duplicate = e.getValue().copy(fileName);
				ret.put(fileName, duplicate);
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
	@Override
	public void reorder(int srcIdx, int destIdx) {
		doReorder(srcIdx, destIdx);
		undoManager
				.storeAction(ActionType.MOVE_PAGE, new String[]{srcIdx + "", destIdx + ""},
						ActionType.MOVE_PAGE, destIdx + "", srcIdx + "");
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
	@Override
	public void updatePreviewImage() {
		if (selectedCard != null) {
			selectedCard.updatePreviewImage();
		}
	}

	@Override
	public void updatePreviewImage(String slideID) {
		int index = indexOfId(slideID, -1);
		if (index > -1) {
			PagePreviewCard card = slides.get(index);
			if (card != null) {
				card.updatePreviewImage();
			}
		}
	}

	@Override
	public void loadPage(int index) {
		dispatchSelected(index);
		savePreviewCard(selectedCard);
		loadSlide(index);
		setCardSelected(index);
	}

	/**
	 * @param index
	 *            card index
	 */
	public void setCardSelected(int index) {
		setCardSelected(getCard(index));
	}

	/**
	 * load existing page
	 *
	 * @param index
	 *            index of page to load
	 */
	public void loadNewPageStoreUndo(int index) {
		this.loadNewPage(index);
		app.getUndoManager().storeAction(ActionType.ADD_PAGE, new String[]{index + "",
						getSlide(index).getID()}, ActionType.REMOVE_PAGE,
				index + "");
	}

	private void loadNewPage(int index) {
		saveMaterialProperties();
		savePreviewCard(selectedCard);
		app.loadEmptySlide();
		setCardSelected(index);
		updatePreviewImage();
		restoreMaterialProperties();
	}

	@Override
	public void clickPage(int pageIdx, boolean select) {
		if (slides.get(pageIdx) == selectedCard && !selectedCardChangedAfterLoad) {
			return; // no change
		}
		dispatchSelected(pageIdx);
		loadSlide(pageIdx);
		selectedCardChangedAfterLoad = false;
		if (select) {
			setCardSelected(pageIdx);
		}
	}

	private void dispatchSelected(int pageIdx) {
		// first notify listeners about deselecting all objects on current page
		app.getSelectionManager().clearSelectedGeos(false);
		app.dispatchEvent(new Event(EventType.SELECT_PAGE,
				null, slides.get(pageIdx).getID()));
	}

	@Override
	public void selectSlide(String pageId) {
		saveSelected();
		findById(pageId).ifPresent(card -> {
			loadSlide(card.getPageIndex());
			setCardSelected(card);
		});
	}

	@Override
	public void handlePageAction(String eventType, String pageId, Object appState) {
		refreshSlide(selectedCard);
		JsPropertyMap<?> args = appState == null ? JsPropertyMap.of() : Js.asPropertyMap(appState);
		switch (eventType) {
		case "addPage":
			PagePreviewCard card = addNewPreviewCard(false, getSlideCount(),
					new GgbFile(pageId));
			card.clearBackground();
			break;

		case "removePage":
			findById(pageId).ifPresent(removeCard -> {
				int index = slides.indexOf(removeCard);
				removeSlide(index);
				if (isLoaded(removeCard.getID())) {
					int toLoad = Math.min(index, getSlideCount() - 1);
					loadPage(toLoad);
				}
			});
			break;

		case "movePage":
			findById(pageId).ifPresent(removeCard -> {
				int index = slides.indexOf(removeCard);
				Any to = args.getAsAny("to");
				doReorder(index, Math.max(0, to.asInt()));
			});
			break;

		case "renamePage":
			findById(pageId).ifPresent(renameCard -> renameCard.setCardTitle(
					args.get("title").toString()));
			break;
		case "pastePage":
			GgbFile file = new GgbFile(pageId);
			file.put("geogebra.xml", (String) args.get("xml"));
			int to = args.getAsAny("to").asInt();
			card = addNewPreviewCard(false, to, file);
			card.clearBackground();
			break;

		case "clearPage":
			loadNewPage(0);
			break;

		default:
			Log.error("No event type sent");
			break;
		}
		listener.update();
	}

	@Override
	public PageContent getPageContent(String pageId) {
		if (isLoaded(pageId)) {
			String thumb = ((EuclidianViewWInterface) app.getActiveEuclidianView())
					.getExportImageDataUrl(0.5, false, false);
			return PageContent.of(app.getXML(), app.getGgbApi().getAllObjectNames(), thumb,
					selectedCard.getCardTitle(), selectedCard.getPageIndex());
		}

		PagePreviewCard target = findById(pageId).orElse(null);
		if (target == null) {
			Log.warn("Page not found: " + pageId);
			return null;
		}
		ArchiveEntry archiveEntry = target.getFile().get("geogebra.xml");
		String xml = archiveEntry == null ? "" : archiveEntry.string;
		ArchiveEntry thumb = target.getFile().get("geogebra_thumbnail.xml");
		String thumbUrl = thumb == null ? "" : thumb.export();
		return PageContent.of(xml, ObjectLabelHandler.findObjectNames(xml), thumbUrl,
				target.getCardTitle(), target.getPageIndex());
	}

	private Optional<PagePreviewCard> findById(String pageId) {
		return slides.stream().filter(card ->
				card.getID().equals(pageId)).findFirst();
	}

	@Override
	public void setPageContent(String pageId, PageContent content) {
		if (StringUtil.empty(content.xml)) {
			slides.get(content.order).replaceId(pageId);
			return;
		}
		PagePreviewCard target = findById(pageId).orElse(null);
		if (target == null) {
			target = addSlide(slides.size(), new GgbFile(pageId));
		}
		target.getFile().put("geogebra.xml", content.xml);
		target.getFile().put("geogebra_thumbnail.png", content.thumbnail);
		if (isLoaded(pageId)) {
			app.setXML(content.xml, true);
		}
		target.updatePreviewFromFile();
		target.setCardTitle(content.title);
		listener.update();
	}

	@Override
	public String getActivePage() {
		return selectedCard.getID();
	}

	@Override
	public String[] getPages() {
		return slides.stream().map(PagePreviewCard::getID).toArray(String[]::new);
	}

	private boolean isLoaded(String index) {
		return selectedCard.getID().equals(index);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (NavigatorUtil.isMobile()) {
			return;
		}
		event.preventDefault();
		event.stopPropagation();
		dragCtrl.start(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (NavigatorUtil.isMobile()) {
			return;
		}
		dragCtrl.move(event.getClientY(), false);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (NavigatorUtil.isMobile()) {
			return;
		}
		dragCtrl.stop(isRightClick(event), event.getNativeEvent());
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		dragCtrl.cancelClick();
		dragCtrl.stop(isRightClick(event), event.getNativeEvent());
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		Touch t = event.getTargetTouches().get(0);
		dragCtrl.start(t.getClientX(), t.getClientY());
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		Touch t = event.getTargetTouches().get(0);
		if (dragCtrl.move(t.getClientY(), true)) {
			event.preventDefault();
			event.stopPropagation();
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		dragCtrl.stop(isRightClick(event), event.getNativeEvent());
	}

	// Cards Interface
	@Override
	public PagePreviewCard cardAt(int index) {
		return slides.get(index);
	}
	
	@Override
	public int getCardCount() {
		return getSlideCount();
	}

	@Override
	public void selectCard(PagePreviewCard card) {
		if (selectedCard != null) {
			savePreviewCard(selectedCard);
		}
		setCardSelected(card);
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
	 * @return the card
	 */
	public PagePreviewCard addNewPreviewCard(boolean selected, int index, GgbFile file) {
		final PagePreviewCard card = addSlide(index, file);
		if (selected) {
			setCardSelected(card);
		}
		return card;
	}

	@Override
	public String getSlideID() {
		return selectedCard.getFile().getID() + "";
	}

	@Override
	public boolean executeAction(ActionType action,  String... args) {
		switch (action) {
		case ADD_PAGE:
			executeAddSlideAction(args);
			break;
		case REMOVE_PAGE:
			if (getSlideCount() > 1) {
				int index = args.length > 0 ? Integer.parseInt(args[0])
						: getSlideCount() - 1;
				removeSlide(index);
				int toLoad = index > 0 ? index - 1 : index;
				loadSlide(toLoad);
				setCardSelected(toLoad);
			}
			break;
		case CLEAR_PAGE:
			loadNewPage(indexOfId(args[0], 0));
			break;
		case PASTE_PAGE:
			pasteSlide(slides.get(Integer.parseInt(args[0])), args[1], args[2]);
			break;
		case MOVE_PAGE:
			doReorder(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			break;
		case RENAME_PAGE:
			renameCard(Integer.parseInt(args[0]), args[1]);
			break;
		default:
			return false;
		}
		listener.update();
		listener.open();
		return true;
	}

	private void executeAddSlideAction(String... args) {
		int idx = args.length > 0 ? Integer.parseInt(args[0])
				: getSlideCount();
		GgbFile file = args.length < 2 ? new GgbFile()
				: new GgbFile(args[1]);
		if (args.length > 2) {
			UndoCommand cmd = undoManager.getCheckpoint(args[2]);
			AppState state = undoManager.extractFromCommand(cmd);
			if (state != null) {
				file.put("geogebra.xml", state.getXml());
			}
		}

		if (idx >= 0) {
			addNewPreviewCard(false, idx, file);
		} else {
			slides.get(0).setFile(file);
		}
		idx = Math.max(idx, 0);
		if (file.isEmpty()) {
			// new file
			app.loadEmptySlide();

		} else {
			String perspXML = app.getGgbApi().getPerspectiveXML();
			// load last status of file
			changeSlide(slides.get(idx));
			app.getGgbApi().setPerspective(perspXML);
		}

		setCardSelected(idx);
		updatePreviewImage();
	}

	private void renameCard(int pageIndex, String title) {
		cardAt(pageIndex).setCardTitle(title);
	}

	private int indexOfId(String slideID, int fallback) {
		if (slideID == null) {
			return fallback;
		}
		for (int i = 0; i < slides.size(); i++) {
			if (slideID.equals(slides.get(i).getFile().getID())) {
				return i;
			}
		}
		return fallback;
	}

	@Override
	public void setActiveSlide(String slideID) {
		selectCard(slides.get(indexOfId(slideID, 0)));
	}

	@Override
	public void sendEvent(Event evt) {
		if (evt.getType() == EventType.UNDO
				|| evt.getType() == EventType.REDO) {
			savePreviewCard(selectedCard);
		}
	}

	@Override
	public void saveSelected() {
		this.savePreviewCard(selectedCard);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LogOutEvent) {
			BrowserStorage.LOCAL.removeItem(BrowserStorage.COPY_SLIDE);
		}
	}

	/**
	 * Renaming a slide
	 * @param card to rename.
	 * @param title the new title.
	 */
	public void rename(PagePreviewCard card, String title) {
		storeRenameAction(card, title);
		card.setCardTitle(title);
	}

	@Override
	public void clickPage(String slideID) {
		int pageIdx = indexOfId(slideID, -1);
		if (pageIdx > -1) {
			clickPage(pageIdx, true);
		}
		listener.open();
	}

	private void storeRenameAction(PagePreviewCard card, String oldTitle) {
		undoManager.storeAction(ActionType.RENAME_PAGE, new String[]{"" + card.getPageIndex(),
						card.getCardTitle()},
				ActionType.RENAME_PAGE, "" + card.getPageIndex(), oldTitle);
		Event evt = new Event(EventType.RENAME_PAGE, null,
				card.getID());
		HashMap<String, Object> args = new HashMap<>();
		args.put("title", oldTitle);
		app.getEventDispatcher().dispatchEvent(evt.setJsonArgument(args));
	}

	@Override
	public void resetCardPositions() {
		for (PagePreviewCard card : getCards()) {
			card.resetTop();
		}
		listener.updateContentPanelHeight();
	}

	public static String nextID() {
		return "p" + Math.floor(Math.random() * 1E6);
	}

	/**
	 * Remove page and store undo info
	 * @param index page index
	 */
	public void removePage(int index) {
		String id = getSlide(index).getID();
		if (index == 0 && getSlideCount() == 1) {
			app.getUndoManager().storeActionWithSlideId(id, ActionType.CLEAR_PAGE, new String[]{id},
					ActionType.ADD_PAGE, new String[]{"-1", id, id});
			loadNewPage(0);
		} else {
			removeSlide(index);
			app.getUndoManager()
					.storeActionWithSlideId(id, ActionType.REMOVE_PAGE, new String[]{index + ""},
							ActionType.ADD_PAGE, new String[]{index + "", id, id});
			listener.updateIndexes(index);
			// load new slide
			if (index == getSlideCount()) {
				// last slide was deleted
				loadPage(index - 1);
			} else {
				// otherwise
				loadPage(index);
			}
		}
	}

	private boolean isRightClick(HumanInputEvent<?> event) {
		return event.isControlKeyDown()
				|| event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT;
	}
}

package org.geogebra.web.web.gui.pagecontrolpanel;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.CancelEventTimer;

class DragController {
	/**
	 * Class to handle drag and drop cards 
	 * @author laszlo 
	 */
	private final Cards cards;
	private App app;
	PagePreviewCard dragged = null;
	PagePreviewCard lastTarget = null;
	private int startSpaceIdx;
	
	interface Cards {
		ArrayList<PagePreviewCard> getCards();
		int getCardCount();
		CardListInterface getListener();
		PagePreviewCard cardAt(int index); 
		void reorder(int srcIdx, int destIdx);
		void clickPage(int pageIdx);
	}

	DragController(Cards slides, App app) {
		this.cards = slides;
		this.app = app;
		reset();
	}

	private void reset() {
		dragged = null;
		lastTarget = null;
	}

	void setIndex(int idx) {
		if (idx >= 0 && idx < cards.getCardCount()) {
			dragged = cards.cardAt(idx);
			dragged.addStyleName("dragged");
		} else {
			reset();
		}
	}

	int index() {
		return isValid() ? dragged.getPageIndex() : -1;
	}

	int dropToIndex() {
		return lastTarget != null ? lastTarget.getPageIndex() : -1;
	}

	void setPosition(int x, int y) {
		dragged.setDragPosition(x, y);
	}

	public void cancel() {
		CancelEventTimer.resetDrag();
		if (isValid()) {
			dragged.removeStyleName("dragged");
		}
		reset();
	}

	public boolean getDirection(int y) {
		return dragged.getDragDirection(y);
	}

	boolean isAnimated() {
		return app.has(Feature.MOW_DRAG_AND_DROP_ANIMATION);
	}

	boolean isValid() {
		return dragged != null;
	}
	
	boolean drop() {
		if (!isValid()) {
			return false;
		}

		int srcIdx = index();
		int destIdx = dropToIndex();

		if (srcIdx != -1 && destIdx != -1) {
			Log.debug("drag: " + srcIdx + " drop to " + destIdx);

			cards.reorder(srcIdx, destIdx);
			return true;
		}
		return false;
	}
	
	void startDrag(int x, int y) {
		setIndex(cardIndexAt(x, y));
		if (isValid() && isAnimated()) {
			if (index() < cards.getCardCount() - 1) {
				startSpaceIdx = index() + 1;
				cards.cardAt(startSpaceIdx).addStyleName("spaceBeforeAnimated");
			}
		}
	}
	
	void drag(int x, int y) {
		if (CancelEventTimer.isDragStarted()) {
			startDrag(x, y);
		} else if (CancelEventTimer.isDragging()) {
			int targetIdx = doDrag(y);
			if (targetIdx != -1 && !isAnimated()) {
				cards.getListener().insertDivider(targetIdx);
			}
		}
	}


	private int doDrag(int y) {
		if (!isValid()) {
			return -1;
		}

		boolean down = getDirection(y);

		setPosition(0, y);

		int idx = cardIndexAt(
				dragged.getAbsoluteLeft() + dragged.getOffsetWidth() / 2,
				down ? dragged.getBottom() : dragged.getAbsoluteTop());
		if (idx == -1) {
			return -1;
		}

		PagePreviewCard target = cards.cardAt(idx);
		if (target == null) {
			return -1;
		}

		int targetIdx = target.getPageIndex();

		boolean bellowMiddle = target
				.isBellowMiddle(dragged.getAbsoluteTop());

		if (isAnimated()) {
			int treshold = target.getOffsetHeight() / 5;
			Log.debug("[DND] target is " + targetIdx);

			if (down) {
				dragDown(target, treshold);
			} else {
				dragUp(target, treshold);
			}
		}
		lastTarget = target;
		return bellowMiddle ? targetIdx + 1 : targetIdx;
	}

	private void dragDown(PagePreviewCard target, int treshold) {
		int beforeIdx = target.getPageIndex() - 1;
		if (beforeIdx > 0) {
			for (int i = 0; i < beforeIdx; i++) {
//				removeSpaceStyles(cards.cardAt(i));
			}
		}

		Log.debug("[DND] dragDown");
		boolean hit = target.getAbsoluteTop()
				- dragged.getBottom() < treshold;
		if (hit) {
			addSpaceAfter(target);
		} else {
			addSpaceBefore(target);
		}
	}

	private void dragUp(PagePreviewCard target, int treshold) {
		int afterIdx = target.getPageIndex() + 1;
		PagePreviewCard afterCard = afterIdx < cards.getCardCount()
				? cards.cardAt(afterIdx)
				: null;
		int diff = dragged.getAbsoluteTop() - target.getAbsoluteTop();
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

	void stopDrag(int x, int y) {
		if (CancelEventTimer.isDragging()) {
			if (drop()) {
				cards.getListener().update();
			}
		} else {
			int idx = cardIndexAt(x, y);
			if (idx != -1) {
				cards.clickPage(idx);
			}
		}
		
		cancelDrag();
	}

	
	void cancelDrag() {
		CancelEventTimer.resetDrag();
		cancel();
		clearSpaces();
		cards.getListener().removeDivider();
	}
	
	private int cardIndexAt(int x, int y) {
		int result = -1;
		for (PagePreviewCard card: cards.getCards()) {
			if ((!isValid() || card != dragged)
					&& card.isHit(x, y)) {
				result = card.getPageIndex();
			}
		}
		return result;
	}


	private void clearSpaces() {
		clearSpacesBut(-1);
	}

	private void clearSpacesBut(int index) {
		for (PagePreviewCard card: cards.getCards()) {
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

}
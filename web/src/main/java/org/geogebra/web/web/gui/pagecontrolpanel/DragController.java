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
	private DragCard dragged;
	private App app;
	private int startSpaceIdx;
	
	interface Cards {
		ArrayList<PagePreviewCard> getCards();
		int getCardCount();
		CardListInterface getListener();
		PagePreviewCard cardAt(int index); 
		void reorder(int srcIdx, int destIdx);
		void clickPage(int pageIdx);
	}

	private class DragCard {
		PagePreviewCard card = null;
		PagePreviewCard target = null;
		PagePreviewCard lastTarget = null;
		int lastY = 0;
		int lastTop = 0;
		DragCard() {
			reset();
		}
		private void reset() {
			card = null;
			target = null;
			lastTarget = null;
		}

		void setIndex(int idx) {
			if (idx >= 0 && idx < cards.getCardCount()) {
				card = cards.cardAt(idx);
				card.addStyleName("dragged");
			} else {
				reset();
			}
		}

		int index() {
			return isValid() ? card.getPageIndex() : -1;
		}

		int destIndex() {
			return lastTarget != null ? lastTarget.getPageIndex() : -1;
		}

		void setPosition(int x, int y) {
			card.setDragPosition(x, y);
			lastY = y;
			target = findTarget();
			if (target != null && target != lastTarget) { 
				onTargetChange();
			}
		}

		private void onTargetChange() {
			Log.debug("[DND] target change: " + target.getPageIndex());
			lastTop = target.getAbsoluteTop();
			
		}
		private PagePreviewCard findTarget() {
	
			int idx = cardIndexAt(card.getMiddleX(), 
					isAnimated() ? card.getBottom() : card.getMiddleY());

			if (idx == -1 && isAnimated()) {

				idx = cardIndexAt(card.getMiddleX(), card.getAbsoluteTop());
					
			}
			return idx != -1 ? cards.cardAt(idx): null;
		}
		public void cancel() {
			CancelEventTimer.resetDrag();
			if (isValid()) {
				card.removeStyleName("dragged");
			}
			reset();
		}

		public boolean getDirection(int y) {
			return lastY <= y;
		}

		boolean isAnimated() {
			return app.has(Feature.MOW_DRAG_AND_DROP_ANIMATION);
		}

		boolean isValid() {
			return card != null;
		}

		void pushUp() {
		}

		void pushDown() {
			int h = target.getOffsetHeight();
			int diff = card.getBottom() - lastTop;
			if (diff < h) {
				Log.debug("PUSH DOWN - target: " + target.getPageIndex() + " diff: " + diff);
				target.setMargin(diff);
			}
		}
	}
	
	DragController(Cards slides, App app) {
		this.cards = slides;
		this.app = app;
		dragged = new DragCard();
	}

	void startDrag(int x, int y) {
		dragged.setIndex(cardIndexAt(x, y));
		if (dragged.isValid() && dragged.isAnimated()) {
			if (dragged.index() < cards.getCardCount() - 1) {
				startSpaceIdx = dragged.index() + 1;
				cards.cardAt(startSpaceIdx).addStyleName("space");
				dragged.lastTop = cards.cardAt(startSpaceIdx).getAbsoluteTop();
			}
		}
	}
	
	void move(int x, int y) {
		if (CancelEventTimer.isDragStarted()) {
			startDrag(x, y);
		} else if (CancelEventTimer.isDragging()) {
			int targetIdx = drag(y);
			if (targetIdx != -1 && !dragged.isAnimated()) {
				cards.getListener().insertDivider(targetIdx);
			}
		}
	}


	private int drag(int y) {
		if (!dragged.isValid()) {
			return -1;
		}

		boolean down = dragged.getDirection(y);

		dragged.setPosition(0, y);

		if (dragged.target == null) {
			return -1;
		}

		int targetIdx = dragged.target.getPageIndex();

		boolean bellowMiddle = dragged.target.getMiddleY() < dragged.card.getAbsoluteTop();

		if (dragged.isAnimated()) {
			if (down) {
				dragged.pushDown();
			} else {
				dragged.pushUp();
			}
		}
		dragged.lastTarget = dragged.target;
		return bellowMiddle ? targetIdx + 1 : targetIdx;
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
		dragged.cancel();
		clearSpaces();
		cards.getListener().removeDivider();
	}
	
	boolean drop() {
		if (!dragged.isValid()) {
			return false;
		}

		int srcIdx = dragged.index();
		int destIdx = dragged.destIndex();

		if (srcIdx != -1 && destIdx != -1) {
			Log.debug("drag: " + srcIdx + " drop to " + destIdx);

			cards.reorder(srcIdx, destIdx);
			return true;
		}
		return false;
	}
	
	private int cardIndexAt(int x, int y) {
		int result =  - 1;
		for (PagePreviewCard card: cards.getCards()) {
			if ((!dragged.isValid() || card != dragged.card)
					&& card.isHit(x, y)) {
				result = card.getPageIndex();
			}
		}
		return result;
	}


	private void clearSpaces() {
		for (PagePreviewCard card: cards.getCards()) {
			card.removeSpace();
		}
	}
}
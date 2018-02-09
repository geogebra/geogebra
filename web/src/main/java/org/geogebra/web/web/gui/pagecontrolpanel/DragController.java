package org.geogebra.web.web.gui.pagecontrolpanel;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.CancelEventTimer;

class DragController {
	/**
	 * Class to handle drag and drop cards 
	 * @author laszlo 
	 */
	private final Cards cards;
	private DragCard dragged;
	private App app;
	
	interface Cards {
		ArrayList<PagePreviewCard> getCards();
		int getCardCount();
		CardListInterface getListener();
		PagePreviewCard cardAt(int index); 
		void reorder(int srcIdx, int destIdx);
		void clickPage(int pageIdx);
	}

	private static class LastTarget {
		PagePreviewCard target = null;
		int top;
		int bottom;
		
		void reset() {
			target = null;
			top = 0;
			bottom = 0;
		}
		
		void setTarget(PagePreviewCard card) {
			this.target = card;
			top = card.getAbsoluteTop();
			bottom = card.getBottom();
		}

		int index() {
			return target != null ? target.getPageIndex() : -1;
		}

	}
	
	private class DragCard {
	
		
		private static final int CARD_MARGIN = 16;
		PagePreviewCard card = null;
		PagePreviewCard target = null;
		LastTarget last = new LastTarget();
		int lastY = -1;
		DragCard() {
			reset();
		}
		private void reset() {
			card = null;
			target = null;
			last.reset();
			lastY = -1;
		}

		void setIndex(int x, int y) {
			int idx = cardIndexAt(x, y);
			if (idx >= 0 && idx < cards.getCardCount()) {
				card = cards.cardAt(idx);
				card.addStyleName("dragged");
				last.reset();
			} else {
				reset();
			}
		}

		int index() {
			return isValid() ? card.getPageIndex() : -1;
		}

	
		void setPosition(int x, int y) {
			card.setDragPosition(x, y);
			
			
			boolean down = lastY < y;
			target = findTarget(down);
			lastY = y;
			if (target != null && target != last.target) { 
				onTargetChange(down);
				last.setTarget(target);
			}
		}

		private void onTargetChange(boolean down) {
			if (last.target != null) {
				last.target.removeSpace();
				target.addSpace(down);
			}
		}
		private PagePreviewCard findTarget(boolean down) {
	
			int y1 = card.getBottom() - CARD_MARGIN;
			int y2 = card.getAbsoluteTop() + CARD_MARGIN; 
		
			int idx = cardIndexAt(card.getMiddleX(), 
					isAnimated() ? (down ?  y1: y2): card.getMiddleY());

			if (idx == -1 && isAnimated()) {
				idx = cardIndexAt(card.getMiddleX(), (down ?  y2: y1));
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
		
		void start(int x, int y) {
			setIndex(x, y);
			if (isValid() && isAnimated()) {
	 			if (index() < cards.getCardCount() - 1) {
					PagePreviewCard next = cards.cardAt(index() + 1);
					next.addStyleName("spaceOnTop");
					last.setTarget(next);
					}
	 			if (index() > 0) {
					last.bottom = cards.cardAt(index() - 1).getBottom();
					}
			}
		}
		
		void pushUp() {
			int h = target.getOffsetHeight() - CARD_MARGIN;
			int diff = last.bottom - card.getAbsoluteTop();
			if (diff < h) {
				target.setSpaceValue(diff, false);
			}
		}
		
		void pushDown() {
			int h = target.getOffsetHeight() - CARD_MARGIN;
			int diff = card.getBottom() - last.top;
			if (diff < h) {
				target.setSpaceValue(diff, true);
			}
		}
	}
	
	DragController(Cards slides, App app) {
		this.cards = slides;
		this.app = app;
		dragged = new DragCard();
	}

	
	void move(int x, int y) {
		if (CancelEventTimer.isDragStarted()) {
			dragged.start(x, y);
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
		int destIdx = dragged.last.index();

		if (srcIdx != -1 && destIdx != -1) {
//			Log.debug("drag: " + srcIdx + " drop to " + destIdx);

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
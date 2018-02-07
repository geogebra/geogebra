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
			boolean down = lastY < y;
			card.setDragPosition(x, y);
			findTarget(down);
			lastY = y;
		}

		private void findTarget(boolean down) {
	
			int idx = cardIndexAt(card.getMiddleX(), card.getMiddleY());
			
			target = idx != -1 ? cards.cardAt(idx): null;

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

		void pushUp(int treshold) {
			int diff = card.getMiddleY() - target.getMiddleY();
			if (diff < treshold) {
				addSpace(target);
				int nextIdx = target.getPageIndex() + 1;
				if (nextIdx == index()) {
					// skip dragging card
					nextIdx++;
				}
				if (nextIdx < cards.getCardCount()) {
					PagePreviewCard next = cards.cardAt(nextIdx);
					next.removeStyleName("spaceAnimated");
					next.addStyleName("noSpaceAnimated");				
				}
			}
		}

		void pushDown(int treshold) {
			int diff = target.getMiddleY() - card.getBottom();
			if (diff < treshold) {
				addSpace(target);
				int prevIdx = target.getPageIndex() - 1;
				if (prevIdx == index()) {
					// skip dragging card
					prevIdx--;
				}
				if (prevIdx > -1) {
					PagePreviewCard prev = cards.cardAt(prevIdx);
					prev.removeStyleName("spaceAnimated");
					prev.addStyleName("noSpaceAnimated");				
				}
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
			int treshold = dragged.target.getOffsetHeight() / 5;
			if (down) {
				dragged.pushDown(treshold);
			} else {
				dragged.pushUp(treshold);
			}
		}
		dragged.lastTarget = dragged.target;
		return bellowMiddle ? targetIdx + 1 : targetIdx;
	}

	private static void addSpace(PagePreviewCard target) {
		target.removeStyleName("noSpaceAnimated");
		target.addStyleName("spaceAnimated");
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
		if (result == -1) {
			int lastIdx = cards.getCardCount() - 1;
			if (cards.cardAt(lastIdx).getBottom() < y) {
				result = lastIdx;
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
		card.removeStyleName("noSpace");
		card.removeStyleName("space");
		card.removeStyleName("spaceAnimated");
		card.removeStyleName("noSpaceAnimated");
	}
}
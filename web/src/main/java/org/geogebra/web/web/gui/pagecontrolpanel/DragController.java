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
		
		void reset() {
			target = null;
		}
	
		int index() {
			return target != null ? target.getPageIndex() : -1;
		}

	}
	
	private class DragCard {
//		private static final int CARD_MARGIN = 16;
		PagePreviewCard card = null;
//		PagePreviewCard target = null;
		LastTarget last = new LastTarget();
		private int prevY;
		private Boolean down;
			
		DragCard() {
			reset();
		}
	
		private void reset() {
			card = null;
//			target = null;
			down = null;
			last.reset();
		}

		int index() {
			return isValid() ? card.getPageIndex() : -1;
		}

		boolean isAnimated() {
			return app.has(Feature.MOW_DRAG_AND_DROP_ANIMATION);
		}

		boolean isValid() {
			return card != null;
		}
		
		void start(int x, int y) {
			prevY = y;
			int idx = cardIndexAt(x, y);
			int count = cards.getCardCount();
			if (idx >= 0 && idx < count) {
				if (idx < count - 1) {
					PagePreviewCard next = cards.cardAt(idx + 1);
					next.addSpace();
				}
				card = cards.cardAt(idx);
				card.addStyleName("dragged");
				last.reset();
			} else {
				reset();
			}
		}

		int move(int y) {
			if (!isValid()) {
				return -1;
			}
			dragTo(0, y);
			return 0;
		}

		void dragTo(int x, int y) {
			if (down == null) {
				down = prevY < y;
				Log.debug("[D] prevY: " + prevY + " y: " + y + " down: " + down);
			}
			card.setDragPosition(x, y);
		}
				
		boolean drop() {
			if (!isValid()) {
				return false;
			}

			int srcIdx = index();
			int destIdx = last.index();

			if (srcIdx != -1 && destIdx != -1) {
				cards.reorder(srcIdx, destIdx);
				return true;
			}
			return false;
		}
		
		public void cancel() {
			CancelEventTimer.resetDrag();
			if (isValid()) {
				card.removeStyleName("dragged");
			}
			reset();
		}

 		
	}
	
	DragController(Cards slides, App app) {
		this.cards = slides;
		this.app = app;
		dragged = new DragCard();
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

	
	void move(int x, int y) {
		if (CancelEventTimer.isDragStarted()) {
			dragged.start(x, y);			
		} else if (CancelEventTimer.isDragging()) {
			int targetIdx = dragged.move(y);
			if (targetIdx != -1 && !dragged.isAnimated()) {
				cards.getListener().insertDivider(targetIdx);
			}
		}
	}


	void stopDrag(int x, int y) {
		if (CancelEventTimer.isDragging()) {
			if (dragged.drop()) {
				cards.getListener().update();
			}
		} else {
			int idx = cardIndexAt(x, y);
			if (idx != -1) {
				cards.clickPage(idx);
			}
		}
		
		CancelEventTimer.resetDrag();
		dragged.cancel();
		clearSpaces();
		cards.getListener().removeDivider();
	}

	private void clearSpaces() {
		for (PagePreviewCard card: cards.getCards()) {
			card.removeSpace();
		}
	}
}
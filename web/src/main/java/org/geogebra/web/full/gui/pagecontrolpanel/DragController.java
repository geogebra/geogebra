package org.geogebra.web.full.gui.pagecontrolpanel;

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
	PagePreviewCard clicked;
	
	interface Cards {
		ArrayList<PagePreviewCard> getCards();
		int getCardCount();
		CardListInterface getListener();
		PagePreviewCard cardAt(int index); 

		void selectCard(PagePreviewCard card);
		void reorder(int srcIdx, int destIdx);

		void clickPage(int pageIdx, boolean select);
	}

	private static class LastTarget {
		PagePreviewCard target = null;
		int top;
		int bottom;
		void reset() {
			target = null;
		}
	
		int index() {
			return target != null ? target.getPageIndex() : -1;
		}

		public void setTop(int top) {
			this.top = top;
		}

		public void setBottom(int bottom) {
			this.bottom = bottom;
		}

	}
	
	private class DragCard {
		;
		PagePreviewCard card = null;
		PagePreviewCard target = null;
		LastTarget last = new LastTarget();
		private int prevY;
		private Boolean down;
		private int diff;
			
		DragCard() {
			reset();
		}
	
		private void reset() {
			card = null;
			target = null;
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
		
		private void prepareDragCard() {
			card.removeSpace();
			card.addStyleName("dragged");
			last.reset();
			if (down) {
				PagePreviewCard next = next();
				if (next != null) {
					next.addSpaceTop();
					last.target = next;
					last.top = next.getAbsoluteTop();
				}
			} else {
				PagePreviewCard prev = prev();
				if (prev != null) {
					prev.addSpaceBottom();
					last.target = prev;
					last.bottom = prev.getAbsoluteBottom();
				}
			}
		}

		void start(int x, int y) {
			prevY = y;
			int idx = cardIndexAt(x, y);
			int count = cards.getCardCount();
			if (idx >= 0 && idx < count) {
				card = cards.cardAt(idx);
				cards.selectCard(card);
			} else {
				reset();
			}
		}

		int move(int y) {
			if (!isValid()) {
				return -1;
			}

			dragTo(0, y);

			if (target == null) {
				return -1;
			}
			int idx = target.getPageIndex();

			return y < target.getMiddleY() ? idx : idx + 1;
		}

		private void moveAnimated() {
			int h = PagePreviewCard.SPACE_HEIGHT - PagePreviewCard.MARGIN;
			diff = down ? card.getAbsoluteBottom() - last.top
					: last.bottom - card.getAbsoluteTop();

			if (diff > PagePreviewCard.MARGIN && diff < h) {
				target.setSpaceValue(diff + PagePreviewCard.MARGIN, down);
			}
         }
        
		void dragTo(int x, int y) {

			if (down == null) {
				down = prevY < y;
				prepareDragCard();
			}
	
			card.setDragPosition(x, y);
		
			findTarget();
			
			if (target != null) {
				if (!onTargetChange() && isAnimated()) {
					moveAnimated();
				}
			}
		}
				
		private void findTarget() {
			int y1 = card.getAbsoluteBottom();
			int y2 = card.getAbsoluteTop(); 

			int idx = cardIndexAt(card.getMiddleX(), 
					isAnimated() ? (down ?  y1: y2): card.getMiddleY());

			if (idx == -1 && isAnimated()) {
				idx = cardIndexAt(card.getMiddleX(), (down ?  y2: y1));
			}
			
			target = idx != -1 ? cards.cardAt(idx): null;
			
			
		}

		private boolean onTargetChange() {
			clicked = null;
			if (target == last.target) {
				return false;
			}

			boolean spaceAtTop = down;
			if (last.target != null) {
				last.target.removeSpace();
				spaceAtTop = last.target.getPageIndex() < target.getPageIndex();
			}

			if (spaceAtTop) {
				target.addSpaceTop();
				last.setTop(target.getAbsoluteTop());
				last.setBottom(target.getAbsoluteTop());
			} else {
				target.addSpaceBottom();
				last.setTop(target.getAbsoluteTop());
				last.setBottom(target.getAbsoluteBottom());
			}



			last.target = target;
			return true;
		}

		boolean drop(int y) {
			if (!isValid()) {
				return false;
			}

			if (isAnimated()) {
				return dropAnimated(y);
			}

			int srcIdx = index();
			int destIdx = last.index();

			if (srcIdx != -1 && destIdx != -1) {
				cards.reorder(srcIdx, destIdx);
				return true;
			}
			return false;
		}

		boolean dropAnimated(int y) {
			int dropToIdx = -1;
			if (target != null) {
				dropToIdx = target.getPageIndex();
				boolean dragUnderTarget = card.getAbsoluteBottom() > target.getAbsoluteBottom();
				if (down && !dragUnderTarget && dropToIdx > 0) {
					dropToIdx--;
				} else if (!down && dragUnderTarget && dropToIdx < cards.getCardCount() - 1) {
					dropToIdx++;
				}
			} else if (last.target != null) {
				dropToIdx = last.target.getPageIndex();
			}

			if (index() != -1 && dropToIdx != -1) {
				cards.reorder(index(), dropToIdx);
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

		public PagePreviewCard prev() {
			if (index() > 0) {
				return cards.cardAt(index() - 1);
			}
			return null;
		}

		public PagePreviewCard next() {
			if (index() < cards.getCardCount() - 1) {
				return cards.cardAt(index() + 1);
			}
			return null;
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

	public void start(int x, int y) {
		if (clicked != null || dragged.isValid()) {
			cancel();
		}

		int idx = cardIndexAt(x, y);
		if (idx == -1) {
			clicked = null;
		} else {
			clicked = cards.cardAt(idx);
			cards.getListener().hideScrollbar();
			CancelEventTimer.dragCanStart();
		}
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


	void stop(int x, int y) {
		if (clicked != null) {
			cards.clickPage(clicked.getPageIndex(), true);
		} else if (CancelEventTimer.isDragging()) {
			if (dragged.drop(y)) {
				cards.getListener().update();
			}
			if (dragged.isValid()) {
				cards.clickPage(dragged.index(), false);
			}

		}

		cancel();
	}

	void cancel() {
		CancelEventTimer.resetDrag();
		dragged.cancel();
		clearSpaces();
		cards.getListener().restoreScrollbar();
		cards.getListener().removeDivider();
	}

	private void clearSpaces() {
		for (PagePreviewCard card: cards.getCards()) {
			card.removeSpace();
		}
	}

}
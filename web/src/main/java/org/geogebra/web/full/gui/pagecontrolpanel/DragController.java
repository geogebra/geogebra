package org.geogebra.web.full.gui.pagecontrolpanel;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;

import com.google.gwt.user.client.Timer;

/**
 * Class
 * 
 * @author laszlo
 *
 */
class DragController {
	private static final int DROPANIM_SPEED = 5;
	/**
	 * Class to handle drag and drop cards 
	 * @author laszlo 
	 */
	final Cards cards;
	/** Currently dragged card */
	DragCard dragged;
	private App app;

	/** The page which the pointer was down at start. */
	PagePreviewCard clicked;
	private Timer dropAnimTimer;
	private AutoScrollTimer autoScroll;

	/** interface to query the list of cards */
	interface Cards {
		/** @return all the cards in list */
		ArrayList<PagePreviewCard> getCards();

		/** @return the number of cards */
		int getCardCount();

		/** @return the listener of the list panel */
		CardListInterface getListener();

		/**
		 * @param index
		 *            card index to get.
		 * @return the card at given index
		 */
		PagePreviewCard cardAt(int index); 

		/**
		 * Select the card.
		 * 
		 * @param card
		 *            to select.
		 */
		void selectCard(PagePreviewCard card);

		/**
		 * Change the position of two cards.
		 * 
		 * @param srcIdx
		 *            the source index (the dragged one)
		 * @param destIdx
		 *            the destination index (to drop the card)
		 */
		void reorder(int srcIdx, int destIdx);

		/**
		 * Perform a click on the given page.
		 * 
		 * @param pageIdx
		 *            the index of the page to click.
		 * @param select
		 *            true if the page should also be selected.
		 */
		void clickPage(int pageIdx, boolean select);
	}

	private class AutoScrollTimer extends Timer {
		private static final int CANCEL_THRESHOLD = 10;
		private static final int SCROLL_TIME = 100;
		private static final int SCROLL_SPEED = 10;
		private boolean scrollDown;
		private int autoScrollY;

		protected AutoScrollTimer() {
			// avoid synthetic constructor
		}

		@Override
		public void run() {
			if (scroll()) {
				int pos = getListener().getVerticalScrollPosition();
				if (scrollDown) {
					pos += getListener().getScrollParentHeight()
							- PagePreviewCard.SPACE_HEIGHT
							- PagePreviewCard.MARGIN;
				}

				onScroll(pos);
			} else {
				onScrollCancel();
			}
		}

		private boolean scroll() {
			return getListener().scrollBy((scrollDown ? 1 : -1) * SCROLL_SPEED);
		}

		public void start(boolean b) {
			scrollDown = b;
			clearSpaces();
			scheduleRepeating(SCROLL_TIME);
		}
		
		public void checkIfNeeded(int y) {
			int diff = y - autoScrollY;
			int scrollPos = getListener().getVerticalScrollPosition();
			boolean d = diff > 0;
			if (isRunning()) {
				if ((d != scrollDown && Math.abs(diff) > CANCEL_THRESHOLD)
						|| (!scrollDown && scrollPos == 0)) {
					onScrollCancel();
				}
			} else if (!d && dragged.card.getAbsoluteTop() <= PagePreviewCard.MARGIN
					&& scrollPos > 0) {
				start(false);
			}
			if (d && dragged.card
					.getAbsoluteBottom() > getListener().getScrollParentHeight()
					- PagePreviewCard.MARGIN) {
				start(true);
			}
			autoScrollY = y;
		}
	}

	private static class LastTarget {
		PagePreviewCard target = null;
		int top;
		int bottom;

		protected LastTarget() {
			// protected constructor
		}

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
		private static final int AUTOMOVE_SPEED = 1;
		PagePreviewCard card = null;
		PagePreviewCard target = null;
		LastTarget last = new LastTarget();
		private int prevY;
		private Boolean down;
		private int diff;
		private int dropToIdx;
		private boolean dropBellow;

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

		boolean isValid() {
			return card != null;
		}

		int getDropIndex() {
			return dropToIdx;
		}

		private void addSpaceTop() {
			PagePreviewCard next = next();
			if (next != null) {
				next.addSpaceTop();
				last.target = next;
				last.top = next.getComputedTop();
			}
		}

		private void addSpaceBottom() {
			PagePreviewCard prev = prev();
			if (prev != null) {
				prev.addSpaceBottom();
				last.target = prev;
				last.bottom = prev.getComputedBottom();
			}
		}

		private void prepareDragCard() {
			card.removeSpace();
			card.addStyleName("dragged");

			last.reset();

			if (down) {
				addSpaceTop();
				if (card.getPageIndex() == 0 && next() != null) {
					last.top = next().getComputedTop();
				}
			} else {
				addSpaceBottom();
			}
		}

		void start(int y) {
			prevY = y;
			card = clicked;
			card.grabCard(y);
			card.addStyleName("dragCanStart");
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
			int h = PagePreviewCard.SPACE_HEIGHT; // - PagePreviewCard.MARGIN;
			diff = down ? computedY(card.getAbsoluteBottom()) - last.top
					: last.bottom - computedY(card.getAbsoluteTop());

			if (diff > 0 && diff < h) {
				target.setSpaceValue(diff + PagePreviewCard.MARGIN, down);
			}
		}

		void dragTo(int x, int y) {
			if (down == null) {
				down = prevY <= y;
				prepareDragCard();
			}
			card.setDragPosition(x, y);
			handleTarget(y);
		}

		public void handleTarget(int y) {
			findTarget();
			if (target != null && getApp().isWhiteboardActive()) {
				if (onTargetChange()) {
					down = prevY < y;
				} else {
					moveAnimated();
				}
			}
		}

		boolean autoMove() {
			if (diff >= PagePreviewCard.SPACE_HEIGHT - 2 * PagePreviewCard.MARGIN || diff < 0) {
				return false;
			}
			int d = (dropBellow ? 1 : -1) * AUTOMOVE_SPEED;
			if (card == null) {
				return false;
			}
			card.setTopBy(d);
			moveAnimated();
			return true;
		}

		private void findTarget() {
			int y1 = computedY(card.getAbsoluteBottom());
			int y2 = computedY(card.getAbsoluteTop());

			int idx = cardIndexAt(card.getMiddleX(), 
					getApp().isWhiteboardActive() ? (down ? y1 : y2)
							: card.getMiddleY());

			if (idx == -1 && getApp().isWhiteboardActive()) {
				idx = cardIndexAt(card.getMiddleX(), (down ? y2 : y1));
			}
			target = idx != -1 ? cards.cardAt(idx) : null;
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

			int top = target.getComputedTop();
			if (spaceAtTop) {
				target.addSpaceTop();
				last.setTop(top);
				last.setBottom(top);
			} else {
				target.addSpaceBottom();
				last.setTop(top);
				last.setBottom(target.getComputedBottom());
			}

			last.target = target;
			return true;
		}

		boolean drop() {
			if (!isValid()) {
				return false;
			}

			if (getApp().isWhiteboardActive()) {
				return dropAnimated();
			}

			int srcIdx = index();
			int destIdx = last.index();

			if (srcIdx != -1 && destIdx != -1) {
				cards.reorder(srcIdx, destIdx);
				return true;
			}
			return false;
		}

		int getDropIndex(PagePreviewCard t) {
			if (t == null) {
				return -1;
			}

			int idx = t.getPageIndex();
			boolean dragUnderTarget = card.getAbsoluteBottom() > t.getAbsoluteBottom();
			if (down && !dragUnderTarget && idx > 0) {
				idx--;
			} else if (!down && dragUnderTarget && idx < cards.getCardCount() - 1) {
				idx++;
			}
			dropBellow = dragUnderTarget;

			return idx;
		}

		boolean dropAnimated() {
			dropToIdx = -1;
			if (target != null) {
				dropToIdx = getDropIndex(target);
			} else if (last.target != null) {
				dropToIdx = getDropIndex(last.target);
			}

			if (index() != -1 && dropToIdx != -1) {
				return true;
			}
			return false;
		}

		public void cancel() {
			CancelEventTimer.resetDrag();
			if (isValid()) {
				card.removeStyleName("dragged");
				card.removeStyleName("dragCanStart");
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
	
	/**
	 * Constructor
	 * 
	 * @param slides
	 *            the cards interface.
	 * @param app
	 *            The application.
	 */
	DragController(Cards slides, App app) {
		this.cards = slides;
		this.app = app;
		dragged = new DragCard();
		dropAnimTimer = new Timer() {
			@Override
			public void run() {
				if (!dragged.autoMove()) {
					onDrop();
				}
			}
		};
		autoScroll = new AutoScrollTimer();
	}

	/**
	 * @return see {@link App}
	 */
	public App getApp() {
		return app;
	}

	/**
	 * @param pos
	 *            scroll vertical position
	 */
	public void onScroll(int pos) {
		dragged.card.setTop(pos);
		dragged.handleTarget(pos);
	}

	/**
	 * @param x
	 *            mouse x
	 * @param y
	 *            mouse y
	 * @return card index
	 */
	int cardIndexAt(int x, int y) {
		int result =  - 1;
		for (PagePreviewCard card: cards.getCards()) {
			if ((!dragged.isValid() || card != dragged.card)
					&& card.isHit(x, y)) {
				result = card.getPageIndex();
			}
		}
		return result;
	}

	/**
	 * Called at pointer (mouse or touch) down.
	 * 
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 */
	public void start(int x, int y) {
		if (clicked != null || dragged.isValid()) {
			cancelDrag();
		}

		int idx = cardIndexAt(x, computedY(y));
		if (idx == -1) {
			clicked = null;
		} else {
			clicked = cards.cardAt(idx);
			cards.selectCard(clicked);
			CancelEventTimer.dragCanStart();

			Timer dragStyleTimer = new Timer() {
				@Override
				public void run() {
					// clicked may become null before timeout
					if (!CancelEventTimer.noDrag() && clicked != null) {
						clicked.addDragStartStyle(true);
					}
				}
			};
			dragStyleTimer.schedule(CancelEventTimer.TIME_BETWEEN_TOUCH_AND_DRAG);
		}
	}
	
	/**
	 * Converts absolute (screen) y coordinate to the one that is between 0 and the
	 * whole height of cards in panel, which is scroll independent.
	 * 
	 * @param y
	 *            absolute coordinate.
	 * @return scroll independent coordinate.
	 */
	int computedY(int y) {
		return y + getListener().getVerticalScrollPosition();
	}

	/**
	 * Called at pointer (mouse or touch) move.
	 * 
	 * @param x
	 *            coordinate.
	 * @param y0
	 *            coordinate.
	 * @param touch
	 *            Whether move event comes from touch or desktop.
	 * @return true if drag has occurred.
	 */
	boolean move(int x, int y0, boolean touch) {
		if (touch && CancelEventTimer.cancelDragEvent()) {
			return false;
		}

		if (CancelEventTimer.noDrag() || dropAnimTimer.isRunning()) {
			return false;
		}
		int y = computedY(y0);
		if (CancelEventTimer.isDragStarted()) {
			dragged.start(y);
		} else if (CancelEventTimer.isDragging()) {
			autoScroll.checkIfNeeded(y);
			if (autoScroll.isRunning()) {
				dragged.card.grabCard(y);
				return true;
			}
			int targetIdx = dragged.move(y);
			if (targetIdx != -1 && !app.isWhiteboardActive()) {
				getListener().insertDivider(targetIdx);
			}
		}
		return true;
	}

	/**
	 * Called at pointer (mouse or touch) up.
	 * 
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 */
	void stop(int x, int y) {
		if (clicked != null) {
			cards.clickPage(clicked.getPageIndex(), true);
			clicked.addDragStartStyle(false);
		} else if (CancelEventTimer.isDragging()) {
			if (dragged.drop()) {
				if (app.isWhiteboardActive()) {
					createDropAnimation();
					return;
				}
				getListener().update();
			}
			if (dragged.isValid()) {
				cards.clickPage(dragged.index(), false);
			}

		}
		cancelDrag();
	}

	private void createDropAnimation() {
		CancelEventTimer.resetDrag();
		dropAnimTimer.scheduleRepeating(DROPANIM_SPEED);
	}

	/**
	 * Cancels the drag in progress.
	 */
	void cancelDrag() {
		CancelEventTimer.resetDrag();
		dragged.cancel();
		clearSpaces();
		getListener().restoreScrollbar();
		getListener().removeDivider();
		autoScroll.cancel();
	}

	/**
	 * Remove all spaces
	 */
	void clearSpaces() {
		for (PagePreviewCard card: cards.getCards()) {
			card.removeSpace();
		}
	}

	/**
	 * Called on drop the card.
	 */
	void onDrop() {
		dropAnimTimer.cancel();
		if (dragged.index() >= 0) {
			app.dispatchEvent(new Event(EventType.MOVE_SLIDE, null,
					dragged.index() + "," + dragged.getDropIndex() + ""));
			cards.reorder(dragged.index(), dragged.getDropIndex());
			getListener().update();
			cards.clickPage(dragged.index(), false);
		}
		cancelDrag();
	}

	/**
	 * Called if the scroll by dragging should be canceled.
	 */
	void onScrollCancel() {
		autoScroll.cancel();
		clearSpaces();
	}

	/**
	 * @return cards listener.
	 */
	CardListInterface getListener() {
		return cards.getListener();
	}
}
package org.geogebra.web.full.gui.pagecontrolpanel;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

class DragController {

	private static final int DROPANIM_SPEED = 5;

	private final Cards cards;
	private final App app;

	/** The page which the pointer was down at start. */
	private PagePreviewCard clicked;
	private final Timer dropAnimTimer;
	private final AutoScrollTimer autoScroll;

	/** Currently dragged card */
	private PagePreviewCard dragged;
	private int target;

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

		/**
		 * Remove all spaces
		 */
		void resetCardPositions();
	}

	private class AutoScrollTimer extends Timer {

		private static final int SCROLL_TIME = 5;
		private int scroll;

		@Override
		public void run() {
			getListener().scrollBy(scroll);
			if (dragged != null) {
				setTopBy(dragged, scroll);
				findTarget();
				adjustAllCards();
			}
		}

		private void start(int scroll) {
			this.scroll = scroll;
			scheduleRepeating(SCROLL_TIME);
		}
		
		public void checkIfNeeded(int y) {
			if (y < getApp().getAbsTop() + PagePreviewCard.MARGIN) {
				start(-2);
			} else if (y > getApp().getHeight() - PagePreviewCard.MARGIN) {
				start(2);
			} else {
				super.cancel();
			}
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
		dropAnimTimer = new Timer() {
			@Override
			public void run() {
				if (!autoMove()) {
					onDrop();
				}
			}
		};
		autoScroll = new AutoScrollTimer();
	}

	private int index() {
		return isValid() ? dragged.getPageIndex() : -1;
	}

	private boolean isValid() {
		return dragged != null;
	}

	private int getDropIndex() {
		return (int) Math.round((double) (dragged.getTop() - PagePreviewCard.MARGIN)
				/ PagePreviewCard.TOTAL_HEIGHT);
	}

	private void prepareDragCard() {
		dragged.addStyleName("dragged");
	}

	private void start(int y) {
		dragged = clicked;
		clicked = null;
		dragged.grabCard(y);
		dragged.addStyleName("dragCanStart");
	}

	private void move(int y) {
		if (!isValid()) {
			return;
		}
		dragTo(y);
	}

	private void dragTo(int y) {
		prepareDragCard();
		setDragPosition(dragged, y);
		findTarget();
		adjustAllCards();
	}

	private void adjustAllCards() {
		for (PagePreviewCard curr : cards.getCards()) {
			if (curr.getPageIndex() != target && curr != dragged) {
				adjustTop(curr);
			}
		}

		if (target >= 0 && target < cards.getCardCount()) {
			// instead of rounding away from zero, this is the position rounded towards zero
			int adjustedTarget = target;
			if (target < dragged.getPageIndex()) {
				adjustedTarget++;
			}
			if (target > dragged.getPageIndex()) {
				adjustedTarget--;
			}

			PagePreviewCard targetCard = cards.cardAt(target);
			int newTop = PagePreviewCard.computeTop(target + adjustedTarget)
					- dragged.getTop() + PagePreviewCard.MARGIN;
			setTop(targetCard, newTop);
		}
	}

	private boolean autoMove() {
		if (!isValid()) {
			return false;
		}

		int newTop = PagePreviewCard.computeTop(getDropIndex());
		if (newTop != dragged.getTop()) {
			int d = newTop > dragged.getTop() ? 1 : -1;

			setTopBy(dragged, d);
			if (target >= 0 && target < cards.getCardCount()) {
				setTopBy(cards.cardAt(target), -d);
			}
			return true;
		}

		return false;
	}

	private void findTarget() {
		double ratio = (double) (dragged.getTop() - dragged.getComputedTop())
				/ PagePreviewCard.TOTAL_HEIGHT;
		int diff = (int) ((ratio > 0) ? Math.ceil(ratio) : Math.floor(ratio));
		target = dragged.getPageIndex() + diff;
	}

	public void adjustTop(PagePreviewCard current) {
		int index = current.getPageIndex();
		if (dragged.getPageIndex() > index && index > target) {
			index++;
		} else if (dragged.getPageIndex() < index && index < target) {
			index--;
		}

		setTop(current, PagePreviewCard.computeTop(index));
	}

	private boolean canDrop() {
		return index() != -1 && getDropIndex() != -1;
	}

	public void cancel() {
		CancelEventTimer.resetDrag();
		if (isValid()) {
			dragged.removeStyleName("dragged");
			dragged.removeStyleName("dragCanStart");
		}
		dragged = null;
		clicked = null;
	}

	/**
	 * @return see {@link App}
	 */
	public AppW getApp() {
		return (AppW) app;
	}

	private void setTop(PagePreviewCard card, int top) {
		card.setTop(PagePreviewCard.clampTop(top, cards.getCardCount()));
	}

	private void setTopBy(PagePreviewCard card, int value) {
		int top = card.getTop() + value;
		setTop(card, top);
	}

	private void setDragPosition(PagePreviewCard card, int y) {
		int top = card.getTopFromDrag(y);
		setTop(card, top);
	}

	/**
	 * @param x
	 *            mouse x
	 * @param y
	 *            mouse y
	 * @return card index
	 */
	private int cardIndexAt(int x, int y) {
		for (PagePreviewCard card : cards.getCards()) {
			if ((!isValid() || card != dragged)
					&& card.isHit(x, y)) {
				return card.getPageIndex();
			}
		}
		return -1;
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
		if (clicked != null || isValid()) {
			cancelDrag();
		}

		int idx = cardIndexAt(x, y);
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
	 * Called at pointer (mouse or touch) move.
	 * 
	 * @param y
	 *            coordinate.
	 * @param touch
	 *            Whether move event comes from touch or desktop.
	 * @return true if drag has occurred.
	 */
	boolean move(int y, boolean touch) {
		if (touch && CancelEventTimer.cancelDragEvent()) {
			return false;
		}

		if (CancelEventTimer.noDrag() || dropAnimTimer.isRunning()) {
			return false;
		}

		if (CancelEventTimer.isDragStarted()) {
			start(y);
		} else if (CancelEventTimer.isDragging()) {
			autoScroll.checkIfNeeded(y);
			move(y);
		}
		return true;
	}

	void cancelClick() {
		clicked = null;
	}

	/**
	 * Called at pointer (mouse or touch) up.
	 */
	void stop() {
		if (clicked != null) {
			cards.clickPage(clicked.getPageIndex(), true);
			clicked.addDragStartStyle(false);
		} else if (CancelEventTimer.isDragging()) {
			if (canDrop()) {
				createDropAnimation();
				return;
			}
			if (isValid()) {
				cards.clickPage(index(), false);
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
	private void cancelDrag() {
		CancelEventTimer.resetDrag();
		cancel();
		cards.resetCardPositions();
		autoScroll.cancel();
	}

	/**
	 * Called on drop the card.
	 */
	private void onDrop() {
		dropAnimTimer.cancel();
		if (index() >= 0) {
			app.dispatchEvent(new Event(EventType.MOVE_SLIDE, null,
					index() + "," + getDropIndex() + ""));
			cards.reorder(index(), getDropIndex());
			getListener().update();
			cards.clickPage(index(), false);
		}
		cancelDrag();
	}

	/**
	 * @return cards listener.
	 */
	private CardListInterface getListener() {
		return cards.getListener();
	}
}

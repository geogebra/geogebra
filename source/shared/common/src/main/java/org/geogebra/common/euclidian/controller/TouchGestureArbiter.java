/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.controller;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Decides whether finger contacts on a graphics view are input for the active tool or a
 * two-finger navigation of the view.
 * <p>
 * The first finger is held back for a short time (see
 * {@link EuclidianController#getTouchStartDelay()}). If a second finger lands in that time,
 * the tool never sees either finger: the two fingers navigate the view until all fingers are
 * lifted. Otherwise the held finger is passed on to the tool, together with everything it did
 * in the meantime. A second finger that lands after that is either handled the way it was
 * before this class existed (see {@link Target#lateTwoTouchStart}) or ignored.
 * </p>
 * A stylus is never held back and never takes part in a two-finger gesture.
 */
public final class TouchGestureArbiter implements GTimerListener {

	/** Default time in milliseconds for which the first finger is held back. */
	public static final int DEFAULT_START_DELAY = 150;

	private final Target target;
	private final GTimer timer;
	private final ArrayList<Contact> contacts = new ArrayList<>();
	private final ArrayList<AbstractEvent> heldMoves = new ArrayList<>();
	private @Nullable AbstractEvent heldStart;
	private State state = State.IDLE;
	private boolean stylusActsOnTool;

	/**
	 * Receives the tool input and the navigation that the arbiter recognised.
	 */
	public interface Target {

		/**
		 * @return time in milliseconds for which the first finger is held back
		 */
		int getStartDelay();

		/**
		 * A single contact started acting on the tool.
		 * @param event the event of the contact landing; its time is earlier than now if the
		 * contact was held back
		 */
		void touchStart(@NonNull AbstractEvent event);

		/**
		 * The contact acting on the tool moved.
		 * @param event new position
		 */
		void touchMove(@NonNull AbstractEvent event);

		/**
		 * The contact acting on the tool was lifted.
		 * @param event position where it was lifted
		 */
		void touchEnd(@NonNull AbstractEvent event);

		/**
		 * Two fingers started navigating the view; the tool got no input from them.
		 * Also called when the pair of navigating fingers changes.
		 * @param x1 x-coordinate of the first finger
		 * @param y1 y-coordinate of the first finger
		 * @param x2 x-coordinate of the second finger
		 * @param y2 y-coordinate of the second finger
		 */
		void navigationStart(double x1, double y1, double x2, double y2);

		/**
		 * One of the navigating fingers moved.
		 * @param x1 x-coordinate of the first finger
		 * @param y1 y-coordinate of the first finger
		 * @param x2 x-coordinate of the second finger
		 * @param y2 y-coordinate of the second finger
		 */
		void navigationMove(double x1, double y1, double x2, double y2);

		/**
		 * All fingers of a navigation were lifted.
		 */
		void navigationEnd();

		/**
		 * A second finger landed while the first one was already acting on the tool.
		 * @param x1 x-coordinate of the first finger
		 * @param y1 y-coordinate of the first finger
		 * @param x2 x-coordinate of the second finger
		 * @param y2 y-coordinate of the second finger
		 * @return true if the two fingers now act together, false to ignore the second finger
		 */
		boolean lateTwoTouchStart(double x1, double y1, double x2, double y2);

		/**
		 * One of two fingers accepted by {@link #lateTwoTouchStart} moved.
		 * @param x1 x-coordinate of the first finger
		 * @param y1 y-coordinate of the first finger
		 * @param x2 x-coordinate of the second finger
		 * @param y2 y-coordinate of the second finger
		 */
		void lateTwoTouchMove(double x1, double y1, double x2, double y2);

		/**
		 * One of two fingers accepted by {@link #lateTwoTouchStart} was lifted. The other one
		 * continues to act on the tool on its own.
		 * @param lifted position where the finger was lifted
		 * @param remaining current position of the other finger
		 */
		void lateTwoTouchEnd(@NonNull AbstractEvent lifted, @NonNull AbstractEvent remaining);
	}

	private enum State {
		/** no contact */
		IDLE,
		/** first finger is down, the tool does not know about it yet */
		HELD,
		/** first contact acts on the tool, further contacts are ignored */
		TOOL,
		/** first two fingers act together, see {@link Target#lateTwoTouchStart} */
		LATE_TWO_TOUCH,
		/** first two fingers navigate the view */
		NAVIGATION,
		/** the contact that acted on the tool is gone, ignored contacts are still down */
		DRAINING
	}

	private static final class Contact {
		private final long id;
		private @NonNull AbstractEvent event;

		private Contact(long id, @NonNull AbstractEvent event) {
			this.id = id;
			this.event = event;
		}
	}

	/**
	 * @param target receiver of tool input and navigation; provides the start delay
	 */
	public TouchGestureArbiter(@NonNull Target target) {
		this.target = target;
		this.timer = UtilFactory.getPrototype().newTimer(this, DEFAULT_START_DELAY);
	}

	/**
	 * A contact landed on the view.
	 * @param id identifies the contact until it is lifted
	 * @param event position of the contact
	 * @param stylus whether the contact is a stylus rather than a finger
	 */
	public void pointerDown(long id, @NonNull AbstractEvent event, boolean stylus) {
		Contact contact = new Contact(id, event);
		switch (state) {
			case IDLE:
				contacts.add(contact);
				int delay = target.getStartDelay();
				if (stylus || delay <= 0) {
					startTool(event, stylus);
				} else {
					state = State.HELD;
					heldStart = event;
					timer.setDelay(delay);
					timer.start();
				}
				break;
			case HELD:
				dropHeld();
				if (stylus) {
					// e.g. palm first, then stylus: only the stylus acts on the tool
					contacts.add(0, contact);
					startTool(event, true);
				} else {
					contacts.add(contact);
					state = State.NAVIGATION;
					startNavigation();
				}
				break;
			case TOOL:
				contacts.add(contact);
				if (contacts.size() == 2 && !stylus && !stylusActsOnTool && lateTwoTouchStart()) {
					state = State.LATE_TWO_TOUCH;
				}
				break;
			case NAVIGATION:
				if (!stylus) {
					contacts.add(contact);
					if (contacts.size() == 2) {
						startNavigation();
					}
				}
				break;
			default:
				contacts.add(contact);
				break;
		}
	}

	/**
	 * A contact moved.
	 * @param id identifier passed to {@link #pointerDown}
	 * @param event new position of the contact
	 */
	public void pointerMove(long id, @NonNull AbstractEvent event) {
		int index = indexOf(id);
		if (index < 0) {
			return;
		}
		contacts.get(index).event = event;
		switch (state) {
			case HELD:
				heldMoves.add(event);
				break;
			case TOOL:
				if (index == 0) {
					target.touchMove(event);
				}
				break;
			case LATE_TWO_TOUCH:
				if (index < 2) {
					target.lateTwoTouchMove(x(0), y(0), x(1), y(1));
				}
				break;
			case NAVIGATION:
				if (index < 2 && contacts.size() >= 2) {
					target.navigationMove(x(0), y(0), x(1), y(1));
				}
				break;
			default:
				break;
		}
	}

	/**
	 * A contact was lifted.
	 * @param id identifier passed to {@link #pointerDown}
	 * @param event position where the contact was lifted
	 */
	public void pointerUp(long id, @NonNull AbstractEvent event) {
		int index = indexOf(id);
		if (index < 0) {
			return;
		}
		contacts.remove(index);
		switch (state) {
			case HELD:
				timer.stop();
				deliverHeld();
				finishTool(event);
				break;
			case TOOL:
				if (index == 0) {
					finishTool(event);
				}
				break;
			case LATE_TWO_TOUCH:
				if (index < 2) {
					state = State.TOOL;
					target.lateTwoTouchEnd(event, contacts.get(0).event);
				}
				break;
			case NAVIGATION:
				if (contacts.isEmpty()) {
					state = State.IDLE;
					target.navigationEnd();
				} else if (index < 2 && contacts.size() >= 2) {
					startNavigation();
				}
				break;
			default:
				if (contacts.isEmpty()) {
					state = State.IDLE;
				}
				break;
		}
	}

	/**
	 * Forget all contacts, e.g. because the system took the touches away. A held finger never
	 * reaches the tool; a contact acting on the tool ends where it was last; a running
	 * navigation is ended.
	 */
	public void cancel() {
		State previous = state;
		ArrayList<Contact> lastContacts = new ArrayList<>(contacts);
		dropHeld();
		contacts.clear();
		state = State.IDLE;
		if (previous == State.NAVIGATION) {
			target.navigationEnd();
		} else if (previous == State.LATE_TWO_TOUCH) {
			target.lateTwoTouchEnd(lastContacts.get(1).event, lastContacts.get(0).event);
			target.touchEnd(lastContacts.get(0).event);
		} else if (previous == State.TOOL) {
			target.touchEnd(lastContacts.get(0).event);
		}
	}

	/**
	 * @return whether a contact is acting on the tool right now
	 */
	public boolean isToolActive() {
		return state == State.TOOL || state == State.LATE_TWO_TOUCH;
	}

	/**
	 * @return whether no contact is being tracked
	 */
	public boolean isIdle() {
		return state == State.IDLE;
	}

	@Override
	public void onRun() {
		if (state == State.HELD) {
			deliverHeld();
		}
	}

	private void startTool(AbstractEvent event, boolean stylus) {
		state = State.TOOL;
		stylusActsOnTool = stylus;
		target.touchStart(event);
	}

	private void finishTool(AbstractEvent event) {
		state = contacts.isEmpty() ? State.IDLE : State.DRAINING;
		target.touchEnd(event);
	}

	private void deliverHeld() {
		AbstractEvent start = heldStart;
		ArrayList<AbstractEvent> moves = new ArrayList<>(heldMoves);
		heldStart = null;
		heldMoves.clear();
		if (start == null) {
			return;
		}
		startTool(start, false);
		for (AbstractEvent move : moves) {
			target.touchMove(move);
		}
	}

	private void dropHeld() {
		timer.stop();
		heldStart = null;
		heldMoves.clear();
	}

	private void startNavigation() {
		target.navigationStart(x(0), y(0), x(1), y(1));
	}

	private boolean lateTwoTouchStart() {
		return target.lateTwoTouchStart(x(0), y(0), x(1), y(1));
	}

	private int indexOf(long id) {
		for (int i = 0; i < contacts.size(); i++) {
			if (contacts.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}

	private double x(int index) {
		return contacts.get(index).event.getX();
	}

	private double y(int index) {
		return contacts.get(index).event.getY();
	}
}

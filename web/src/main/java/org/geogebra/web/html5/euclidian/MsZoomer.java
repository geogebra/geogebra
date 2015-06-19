package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.ZeroOffset;

import com.google.gwt.dom.client.Element;

public class MsZoomer {

	private final IsEuclidianController tc;
	private HasOffsets off;

	private class MsOffset extends ZeroOffset {

		private IsEuclidianController ec;

		public MsOffset(IsEuclidianController ec) {
			this.ec = ec;
		}

		@Override
		public int mouseEventX(int clientX) {
			return Math.round(clientX + (zoom() - 1));
		}

		private native int zoom() /*-{
			return $wnd.screen.deviceXDPI / $wnd.screen.logicalXDPI;
		}-*/;

		@Override
		public int mouseEventY(int clientY) {
			return Math.round(clientY + (zoom() - 1));
		}

		@Override
		public int touchEventX(int clientX) {
			return mouseEventX(clientX);
		}

		@Override
		public int touchEventY(int clientY) {
			return mouseEventY(clientY);
		}

		@Override
		public int getEvID() {
			return ec.getEvNo();
		}

	}

	public MsZoomer(IsEuclidianController tc) {
		this.tc = tc;
		// this.off = (HasOffsets)this.tc;
		this.off = new MsOffset(tc);
	}

	private void pointersUp() {
		this.tc.getLongTouchManager().cancelTimer();
		this.tc.setExternalHandling(false);
	}

	private void twoPointersDown(double x1, double y1, double x2, double y2) {
		this.tc.setExternalHandling(true);
		this.tc.twoTouchStart(x1, y1, x2, y2);
	}

	private void twoPointersMove(double x1, double y1, double x2, double y2) {
		this.tc.twoTouchMove(x1, y1, x2, y2);
	}

	private void setPointerTypeTouch(boolean b) {
		this.tc.setDefaultEventType(b ? PointerEventType.TOUCH
		        : PointerEventType.MOUSE);
	}

	private void startLongTouch(int x, int y) {
		if (this.tc.getMode() == EuclidianConstants.MODE_MOVE) {
			this.tc.getLongTouchManager().scheduleTimer(tc, x, y);
		}
	}

	private void moveLongTouch(int x, int y) {
		if (!tc.isDraggingBeyondThreshold()) {
			/*
			 * this.tc.getLongTouchManager().rescheduleTimerIfRunning(tc, x, y,
			 * false);
			 */
		} else {
			this.tc.getLongTouchManager().cancelTimer();
		}

	}

	public native void reset()/*-{
		$wnd.first = {
			id : -1
		};
		$wnd.second = {
			id : -1
		};
	}-*/;

	public static native void attachTo(Element element, MsZoomer zoomer) /*-{
		$wnd.first = {
			id : -1
		};
		$wnd.second = {
			id : -1
		};

		element
				.addEventListener(
						"MSPointerMove",
						function(e) {
							if ($wnd.first.id >= 0 && $wnd.second.id >= 0) {
								if ($wnd.second.id === e.pointerId) {
									$wnd.second.x = e.x;
									$wnd.second.y = e.y;
									zoomer
											.@org.geogebra.web.html5.euclidian.MsZoomer::twoPointersMove(
													DDDD)($wnd.first.x,
													$wnd.first.y,
													$wnd.second.x,
													$wnd.second.y);
								} else {
									$wnd.first.x = e.x;
									$wnd.first.y = e.y;
								}

							}

							zoomer.@org.geogebra.web.html5.euclidian.MsZoomer::moveLongTouch(II)($wnd.first.x, $wnd.first.y);

							zoomer.@org.geogebra.web.html5.euclidian.MsZoomer::setPointerTypeTouch(Z)(e.pointerType == 2 || e.pointerType == "touch");
						});

		element
				.addEventListener(
						"MSPointerDown",
						function(e) {
							if ($wnd.first.id >= 0 && $wnd.second.id >= 0) {
								return;
							}
							if ($wnd.first.id >= 0) {
								$wnd.second.id = e.pointerId;
								$wnd.second.x = e.x;
								$wnd.second.y = e.y;
							} else {
								$wnd.first.id = e.pointerId;
								$wnd.first.x = e.x;
								$wnd.first.y = e.y;
							}
							if ($wnd.first.id >= 0 && $wnd.second.id >= 0) {
								zoomer
										.@org.geogebra.web.html5.euclidian.MsZoomer::twoPointersDown(
												DDDD)($wnd.first.x,
												$wnd.first.y, $wnd.second.x,
												$wnd.second.y);
							}
							if (e.pointerType == 2 || e.pointerType == "touch") {
								zoomer.@org.geogebra.web.html5.euclidian.MsZoomer::startLongTouch(II)($wnd.first.x, $wnd.first.y);
							}
							zoomer.@org.geogebra.web.html5.euclidian.MsZoomer::setPointerTypeTouch(Z)(e.pointerType == 2 || e.pointerType == "touch");

						});

		element
				.addEventListener(
						"MSPointerUp",
						function(e) {
							if ($wnd.first.id == e.pointerId) {
								$wnd.first.id = -1;
							} else {
								$wnd.second.id = -1;
							}
							zoomer.@org.geogebra.web.html5.euclidian.MsZoomer::pointersUp()();
							zoomer.@org.geogebra.web.html5.euclidian.MsZoomer::setPointerTypeTouch(Z)(e.pointerType == 2 || e.pointerType == "touch");
						});
	}-*/;

}

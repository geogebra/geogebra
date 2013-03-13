package geogebra.web.main;

import geogebra.common.kernel.View;
import geogebra.common.main.App;
import geogebra.web.euclidian.EuclidianViewWeb;
import geogebra.web.gui.view.algebra.AlgebraViewWeb;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;

import java.util.Date;

import com.google.gwt.user.client.Timer;
//import geogebra.web.gui.view.algebra.AlgebraViewW;

public class TimerSystemW {

	public static int euclidianMillis = 34; // = 30 FPS, half of screen Hz
	public static int algebraMillis = 334; // = 3 FPS
	public static int spreadsheetMillis = 334; // = 3 FPS

	AppWeb application;

	// in the final implementation, these wouldn't be static
	static boolean euclidian1Timed = false;
	static boolean algebraTimed = false;
	static boolean spreadsheetTimed = false;

	// in the final implementation, these wouldn't be static
	private static Date euclidian1Latest = null;//new Date();
	static Date algebraLatest = null;//new Date();
	static Date spreadsheetLatest = null;//new Date();
	private long euclidian1Sum = 0;
	long algebraSum = 0;
	long spreadsheetSum = 0;

	int nextbigview = 0;// 0 = algebra, 1 = spreadsheet
	long nextrepainttime = 0;// for its repainting length, EV's will repaint

	EuclidianViewWeb euclidianView1 = null;
	AlgebraViewWeb algebraView = null;
	SpreadsheetViewW spreadsheetView = null;

	public TimerSystemW(AppWeb app) {
		application = app;
		euclidianView1 = application.getEuclidianView1();
		if (application.getViewManager().hasAlgebraView())
			algebraView = (AlgebraViewWeb) application.getAlgebraView();
		if (application.getViewManager().hasSpreadsheetView())
			spreadsheetView = (SpreadsheetViewW) application.getViewManager().getSpreadsheetView();
	}

	// one timer for more views, use the minimum FPS of the views for timing
	private Timer repaintTimer = new Timer() {
		public void run() {

			// ideally, the goal is like "E E"A "E E"S "E E"A "E E"S "E E"A "E E"S
			// where the euclidian views use at least as much time as the others

			// repaint the EV every time it comes here
			if (euclidian1Timed) {
				euclidianView1.doRepaint();

				if (nextrepainttime <= 0) {
					if (nextbigview == 0 && algebraTimed) {
						long millis = algebraMillis;
						if (algebraSum > algebraMillis) millis = algebraSum;
						if ((algebraLatest == null) ||
						    (new Date().getTime() - algebraLatest.getTime() - millis > 0)) {
							nextbigview = 1 - nextbigview;
							algebraView.doRepaint();
						}
					} else if (nextbigview == 1 && spreadsheetTimed) {
						long millis = spreadsheetMillis;
						if (spreadsheetSum > spreadsheetMillis) millis = spreadsheetSum;
						if ((spreadsheetLatest == null) ||
						    (new Date().getTime() - spreadsheetLatest.getTime() - millis > 0)) {
							nextbigview = 1 - nextbigview;
							spreadsheetView.doRepaint();
						}
					}
				}
			} else if (algebraTimed && nextbigview == 0) {
				long millis = algebraMillis;
				if (algebraSum > algebraMillis) millis = algebraSum;
				if ((algebraLatest == null) ||
				    (new Date().getTime() - algebraLatest.getTime() - millis > 0)) {
					nextbigview = 1 - nextbigview;
					algebraView.doRepaint();
				}
			} else if (spreadsheetTimed && nextbigview == 1) {
				long millis = spreadsheetMillis;
				if (spreadsheetSum > spreadsheetMillis) millis = spreadsheetSum;
				if ((spreadsheetLatest == null) ||
				    (new Date().getTime() - spreadsheetLatest.getTime() - millis > 0)) {
					nextbigview = 1 - nextbigview;
					spreadsheetView.doRepaint();
				}
			} else if (algebraTimed) {
				long millis = algebraMillis;
				if (algebraSum > algebraMillis) millis = algebraSum;
				if ((algebraLatest == null) ||
				    (new Date().getTime() - algebraLatest.getTime() - millis > 0)) {
					nextbigview = 1 - nextbigview;
					algebraView.doRepaint();
				}
			} else if (spreadsheetTimed) {
				long millis = spreadsheetMillis;
				if (spreadsheetSum > spreadsheetMillis) millis = spreadsheetSum;
				if ((spreadsheetLatest == null) ||
				    (new Date().getTime() - spreadsheetLatest.getTime() - millis > 0)) {
					nextbigview = 1 - nextbigview;
					spreadsheetView.doRepaint();
				}
			}

			// some timed variables were set to false, check if it's necessary
			// to continue here - the timer is executing at commonMillis rate,
			// except if everything is done
			if (repaintTimed())
				repaintTimer.schedule(commonMillis());
		}
	};

	public boolean repaintTimed() {
		return euclidian1Timed || algebraTimed || spreadsheetTimed;
	}

	public int commonMillis() {
		return euclidianMillis;
	}

	public void viewRepaint(View view) {

		if (view == null)
			return;
		else if (view == euclidianView1)
			euclidianRepaint();
		else if (view == algebraView)
			algebraRepaint();
		else if (view == spreadsheetView)
			spreadsheetRepaint();
		else {
			if (view.getViewID() == App.VIEW_EUCLIDIAN) {
				euclidianView1 = application.getEuclidianView1();
				if (view == euclidianView1)
					euclidianRepaint();
			} else if (view.getViewID() == App.VIEW_EUCLIDIAN2) {
				//TODO
			} else if (view.getViewID() == App.VIEW_ALGEBRA) {
				algebraView = (AlgebraViewWeb)application.getAlgebraView();
				if (view == algebraView)
					algebraRepaint();
			} else if (view.getViewID() == App.VIEW_SPREADSHEET) {
				spreadsheetView = (SpreadsheetViewW) application.getViewManager().getSpreadsheetView();
				if (view == spreadsheetView)
					spreadsheetRepaint();
			}
		}
	}

	public void euclidianRepaint() {

		if (euclidianView1 == null)
			return;

		if (repaintTimed()) {
			if (!euclidian1Timed)
				euclidian1Timed = true;
			return;
		}

		long millis = euclidianMillis;
		if (euclidian1Sum > euclidianMillis) millis = euclidian1Sum;

		if (euclidian1Latest != null)
		if ((millis = new Date().getTime() - euclidian1Latest.getTime() - millis) < 0) {
			euclidian1Timed = true;
			repaintTimer.schedule((int)-millis);
			return;
		}
		euclidian1Timed = true;
		repaintTimer.schedule(0);//euclidianView1.doRepaint();
	}

	public void algebraRepaint() {

		if (algebraView == null)
			return;

		if (repaintTimed()) {
			if (!algebraTimed)
				algebraTimed = true;
			return;
		}

		long millis = algebraMillis;
		if (algebraSum > algebraMillis) millis = algebraSum;

		if (algebraLatest != null)
		if (new Date().getTime() - algebraLatest.getTime() - millis < 0) {
			algebraTimed = true;
			repaintTimer.schedule(commonMillis());
			return;
		}
		algebraView.doRepaint();
	}

	public void spreadsheetRepaint() {

		if (spreadsheetView == null)
			return;

		if (repaintTimed()) {
			if (!spreadsheetTimed)
				spreadsheetTimed = true;
			return;
		}

		// do repaint immediately only if everything is finished repainting
		// and spreadsheetMillis ms also expired
		long millis = spreadsheetMillis;
		if (spreadsheetSum > spreadsheetMillis) millis = spreadsheetSum;

		if (spreadsheetLatest != null)
		if (new Date().getTime() - spreadsheetLatest.getTime() - millis < 0) {
			spreadsheetTimed = true;
			repaintTimer.schedule(commonMillis());
			return;
		}
		spreadsheetView.doRepaint();
	}

	public void viewRepainting(View view) {

		if (view == null)
			return;

		if (view == euclidianView1) {
			euclidian1Timed = false;
			euclidian1Latest = new Date();
		} else if (view == algebraView) {
			algebraTimed = false;
			algebraLatest = new Date();
		} else if (view == spreadsheetView) {
			spreadsheetTimed = false;
			spreadsheetLatest = new Date();
		}
	}

	public void viewRepainted(View view) {

		if (view == null)
			return;

		if (view == euclidianView1) {
			euclidian1Sum = new Date().getTime() - euclidian1Latest.getTime();
			nextrepainttime -= euclidian1Sum;
		} else if (view == algebraView)
			nextrepainttime = algebraSum = new Date().getTime() - algebraLatest.getTime();
		else if (view == spreadsheetView)
			nextrepainttime = spreadsheetSum = new Date().getTime() - spreadsheetLatest.getTime();
	}
}

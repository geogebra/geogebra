package geogebra.web.main;

import geogebra.common.kernel.View;
import geogebra.common.main.App;

import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;

import java.util.Date;

import com.google.gwt.user.client.Timer;

public class TimerSystemW {

	public static int euclidianMillis = 34; // = 30 FPS, half of screen Hz
	public static int algebraMillis = 334; // = 3 FPS
	public static int spreadsheetMillis = 334; // = 3 FPS

	AppW application;

	// in the final implementation, these wouldn't be static
	private static boolean euclidian1Timed = false;
	private static boolean algebraTimed = false;
	private static boolean spreadsheetTimed = false;

	// in the final implementation, these wouldn't be static
	private static Date euclidian1Latest = new Date();
	private static Date algebraLatest = new Date();
	private static Date spreadsheetLatest = new Date();
	private long euclidian1Sum = 0;
	private long algebraSum = 0;
	private long spreadsheetSum = 0;

	int nextbigview = 0;// 0 = algebra, 1 = spreadsheet
	long nextrepainttime = 0;// for its repainting length, EV's will repaint

	private EuclidianViewW euclidianView1 = null;
	private AlgebraViewW algebraView = null;
	private SpreadsheetViewW spreadsheetView = null;

	public TimerSystemW(AppW app) {
		application = app;
		euclidianView1 = application.getEuclidianView1();
		if (application.getGuiManager().hasAlgebraView())
			algebraView = (AlgebraViewW)application.getAlgebraView();
		if (application.getGuiManager().hasSpreadsheetView())
			spreadsheetView = application.getGuiManager().getSpreadsheetView();
	}

	// one timer for more views, use the minimum FPS of the views for timing
	private Timer repaintTimer = new Timer() {
		public void run() {

			// ideally, the goal is like "E E"A "E E"S "E E"A "E E"S "E E"A "E E"S
			// where the euclidian views use at least as much time as the others

			// repaint the EV every time it comes here
			if (euclidian1Timed) {
				euclidian1Timed = false;
				euclidianView1.doRepaint();

				if (nextrepainttime <= 0) {
					if (nextbigview == 0 && algebraTimed) {
						long millis = algebraMillis;
						if (algebraSum > algebraMillis) millis = algebraSum;
						if (new Date().getTime() - algebraLatest.getTime() - millis > 0) {
							algebraTimed = false;
							nextbigview = 1 - nextbigview;
							algebraView.doRepaint();
						}
					} else if (nextbigview == 1 && spreadsheetTimed) {
						long millis = spreadsheetMillis;
						if (spreadsheetSum > spreadsheetMillis) millis = spreadsheetSum;
						if (new Date().getTime() - spreadsheetLatest.getTime() - millis > 0) {
							spreadsheetTimed = false;
							nextbigview = 1 - nextbigview;
							spreadsheetView.doRepaint();
						}
					}
				}
			} else if (algebraTimed && nextbigview == 0) {
				long millis = algebraMillis;
				if (algebraSum > algebraMillis) millis = algebraSum;
				if (new Date().getTime() - algebraLatest.getTime() - millis > 0) {
					algebraTimed = false;
					nextbigview = 1 - nextbigview;
					algebraView.doRepaint();
				}
			} else if (spreadsheetTimed && nextbigview == 1) {
				long millis = spreadsheetMillis;
				if (spreadsheetSum > spreadsheetMillis) millis = spreadsheetSum;
				if (new Date().getTime() - spreadsheetLatest.getTime() - millis > 0) {
					spreadsheetTimed = false;
					nextbigview = 1 - nextbigview;
					spreadsheetView.doRepaint();
				}
			} else if (algebraTimed) {
				long millis = algebraMillis;
				if (algebraSum > algebraMillis) millis = algebraSum;
				if (new Date().getTime() - algebraLatest.getTime() - millis > 0) {
					algebraTimed = false;
					nextbigview = 1 - nextbigview;
					algebraView.doRepaint();
				}
			} else if (spreadsheetTimed) {
				long millis = spreadsheetMillis;
				if (spreadsheetSum > spreadsheetMillis) millis = spreadsheetSum;
				if (new Date().getTime() - spreadsheetLatest.getTime() - millis > 0) {
					spreadsheetTimed = false;
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
				;//TODO
			} else if (view.getViewID() == App.VIEW_ALGEBRA) {
				algebraView = (AlgebraViewW)application.getAlgebraView();
				if (view == algebraView)
					algebraRepaint();
			} else if (view.getViewID() == App.VIEW_SPREADSHEET) {
				spreadsheetView = application.getGuiManager().getSpreadsheetView();
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
		if ((millis = new Date().getTime() - euclidian1Latest.getTime() - millis) < 0) {
			euclidian1Timed = true;
			repaintTimer.schedule((int)-millis);
			return;
		}

		euclidianView1.doRepaint();
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
		if (new Date().getTime() - algebraLatest.getTime() - millis < 0) {
			algebraTimed = true;
			repaintTimer.schedule(commonMillis());
		} else {
			algebraView.doRepaint();
		}
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
		if (new Date().getTime() - spreadsheetLatest.getTime() - millis < 0) {
			spreadsheetTimed = true;
			repaintTimer.schedule(commonMillis());
		} else {
			spreadsheetView.doRepaint();
		}
	}

	public void viewRepainting(View view) {

		if (view == null)
			return;

		if (view == euclidianView1)
			euclidian1Latest = new Date();
		else if (view == algebraView)
			algebraLatest = new Date();
		else if (view == spreadsheetView)
			spreadsheetLatest = new Date();
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

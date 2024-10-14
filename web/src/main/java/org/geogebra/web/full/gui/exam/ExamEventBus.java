package org.geogebra.web.full.gui.exam;

import java.util.ArrayList;

import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;

public class ExamEventBus implements ExamListener {
	private ArrayList<ExamListener> childListeners = new ArrayList<>();

	public void add(ExamListener listener) {
		childListeners.add(listener);
	}

	@Override
	public void examStateChanged(ExamState newState) {
		childListeners.forEach(listener -> listener.examStateChanged(newState));
	}
}

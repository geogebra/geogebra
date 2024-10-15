package org.geogebra.web.full.gui.exam;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;

public final class ExamEventBus implements ExamListener {
	private final List<ExamListener> childListeners = new ArrayList<>();

	public void add(ExamListener listener) {
		childListeners.add(listener);
	}

	@Override
	public void examStateChanged(ExamState newState) {
		childListeners.forEach(listener -> listener.examStateChanged(newState));
	}
}

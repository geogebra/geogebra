package org.geogebra.web.solver;

import com.himamis.retex.editor.share.event.MathFieldListener;

public class SolverMathFieldListener implements MathFieldListener {

	private Solver solver;

	SolverMathFieldListener(Solver solver) {
		this.solver = solver;
	}

	@Override
	public void onEnter() {
		solver.hideKeyboardAndCompute();
	}

	@Override
	public void onKeyTyped(String key) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		return false;
	}

	@Override
	public void onInsertString() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onEscape() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// TODO Auto-generated method stub
	}
}

package org.geogebra.common.main;

public class DefaultInitialViewState implements InitialViewState {
	@Override
	public void store() {
		// nothing to do
	}

	@Override
	public boolean hasAlgebra() {
		return true;
	}

	@Override
	public boolean hasCas() {
		return true;
	}

	@Override
	public boolean hasTableOfValues() {
		return true;
	}

	@Override
	public boolean hasSpreadsheet() {
		return true;
	}

	@Override
	public boolean hasConstructionProtocol() {
		return true;
	}

	@Override
	public boolean hasProbability() {
		return true;
	}

	@Override
	public boolean hasProperties() {
		return true;
	}

	@Override
	public boolean hasGraphicsView1() {
		return true;
	}

	@Override
	public boolean hasGraphicsView2() {
		return true;
	}
}

package org.geogebra.common.gui.view.algebra;

public interface StepGuiBuilder {

	public void addPlainRow(String equations);

	public void addLatexRow(String equations);

	public void show();

	public void startGroup();

	public void endGroup();

	public void startDefault();

	public void switchToDetailed();

	public void endDetailed();
}

package org.geogebra.common.util;

import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;

public interface TextField {

	void setColumns(int fieldWidth);

	void addActionListener(StatisticsCalculator statisticsCalculator);

	void addFocusListener(StatisticsCalculator statisticsCalculator);

	void setText(String format);

	String getText();

	void removeActionListener(StatisticsCalculator statisticsCalculator);

	void setVisible(boolean b);

	void setEditable(boolean b);

}

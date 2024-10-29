package org.geogebra.common.kernel.commands;

import static org.geogebra.common.kernel.commands.CommandsConstants.TABLE_CAS;
import static org.geogebra.test.commands.AlgebraTestHelper.shouldFail;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.App;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.Test;

public class CommandsValidationTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void testArgumentTypeValidation() {
		for (Commands command: Commands.values()) {
			List<Integer> signature = CommandSignatures.getSignature(command.name(), getApp());
			if (signature != null && command.getTable() != TABLE_CAS
					&& !acceptsAnyArgType(command)) {
				checkArgumentTypeValidation(command.name(), signature);
			}
		}
	}

	@Test
	public void testArgumentNumberValidation() {
		for (Commands command: Commands.values()) {
			List<Integer> signature = CommandSignatures.getSignature(command.name(), getApp());
			if (signature != null && command.getTable() != TABLE_CAS
				&& command != Commands.PenStroke
				&& command != Commands.SelectObjects
				&& command != Commands.StartAnimation) {
				checkArgumentNumberValidation(command.name(), signature);
			}
		}
	}

	@Test
	public void testCasTableValidation() {
		for (Commands command: Commands.values()) {
			if (command.getTable() == TABLE_CAS && command != Commands.SolveQuartic) {
				shouldFail(command.name() + "()", "available only in the CAS", getApp());
			}
		}
	}

	private void checkArgumentTypeValidation(String cmdName,
			List<Integer> signature) {
		App app = getApp();
		for (int args : signature) {
			StringBuilder withArgs = new StringBuilder(cmdName).append("(");
			for (int i = 0; i < args - 1; i++) {
				withArgs.append("space,");
			}
			withArgs.append("space)");
			if (args > 0) {
				shouldFail(withArgs.toString(), "arg", "IllegalArgument:", app);
			}
		}
	}

	private boolean acceptsAnyArgType(Commands cmdName) {
		return Arrays.asList(Commands.Delete,
				Commands.ConstructionStep,
				Commands.Text,
				Commands.LaTeX,
				Commands.RunClickScript,
				Commands.RunUpdateScript,
				Commands.Defined,
				Commands.AreEqual,
				Commands.AreCongruent,
				Commands.Textfield,
				Commands.GetTime,
				Commands.CopyFreeObject,
				Commands.Name,
				Commands.Relation,
				Commands.SelectObjects,
				Commands.Dot, Commands.Cross,
				Commands.SetConstructionStep,
				Commands.TableText, Commands.SetValue).contains(cmdName);
	}

	private void checkArgumentNumberValidation(String cmdName,
			List<Integer> signature) {
		if (!signature.contains(0)) {
			shouldFail(cmdName + "()", "Illegal number of arguments: 0",
					"IllegalArgumentNumber", getApp());
		} else {
			shouldFail(cmdName + "(space,space,space,space,space,space,space,space,space)",
					"Illegal number of arguments: 9", "IllegalArgumentNumber", getApp());
		}
	}
}

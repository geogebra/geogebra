package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserMenuItemGroup extends AbstractMenuItemGroup {

	private LogInOperation logInOperation;
	private List<MenuItem> loggedInItems;

	UserMenuItemGroup(LogInOperation logInOperation) {
		this.logInOperation = logInOperation;
		this.loggedInItems = createLoggedInItems();
	}

	private List<MenuItem> createLoggedInItems() {
		MenuItem signIn = new ActionableItemImpl(Icon.EXIT_TO_APP, "SignIn", Action.SIGN_IN);
		return Collections.singletonList(signIn);
	}

	private List<MenuItem> createLoggedOutItems() {
		GeoGebraTubeUser user = logInOperation.getModel().getLoggedInUser();
		MenuItem userItem = new ActionableItemImpl(Icon.USER_ICON, user.getUserName(), Action.NONE);
		MenuItem signOut = new ActionableItemImpl(Icon.SIGN_OUT, "SignOut", Action.SIGN_OUT);
		return Arrays.asList(userItem, signOut);
	}

	@Override
	public List<MenuItem> getMenuItems() {
		return logInOperation.isLoggedIn() ? loggedInItems : createLoggedOutItems();
	}
}

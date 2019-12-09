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

	private List<MenuItem> items;

	UserMenuItemGroup(LogInOperation logInOperation) {
		this.items = logInOperation.isLoggedIn() ? createLoggedInItems() : createLoggedOutItems(logInOperation);
	}

	private List<MenuItem> createLoggedInItems() {
		MenuItem signIn = new ActionableItemImpl(Icon.EXIT_TO_APP, "SignIn", Action.SIGN_IN);
		return Collections.singletonList(signIn);
	}

	private List<MenuItem> createLoggedOutItems(LogInOperation logInOperation) {
		GeoGebraTubeUser user = logInOperation.getModel().getLoggedInUser();
		MenuItem userItem = new ActionableItemImpl(Icon.USER_ICON, user.getUserName(), Action.OPEN_PROFILE_PAGE);
		MenuItem signOut = new ActionableItemImpl(Icon.SIGN_OUT, "SignOut", Action.SIGN_OUT);
		return Arrays.asList(userItem, signOut);
	}

	@Override
	public List<MenuItem> getMenuItems() {
		return items;
	}
}

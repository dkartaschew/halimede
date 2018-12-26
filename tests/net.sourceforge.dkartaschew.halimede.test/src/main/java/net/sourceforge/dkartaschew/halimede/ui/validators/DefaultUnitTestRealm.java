/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2019 Darran Kartaschew 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 */

package net.sourceforge.dkartaschew.halimede.ui.validators;

import org.eclipse.core.databinding.observable.Realm;

/**
 * Simple realm implementation that will set itself as default when constructed. Invoke {@link #dispose()} to remove the
 * realm from being the default.
 */
public class DefaultUnitTestRealm extends Realm {
	private Realm previousRealm;

	public DefaultUnitTestRealm() {
		previousRealm = super.setDefault(this);
	}

	@Override
	public boolean isCurrent() {
		return true;
	}

	@Override
	protected void syncExec(Runnable runnable) {
		runnable.run();
	}

	@Override
	public void asyncExec(Runnable runnable) {
		runnable.run();
	}

	/**
	 * Removes the realm from being the current and sets the previous realm to the default.
	 */
	public void dispose() {
		if (getDefault() == this) {
			setDefault(previousRealm);
		}
	}
}

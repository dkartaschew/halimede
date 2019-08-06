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
package net.sourceforge.dkartaschew.halimede;

import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestListAllAlgorithms {

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
	}
	
	@Test
	public void listAllProvidersAlgorithms() {
		try {
			java.security.Provider p[] = Security.getProviders();
			for (int i = 0; i < p.length; i++) {
				System.out.println(p[i]);
				List<String> items = new ArrayList<>();
				for (Enumeration<Object> e = p[i].keys(); e.hasMoreElements();) {
					items.add(e.nextElement().toString());
				}
				Collections.sort(items);
				for(String s : items) {
					System.out.println("\t" + s);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}

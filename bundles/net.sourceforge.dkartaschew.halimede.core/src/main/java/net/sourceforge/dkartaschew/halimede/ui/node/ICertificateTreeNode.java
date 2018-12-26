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

package net.sourceforge.dkartaschew.halimede.ui.node;

public interface ICertificateTreeNode {

	/**
	 * Get the Image to use for the Tree Node
	 * 
	 * @return The Image to use for the Tree Node.
	 */
	String getImage();

	/**
	 * Get the String description for the Tree Node
	 * 
	 * @return The description to display in the Tree Node.
	 */
	String getDescription();

	/**
	 * Get the items for this node.
	 * 
	 * @return The items for this node.
	 */
	Object[] getItems();

	/**
	 * Get the element type of this node.
	 * 
	 * @return The element type.
	 */
	ElementType getType();
}

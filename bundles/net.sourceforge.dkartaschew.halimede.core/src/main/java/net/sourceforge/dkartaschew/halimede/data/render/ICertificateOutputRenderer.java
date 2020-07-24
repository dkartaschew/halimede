/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.data.render;

/**
 * Generalised Interface for certificate detail output streams.
 */
public interface ICertificateOutputRenderer {

	/**
	 * Add a bolded header line to the output stream.
	 * 
	 * @param value The text to add.
	 */
	void addHeaderLine(String value);

	/**
	 * Add a bolded header line to the output stream.
	 * 
	 * @param parent The composite to add to.
	 * @param value The text to add.
	 */
	void addEmptyLine();

	/**
	 * Add a line to the output stream. <br>
	 * Value is rendered with normal font.
	 * 
	 * @param value The String to add
	 */
	void addContentLine(String value);
	
	/**
	 * Add a line to the output stream. <br>
	 * Value is rendered with normal font.
	 * 
	 * @param key The key field.
	 * @param value The text to add.
	 */
	void addContentLine(String key, String value);

	/**
	 * Add a line to the output stream.
	 * 
	 * @param key The key field.
	 * @param value The text to add.
	 * @param monospace TRUE if the value is to be rendered in monospace
	 */
	void addContentLine(String key, String value, boolean monospace);

	/**
	 * Add a horizontal line.
	 */
	void addHorizontalLine();
	
	/**
	 * Finalise the render process.
	 */
	void finaliseRender();


}

/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2018 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.ui.data;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.ui.composite.IColumnComparator;

public class CADetailsComparator extends ViewerComparator implements Comparator<Object> {

	/**
	 * Column ID for default sorting.
	 */
	public final static int DEFAULT_SORT = -1;

	/**
	 * Collection of current providers.
	 */
	private IColumnComparator<IssuedCertificateProperties> icProvider = new IssuedCertificateComparator();
	private IColumnComparator<CertificateKeyPairTemplate> templateProvider = new TemplateColumnComparator();
	private IColumnComparator<CertificateRequestProperties> csrProvider = new CSRColumnComparator();
	private IColumnComparator<CRLProperties> crlProvider = new CRLColumnComparator();

	private int propertyIndex = 0;
	private static final int SORTED = 1;
	private static final int REVERSED = -1;
	private int direction = SORTED;

	/**
	 * Get the sort direction (SWT.DOWN or SWT.UP)
	 * 
	 * @return The sort direction.
	 */
	public int getDirection() {
		return direction == SORTED ? SWT.DOWN : SWT.UP;
	}

	/**
	 * Reset the direction for sorting (SWT.DOWN or SWT.UP)
	 * 
	 * @param direction The direction of sort.
	 */
	public void setDirection(int direction) {
		this.direction = direction != SWT.UP ? SORTED : REVERSED;
	}

	/**
	 * Get the current sort column
	 * 
	 * @return The current sort column.
	 */
	public int getColumn() {
		return propertyIndex;
	}

	/**
	 * Set the column to sort
	 * 
	 * @param column The column ID to sort
	 */
	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = -direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = SORTED;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return compare(propertyIndex, e1, e2);
	}

	@Override
	public int compare(Object o1, Object o2) {
		return compare(DEFAULT_SORT, o1, o2);
	}

	/**
	 * Compare the two objects based on the column ID.
	 * 
	 * @param column The column
	 * @param e1 The first object
	 * @param e2 The second object
	 * @return The comparison.
	 */
	private int compare(int column, Object e1, Object e2) {
		int rc = 0;
		if (e1 instanceof IssuedCertificateProperties) {
			rc = icProvider.compare(column, //
					(IssuedCertificateProperties) e1, //
					(IssuedCertificateProperties) e2);
		}
		if (e1 instanceof CertificateKeyPairTemplate) {
			rc = templateProvider.compare(column, //
					(CertificateKeyPairTemplate) e1, //
					(CertificateKeyPairTemplate) e2);
		}

		if (e1 instanceof CertificateRequestProperties) {
			rc = csrProvider.compare(column, //
					(CertificateRequestProperties) e1, //
					(CertificateRequestProperties) e2);
		}

		if (e1 instanceof CRLProperties) {
			rc = crlProvider.compare(column, //
					(CRLProperties) e1, //
					(CRLProperties) e2);
		}
		// If reversed order, flip the direction
		if (column != -1 && direction == REVERSED) {
			rc = -rc;
		}
		return rc;
	}

}

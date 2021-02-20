/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2021 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.backup;

/**
 * Single entry for backup manifest
 */
public class BackupManifestEntry {

	/**
	 * The entry filename
	 */
	private String filename;
	/**
	 * The size of the file
	 */
	private long size;
	/**
	 * The SHA512 digest.
	 */
	private String sha512;

	public BackupManifestEntry() {
	}

	/**
	 * Create a new entry
	 * 
	 * @param filename The filename
	 * @param length The length of the file
	 * @param sha512 The SHA512 digest.
	 */
	public BackupManifestEntry(String filename, int length, String sha512) {
		this.filename = filename;
		this.size = length;
		this.sha512 = sha512;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSha512() {
		return sha512;
	}

	public void setSha512(String sha512) {
		this.sha512 = sha512;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + ((sha512 == null) ? 0 : sha512.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BackupManifestEntry other = (BackupManifestEntry) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (sha512 == null) {
			if (other.sha512 != null)
				return false;
		} else if (!sha512.equals(other.sha512))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BackupManifestEntry [filename=" + filename + ", size=" + size + ", sha512=" + sha512 + "]";
	}

}

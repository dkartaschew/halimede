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

package net.sourceforge.dkartaschew.halimede.backup;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sourceforge.dkartaschew.halimede.data.persistence.UUIDPersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.data.persistence.ZonedDateTimePersistenceDelegate;

/**
 * Backup Manifest
 * <p>
 * Simple manifest of all items contained in backup set.
 */
public class BackupManifest {

	/**
	 * Create a new instance of the manifest from the input stream
	 * 
	 * @param stream The input stream
	 * @return An instance of the Backup Manifest
	 */
	public static BackupManifest read(InputStream stream) {
		try (XMLDecoder decoder = new XMLDecoder(stream)) {
			return (BackupManifest) decoder.readObject();
		}
	}

	/**
	 * Write the given manifest to the outputstream
	 * 
	 * @param stream The output stream to write to.
	 * @param manifest The manifest to write.
	 */
	public static void write(OutputStream stream, BackupManifest manifest) {
		try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(stream))) {
			// Add a specialised persistence delegate for UUIDs, ZonedDateTime, etc.
			encoder.setPersistenceDelegate(UUID.class, new UUIDPersistenceDelegate());
			encoder.setPersistenceDelegate(ZonedDateTime.class, new ZonedDateTimePersistenceDelegate());
			encoder.writeObject(manifest);
		}
	}

	/**
	 * UUID of the CA
	 */
	private UUID uuid;
	/**
	 * Basic CA Description
	 */
	private String description;
	/**
	 * Basckup Set create time.
	 */
	private ZonedDateTime creationDate;
	/**
	 * Collection of entries.
	 */
	private List<BackupManifestEntry> entries;

	public BackupManifest() {
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<BackupManifestEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<BackupManifestEntry> entries) {
		this.entries = entries;
	}

	public void addEntry(BackupManifestEntry entry) {
		if (this.entries == null) {
			this.entries = new ArrayList<>();
		}
		this.entries.add(entry);
	}

	public ZonedDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(ZonedDateTime creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		BackupManifest other = (BackupManifest) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BackupManifest [uuid=" + uuid + //
				", description=" + description + //
				", creationDate=" + creationDate + //
				", entries=" + entries + "]";
	}

}

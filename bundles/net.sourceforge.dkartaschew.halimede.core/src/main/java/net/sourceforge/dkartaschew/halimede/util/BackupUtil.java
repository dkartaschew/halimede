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

package net.sourceforge.dkartaschew.halimede.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;

import net.sourceforge.dkartaschew.halimede.backup.BackupManifest;
import net.sourceforge.dkartaschew.halimede.backup.BackupManifestEntry;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;

/**
 * Backup and Restore Utility Functions.
 */
public class BackupUtil {

	public final static String MANIFEST = "manifest.xml";

	/**
	 * Create a backup of the Certificate Authority
	 * 
	 * @param ca The Certificate Authority
	 * @param filename The filename to write to.
	 * @param listener The activity listener. (may be NULL).
	 * @throws IOException If the operation fails.
	 */
	public static void backup(CertificateAuthority ca, Path filename, IProgressMonitor listener) throws IOException {
		Objects.requireNonNull(ca, "Certificate Authority not defined");
		Objects.requireNonNull(filename, "Target filename not defined");
		if (Files.exists(filename)) {
			Files.delete(filename);
		}
		ca.getActivityLogger().log(Level.INFO, "Backup CA to {0}", filename);
		final Path basePath = ca.getBasePath().toAbsolutePath();
		final List<Path> entries = new ArrayList<>();
		Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (!attrs.isDirectory()) {
					entries.add(file);
				}
				return FileVisitResult.CONTINUE;
			}
		});

		boolean cancelActivity = false;

		BackupManifest manifest = new BackupManifest();
		manifest.setCreationDate(ZonedDateTime.now());
		manifest.setUuid(ca.getCertificateAuthorityID());
		manifest.setDescription(ca.getDescription());

		if (listener != null) {
			listener.beginTask("Backup of '" + ca.getDescription() + "'", entries.size());
		}

		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(filename.toFile()))) {
			zip.setComment(ca.getCertificateAuthorityID().toString());
			// Add our entrie
			for (Path file : entries) {

				String entry = file.subpath(basePath.getNameCount(), file.getNameCount()).toString();
				if (entry.startsWith(File.separator)) {
					entry = entry.substring(1);
				}
				// Ensure entry uses '/' as path - 4.4.17.1 of the zip spec.
				entry = entry.replace('\\', '/');
				entry = manifest.getDescription() + "/" + entry;

				if (listener != null) {
					listener.subTask(entry);
					cancelActivity = listener.isCanceled();
				}
				if (cancelActivity) {
					break;
				}
				ZipEntry e = new ZipEntry(entry);
				try {
					zip.putNextEntry(e);
					byte[] data = Files.readAllBytes(file);
					zip.write(data, 0, data.length);

					manifest.addEntry(new BackupManifestEntry(entry, //
							data.length, //
							Strings.toHexString(Digest.sha512(data))));
				} finally {
					zip.closeEntry();
				}
				if (listener != null) {
					listener.worked(1);
				}
			}
			// Add in the manifest.
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			BackupManifest.write(stream, manifest);

			ZipEntry e = new ZipEntry(MANIFEST);
			try {
				zip.putNextEntry(e);
				byte[] data = stream.toByteArray();
				zip.write(data, 0, data.length);
			} finally {
				zip.closeEntry();
			}
		} finally {
			if (listener != null) {
				listener.subTask("Complete");
				listener.done();
			}
			if (cancelActivity) {
				Files.delete(filename);
			}
		}
	}

	/**
	 * Restore the given backup file to the given destination
	 * 
	 * @param filename The filename of the backup file
	 * @param destination The destination location
	 * @param listener The activity listener. (may be NULL).
	 * @return The base location of the CA. (Will be NULL is operation cancelled or failed).
	 * @throws IOException If reading the backup file fails, or restoring operation fails.
	 */
	public static Path restore(Path filename, Path destination, IProgressMonitor listener) throws IOException {
		Objects.requireNonNull(filename, "Backup filename not defined");
		Objects.requireNonNull(destination, "Destination Location not defined");
		if (!Files.exists(filename) || !Files.isReadable(filename) || !Files.isRegularFile(filename)) {
			throw new IOException("Backup file '" + filename.toString() + "' does not exist or is not readable");
		}
		if (!Files.isDirectory(destination) || !Files.isWritable(destination)) {
			throw new IOException("Destination Location '" + filename.toString()
					+ "' does not exist, is not a Directory or is not writable");
		}
		boolean cancelActivity = false;
		Path basePath = null;
		try {
			destination = destination.toAbsolutePath().normalize();
		} catch (IOError e) {
			throw new IOException("Destination path normalisation failed.", e);
		}

		if (listener != null) {
			listener.setTaskName("Start Restoration");
		}

		// Open the zip container.
		try (ZipFile zip = new ZipFile(filename.toFile())) {
			// Check for manifest
			ZipEntry zipEntry = zip.getEntry(MANIFEST);
			if (zipEntry == null) {
				throw new IOException("File does not appear to be a Halimede Backup file. Missing backup manifest.");
			}
			// Read in the manifest and compare the CA UUID to the zip comment.
			BackupManifest manifest = BackupManifest.read(zip.getInputStream(zipEntry));
			UUID uuid = null;
			try {
				uuid = UUID.fromString(zip.getComment());
			} catch (NullPointerException | IllegalArgumentException e) {
				throw new IOException(
						"File does not appear to be a Halimede Backup file. " + "Missing UUID identifier.", e);
			}
			if (manifest.getUuid() == null || !manifest.getUuid().equals(uuid)) {
				throw new IOException("File does not appear to be a Halimede Backup file. "
						+ "Certificate Authority UUID doesn't match backup file UUID.");
			}
			// Start extraction
			try {
				basePath = destination.resolve(manifest.getDescription()).toAbsolutePath().normalize();
			} catch (NullPointerException | InvalidPathException | IOError e) {
				throw new IOException("Invalid entry in backup manifest found", e);
			}
			if (!basePath.startsWith(destination)) {
				throw new IOException("Invalid entry in backup manifest found");
			}
			if (Files.exists(basePath)) {
				throw new IOException("Restore function will overwrite existing files, aborting");
			}
			if (manifest.getEntries().size() < 2) {
				// We MUST have at least 2 entries...
				cancelActivity = true;
				throw new IOException("Backup Manifest appears malformed?");
			}
			if (listener != null) {
				listener.beginTask("Restoring '" + manifest.getDescription() + "'", manifest.getEntries().size());
			}
			for (BackupManifestEntry e : manifest.getEntries()) {
				// Notification listener.
				if (listener != null) {
					listener.subTask(e.getFilename());
					cancelActivity = listener.isCanceled();
				}
				if (cancelActivity) {
					break;
				}

				/*
				 * Get the target location. Ensure the target location is strictly in the destination folder.
				 */
				String entry = e.getFilename();
				if (entry == null || entry.trim().isEmpty()) {
					cancelActivity = true;
					throw new IOException("Invalid entry in backup file found");
				}
				Path target = destination.resolve(entry).toAbsolutePath().normalize();
				if (!target.startsWith(basePath)) {
					cancelActivity = true;
					throw new IOException("Invalid entry in backup file found");
				}
				zipEntry = zip.getEntry(entry);
				if (zipEntry == null) {
					cancelActivity = true;
					throw new IOException("Invalid entry in backup file found. Backup Container is missing entry '"
							+ e.getFilename() + "'");
				}
				if (zipEntry.getSize() != e.getSize()) {
					cancelActivity = true;
					throw new IOException("Invalid entry in backup file found. File entry size is different for entry '"
							+ e.getFilename() + "'");
				}
				Path parent = target.getParent();
				if (parent == null) {
					throw new IOException("Invalid entry in backup file found. Missing parent information");
				}
				if (!Files.exists(parent)) {
					Files.createDirectories(parent);
				}

				/*
				 * Extract the contents of file, verify the contents, and then write out...
				 */
				try (FileOutputStream out = new FileOutputStream(target.toFile(), false);
						InputStream in = zip.getInputStream(zipEntry)) {
					byte[] data = new byte[(int) e.getSize()];
					int read = read(in, data);
					if (read != e.getSize()) {
						cancelActivity = true;
						throw new IOException("Entry '" + e.getFilename() + "' not read fully?");
					}
					String sha512 = Strings.toHexString(Digest.sha512(data));
					if (!sha512.equalsIgnoreCase(e.getSha512())) {
						cancelActivity = true;
						throw new IOException("Entry '" + e.getFilename() + "' fails digest verification?");
					}
					out.write(data);
					out.flush();
				}
				if (listener != null) {
					listener.worked(1);
				}

			}
		} finally {
			if (listener != null) {
				listener.subTask("Complete");
				listener.done();
			}

			if (cancelActivity) {
				// Cleanup...
				if (basePath != null && Files.exists(basePath)) {
					Files.walk(basePath)//
							.sorted(Comparator.reverseOrder())//
							.map(Path::toFile)//
							.forEach(File::delete);
					basePath = null;
				}
			}
		}
		return basePath;
	}

	/**
	 * Read from the input stream filling the buffer until EOF.
	 * 
	 * @param in The Inputstream to read from.
	 * @param data The data buffer to read into.
	 * @return The number of bytes read.
	 * @throws IOException If reading from the stream fails.
	 */
	private static int read(InputStream in, byte[] data) throws IOException {
		int toRead = data.length;
		int offset = 0;
		int read = 0;
		while (toRead > 0) {
			int r = in.read(data, offset, data.length - offset);
			if (r == -1) {
				// EOF
				return read;
			}
			offset += r;
			toRead -= r;
			read += r;
		}
		return read;
	}

}

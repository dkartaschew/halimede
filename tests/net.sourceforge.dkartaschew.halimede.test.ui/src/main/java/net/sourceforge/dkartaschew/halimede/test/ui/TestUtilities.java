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

package net.sourceforge.dkartaschew.halimede.test.ui;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.UUID;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.osgi.framework.BundleContext;

import net.sourceforge.dkartaschew.halimede.e4rcp.Activator;

public class TestUtilities {

	public final static String TMP = System.getProperty("java.io.tmpdir");

	public final static int TEST_MAX_KEY_LENGTH = 2048;

	public static class NullOutputStream extends OutputStream {
		@Override
		public void write(byte[] buf) throws IOException {
			// do nothing
		}

		@Override
		public void write(byte[] buf, int off, int len) throws IOException {
			// do nothing
		}

		@Override
		public void write(int b) throws IOException {
			// do nothing
		}
	}

	public static void cleanup(Path path) throws IOException {
		if (Files.exists(path)) {
			System.out.println("Deleting:");
			Files.walk(path)//
					.sorted(Comparator.reverseOrder())//
					.map(Path::toFile)//
					.peek(System.out::println) //
					.forEach(File::delete);
		}
	}

	public static void delete(Path filename) throws IOException {
		System.out.println("Deleting :" + filename);
		Files.deleteIfExists(filename);
	}

	public static Path getFile(String filename) {
		ClassLoader classLoader = new TestUtilities().getClass().getClassLoader();
		URL url = classLoader.getResource(filename);
		// Bundle reference.
		if (url.getProtocol().startsWith("bundle")) {
			try {
				url = org.eclipse.core.runtime.FileLocator.toFileURL(url);
			} catch (IOException e) {
				fail();
			}
		}
		// Direct file reference
		if (url.getProtocol().equals("file")) {
			File file = new File(URI.create(url.toString().replaceAll(" ", "%20")));
			System.out.println("Referenced File: " + file);
			assertTrue(file.exists());
			assertTrue(file.isFile());
			assertTrue(file.canRead());
			return file.toPath();
		}

		return null;
	}

	public static Path getFolder(String filename) {
		ClassLoader classLoader = new TestUtilities().getClass().getClassLoader();
		URL url = classLoader.getResource(filename);
		// Bundle reference.
		if (url.getProtocol().startsWith("bundle")) {
			try {
				url = org.eclipse.core.runtime.FileLocator.toFileURL(url);
			} catch (IOException e) {
				fail();
			}
		}
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			fail();
		}
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		assertTrue(file.canRead());
		return file.toPath();
	}

	public static class CopyDir extends SimpleFileVisitor<Path> {
		private Path sourceDir;
		private Path targetDir;

		public CopyDir(Path sourceDir, Path targetDir) {
			this.sourceDir = sourceDir;
			this.targetDir = targetDir;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
			try {
				Path targetFile = targetDir.resolve(sourceDir.relativize(file));
				Files.copy(file, targetFile);
			} catch (IOException ex) {
				System.err.println(ex);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {
			try {
				Path newDir = targetDir.resolve(sourceDir.relativize(dir));
				Files.createDirectory(newDir);
			} catch (IOException ex) {
				System.err.println(ex);
			}
			return FileVisitResult.CONTINUE;
		}
	}

	public static void copyFolder(Path sourceDir, Path targetDir) throws IOException {
		Files.walkFileTree(sourceDir, new CopyDir(sourceDir, targetDir));
	}
	
	public static IEclipseContext getEclipseContext() {
		BundleContext bndCtx = Activator.getDefault().getContext();
		IEclipseContext rootContext = EclipseContextFactory.getServiceContext(bndCtx);
		IEclipseContext app = rootContext.get(IWorkbench.class).getApplication().getContext();
		return app;
	}
	
	public static String createBasicCA(SWTBot bot, String location) {
		final String caName = "Test-" + UUID.randomUUID().toString();

		SWTBotTree primaryTree = bot.tree();
		int rows = primaryTree.rowCount();

		primaryTree.contextMenu("Create a New Certificate Authority").click();

		bot.waitUntil(shellIsActive("Create Certificate Authority"));
		bot.textWithLabel("Description:").setText(caName);
		bot.textWithLabel("Location:").setText(location);
		bot.textWithLabel("Subject:").setText("CN=" + caName.replace("-", ""));
		bot.textWithLabel("Passphrase:").setText("Password");
		bot.textWithLabel("Confirmation:").setText("Password");
		bot.button("Create").click();

		bot.waitUntilWidgetAppears(Conditions.treeHasRows(primaryTree, rows + 1));

		primaryTree.getTreeItem(caName).expand();
		return caName;
	}

}

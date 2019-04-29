package org.ripacola.lang.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtext.diagnostics.Severity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.osgi.framework.Bundle;
import org.ripacola.lang.RipacolaParserHelper;
import org.ripacola.lang.RipacolaParserHelper.RipacolaParseResult;

@RunWith(Parameterized.class)
public class RipFilesParsingTest {

	final URL sourceURL;

	public RipFilesParsingTest(final URL sourceURL) {
		this.sourceURL = sourceURL;
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner scanner = new java.util.Scanner(is);
		java.util.Scanner s = scanner.useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		scanner.close();
		return result;
	}

	/**
	 * @throws IOException
	 */
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() throws IOException {
		final List<Object[]> arrayList = new ArrayList<Object[]>();

		final Bundle bundle = Platform.getBundle("org.ripacola.tests");
		if (bundle != null) {
			Collections.list(bundle.getEntryPaths("/resources")).stream().filter(s -> s.endsWith(".rip")).forEach(s -> {
				final URL find = FileLocator.find(bundle, new Path(s));
				if (find != null) {
					arrayList.add(new Object[] { find });
				}
			});
		} else {
			// try to get files from local file tree
			final File file = new File("resources").getAbsoluteFile();

			Files.walkFileTree(file.toPath(), new SimpleFileVisitor<java.nio.file.Path>() {
				@Override
				public FileVisitResult visitFile(java.nio.file.Path path, BasicFileAttributes attrs) throws IOException {
					if (path.toString().endsWith(".rip")) {
						arrayList.add(new Object[] { path.toUri().toURL() });
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}

		return arrayList;
	}

	@Test
	public void testParse() throws IOException {
		RipacolaParseResult parse = RipacolaParserHelper.parse(sourceURL);
		if (parse.error) {
			System.out.println(sourceURL.toString());
			parse.issues.stream().filter(i -> i.getSeverity() == Severity.ERROR).forEach(i -> System.out.println(i));
		}
		Assert.assertFalse(parse.error);
	}
}

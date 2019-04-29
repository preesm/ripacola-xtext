package org.ripacola.lang;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.ripacola.lang.ripacola.Program;

import com.google.inject.Injector;

public class RipacolaParserHelper {

	// forbid instantiation
	private RipacolaParserHelper() {
	}

	public static final class RipacolaParseResult {
		public final List<Issue> issues;
		public final Program ast;
		public final boolean error;

		public RipacolaParseResult(final Resource resource, final List<Issue> issues) {
			this.issues = issues;
			this.error = issues.stream().anyMatch(i -> i.getSeverity() == Severity.ERROR);
			if (this.error) {
				this.ast = null;
			} else {
				this.ast = (Program) resource.getContents().get(0);
			}
		}
	}

	public static final RipacolaParseResult parse(final File sourceFile) {
		return parse(URI.createURI(sourceFile.toURI().toString()));
	}

	public static final RipacolaParseResult parse(final URL sourceURL) {
		try {
			return parse(URI.createURI(sourceURL.toURI().toString()));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Could not create URI from given URL '" + sourceURL + "'", e);
		}
	}

	private static final Injector injector = new RipacolaStandaloneSetup().createInjectorAndDoEMFRegistration();
	private static final ResourceSet rs = injector.getInstance(ResourceSet.class);
	private static final IResourceValidator validator = injector.getInstance(IResourceValidator.class);

	public static final RipacolaParseResult parse(final URI sourceFileUri) {
		final Resource resource = rs.getResource(sourceFileUri, true);
		try {
			resource.load(null);
		} catch (final IOException e) {
			throw new RipacolaParserException("Could not load file.", e);
		}
		final List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		return new RipacolaParseResult(resource, issues);
	}
}

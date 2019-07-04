package net.sourceforge.dkartaschew.halimede.ui.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestExistingCAValidator {

	private DefaultUnitTestRealm realm;

	@Before
	public void setUp() throws Exception {
		realm = new DefaultUnitTestRealm();
	}

	@After
	public void tearDown() throws Exception {
		realm.dispose();
	}

	@Test
	public void testClass() {
		IObservableValue<String> name = mock(IObservableValue.class);
		IObservableValue<String> loc = mock(IObservableValue.class);
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		v.toString();
		v.hashCode();
		assertTrue(v.equals(v));
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullSecond() {
		IObservableValue<String> name = mock(IObservableValue.class);
		new ExistingCAValidator(name, null);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullFirst() {
		IObservableValue<String> loc = mock(IObservableValue.class);
		new ExistingCAValidator(null, loc);
	}

	@Test
	public void testNullValues() {
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn(null);
		IObservableValue<String> loc = mock(IObservableValue.class);
		when(loc.getValue()).thenReturn(null);
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testNullNameValues() {
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn(null);
		IObservableValue<String> loc = mock(IObservableValue.class);
		when(loc.getValue()).thenReturn("");
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testNullLocValues() {
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("");
		IObservableValue<String> loc = mock(IObservableValue.class);
		when(loc.getValue()).thenReturn(null);
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testEmptyValues() {
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("");
		IObservableValue<String> loc = mock(IObservableValue.class);
		when(loc.getValue()).thenReturn("");
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testGoodValues() {
		IObservableValue<String> loc = mock(IObservableValue.class);
		when(loc.getValue()).thenReturn(System.getProperty("java.io.tmpdir"));
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("ABC");
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testBadNameValues() {
		IObservableValue<String> loc = mock(IObservableValue.class);
		when(loc.getValue()).thenReturn(System.getProperty("java.io.tmpdir"));
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("file::\\/\u0000\\!@#$%^&*()..//..\\.\\.\\\n\t \u0007 \u0002 \u0001");
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testLocationNameValues() {
		IObservableValue<String> loc = mock(IObservableValue.class);
		when(loc.getValue()).thenReturn("/file::\\/\u0000\\!@#$%^&*()..//..\\.\\.\\\n\t \u0007 \u0002 \u0001");
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("ABC");
		ExistingCAValidator v = new ExistingCAValidator(name, loc);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testTempFolderValues() throws IOException {
		Path f = Files.createTempDirectory("ABC");
		try {
			IObservableValue<String> name = mock(IObservableValue.class);
			when(name.getValue()).thenReturn(f.getFileName().toString());
			IObservableValue<String> loc = mock(IObservableValue.class);
			when(loc.getValue()).thenReturn(f.getParent().toString());
			ExistingCAValidator v = new ExistingCAValidator(name, loc);
			assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
		} finally {
			Files.deleteIfExists(f);
		}
	}
	
	@Test
	public void testTempFileValues() throws IOException {
		Path f = Files.createTempFile("ABC", ".tmp");
		try {
			IObservableValue<String> name = mock(IObservableValue.class);
			when(name.getValue()).thenReturn(f.getFileName().toString());
			IObservableValue<String> loc = mock(IObservableValue.class);
			when(loc.getValue()).thenReturn(f.getParent().toString());
			ExistingCAValidator v = new ExistingCAValidator(name, loc);
			assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
		} finally {
			Files.deleteIfExists(f);
		}
	}

}

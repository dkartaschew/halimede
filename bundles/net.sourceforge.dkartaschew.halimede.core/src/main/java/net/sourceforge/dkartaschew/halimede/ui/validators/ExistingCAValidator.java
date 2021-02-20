package net.sourceforge.dkartaschew.halimede.ui.validators;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ExistingCAValidator extends MultiValidator {

	private final IObservableValue<String> nameField;
	private final IObservableValue<String> locationField;

	/**
	 * Create a validator to ensure the location doesn't already contain a folder
	 * with the given name
	 * 
	 * @param nameField     The name field
	 * @param locationField The location field.
	 */
	public ExistingCAValidator(IObservableValue<String> nameField, IObservableValue<String> locationField) {
		Objects.requireNonNull(nameField, "Name field is null");
		Objects.requireNonNull(locationField, "Location field is null");
		this.nameField = nameField;
		this.locationField = locationField;
	}

	@Override
	protected IStatus validate() {
		// If either are null return OK. (This will be caught in the other validators).
		if (locationField.getValue() == null || nameField.getValue() == null) {
			return ValidationStatus.ok();
		}
		try {
			Path p = Paths.get(locationField.getValue(), nameField.getValue());
			if (Files.exists(p)) {
				return ValidationStatus.error("Location appears to have a location with the Name already defined.");
			}
			return ValidationStatus.ok();
		} catch (InvalidPathException e) {
			return ValidationStatus.error("Location and name appears malformed?");
		}
	}

}

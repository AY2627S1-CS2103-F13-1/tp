package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's persistent ID in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPersonId(String)}
 */
public class PersonId implements Comparable<PersonId> {

    public static final String MESSAGE_CONSTRAINTS =
            "Contact IDs should be a positive whole number without leading zeros, e.g. 1 or 42";
    public static final String VALIDATION_REGEX = "[1-9]\\d*";

    private final int value;

    /**
     * Constructs a {@code PersonId} from its text form.
     *
     * @param id A valid person ID, e.g. "42".
     */
    public PersonId(String id) {
        requireNonNull(id);
        checkArgument(isValidPersonId(id), MESSAGE_CONSTRAINTS);
        value = Integer.parseInt(id);
    }

    /**
     * Constructs a {@code PersonId} from its numeric value.
     *
     * @param value A positive number, e.g. 42.
     */
    public PersonId(int value) {
        checkArgument(value > 0, MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    /**
     * Returns true if a given string is a valid person ID.
     */
    public static boolean isValidPersonId(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }

        try {
            Integer.parseInt(test);
            return true;
        } catch (NumberFormatException nfe) {
            return false; // number is too large to fit in an int
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(PersonId other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonId otherPersonId)) {
            return false;
        }

        return value == otherPersonId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}

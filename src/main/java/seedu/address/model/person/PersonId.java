package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's persistent ID in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPersonId(String)}
 */
public class PersonId implements Comparable<PersonId> {

    public static final String PREFIX = "C";
    public static final String MESSAGE_CONSTRAINTS =
            "Contact IDs should be an uppercase 'C' followed by a positive whole number without leading zeros, "
            + "e.g. C1 or C42";
    public static final String VALIDATION_REGEX = PREFIX + "[1-9]\\d*";

    private final int value;

    /**
     * Constructs a {@code PersonId} from its display form.
     *
     * @param id A valid person ID, e.g. "C42".
     */
    public PersonId(String id) {
        requireNonNull(id);
        checkArgument(isValidPersonId(id), MESSAGE_CONSTRAINTS);
        value = Integer.parseInt(id.substring(PREFIX.length()));
    }

    /**
     * Constructs a {@code PersonId} from its numeric value.
     *
     * @param value A positive number, e.g. 42 for "C42".
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
            Integer.parseInt(test.substring(PREFIX.length()));
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
        return PREFIX + value;
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

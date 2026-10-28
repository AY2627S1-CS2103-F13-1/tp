package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's persistent ID in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPersonId(String)}
 */
public class PersonId implements Comparable<PersonId> {

    /** The largest ID. IDs are never reused, so this caps how many persons can ever be added. */
    public static final int MAX_VALUE = 999_999;
    public static final String MESSAGE_CONSTRAINTS =
            "Contact IDs should be a whole number from 1 to " + MAX_VALUE + ", without leading zeros";
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
     * @param value A number from 1 to {@link #MAX_VALUE}, e.g. 42.
     */
    public PersonId(int value) {
        checkArgument(isWithinRange(value), MESSAGE_CONSTRAINTS);
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
            return isWithinRange(Integer.parseInt(test));
        } catch (NumberFormatException nfe) {
            return false; // number is too large to fit in an int
        }
    }

    private static boolean isWithinRange(int value) {
        return value >= 1 && value <= MAX_VALUE;
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

package seedu.address.model.person;

import seedu.address.commons.util.ToStringBuilder;

import java.util.function.Predicate;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class AnyFieldContainsSubstringPredicate implements Predicate<Person> {
    private final String substring;

    public AnyFieldContainsSubstringPredicate(String substring) {
        this.substring = substring;
    }

    @Override
    public boolean test(Person person) {
        return person.toString().toLowerCase().contains(substring.toLowerCase());
        // Assumes person.toString() prints all fields
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AnyFieldContainsSubstringPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return substring.equals(otherNameContainsKeywordsPredicate.substring);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("substring", substring).toString();
    }
}

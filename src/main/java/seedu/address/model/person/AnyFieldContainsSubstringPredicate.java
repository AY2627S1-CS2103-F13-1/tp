package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s fields contains the given substring.
 */
public class AnyFieldContainsSubstringPredicate implements Predicate<Person> {
    private final String substring;

    public AnyFieldContainsSubstringPredicate(String substring) {
        this.substring = substring;
    }

    @Override
    public boolean test(Person person) {
        boolean isNameMatch = person.getName().fullName.toLowerCase().contains(substring);
        boolean isPhoneMatch = person.getPhone().value.toLowerCase().contains(substring);
        boolean isEmailMatch = person.getEmail().value.toLowerCase().contains(substring);
        boolean isAddressMatch = person.getAddress().value.toLowerCase().contains(substring);
        boolean isTagsMatch = person.getTags().stream().anyMatch(tag -> tag.tagName.toLowerCase().contains(substring));
        return isNameMatch || isPhoneMatch || isEmailMatch || isAddressMatch || isTagsMatch;
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

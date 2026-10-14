package seedu.address.model.person;

import seedu.address.commons.util.ToStringBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tests that a {@code Person}'s fields contains any given substring, respective to each field.
 */
public class EachFieldContainsSubstringPredicate implements Predicate<Person> {
    private final Optional<String> nameKey;
    private final Optional<String> phoneKey;
    private final Optional<String> emailKey;
    private final Optional<String> addressKey;
    private final List<String> tagKeys;

    public EachFieldContainsSubstringPredicate(
            Optional<String> nameKey, Optional<String> phoneKey, Optional<String> emailKey,
            Optional<String> addressKey, List<String> tagKeysKey) {
           this.nameKey = nameKey;
           this.phoneKey = phoneKey;
           this.emailKey = emailKey;
           this.addressKey = addressKey;
           this.tagKeys = tagKeysKey;
    }

    @Override
    public boolean test(Person person) {

        return (nameKey.isEmpty() || person.getName().fullName.toLowerCase().contains(nameKey.get().toLowerCase()))
                && (phoneKey.isEmpty() || person.getPhone().value.toLowerCase().contains(phoneKey.get().toLowerCase()))
                && (emailKey.isEmpty() || person.getEmail().value.toLowerCase().contains(emailKey.get().toLowerCase()))
                && (addressKey.isEmpty()
                        || person.getAddress().value.toLowerCase().contains(addressKey.get().toLowerCase()))
                && (tagKeys.isEmpty()
                        || tagKeys.stream().allMatch(
                                tagKey -> person.getTags().stream().anyMatch(
                                        tag -> tag.tagName.toLowerCase().contains(tagKey))));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EachFieldContainsSubstringPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return nameKey.equals(otherNameContainsKeywordsPredicate.nameKey)
                && phoneKey.equals(otherNameContainsKeywordsPredicate.phoneKey)
                && emailKey.equals(otherNameContainsKeywordsPredicate.emailKey)
                && addressKey.equals(otherNameContainsKeywordsPredicate.addressKey)
                && tagKeys.equals(otherNameContainsKeywordsPredicate.tagKeys);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKey", nameKey)
                .add("phoneKey", phoneKey)
                .add("emailKey", emailKey)
                .add("addressKey", addressKey)
                .add("tagKeys", tagKeys)
                .toString();
    }
}

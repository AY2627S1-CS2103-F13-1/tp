package seedu.address.model.person;

import seedu.address.commons.util.ToStringBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class EachFieldContainsSubstringPredicate implements Predicate<Person> {
    private final Optional<String> name;
    private final Optional<String> phone;
    private final Optional<String> email;
    private final Optional<String> address;
    private final List<String> tagList;

    public EachFieldContainsSubstringPredicate(
            Optional<String> name, Optional<String> phone, Optional<String> email,
            Optional<String> address, List<String> tagList) {
           this.name = name;
           this.phone = phone;
           this.email = email;
           this.address = address;
           this.tagList = tagList;
    }

    @Override
    public boolean test(Person person) {

        return (name.isEmpty() || person.getName().fullName.toLowerCase().contains(name.get().toLowerCase()))
                && (phone.isEmpty() || person.getPhone().value.toLowerCase().contains(phone.get().toLowerCase()))
                && (email.isEmpty() || person.getEmail().value.toLowerCase().contains(email.get().toLowerCase()))
                && (address.isEmpty() || person.getAddress().value.toLowerCase().contains(address.get().toLowerCase()))
                && (tagList.isEmpty()
                        || person.getTags().stream().anyMatch(tag -> tagList.stream().anyMatch(
                                tagkey -> tag.tagName.toLowerCase().contains(tagkey.toLowerCase()))));
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

        return name.equals(otherNameContainsKeywordsPredicate.name)
                && phone.equals(otherNameContainsKeywordsPredicate.phone)
                && email.equals(otherNameContainsKeywordsPredicate.email)
                && address.equals(otherNameContainsKeywordsPredicate.address)
                && tagList.equals(otherNameContainsKeywordsPredicate.tagList);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tagList", tagList)
                .toString();
    }
}

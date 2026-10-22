package seedu.address.model.person;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.model.search.Search;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class MatchPersonPredicate implements Predicate<Person> {
    private final ArgumentMultimap argMultimap;

    public MatchPersonPredicate(ArgumentMultimap argMultimap) {
        this.argMultimap = argMultimap;
    }

    @Override
    public boolean test(Person person) {
        String preambleKey = argMultimap.getPreamble();
        Optional<String> nameKey = argMultimap.getValue(PREFIX_NAME);
        Optional<String> phoneKey = argMultimap.getValue(PREFIX_PHONE);
        Optional<String> emailKey = argMultimap.getValue(PREFIX_EMAIL);
        Optional<String> addressKey = argMultimap.getValue(PREFIX_ADDRESS);
        List<String> tagKeys = argMultimap.getAllValues(PREFIX_TAG);

        if (preambleKey.isEmpty() && nameKey.isEmpty() && phoneKey.isEmpty() && emailKey.isEmpty()
                && addressKey.isEmpty() && tagKeys.isEmpty()) {
            return false;
        }

        String nameTarget = person.getName().fullName;
        String phoneTarget = person.getPhone().value;
        String emailTarget = person.getEmail().value;
        String addressTarget = person.getAddress().value;
        Set<Tag> tagListTarget = person.getTags();

        assert !nameTarget.isBlank();
        assert !phoneTarget.isBlank();
        assert !emailTarget.isBlank();
        assert !addressTarget.isBlank();

        boolean isPreamble = preambleKey.isBlank();
        if (!preambleKey.isBlank()) {
            isPreamble = Search.substring(nameTarget, preambleKey)
                    || Search.substring(phoneTarget, preambleKey)
                    || Search.substring(emailTarget, preambleKey)
                    || Search.substring(addressTarget, preambleKey)
                    || tagListTarget.stream().anyMatch(
                        tagTarget -> Search.substring(tagTarget.tagName, preambleKey));
        }

        boolean isName = nameKey.isEmpty();
        if (nameKey.isPresent()) {
            isName = Search.substring(nameTarget, nameKey.get());
        }

        boolean isPhone = phoneKey.isEmpty();
        if (phoneKey.isPresent()) {
            isPhone = Search.substring(phoneTarget, phoneKey.get());
        }

        boolean isEmail = emailKey.isEmpty();
        if (emailKey.isPresent()) {
            isEmail = Search.substring(emailTarget, emailKey.get());
        }

        boolean isAddress = addressKey.isEmpty();
        if (addressKey.isPresent()) {
            isAddress = Search.substring(addressTarget, addressKey.get());
        }

        boolean isTags = tagKeys.isEmpty();
        if (!tagKeys.isEmpty()) {
            isTags = tagKeys.stream().allMatch(
                    tagKey -> tagListTarget.stream().anyMatch(
                            tag -> Search.substring(tag.tagName, tagKey)));
        }

        return isPreamble && isName && isPhone && isEmail && isAddress && isTags;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MatchPersonPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return argMultimap.equals(otherNameContainsKeywordsPredicate.argMultimap);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("argMultimap", argMultimap).toString();
    }
}

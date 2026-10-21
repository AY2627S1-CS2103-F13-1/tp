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

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.parser.ArgumentMultimap;
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

        String nameTargetLowerCase = person.getName().fullName.toLowerCase();
        String phoneTargetLowerCase = person.getPhone().value.toLowerCase();
        String emailTargetLowerCase = person.getEmail().value.toLowerCase();
        String addressTargetLowerCase = person.getAddress().value.toLowerCase();
        Set<Tag> tagListTarget = person.getTags();

        assert !nameTargetLowerCase.isBlank();
        assert !phoneTargetLowerCase.isBlank();
        assert !emailTargetLowerCase.isBlank();
        assert !addressTargetLowerCase.isBlank();

        boolean isPreamble = preambleKey.isBlank();
        if (!preambleKey.isBlank()) {
            String preambleKeyLowerCase = preambleKey.toLowerCase();
            isPreamble = nameTargetLowerCase.contains(preambleKeyLowerCase)
                    || phoneTargetLowerCase.contains(preambleKeyLowerCase)
                    || emailTargetLowerCase.contains(preambleKeyLowerCase)
                    || addressTargetLowerCase.contains(preambleKeyLowerCase)
                    || tagListTarget.stream().anyMatch(
                    tagTarget -> tagTarget.tagName.toLowerCase().contains(preambleKeyLowerCase));
        }

        boolean isName = nameKey.isEmpty();
        if (nameKey.isPresent()) {
            String nameKeyLowerCase = nameKey.get().toLowerCase();
            isName = nameTargetLowerCase.contains(nameKeyLowerCase);
        }

        boolean isPhone = phoneKey.isEmpty();
        if (phoneKey.isPresent()) {
            String phoneKeyLowerCase = phoneKey.get().toLowerCase();
            isPhone = phoneTargetLowerCase.contains(phoneKeyLowerCase);
        }

        boolean isEmail = emailKey.isEmpty();
        if (emailKey.isPresent()) {
            String emailKeyLowerCase = emailKey.get().toLowerCase();
            isEmail = emailTargetLowerCase.contains(emailKeyLowerCase);
        }

        boolean isAddress = addressKey.isEmpty();
        if (addressKey.isPresent()) {
            String addressKeyLowerCase = addressKey.get().toLowerCase();
            isAddress = addressTargetLowerCase.contains(addressKeyLowerCase);
        }

        boolean isTags = tagKeys.isEmpty();
        if (!tagKeys.isEmpty()) {
            isTags = tagKeys.stream().allMatch(
                    tagKey -> tagListTarget.stream().anyMatch(
                            tag -> tag.tagName.toLowerCase().contains(tagKey.toLowerCase())));
        }

        return isPreamble || isName || isPhone || isEmail || isAddress || isTags;
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

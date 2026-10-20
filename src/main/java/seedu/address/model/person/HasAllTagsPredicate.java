package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person} has every required tag.
 */
public class HasAllTagsPredicate implements Predicate<Person> {
    private final Set<Tag> requiredTags;

    /**
     * Creates a predicate that matches persons containing every tag in {@code requiredTags}.
     */
    public HasAllTagsPredicate(Set<Tag> requiredTags) {
        requireNonNull(requiredTags);
        this.requiredTags = new HashSet<>(requiredTags);
    }

    @Override
    public boolean test(Person person) {
        return person.getTags().containsAll(requiredTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof HasAllTagsPredicate otherPredicate)) {
            return false;
        }

        return requiredTags.equals(otherPredicate.requiredTags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("requiredTags", requiredTags)
                .toString();
    }
}

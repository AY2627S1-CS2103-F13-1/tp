package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class HasAllTagsPredicateTest {

    private final Person person = new PersonBuilder()
            .withTags("housing policy", "expert", "singapore")
            .build();

    @Test
    public void constructor_nullTags_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HasAllTagsPredicate(null));
    }

    @Test
    public void test_oneExactMatchingTag_returnsTrue() {
        assertTrue(createPredicate("expert").test(person));
    }

    @Test
    public void test_oneMissingTag_returnsFalse() {
        assertFalse(createPredicate("economics").test(person));
    }

    @Test
    public void test_multipleMatchingTags_returnsTrue() {
        assertTrue(createPredicate("housing policy", "expert").test(person));
    }

    @Test
    public void test_multipleTagsWithOneMissing_returnsFalse() {
        assertFalse(createPredicate("housing policy", "economics").test(person));
    }

    @Test
    public void test_personWithAdditionalUnrelatedTags_returnsTrue() {
        assertTrue(createPredicate("expert").test(person));
    }

    @Test
    public void test_untaggedPerson_returnsFalse() {
        Person untaggedPerson = new PersonBuilder(person).withTags().build();
        assertFalse(createPredicate("expert").test(untaggedPerson));
    }

    @Test
    public void test_differentCapitalisation_returnsTrue() {
        assertTrue(createPredicate("EXPERT", "Housing Policy").test(person));
    }

    @Test
    public void test_partialTagText_returnsFalse() {
        assertFalse(createPredicate("housing").test(person));
        assertFalse(createPredicate("policy").test(person));
    }

    @Test
    public void test_repeatedEquivalentRequestedTags_treatedAsOneCondition() {
        Set<Tag> repeatedTags = new HashSet<>();
        repeatedTags.add(new Tag("expert"));
        repeatedTags.add(new Tag("EXPERT"));

        assertEquals(1, repeatedTags.size());
        assertTrue(new HasAllTagsPredicate(repeatedTags).test(person));
    }

    @Test
    public void test_requiredTagSetChangedAfterConstruction_usesDefensiveCopy() {
        Set<Tag> requiredTags = new HashSet<>(Set.of(new Tag("expert")));
        HasAllTagsPredicate predicate = new HasAllTagsPredicate(requiredTags);

        requiredTags.add(new Tag("economics"));

        assertTrue(predicate.test(person));
    }

    @Test
    public void equals_variousComparisons_returnsExpectedResult() {
        HasAllTagsPredicate predicate = createPredicate("housing policy", "expert");

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(createPredicate("EXPERT", "Housing Policy")));
        assertFalse(predicate.equals(createPredicate("housing policy")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(1));
    }

    @Test
    public void toString_oneRequiredTag_returnsExpectedString() {
        HasAllTagsPredicate predicate = createPredicate("expert");
        String expected = HasAllTagsPredicate.class.getCanonicalName()
                + "{requiredTags=[[expert]]}";

        assertEquals(expected, predicate.toString());
    }

    private HasAllTagsPredicate createPredicate(String... tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            tags.add(new Tag(tagName));
        }
        return new HasAllTagsPredicate(tags);
    }
}

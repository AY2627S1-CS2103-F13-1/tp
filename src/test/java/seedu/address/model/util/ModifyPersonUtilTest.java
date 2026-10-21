package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class ModifyPersonUtilTest {

    @Test
    public void setIsArchived_activePerson_returnsArchivedCopy() {
        Person original = new PersonBuilder(ALICE).withArchiveStatus(false).build();
        Person snapshot = new PersonBuilder(original).build();
        Person expected = new PersonBuilder(original).withArchiveStatus(true).build();

        Person updated = ModifyPersonUtil.setIsArchived(original, true);

        assertEquals(expected, updated);
        assertNotSame(original, updated);
        assertEquals(snapshot, original);
    }

    @Test
    public void setIsArchived_archivedPerson_returnsActiveCopy() {
        Person original = new PersonBuilder(ALICE).withArchiveStatus(true).build();
        Person snapshot = new PersonBuilder(original).build();
        Person expected = new PersonBuilder(original).withArchiveStatus(false).build();

        Person updated = ModifyPersonUtil.setIsArchived(original, false);

        assertEquals(expected, updated);
        assertNotSame(original, updated);
        assertEquals(snapshot, original);
    }

    @Test
    public void setIsArchived_unchangedStatus_returnsEqualCopy() {
        Person updated = ModifyPersonUtil.setIsArchived(ALICE, ALICE.isArchived());

        assertEquals(ALICE, updated);
        assertNotSame(ALICE, updated);
    }

    @Test
    public void setIsArchived_personWithoutId_throwsNoSuchElementException() {
        Person person = new PersonBuilder().build();

        assertThrows(NoSuchElementException.class, () -> ModifyPersonUtil.setIsArchived(person, true));
    }

    @Test
    public void setIsArchived_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ModifyPersonUtil.setIsArchived(null, true));
    }

    @Test
    public void setTags_activePerson_replacesTagsAndPreservesOtherFields() {
        Person original = new PersonBuilder(ALICE).withArchiveStatus(false).withTags("old").build();
        Person snapshot = new PersonBuilder(original).build();
        Person expected = new PersonBuilder(original).withTags("new", "friend").build();

        Person updated = ModifyPersonUtil.setTags(original, Set.of(new Tag("new"), new Tag("friend")));

        assertEquals(expected, updated);
        assertNotSame(original, updated);
        assertEquals(snapshot, original);
    }

    @Test
    public void setTags_archivedPerson_preservesArchiveStatusAndOtherFields() {
        Person original = new PersonBuilder(ALICE).withArchiveStatus(true).withTags("old").build();
        Person snapshot = new PersonBuilder(original).build();
        Person expected = new PersonBuilder(original).withTags("new").build();

        Person updated = ModifyPersonUtil.setTags(original, Set.of(new Tag("new")));

        assertEquals(expected, updated);
        assertNotSame(original, updated);
        assertEquals(snapshot, original);
    }

    @Test
    public void setTags_emptyTags_removesAllTags() {
        Person original = new PersonBuilder(ALICE).withTags("old").build();
        Person snapshot = new PersonBuilder(original).build();
        Person expected = new PersonBuilder(original).withTags().build();

        Person updated = ModifyPersonUtil.setTags(original, Set.of());

        assertEquals(expected, updated);
        assertEquals(snapshot, original);
    }

    @Test
    public void setTags_unchangedTags_returnsEqualCopy() {
        Person updated = ModifyPersonUtil.setTags(ALICE, ALICE.getTags());

        assertEquals(ALICE, updated);
        assertNotSame(ALICE, updated);
    }

    @Test
    public void setTags_mutableTags_defensivelyCopiesTags() {
        Set<Tag> tags = new HashSet<>(Set.of(new Tag("new")));
        Person expected = new PersonBuilder(ALICE).withTags("new").build();
        Person updated = ModifyPersonUtil.setTags(ALICE, tags);

        tags.clear();
        tags.add(new Tag("different"));

        assertEquals(expected, updated);
    }

    @Test
    public void setTags_personWithoutId_throwsNoSuchElementException() {
        Person person = new PersonBuilder().build();

        assertThrows(NoSuchElementException.class, () -> ModifyPersonUtil.setTags(person, Set.of()));
    }

    @Test
    public void setTags_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ModifyPersonUtil.setTags(null, Set.of()));
    }

    @Test
    public void setTags_nullTags_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ModifyPersonUtil.setTags(ALICE, null));
    }
}

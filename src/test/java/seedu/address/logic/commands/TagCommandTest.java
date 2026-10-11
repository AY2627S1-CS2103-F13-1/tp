package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Tests for TagCommand.
 */
public class TagCommandTest {

    private final Model model =
            new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addTags_preservesExistingDetailsAndId() throws Exception {
        Person original = model.getFilteredPersonList().get(0);

        Set<Tag> additions = Set.of(
                new Tag("housing policy"),
                new Tag("EXPERT"));

        new TagCommand(INDEX_FIRST_PERSON, additions).execute(model);

        Person updated = model.getFilteredPersonList().get(0);

        Set<Tag> expectedTags = new HashSet<>(original.getTags());
        expectedTags.addAll(additions);

        assertEquals(original.getId(), updated.getId());
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(expectedTags, updated.getTags());
    }

    @Test
    public void execute_duplicateTags_notDuplicated() throws Exception {
        Person original = model.getFilteredPersonList().get(0);

        if (original.getTags().isEmpty()) {
            return;
        }

        Tag existing = original.getTags().iterator().next();

        new TagCommand(
                INDEX_FIRST_PERSON,
                Set.of(new Tag(existing.tagName.toUpperCase(
                        java.util.Locale.ROOT)))).execute(model);

        Person updated = model.getFilteredPersonList().get(0);

        assertEquals(original.getTags(), updated.getTags());
    }

    @Test
    public void execute_multipleTags_success() throws Exception {
        Set<Tag> additions = Set.of(
                new Tag("expert"),
                new Tag("housing policy"));

        new TagCommand(INDEX_FIRST_PERSON, additions).execute(model);

        Person updated = model.getFilteredPersonList().get(0);

        assertTrue(updated.getTags().containsAll(additions));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        TagCommand command = new TagCommand(
                Index.fromOneBased(999), Set.of(new Tag("expert")));

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(model));

        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX,
                exception.getMessage());
    }

    @Test
    public void execute_existingAndNewTags_preservesBoth() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Set<Tag> additions = new HashSet<>(original.getTags());
        additions.add(new Tag("investigative journalist"));

        new TagCommand(INDEX_FIRST_PERSON, additions).execute(model);

        Person updated = model.getFilteredPersonList().get(0);
        assertEquals(additions, updated.getTags());
        assertEquals(original.getId(), updated.getId());
    }

    @Test
    public void equals() {
        TagCommand first = new TagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        TagCommand same = new TagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("EXPERT")));

        TagCommand differentIndex = new TagCommand(
                INDEX_SECOND_PERSON, Set.of(new Tag("expert")));

        assertEquals(first, same);
        assertNotEquals(first, differentIndex);
        assertNotEquals(first, null);
    }
}

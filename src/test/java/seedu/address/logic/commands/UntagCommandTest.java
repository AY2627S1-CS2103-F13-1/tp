package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
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
 * Tests for the untag command.
 */
public class UntagCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_removeSingleTag_success() throws CommandException {
        Person original = model.getFilteredPersonList().get(0);
        Person tagged = withTags(original, Set.of(new Tag("expert"), new Tag("politics")));
        model.setPerson(original, tagged);

        UntagCommand command = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        command.execute(model);

        Person updated = model.getFilteredPersonList().get(0);

        assertEquals(Set.of(new Tag("politics")), updated.getTags());
        assertEquals(tagged.getId(), updated.getId());
        assertEquals(tagged.getName(), updated.getName());
        assertEquals(tagged.getPhone(), updated.getPhone());
        assertEquals(tagged.getEmail(), updated.getEmail());
        assertEquals(tagged.getAddress(), updated.getAddress());
    }

    @Test
    public void execute_removeMultipleTags_success() throws CommandException {
        Person original = model.getFilteredPersonList().get(0);
        Person tagged = withTags(original, Set.of(
                new Tag("expert"),
                new Tag("politics"),
                new Tag("housing policy")));
        model.setPerson(original, tagged);

        UntagCommand command = new UntagCommand(
                INDEX_FIRST_PERSON,
                Set.of(new Tag("expert"), new Tag("politics")));

        command.execute(model);

        Person updated = model.getFilteredPersonList().get(0);

        assertEquals(Set.of(new Tag("housing policy")), updated.getTags());
    }

    @Test
    public void execute_caseInsensitiveTag_success() throws CommandException {
        Person original = model.getFilteredPersonList().get(0);
        Person tagged = withTags(original, Set.of(new Tag("Expert")));
        model.setPerson(original, tagged);

        UntagCommand command = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("EXPERT")));

        command.execute(model);

        assertTrue(model.getFilteredPersonList().get(0).getTags().isEmpty());
    }

    @Test
    public void execute_nonexistentTag_preservesExistingTags() throws CommandException {
        Person original = model.getFilteredPersonList().get(0);
        Person tagged = withTags(original, Set.of(new Tag("politics")));
        model.setPerson(original, tagged);

        UntagCommand command = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        command.execute(model);

        assertEquals(Set.of(new Tag("politics")),
                model.getFilteredPersonList().get(0).getTags());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(
                model.getFilteredPersonList().size() + 1);

        UntagCommand command = new UntagCommand(
                invalidIndex, Set.of(new Tag("expert")));

        try {
            command.execute(model);
            throw new AssertionError("Expected CommandException");
        } catch (CommandException exception) {
            assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX,
                    exception.getMessage());
        }
    }

    @Test
    public void equals() {
        UntagCommand first = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        UntagCommand same = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("EXPERT")));

        UntagCommand differentTag = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("politics")));

        UntagCommand differentIndex = new UntagCommand(
                Index.fromOneBased(2), Set.of(new Tag("expert")));

        assertTrue(first.equals(first));
        assertEquals(first, same);
        assertNotEquals(first, differentTag);
        assertNotEquals(first, differentIndex);
        assertNotEquals(first, null);
        assertNotEquals(first, "untag");
        assertEquals(first.hashCode(), same.hashCode());
    }

    @Test
    public void toStringMethod() {
        UntagCommand command = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        String result = command.toString();

        assertTrue(result.contains("index"));
        assertTrue(result.contains("tagsToRemove"));
        assertTrue(result.contains("expert"));
    }

    private Person withTags(Person original, Set<Tag> tags) {
        return new Person(
                original.getId().orElseThrow(),
                original.getName(),
                original.getPhone(),
                original.getEmail(),
                original.getAddress(),
                tags);
    }
}

package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
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
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

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
        assertFalse(updated.isArchived());
    }

    @Test
    public void execute_archivedContact_preservesStatusIdAndOtherDetails() throws CommandException {
        Person original = model.getAddressBook().getPersonList().get(1);
        Person archived = new PersonBuilder(original).withTags("expert", "politics")
                .withArchiveStatus(true).build();
        model.setPerson(original, archived);
        new ListCommand(ListCommand.View.ARCHIVED).execute(model);

        new UntagCommand(INDEX_FIRST_PERSON, Set.of(new Tag("expert"))).execute(model);

        Person updated = model.getAddressBook().getPersonList().get(1);
        assertTrue(updated.isArchived());
        assertEquals(archived.getId(), updated.getId());
        assertEquals(archived.getName(), updated.getName());
        assertEquals(archived.getPhone(), updated.getPhone());
        assertEquals(archived.getEmail(), updated.getEmail());
        assertEquals(archived.getAddress(), updated.getAddress());
        assertEquals(Set.of(new Tag("politics")), updated.getTags());
        assertEquals(List.of(updated), model.getFilteredPersonList());
    }

    @Test
    public void execute_nonexistentTagOnArchivedContact_preservesContact() throws CommandException {
        Person original = model.getAddressBook().getPersonList().get(1);
        Person archived = new PersonBuilder(original).withTags("politics").withArchiveStatus(true).build();
        model.setPerson(original, archived);
        new ListCommand(ListCommand.View.ARCHIVED).execute(model);

        new UntagCommand(INDEX_FIRST_PERSON, Set.of(new Tag("expert"))).execute(model);

        assertEquals(archived, model.getAddressBook().getPersonList().get(1));
        assertEquals(List.of(archived), model.getFilteredPersonList());
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

        UntagCommand byId = new UntagCommand(
                PersonTarget.ofId(new PersonId("C1")), Set.of(new Tag("expert")));

        // same ID -> equal
        assertEquals(byId, new UntagCommand(
                PersonTarget.ofId(new PersonId("C1")), Set.of(new Tag("expert"))));

        // different ID -> not equal
        assertNotEquals(byId, new UntagCommand(
                PersonTarget.ofId(new PersonId("C2")), Set.of(new Tag("expert"))));

        // selected by ID instead of index -> not equal
        assertNotEquals(first, byId);
    }

    @Test
    public void execute_validId_success() throws CommandException {
        // BENSON is tagged "owesMoney" and "friends"
        PersonId bensonId = BENSON.getId().orElseThrow();

        new UntagCommand(PersonTarget.ofId(bensonId), Set.of(new Tag("owesMoney"))).execute(model);

        Person updated = model.findPersonById(bensonId).orElseThrow();
        assertEquals(Set.of(new Tag("friends")), updated.getTags());
        assertEquals(BENSON.getName(), updated.getName());
    }

    @Test
    public void execute_validIdOfPersonHiddenByFilter_success() throws CommandException {
        // only ALICE is displayed, but BENSON can still be untagged by his ID
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        PersonId bensonId = BENSON.getId().orElseThrow();

        new UntagCommand(PersonTarget.ofId(bensonId), Set.of(new Tag("owesMoney"))).execute(model);

        assertEquals(Set.of(new Tag("friends")), model.findPersonById(bensonId).orElseThrow().getTags());
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        PersonId unknownId = new PersonId("C99");
        UntagCommand command = new UntagCommand(PersonTarget.ofId(unknownId), Set.of(new Tag("expert")));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, unknownId), exception.getMessage());
    }

    @Test
    public void toStringMethod() {
        UntagCommand command = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        String result = command.toString();

        assertTrue(result.contains("target"));
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
                tags,
                original.isArchived());
    }
}

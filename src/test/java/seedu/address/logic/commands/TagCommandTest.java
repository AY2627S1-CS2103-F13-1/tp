package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

/**
 * Tests for TagCommand.
 */
public class TagCommandTest {

    private static final PersonTarget TARGET_ALICE = PersonTarget.ofId(ALICE.getId().orElseThrow());
    private static final PersonTarget TARGET_BENSON = PersonTarget.ofId(BENSON.getId().orElseThrow());

    private final Model model =
            new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addTags_preservesExistingDetailsAndId() throws Exception {
        Person original = model.getFilteredPersonList().get(0);

        Set<Tag> additions = Set.of(
                new Tag("housing policy"),
                new Tag("EXPERT"));

        new TagCommand(TARGET_ALICE, additions).execute(model);

        Person updated = model.getFilteredPersonList().get(0);

        Set<Tag> expectedTags = new HashSet<>(original.getTags());
        expectedTags.addAll(additions);

        assertEquals(original.getId(), updated.getId());
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(expectedTags, updated.getTags());
        assertFalse(updated.isArchived());
    }

    @Test
    public void execute_archivedContact_preservesStatusIdDetailsAndExistingTags() throws Exception {
        new ArchiveCommand(TARGET_BENSON).execute(model);
        new ListCommand(ListCommand.View.ARCHIVED).execute(model);
        Person original = model.getFilteredPersonList().get(0);
        Set<Tag> expectedTags = new HashSet<>(original.getTags());
        expectedTags.add(new Tag("expert"));

        new TagCommand(TARGET_BENSON, Set.of(new Tag("expert"))).execute(model);

        Person updated = model.findPersonById(BENSON.getId().orElseThrow()).orElseThrow();
        assertTrue(updated.isArchived());
        assertEquals(original.getId(), updated.getId());
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(expectedTags, updated.getTags());
        assertEquals(List.of(updated), model.getFilteredPersonList());
    }

    @Test
    public void execute_duplicateTags_notDuplicated() throws Exception {
        Person original = model.getFilteredPersonList().get(0);

        if (original.getTags().isEmpty()) {
            return;
        }

        Tag existing = original.getTags().iterator().next();

        new TagCommand(
                TARGET_ALICE,
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

        new TagCommand(TARGET_ALICE, additions).execute(model);

        Person updated = model.getFilteredPersonList().get(0);

        assertTrue(updated.getTags().containsAll(additions));
    }

    @Test
    public void execute_existingAndNewTags_preservesBoth() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Set<Tag> additions = new HashSet<>(original.getTags());
        additions.add(new Tag("investigative journalist"));

        new TagCommand(TARGET_ALICE, additions).execute(model);

        Person updated = model.getFilteredPersonList().get(0);
        assertEquals(additions, updated.getTags());
        assertEquals(original.getId(), updated.getId());
    }

    @Test
    public void execute_validId_success() throws Exception {
        PersonId bensonId = BENSON.getId().orElseThrow();
        Set<Tag> expectedTags = new HashSet<>(BENSON.getTags());
        expectedTags.add(new Tag("expert"));

        new TagCommand(PersonTarget.ofId(bensonId), Set.of(new Tag("expert"))).execute(model);

        Person updated = model.findPersonById(bensonId).orElseThrow();
        assertEquals(BENSON.getName(), updated.getName());
        assertEquals(expectedTags, updated.getTags());
    }

    @Test
    public void execute_validIdOfPersonHiddenByFilter_success() throws Exception {
        // only ALICE is displayed, but BENSON can still be tagged by his ID
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        PersonId bensonId = BENSON.getId().orElseThrow();

        new TagCommand(PersonTarget.ofId(bensonId), Set.of(new Tag("expert"))).execute(model);

        Person updated = model.findPersonById(bensonId).orElseThrow();
        assertTrue(updated.getTags().contains(new Tag("expert")));
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        TagCommand command = new TagCommand(PersonTarget.ofId(new PersonId(99)), Set.of(new Tag("expert")));

        CommandException exception = assertThrows(
                CommandException.class, () -> command.execute(model));

        assertEquals(String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 99),
                exception.getMessage());
    }

    @Test
    public void toStringMethod() {
        TagCommand command = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("expert")));

        String expected = TagCommand.class.getCanonicalName()
                + "{target=" + TARGET_ALICE
                + ", tagsToAdd=" + Set.of(new Tag("expert")) + "}";

        assertEquals(expected, command.toString());
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        TagCommand command = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("expert")));

        assertEquals(command, command);
    }

    @Test
    public void equals_differentTags_returnsFalse() {
        TagCommand first = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("expert")));

        TagCommand second = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("journalist")));

        assertNotEquals(first, second);
    }

    @Test
    public void equals_differentType_returnsFalse() {
        TagCommand command = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("expert")));

        assertNotEquals(command, "tag");
    }

    @Test
    public void hashCode_equalCommands_sameHashCode() {
        TagCommand first = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("expert")));

        TagCommand second = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("EXPERT")));

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals() {
        TagCommand first = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("expert")));

        TagCommand same = new TagCommand(
                TARGET_ALICE, Set.of(new Tag("EXPERT")));

        TagCommand differentPerson = new TagCommand(
                TARGET_BENSON, Set.of(new Tag("expert")));

        assertEquals(first, same);
        assertNotEquals(first, differentPerson);
        assertNotEquals(first, null);

    }
}

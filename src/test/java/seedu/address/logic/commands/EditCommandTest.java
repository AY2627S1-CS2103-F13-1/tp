package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showOnlyPerson;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private static final PersonTarget TARGET_ALICE = PersonTarget.ofId(ALICE.getId().orElseThrow());
    private static final PersonTarget TARGET_BENSON = PersonTarget.ofId(BENSON.getId().orElseThrow());

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_fieldsEdited_keepsId() throws Exception {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).build();

        new EditCommand(TARGET_ALICE, descriptor).execute(model);

        Person editedPerson = model.findPersonById(ALICE.getId().orElseThrow()).orElseThrow();
        assertEquals(VALID_NAME_BOB, editedPerson.getName().fullName);
        assertFalse(editedPerson.isArchived());
    }

    @Test
    public void execute_archivedContact_preservesStatusIdAndUneditedDetails() throws Exception {
        new ArchiveCommand(TARGET_BENSON).execute(model);
        new ListCommand(ListCommand.View.ARCHIVED).execute(model);
        Person original = model.getFilteredPersonList().get(0);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("91234567").build();

        new EditCommand(TARGET_BENSON, descriptor).execute(model);

        Person updated = model.findPersonById(BENSON.getId().orElseThrow()).orElseThrow();
        assertTrue(updated.isArchived());
        assertEquals(original.getId(), updated.getId());
        assertEquals("91234567", updated.getPhone().value);
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(original.getTags(), updated.getTags());
        assertEquals(List.of(updated), model.getFilteredPersonList());
    }

    @Test
    public void execute_activeContactAfterListAll_hidesArchivedContacts() throws Exception {
        new ArchiveCommand(TARGET_BENSON).execute(model);
        new ListCommand(ListCommand.View.ALL).execute(model);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("91234567").build();

        new EditCommand(TARGET_ALICE, descriptor).execute(model);

        Person updated = model.findPersonById(ALICE.getId().orElseThrow()).orElseThrow();
        assertTrue(model.getFilteredPersonList().contains(updated));
        assertTrue(model.getFilteredPersonList().stream().noneMatch(Person::isArchived));
    }

    @Test
    public void execute_archivedContactFromActiveView_showsArchivedView() throws Exception {
        new ArchiveCommand(TARGET_BENSON).execute(model);
        new ListCommand(ListCommand.View.ACTIVE).execute(model);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone("91234567").build();

        new EditCommand(TARGET_BENSON, descriptor).execute(model);

        Person updated = model.findPersonById(BENSON.getId().orElseThrow()).orElseThrow();
        assertEquals(List.of(updated), model.getFilteredPersonList());
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().withId("1").build(); // keeps ALICE's ID
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(TARGET_ALICE, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Person lastPerson = model.getFilteredPersonList().get(model.getFilteredPersonList().size() - 1);

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(PersonTarget.ofId(lastPerson.getId().orElseThrow()), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(TARGET_ALICE, new EditPersonDescriptor());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(ALICE));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        // only ALICE is displayed, and she is edited by her ID
        showOnlyPerson(model, ALICE);

        Person editedPerson = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(TARGET_ALICE,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(ALICE).build();
        EditCommand editCommand = new EditCommand(TARGET_BENSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showOnlyPerson(model, ALICE);

        // edit the displayed person into a duplicate of a person hidden by the filter
        EditCommand editCommand = new EditCommand(TARGET_ALICE, new EditPersonDescriptorBuilder(BENSON).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonWithNormalizedName_failure() {
        String duplicateNameVariant = ALICE.getName().toString()
                .toUpperCase(Locale.ROOT)
                .replace(" ", "  ");
        EditCommand editCommand = new EditCommand(TARGET_BENSON, new EditPersonDescriptorBuilder()
                .withName(duplicateNameVariant).withPhone(ALICE.getPhone().toString()).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameAsOtherPersonDifferentPhone_success() {
        Person editedPerson = new PersonBuilder(BENSON).withName(ALICE.getName().toString()).build();
        EditCommand editCommand = new EditCommand(TARGET_BENSON,
                new EditPersonDescriptorBuilder().withName(ALICE.getName().toString()).build());
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(BENSON, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editOwnNameWithDifferentCaseAndSpacing_success() {
        String editedName = ALICE.getName().toString()
                .toUpperCase(Locale.ROOT)
                .replace(" ", "  ");
        Person editedPerson = new PersonBuilder(ALICE).withName(editedName).build();
        EditCommand editCommand = new EditCommand(TARGET_ALICE,
                new EditPersonDescriptorBuilder().withName(editedName).build());
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(PersonTarget.ofId(new PersonId(1)), DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(PersonTarget.ofId(new PersonId(1)), copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different ID -> returns false
        assertFalse(standardCommand.equals(new EditCommand(PersonTarget.ofId(new PersonId(2)), DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(PersonTarget.ofId(new PersonId(1)), DESC_BOB)));
    }

    @Test
    public void execute_validIdSomeFieldsSpecified_success() {
        // only the phone is given, so every other field and the ID stay the same
        Person editedPerson = new PersonBuilder(BENSON).withPhone(VALID_PHONE_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(TARGET_BENSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(BENSON, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIdOfPersonHiddenByFilter_success() {
        // only ALICE is displayed, but BENSON can still be edited by his ID
        showOnlyPerson(model, ALICE);
        Person editedPerson = new PersonBuilder(BENSON).withPhone(VALID_PHONE_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(TARGET_BENSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        // edit shows every person afterwards
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(BENSON, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownId_failure() {
        EditCommand editCommand = new EditCommand(PersonTarget.ofId(new PersonId(99)), DESC_BOB);

        assertCommandFailure(editCommand, model, String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, 99));
    }

    @Test
    public void toStringMethod() {
        PersonTarget target = PersonTarget.ofId(new PersonId(1));
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(target, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{target=" + target
                + ", editPersonDescriptor=" + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}

package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.SortField;
import seedu.address.model.person.SortOrder;
import seedu.address.testutil.AddressBookBuilder;

public class SortCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SortCommand(null, SortOrder.ASCENDING));
    }

    @Test
    public void constructor_nullOrder_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SortCommand(SortField.NAME, null));
    }

    @Test
    public void execute_allFieldsAndOrdersOnEmptyList_returnsExactSuccessMessages() {
        model = new ModelManager();

        for (SortField field : SortField.values()) {
            for (SortOrder order : SortOrder.values()) {
                CommandResult result = new SortCommand(field, order).execute(model);
                String fieldDescription = field.name().toLowerCase(Locale.ROOT);
                String orderDescription = order == SortOrder.ASCENDING ? "ascending" : "descending";
                String expectedMessage = String.format(
                        SortCommand.MESSAGE_SORT_SUCCESS, fieldDescription, orderDescription);

                assertEquals(expectedMessage, result.getFeedbackToUser());
                assertTrue(model.getFilteredPersonList().isEmpty());
            }
        }
    }

    @Test
    public void execute_singlePersonList_success() {
        model = new ModelManager(new AddressBookBuilder().withPerson(ALICE).build(), new UserPrefs());

        CommandResult result = new SortCommand(SortField.NAME, SortOrder.ASCENDING).execute(model);

        assertEquals("Sorted contacts by name in ascending order.", result.getFeedbackToUser());
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_fullListDescending_sortsDisplayedList() {
        CommandResult result = new SortCommand(SortField.NAME, SortOrder.DESCENDING).execute(model);

        assertEquals("Sorted contacts by name in descending order.", result.getFeedbackToUser());
        assertEquals(List.of(GEORGE, FIONA, ELLE, DANIEL, CARL, BENSON, ALICE),
                model.getFilteredPersonList());
    }

    @Test
    public void execute_everySupportedField_sortsBySelectedField() {
        assertAscendingSort(SortField.NAME,
                List.of(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
        assertAscendingSort(SortField.PHONE,
                List.of(ELLE, FIONA, GEORGE, DANIEL, ALICE, CARL, BENSON));
        assertAscendingSort(SortField.EMAIL,
                List.of(ALICE, GEORGE, DANIEL, CARL, BENSON, FIONA, ELLE));
        assertAscendingSort(SortField.ADDRESS,
                List.of(DANIEL, ALICE, BENSON, GEORGE, FIONA, ELLE, CARL));
        assertAscendingSort(SortField.TAG,
                List.of(ALICE, BENSON, DANIEL, CARL, ELLE, FIONA, GEORGE));
    }

    @Test
    public void execute_filteredList_sortsWithoutChangingMembership() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Meier"));
        model.updateFilteredPersonList(predicate);

        new SortCommand(SortField.NAME, SortOrder.DESCENDING).execute(model);

        assertEquals(List.of(DANIEL, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_sortDisplayedList_doesNotChangeStoredOrder() {
        new SortCommand(SortField.EMAIL, SortOrder.DESCENDING).execute(model);

        assertEquals(getTypicalPersons(), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_listAfterSort_clearsSort() {
        new SortCommand(SortField.NAME, SortOrder.DESCENDING).execute(model);

        new ListCommand().execute(model);

        assertEquals(getTypicalPersons(), model.getFilteredPersonList());
    }

    @Test
    public void execute_newFindAfterSort_clearsSort() {
        new SortCommand(SortField.NAME, SortOrder.DESCENDING).execute(model);
        NameContainsKeywordsPredicate predicate =
                new NameContainsKeywordsPredicate(List.of("Kurz", "Elle", "Kunz"));

        new FindCommand(predicate).execute(model);

        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_idBasedCommandAfterSort_ignoresDisplayedOrder() throws Exception {
        new SortCommand(SortField.NAME, SortOrder.DESCENDING).execute(model);
        Person firstDisplayedPerson = model.getFilteredPersonList().get(0);

        // ID 1 is ALICE, even though GEORGE is now displayed first
        CommandResult result = new DeleteCommand(PersonTarget.ofId(new PersonId(1))).execute(model);

        assertEquals(GEORGE, firstDisplayedPerson);
        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(ALICE)),
                result.getFeedbackToUser());
        assertFalse(model.hasPerson(ALICE));
        assertTrue(model.hasPerson(GEORGE));
    }

    @Test
    public void equals() {
        SortCommand sortNameAscending = new SortCommand(SortField.NAME, SortOrder.ASCENDING);
        SortCommand sortNameAscendingCopy = new SortCommand(SortField.NAME, SortOrder.ASCENDING);
        SortCommand sortPhoneAscending = new SortCommand(SortField.PHONE, SortOrder.ASCENDING);
        SortCommand sortNameDescending = new SortCommand(SortField.NAME, SortOrder.DESCENDING);

        assertTrue(sortNameAscending.equals(sortNameAscending));
        assertTrue(sortNameAscending.equals(sortNameAscendingCopy));
        assertFalse(sortNameAscending.equals(sortPhoneAscending));
        assertFalse(sortNameAscending.equals(sortNameDescending));
        assertFalse(sortNameAscending.equals(1));
        assertFalse(sortNameAscending.equals(null));
    }

    @Test
    public void toStringMethod() {
        SortCommand sortCommand = new SortCommand(SortField.NAME, SortOrder.DESCENDING);
        String expected = SortCommand.class.getCanonicalName()
                + "{sortField=NAME, sortOrder=DESCENDING}";

        assertEquals(expected, sortCommand.toString());
    }

    private void assertAscendingSort(SortField field, List<Person> expectedPersons) {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        new SortCommand(field, SortOrder.ASCENDING).execute(model);

        assertEquals(expectedPersons, model.getFilteredPersonList());
    }
}

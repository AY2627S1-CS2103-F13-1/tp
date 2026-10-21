package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showOnlyPerson;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand.View;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private final Person archivedAlice = new PersonBuilder(ALICE).withArchiveStatus(true).build();

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.setPerson(ALICE, archivedAlice);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_activeView_hidesArchivedContacts() {
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE_PERSONS);
        assertCommandSuccess(new ListCommand(View.ACTIVE), model, ListCommand.MESSAGE_ACTIVE_SUCCESS, expectedModel);
        assertFalse(model.getFilteredPersonList().contains(archivedAlice));
        assertEquals(model.getAddressBook().getPersonList().size() - 1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_allView_showsEveryContact() {
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        assertCommandSuccess(new ListCommand(View.ALL), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_archivedView_showsOnlyArchivedContacts() {
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED_PERSONS);
        assertCommandSuccess(new ListCommand(View.ARCHIVED), model, ListCommand.MESSAGE_ARCHIVED_SUCCESS,
                expectedModel);
        assertEquals(List.of(archivedAlice), model.getFilteredPersonList());
    }

    @Test
    public void execute_listIsFiltered_replacesFilter() {
        showOnlyPerson(model, archivedAlice);
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE_PERSONS);
        assertCommandSuccess(new ListCommand(View.ACTIVE), model, ListCommand.MESSAGE_ACTIVE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        ListCommand activeCommand = new ListCommand(View.ACTIVE);

        assertTrue(activeCommand.equals(activeCommand));
        assertTrue(activeCommand.equals(new ListCommand(View.ACTIVE)));
        assertFalse(activeCommand.equals(new ListCommand(View.ALL)));
        assertFalse(activeCommand.equals(new ListCommand(View.ARCHIVED)));
        assertFalse(activeCommand.equals(null));
        assertFalse(activeCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        assertEquals(ListCommand.class.getCanonicalName() + "{view=ARCHIVED}",
                new ListCommand(View.ARCHIVED).toString());
    }
}

package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_success() throws Exception {
        Person personToRemark = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person remarkedPerson = new PersonBuilder(personToRemark)
                .withRemark("Met at conference")
                .build();

        RemarkCommand command =
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Met at conference"));

        String expectedMessage =
                String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, Messages.format(remarkedPerson));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToRemark, remarkedPerson);

        CommandResult result = command.execute(model);

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_emptyRemark_success() throws Exception {
        Person personToRemark = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person remarkedPerson = new PersonBuilder(personToRemark)
                .withRemark("")
                .build();

        RemarkCommand command =
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));

        String expectedMessage =
                String.format(RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS, Messages.format(remarkedPerson));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToRemark, remarkedPerson);

        CommandResult result = command.execute(model);

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        RemarkCommand command =
                new RemarkCommand(INDEX_SECOND_PERSON, new Remark("Test"));

        model.updateFilteredPersonList(person -> false);

        assertThrows(CommandException.class,
                MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () -> command.execute(model));
    }
}

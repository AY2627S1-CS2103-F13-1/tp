package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        // the typical persons have IDs 1 to 7, so the new person gets ID 8
        Person addedPerson = new PersonBuilder(validPerson).withId("8").build();
        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(addedPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonWithNormalizedName_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        String duplicateNameVariant = personInList.getName().toString()
                .toUpperCase(Locale.ROOT)
                .replace(" ", "  ");
        Person duplicatePerson = new PersonBuilder(personInList).withName(duplicateNameVariant).build();

        assertCommandFailure(new AddCommand(duplicatePerson), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_noIdsLeft_throwsCommandException() {
        Model fullModel = new ModelManager(new AddressBook(PersonId.MAX_VALUE + 1), new UserPrefs());
        assertCommandFailure(new AddCommand(new PersonBuilder().build()), fullModel,
                AddCommand.MESSAGE_NO_AVAILABLE_ID);
    }

}

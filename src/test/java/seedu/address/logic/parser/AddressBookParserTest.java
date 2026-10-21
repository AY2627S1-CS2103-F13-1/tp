package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.PersonTarget;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.SortField;
import seedu.address.model.person.SortOrder;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private static final PersonId TARGET_FIRST_ID = new PersonId(1);

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + TARGET_FIRST_ID.getValue());
        assertEquals(new DeleteCommand(PersonTarget.ofId(TARGET_FIRST_ID)), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + TARGET_FIRST_ID.getValue() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(PersonTarget.ofId(TARGET_FIRST_ID), descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_findWithNamePrefix_returnsFieldFindCommand() throws Exception {
        assertFindResults("find n/Alice Pauline", ALICE);
    }

    @Test
    public void parseCommand_findWithoutPrefix_returnsAnyFieldFindCommand() throws Exception {
        assertFindResults("find Clementi", BENSON);
    }

    @Test
    public void parseCommand_findWithMultiplePrefixes_returnsFieldFindCommand() throws Exception {
        assertFindResults("find t/fri p/987 a/Clementi n/Ben t/Money e/@example", BENSON);
    }

    @Test
    public void parseCommand_findWithGeneralAndPrefixes_matchesCombinedConditions() throws Exception {
        assertFindResults("find example n/Benson t/Money", BENSON);
        assertFindResults("find n/Benson example");
    }

    private void assertFindResults(String input, Person... expectedPersons) throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        FindCommand command = (FindCommand) parser.parseCommand(input);
        command.execute(model);
        assertEquals(List.of(expectedPersons), model.getFilteredPersonList());
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertEquals(new ListCommand(ListCommand.View.ACTIVE), parser.parseCommand(ListCommand.COMMAND_WORD));
        assertEquals(new ListCommand(ListCommand.View.ALL), parser.parseCommand(ListCommand.COMMAND_WORD + " all"));
        assertEquals(new ListCommand(ListCommand.View.ARCHIVED),
                parser.parseCommand(ListCommand.COMMAND_WORD + " archive"));
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, ()
            -> parser.parseCommand(ListCommand.COMMAND_WORD + " foo"));
    }

    @Test
    public void parseCommand_sort() throws Exception {
        SortCommand command = (SortCommand) parser.parseCommand(SortCommand.COMMAND_WORD + " name desc");
        assertEquals(new SortCommand(SortField.NAME, SortOrder.DESCENDING), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, ()
                -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_tag() throws Exception {
        assertEquals(new TagCommand(PersonTarget.ofId(TARGET_FIRST_ID), Set.of(new Tag("expert"))),
                parser.parseCommand("tag 1 t/expert"));
    }

    @Test
    public void parseCommand_untag() throws Exception {
        assertEquals(new UntagCommand(PersonTarget.ofId(TARGET_FIRST_ID), Set.of(new Tag("expert"))),
                parser.parseCommand("untag 1 t/expert"));
    }
}

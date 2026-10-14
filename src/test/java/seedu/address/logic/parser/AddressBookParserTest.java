package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Optional;
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
import seedu.address.model.person.AnyFieldContainsSubstringPredicate;
import seedu.address.model.person.EachFieldContainsSubstringPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.SortField;
import seedu.address.model.person.SortOrder;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

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
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_deleteById() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(DeleteCommand.COMMAND_WORD + " id/C1");
        assertEquals(new DeleteCommand(PersonTarget.ofId(new PersonId("C1"))), command);
    }

    @Test
    public void parseCommand_editById() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " id/C1 "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(PersonTarget.ofId(new PersonId("C1")), descriptor), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_findWithNamePrefix_returnsFieldFindCommand() throws Exception {
        String substring = "Alice Pauline";
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + substring);
        assertEquals(new FindCommand(new EachFieldContainsSubstringPredicate(Optional.of(substring),
                Optional.empty(), Optional.empty(), Optional.empty(), List.of())), command);
    }

    @Test
    public void parseCommand_findWithoutPrefix_returnsAnyFieldFindCommand() throws Exception {
        String substring = "Main Street";
        FindCommand command = (FindCommand) parser.parseCommand(FindCommand.COMMAND_WORD + " " + substring);
        assertEquals(new FindCommand(new AnyFieldContainsSubstringPredicate(substring)), command);
    }

    @Test
    public void parseCommand_findWithMultiplePrefixes_returnsFieldFindCommand() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(
                "find t/fri p/2345 a/Main Street n/Al t/col e/@example");
        assertEquals(new FindCommand(new EachFieldContainsSubstringPredicate(Optional.of("Al"),
                Optional.of("2345"), Optional.of("@example"), Optional.of("Main Street"),
                List.of("fri", "col"))), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " archived") instanceof ListCommand);
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
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, Set.of(new Tag("expert"))),
                parser.parseCommand("tag 1 t/expert"));
    }

    @Test
    public void parseCommand_untag() throws Exception {
        assertEquals(new UntagCommand(INDEX_FIRST_PERSON, Set.of(new Tag("expert"))),
                parser.parseCommand("untag 1 t/expert"));
    }

    @Test
    public void parseCommand_untagById() throws Exception {
        assertEquals(new UntagCommand(PersonTarget.ofId(new PersonId("C1")), Set.of(new Tag("expert"))),
                parser.parseCommand("untag id/C1 t/expert"));
    }
}

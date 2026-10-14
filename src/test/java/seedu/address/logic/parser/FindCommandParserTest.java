package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AnyFieldContainsSubstringPredicate;
import seedu.address.model.person.EachFieldContainsSubstringPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "     ", expectedMessage);
        assertParseFailure(parser, " \n \t ", expectedMessage);
    }

    @Test
    public void parse_noPrefix_returnsAnyFieldFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new AnyFieldContainsSubstringPredicate("Al"));
        assertParseSuccess(parser, "Al", expectedFindCommand);
        assertParseSuccess(parser, " \n \t Al \t \n ", expectedFindCommand);
    }

    @Test
    public void parse_noPrefixMultipleWords_preservesSubstring() {
        FindCommand expectedFindCommand =
                new FindCommand(new AnyFieldContainsSubstringPredicate("Alice Bob"));
        assertParseSuccess(parser, " Alice Bob ", expectedFindCommand);

        // Only surrounding whitespace is removed; internal whitespace remains part of the substring
        expectedFindCommand = new FindCommand(new AnyFieldContainsSubstringPredicate("Alice  \t Bob"));
        assertParseSuccess(parser, " Alice  \t Bob ", expectedFindCommand);
    }

    @Test
    public void parse_noPrefixOtherFieldValues_returnsAnyFieldFindCommand() {
        assertParseSuccess(parser, " 12345",
                new FindCommand(new AnyFieldContainsSubstringPredicate("12345")));
        assertParseSuccess(parser, " @example.com",
                new FindCommand(new AnyFieldContainsSubstringPredicate("@example.com")));
        assertParseSuccess(parser, " Main Street",
                new FindCommand(new AnyFieldContainsSubstringPredicate("Main Street")));
        assertParseSuccess(parser, " friends",
                new FindCommand(new AnyFieldContainsSubstringPredicate("friends")));
    }

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " n/Alice n/Alice",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_namePrefix_returnsFieldFindCommand() {
        FindCommand expectedFindCommand = createFieldFindCommand("Alice Bob", "", "", "", List.of());
        assertParseSuccess(parser, " n/Alice Bob", expectedFindCommand);
        assertParseSuccess(parser, " \n n/ Alice Bob \t", expectedFindCommand);

        // Internal whitespace remains part of the substring
        expectedFindCommand = createFieldFindCommand("Alice \n \t Bob", "", "", "", List.of());
        assertParseSuccess(parser, " \n n/ Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_namePrefixPartialName_returnsFieldFindCommand() {
        FindCommand expectedFindCommand = createFieldFindCommand("Al", "", "", "", List.of());
        assertParseSuccess(parser, " n/Al", expectedFindCommand);
    }

    @Test
    public void parse_otherSinglePrefixes_returnsFieldFindCommand() {
        assertParseSuccess(parser, " p/2345", createFieldFindCommand("", "2345", "", "", List.of()));
        assertParseSuccess(parser, " e/@example", createFieldFindCommand("", "", "@example", "", List.of()));
        assertParseSuccess(parser, " a/Main Street",
                createFieldFindCommand("", "", "", "Main Street", List.of()));
        assertParseSuccess(parser, " t/fri", createFieldFindCommand("", "", "", "", List.of("fri")));
    }

    @Test
    public void parse_multiplePrefixesInAnyOrder_returnsFieldFindCommand() {
        FindCommand expectedFindCommand =
                createFieldFindCommand("Al", "2345", "@example", "Main Street", List.of("fri", "col"));
        assertParseSuccess(parser, " n/Al p/2345 e/@example a/Main Street t/fri t/col", expectedFindCommand);
        assertParseSuccess(parser, " t/fri a/Main Street e/@example t/col p/2345 n/Al", expectedFindCommand);
    }

    @Test
    public void parse_repeatedTagPrefixes_returnsFieldFindCommand() {
        assertParseSuccess(parser, " t/fri t/col",
                createFieldFindCommand("", "", "", "", List.of("fri", "col")));
        assertParseSuccess(parser, " t/fri t/fri",
                createFieldFindCommand("", "", "", "", List.of("fri", "fri")));
    }

    @Test
    public void parse_duplicateOtherFieldPrefixes_throwsParseException() {
        assertParseFailure(parser, " p/123 p/456",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
        assertParseFailure(parser, " e/alice e/bob",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
        assertParseFailure(parser, " a/Main a/Orchard",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_preambleBeforePrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" Alice n/Bob"));
        assertThrows(ParseException.class, () -> parser.parse(" Alice t/fri"));
    }

    /**
     * Creates a field search command, using empty strings for unspecified fields.
     */
    private FindCommand createFieldFindCommand(String name, String phone, String email,
            String address, List<String> tags) {
        return new FindCommand(new EachFieldContainsSubstringPredicate(
                Optional.of(name).filter(value -> !value.isEmpty()),
                Optional.of(phone).filter(value -> !value.isEmpty()),
                Optional.of(email).filter(value -> !value.isEmpty()),
                Optional.of(address).filter(value -> !value.isEmpty()), tags));
    }

}

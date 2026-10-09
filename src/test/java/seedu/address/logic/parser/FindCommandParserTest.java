package seedu.address.logic.parser;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.AnyFieldContainsSubstringPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

import java.util.List;

import org.junit.jupiter.api.Test;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " \n \t ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
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
    public void parse_namePrefix_returnsNameFindCommand() {
        // no whitespace between the prefix and the first keyword
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, " n/Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n n/ Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_namePrefixPartialName_returnsNameFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Al")));
        assertParseSuccess(parser, " n/Al", expectedFindCommand);
    }

}

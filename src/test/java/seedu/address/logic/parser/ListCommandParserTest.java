package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListCommand.View;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noKeyword_returnsActiveView() {
        assertParseSuccess(parser, "", new ListCommand(View.ACTIVE));
        assertParseSuccess(parser, "   ", new ListCommand(View.ACTIVE));
    }

    @Test
    public void parse_allKeyword_returnsAllView() {
        assertParseSuccess(parser, " all", new ListCommand(View.ALL));
        assertParseSuccess(parser, "  All  ", new ListCommand(View.ALL));
    }

    @Test
    public void parse_archiveKeyword_returnsArchivedView() {
        assertParseSuccess(parser, " archive", new ListCommand(View.ARCHIVED));
        assertParseSuccess(parser, " ARCHIVE", new ListCommand(View.ARCHIVED));
    }

    @Test
    public void parse_invalidArgs_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " archiv", expectedMessage);
        assertParseFailure(parser, " active", expectedMessage);
        assertParseFailure(parser, " archived", expectedMessage);
        assertParseFailure(parser, " 3", expectedMessage);
        assertParseFailure(parser, " all foo", expectedMessage);
        assertParseFailure(parser, " archive all", expectedMessage);
    }
}

package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RedoCommand;

public class RedoCommandParserTest {

    private final RedoCommandParser parser = new RedoCommandParser();

    @Test
    public void parse_noArgs_success() {
        assertParseSuccess(parser, "", new RedoCommand());
        assertParseSuccess(parser, "   ", new RedoCommand());
    }

    @Test
    public void parse_anyArgs_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RedoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 2", expectedMessage);
        assertParseFailure(parser, " all", expectedMessage);
    }
}

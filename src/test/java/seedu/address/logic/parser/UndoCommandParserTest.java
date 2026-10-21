package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UndoCommand;

public class UndoCommandParserTest {

    private final UndoCommandParser parser = new UndoCommandParser();

    @Test
    public void parse_noArgs_success() {
        assertParseSuccess(parser, "", new UndoCommand());
        assertParseSuccess(parser, "   ", new UndoCommand());
    }

    @Test
    public void parse_anyArgs_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 3", expectedMessage);
        assertParseFailure(parser, " all", expectedMessage);
    }
}

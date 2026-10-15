package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.PersonTarget;
import seedu.address.model.person.PersonId;

public class ArchiveCommandParserTest {

    private final ArchiveCommandParser parser = new ArchiveCommandParser();

    @Test
    public void parse_validId_success() {
        assertParseSuccess(parser, " 2", new ArchiveCommand(PersonTarget.ofId(new PersonId(2))));
    }

    @Test
    public void parse_invalidArgs_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " hello", expectedMessage);
        assertParseFailure(parser, " C2", expectedMessage);
        assertParseFailure(parser, " id/2", expectedMessage);
    }
}

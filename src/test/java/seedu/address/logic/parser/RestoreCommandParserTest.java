package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.PersonTarget;
import seedu.address.logic.commands.RestoreCommand;
import seedu.address.model.person.PersonId;

public class RestoreCommandParserTest {

    private final RestoreCommandParser parser = new RestoreCommandParser();

    @Test
    public void parse_validId_success() {
        assertParseSuccess(parser, " 2", new RestoreCommand(PersonTarget.ofId(new PersonId(2))));
    }

    @Test
    public void parse_invalidArgs_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RestoreCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " hello", expectedMessage);
        assertParseFailure(parser, " C2", expectedMessage);
        assertParseFailure(parser, " id/2", expectedMessage);
    }
}

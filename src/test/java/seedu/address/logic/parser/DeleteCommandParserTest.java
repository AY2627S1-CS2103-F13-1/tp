package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.PersonTarget;
import seedu.address.model.person.PersonId;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occurs inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validId_returnsDeleteCommand() {
        assertParseSuccess(parser, "42", new DeleteCommand(PersonTarget.ofId(new PersonId(42))));

        // whitespace around the ID
        assertParseSuccess(parser, "  42  ", new DeleteCommand(PersonTarget.ofId(new PersonId(42))));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "a", expectedMessage);
        assertParseFailure(parser, "", expectedMessage); // missing ID
        assertParseFailure(parser, "C42", expectedMessage); // IDs are typed without the C
        assertParseFailure(parser, " id/42", expectedMessage); // id/ is no longer used
    }
}

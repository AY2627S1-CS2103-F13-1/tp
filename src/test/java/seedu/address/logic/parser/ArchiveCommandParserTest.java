package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.PersonTarget;
import seedu.address.model.person.PersonId;

public class ArchiveCommandParserTest {

    private final ArchiveCommandParser parser = new ArchiveCommandParser();

    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser, "1", new ArchiveCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validId_success() {
        assertParseSuccess(parser, " id/C2", new ArchiveCommand(PersonTarget.ofId(new PersonId("C2"))));
    }

    @Test
    public void parse_invalidId_failure() {
        assertParseFailure(parser, " id/hello", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                PersonId.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parse_indexAndId_failure() {
        assertParseFailure(parser, "1 id/C2", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ArchiveCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateId_failure() {
        assertParseFailure(parser, " id/C1 id/C2", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID));
    }
}

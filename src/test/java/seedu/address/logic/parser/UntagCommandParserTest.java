package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.PersonTarget;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

/**
 * Tests parsing of the untag command.
 */
public class UntagCommandParserTest {

    private final UntagCommandParser parser = new UntagCommandParser();

    @Test
    public void parse_singleTag_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        assertEquals(expected, parser.parse("1 t/expert"));
    }

    @Test
    public void parse_multipleTags_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                INDEX_FIRST_PERSON,
                Set.of(new Tag("expert"), new Tag("housing policy")));

        assertEquals(expected, parser.parse("1 t/expert t/housing policy"));
    }

    @Test
    public void parse_caseInsensitiveTags_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        assertEquals(expected, parser.parse("1 t/EXPERT"));
    }

    @Test
    public void parse_duplicateTags_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                INDEX_FIRST_PERSON, Set.of(new Tag("expert")));

        assertEquals(expected, parser.parse("1 t/expert t/EXPERT"));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("t/expert"));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("abc t/expert"));
        assertThrows(ParseException.class, () -> parser.parse("0 t/expert"));
        assertThrows(ParseException.class, () -> parser.parse("-1 t/expert"));
    }

    @Test
    public void parse_missingTags_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("1"));
        assertThrows(ParseException.class, () -> parser.parse("1 t/"));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("1 t/hello!"));
    }

    @Test
    public void parse_validId_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                PersonTarget.ofId(new PersonId("C2")), Set.of(new Tag("expert")));

        assertEquals(expected, parser.parse(" id/C2 t/expert"));
    }

    @Test
    public void parse_indexAndId_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("1 id/C2 t/expert"));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UntagCommand.MESSAGE_USAGE),
                exception.getMessage());
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse(" id/hello t/expert"));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PersonId.MESSAGE_CONSTRAINTS),
                exception.getMessage());
    }

    @Test
    public void parse_duplicateId_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () ->
                parser.parse(" id/C1 id/C2 t/expert"));
        assertEquals(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID), exception.getMessage());
    }

    @Test
    public void parse_idWithoutTags_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse(" id/C2"));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UntagCommand.MESSAGE_USAGE),
                exception.getMessage());
    }
}

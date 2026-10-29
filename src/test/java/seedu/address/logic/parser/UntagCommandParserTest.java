package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;

/**
 * Tests parsing of the untag command.
 */
public class UntagCommandParserTest {

    private static final PersonId TARGET_FIRST = new PersonId(1);

    private final UntagCommandParser parser = new UntagCommandParser();

    @Test
    public void parse_singleTag_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                TARGET_FIRST, Set.of(new Tag("expert")));

        assertEquals(expected, parser.parse("1 t/expert"));
    }

    @Test
    public void parse_multipleTags_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                TARGET_FIRST,
                Set.of(new Tag("expert"), new Tag("housing policy")));

        assertEquals(expected, parser.parse("1 t/expert t/housing policy"));
    }

    @Test
    public void parse_caseInsensitiveTags_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                TARGET_FIRST, Set.of(new Tag("expert")));

        assertEquals(expected, parser.parse("1 t/EXPERT"));
    }

    @Test
    public void parse_duplicateTags_success() throws ParseException {
        UntagCommand expected = new UntagCommand(
                TARGET_FIRST, Set.of(new Tag("expert")));

        assertEquals(expected, parser.parse("1 t/expert t/EXPERT"));
    }

    @Test
    public void parse_missingId_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("t/expert"));
    }

    @Test
    public void parse_invalidId_throwsParseException() {
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
    public void parse_idWithCOrPrefix_throwsParseException() {
        // IDs are typed as plain numbers, without the C or id/
        for (String input : new String[] {"C2 t/expert", " id/2 t/expert"}) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse(input));
            assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UntagCommand.MESSAGE_USAGE),
                    exception.getMessage());
        }
    }
}

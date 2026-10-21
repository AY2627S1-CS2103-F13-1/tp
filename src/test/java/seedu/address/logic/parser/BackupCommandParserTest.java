package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.commands.LoadCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Covers backup path normalization and incomplete quotes across platform path rules. */
public class BackupCommandParserTest {
    private final BackupCommandParser parser = new BackupCommandParser();

    @Test
    public void parseLoad_optionalQuotesAndExtension_preservesSpaces() throws Exception {
        Path expected = Path.of("My Contacts", "October backup.json");
        assertEquals(expected, parser.parseLoad("  My Contacts/October backup  ").getPath());
        assertEquals(expected, parser.parseLoad("  \"My Contacts/October backup\"  ").getPath());
        assertEquals(expected, parser.parseLoad("My Contacts/October backup.json").getPath());
    }

    @Test
    public void parse_incompleteQuotes_obeysPlatformPathRules() throws Exception {
        for (String argument : new String[] {"\"", "\"contacts", "contacts\""}) {
            // Windows rejects quote characters in filenames; Unix permits them as literal characters.
            if (File.separatorChar == '\\') {
                assertEquals(LoadCommand.MESSAGE_USAGE,
                        assertThrows(ParseException.class, () -> parser.parseLoad(argument)).getMessage());
                assertEquals(ExportCommand.MESSAGE_USAGE,
                        assertThrows(ParseException.class, () -> parser.parseExport(argument)).getMessage());
            } else {
                assertEquals(Path.of(argument + ".json"), parser.parseLoad(argument).getPath());
            }
        }
    }

    @Test
    public void parse_blankQuotedPath_reportsCorrectCommandUsage() {
        for (String argument : new String[] {"", "   ", "\"\"", "\"   \""}) {
            assertEquals(LoadCommand.MESSAGE_USAGE,
                    assertThrows(ParseException.class, () -> parser.parseLoad(argument)).getMessage());
            assertEquals(ExportCommand.MESSAGE_USAGE,
                    assertThrows(ParseException.class, () -> parser.parseExport(argument)).getMessage());
        }
    }
}

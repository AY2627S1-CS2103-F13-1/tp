package seedu.address.logic.parser;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;

import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.commands.LoadCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses backup commands; the path is the remaining input and may contain spaces. */
public class BackupCommandParser {
    /** Parses an export destination. */
    public ExportCommand parseExport(String arguments) throws ParseException {
        return new ExportCommand(parsePath(arguments, ExportCommand.MESSAGE_USAGE));
    }

    /** Parses a backup to load after protecting non-default current data. */
    public LoadCommand parseLoad(String arguments) throws ParseException {
        return new LoadCommand(parsePath(arguments, LoadCommand.MESSAGE_USAGE));
    }

    private Path parsePath(String argument, String usage) throws ParseException {
        String path = argument.trim();
        if (path.startsWith("\"") && path.endsWith("\"") && path.length() >= 2) {
            path = path.substring(1, path.length() - 1);
        }
        if (path.isBlank()) {
            throw new ParseException(usage);
        }
        if (!path.toLowerCase(Locale.ROOT).endsWith(".json")) {
            path += ".json";
        }
        try {
            return Path.of(path);
        } catch (InvalidPathException e) {
            throw new ParseException(usage, e);
        }
    }
}

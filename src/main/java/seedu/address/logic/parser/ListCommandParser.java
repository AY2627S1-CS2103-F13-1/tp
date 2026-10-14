package seedu.address.logic.parser;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListCommand.View;

/**
 * Adds explicit contact views while preserving list's existing handling of other arguments.
 */
public class ListCommandParser implements Parser<ListCommand> {
    @Override
    public ListCommand parse(String args) {
        return switch (args.trim()) {
            case "active" -> new ListCommand(View.ACTIVE);
            case "archived" -> new ListCommand(View.ARCHIVED);
            default -> new ListCommand();
        };
    }
}

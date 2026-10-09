package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists active contacts by default, or archived contacts on request.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = "list: Lists active contacts, or archived contacts.\n"
            + "Parameters: [archived]\nExample: list archived";
    public static final String MESSAGE_SUCCESS = "Listed all active persons.";
    public static final String MESSAGE_ARCHIVED_SUCCESS = "Listed all archived persons.";

    private final boolean archived;

    public ListCommand() {
        this(false);
    }

    public ListCommand(boolean archived) {
        this.archived = archived;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(archived
                ? Model.PREDICATE_SHOW_ARCHIVED_PERSONS : Model.PREDICATE_SHOW_ACTIVE_PERSONS);
        return new CommandResult(archived ? MESSAGE_ARCHIVED_SUCCESS : MESSAGE_SUCCESS);
    }
}

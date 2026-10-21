package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;

/**
 * Lists active contacts by default, with optional views for all or archived contacts.
 */
public class ListCommand extends Command {
    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows contacts. With no keyword, only active (not archived) contacts are shown.\n"
            + "Parameters: [all | archive]\n"
            + "Examples: " + COMMAND_WORD + ", " + COMMAND_WORD + " all, " + COMMAND_WORD + " archive";

    public static final String MESSAGE_SUCCESS = "Listed all persons.";
    public static final String MESSAGE_ACTIVE_SUCCESS = "Listed all active persons.";
    public static final String MESSAGE_ARCHIVED_SUCCESS = "Listed all archived persons.";

    /**
     * The contact subset displayed by the list command.
     */
    public enum View {
        ALL, ACTIVE, ARCHIVED
    }

    private final View view;

    public ListCommand(View view) {
        this.view = requireNonNull(view);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return switch (view) {
            case ALL -> {
                model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
                yield new CommandResult(MESSAGE_SUCCESS);
            }
            case ACTIVE -> {
                model.updateFilteredPersonList(Model.PREDICATE_SHOW_ACTIVE_PERSONS);
                yield new CommandResult(MESSAGE_ACTIVE_SUCCESS);
            }
            case ARCHIVED -> {
                model.updateFilteredPersonList(Model.PREDICATE_SHOW_ARCHIVED_PERSONS);
                yield new CommandResult(MESSAGE_ARCHIVED_SUCCESS);
            }
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListCommand otherListCommand)) {
            return false;
        }

        return view == otherListCommand.view;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("view", view)
                .toString();
    }
}

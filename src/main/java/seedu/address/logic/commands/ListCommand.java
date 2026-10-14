package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists all contacts by default, with optional active and archived views.
 */
public class ListCommand extends Command {
    public static final String COMMAND_WORD = "list";
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

    public ListCommand() {
        this(View.ALL);
    }

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
}

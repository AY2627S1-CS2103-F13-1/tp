package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists persons using a substring across all fields or substrings within specified fields.
 * Substring matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds persons by substring, ignoring case, and displays them as a list.\n"
            + "Search all fields: " + COMMAND_WORD + " SUBSTRING\n"
            + "Search specific fields: " + COMMAND_WORD
            + " [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]...\n"
            + "Specify at least one prefix for a field search. All conditions must match.\n"
            + "Use each prefix at most once, except t/, which may be repeated.\n"
            + "Examples: " + COMMAND_WORD + " oak; " + COMMAND_WORD + " n/Al e/example.com";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}

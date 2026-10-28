package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * Selects the person a command acts on by its ID.
 */
public class PersonTarget {

    private final PersonId id;

    private PersonTarget(PersonId id) {
        this.id = id;
    }

    /**
     * Returns a target that selects the person with the ID {@code id}.
     */
    public static PersonTarget ofId(PersonId id) {
        requireNonNull(id);
        return new PersonTarget(id);
    }

    /**
     * Returns the person in {@code model} that this target selects.
     * The ID is looked up in the whole address book, so a person hidden from the displayed list can still be
     * selected.
     *
     * @throws CommandException if no person has the ID.
     */
    public Person resolve(Model model) throws CommandException {
        requireNonNull(model);
        return model.findPersonById(id).orElseThrow(() ->
                new CommandException(String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, id.getValue())));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonTarget otherPersonTarget)) {
            return false;
        }

        return id.equals(otherPersonTarget.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .toString();
    }
}

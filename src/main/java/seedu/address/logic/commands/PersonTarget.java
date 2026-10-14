package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * Selects the person a command acts on, either by its index in the displayed person list or by its ID.
 */
public class PersonTarget {

    // exactly one of these is set, depending on how the person was selected
    private final Index index;
    private final PersonId id;

    private PersonTarget(Index index, PersonId id) {
        this.index = index;
        this.id = id;
    }

    /**
     * Returns a target that selects the person at {@code index} in the displayed person list.
     */
    public static PersonTarget ofIndex(Index index) {
        requireNonNull(index);
        return new PersonTarget(index, null);
    }

    /**
     * Returns a target that selects the person with the ID {@code id}.
     */
    public static PersonTarget ofId(PersonId id) {
        requireNonNull(id);
        return new PersonTarget(null, id);
    }

    /**
     * Returns the person in {@code model} that this target selects.
     * An ID is looked up in the whole address book, so a person hidden from the displayed list can still be
     * selected by its ID. An ID is never treated as an index.
     *
     * @throws CommandException if the index is outside the displayed person list, or no person has the ID.
     */
    public Person resolve(Model model) throws CommandException {
        requireNonNull(model);
        if (id != null) {
            return model.findPersonById(id).orElseThrow(() ->
                    new CommandException(String.format(Messages.MESSAGE_PERSON_ID_NOT_FOUND, id)));
        }

        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(index.getZeroBased());
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

        return Objects.equals(index, otherPersonTarget.index)
                && Objects.equals(id, otherPersonTarget.id);
    }

    @Override
    public int hashCode() {
        // Index does not override hashCode, so hash its value instead
        Integer zeroBasedIndex = (index == null) ? null : index.getZeroBased();
        return Objects.hash(zeroBasedIndex, id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("id", id)
                .toString();
    }
}

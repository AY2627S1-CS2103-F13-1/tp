package seedu.address.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code AddressBook} that remembers its earlier states, so that changes to it can be undone.
 * The history holds the state it started with, followed by up to {@code HISTORY_LIMIT} saved changes.
 * Each saved state includes the ID counter, so undoing an addition also frees the ID it used.
 */
public class VersionedAddressBook extends AddressBook {

    /** The maximum number of changes that can be undone. */
    public static final int HISTORY_LIMIT = 20;

    public static final String MESSAGE_NO_EARLIER_STATE = "There is no earlier state to undo to.";

    private final List<AddressBook> states = new ArrayList<>();
    private int currentStateIndex;

    /**
     * Creates a VersionedAddressBook with the data in {@code initialState} as its only saved state.
     */
    public VersionedAddressBook(ReadOnlyAddressBook initialState) {
        super(initialState);
        states.add(new AddressBook(initialState));
        currentStateIndex = 0;
    }

    /**
     * Saves the current data as a new state, unless it is the same as the last saved state.
     * Any undone states are discarded, and the oldest change is forgotten once more than
     * {@code HISTORY_LIMIT} changes are saved.
     */
    public void commit() {
        if (equals(states.get(currentStateIndex))) {
            return;
        }

        states.subList(currentStateIndex + 1, states.size()).clear();
        states.add(new AddressBook(this));
        if (states.size() > HISTORY_LIMIT + 1) {
            states.remove(0);
        }
        currentStateIndex = states.size() - 1;
    }

    /**
     * Returns true if there is a saved state before the current one.
     */
    public boolean canUndo() {
        return currentStateIndex > 0;
    }

    /**
     * Restores the data to the saved state before the current one.
     *
     * @throws IllegalStateException if there is no earlier saved state.
     */
    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException(MESSAGE_NO_EARLIER_STATE);
        }

        currentStateIndex--;
        resetData(states.get(currentStateIndex));
    }
}

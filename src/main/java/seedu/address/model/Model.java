package seedu.address.model;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** Selects active contacts for the default view and ordinary searches. */
    Predicate<Person> PREDICATE_SHOW_ACTIVE_PERSONS = person -> !person.isArchived();

    /** Selects archived contacts for the archive view. */
    Predicate<Person> PREDICATE_SHOW_ARCHIVED_PERSONS = Person::isArchived;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns true if a new person can still be assigned an ID.
     * IDs are never reused, so this stays false once {@code PersonId.MAX_VALUE} has been assigned.
     */
    boolean hasAvailablePersonId();

    /**
     * Returns the person with the ID {@code id}, or {@code Optional.empty()} if no person has that ID.
     * The whole address book is searched, including persons hidden from the filtered person list.
     */
    Optional<Person> findPersonById(PersonId id);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     *
     * @return the person as stored in the address book, with its assigned ID.
     */
    Person addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Saves the current address book data as a new state in the undo history,
     * unless it is the same as the last saved state.
     */
    void commitAddressBook();

    /**
     * Returns true if there is an earlier saved state to undo to.
     */
    boolean canUndoAddressBook();

    /**
     * Restores the address book to the saved state before the current one.
     * The current filter and sort of the displayed list are kept.
     *
     * @throws IllegalStateException if there is no earlier saved state.
     */
    void undoAddressBook();

    /**
     * Returns true if there is an undone state to redo.
     */
    boolean canRedoAddressBook();

    /**
     * Restores the address book to the state most recently undone.
     * The current filter and sort of the displayed list are kept.
     *
     * @throws IllegalStateException if there is no undone state.
     */
    void redoAddressBook();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Sorts the filtered person list using the given {@code comparator}.
     * Contacts that compare equally retain their current displayed order.
     *
     * @throws NullPointerException if {@code comparator} is null.
     */
    void sortFilteredPersonList(Comparator<Person> comparator);

    /** Clears the active sort on the filtered person list. */
    void clearFilteredPersonListSort();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}

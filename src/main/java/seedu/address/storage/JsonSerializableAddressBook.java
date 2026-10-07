package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_PERSON_ID = "Persons list contains duplicate person ID(s).";
    public static final String MESSAGE_INVALID_NEXT_PERSON_ID = "The next person ID must be a positive whole number.";

    private final Integer nextPersonId;
    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given next person ID and persons.
     * {@code nextPersonId} is null for data saved before IDs were introduced.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("nextPersonId") Integer nextPersonId,
            @JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        this.nextPersonId = nextPersonId;
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        nextPersonId = source.getNextPersonId();
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
    }

    /**
     * Returns true if this data was saved before IDs were introduced, or has persons without an ID,
     * and should be saved again once IDs have been assigned.
     */
    public boolean needsUpgrade() {
        return nextPersonId == null || persons.stream().anyMatch(person -> !person.hasId());
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     * Persons without an ID are assigned one above every saved ID and the saved next person ID,
     * so that no ID in use or previously handed out is reused.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        if (nextPersonId != null && nextPersonId < 1) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_PERSON_ID);
        }

        List<Person> modelPersons = new ArrayList<>();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            modelPersons.add(jsonAdaptedPerson.toModelType());
        }

        int startingNextPersonId = nextPersonId == null ? 1 : nextPersonId;
        Set<PersonId> savedIds = new HashSet<>();
        for (Person person : modelPersons) {
            if (person.getId().isEmpty()) {
                continue;
            }
            PersonId id = person.getId().get();
            if (!savedIds.add(id)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON_ID);
            }
            startingNextPersonId = Math.max(startingNextPersonId, id.getValue() + 1);
        }

        AddressBook addressBook = new AddressBook(startingNextPersonId);
        for (Person person : modelPersons) {
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        return addressBook;
    }

}

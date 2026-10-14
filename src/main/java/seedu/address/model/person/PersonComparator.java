package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Optional;

/**
 * Provides comparators for sorting contacts by supported fields.
 */
public final class PersonComparator {
    private static final Comparator<String> TEXT_COMPARATOR = String.CASE_INSENSITIVE_ORDER;

    private PersonComparator() {
    }

    /**
     * Returns a comparator for the given field and order.
     * Contacts with equal primary values are compared by name in ascending order, ignoring case.
     */
    public static Comparator<Person> getComparator(SortField field, SortOrder order) {
        requireNonNull(field);
        requireNonNull(order);

        return (firstPerson, secondPerson) -> {
            int primaryComparison = comparePrimaryValues(firstPerson, secondPerson, field, order);
            if (primaryComparison != 0) {
                return primaryComparison;
            }
            return TEXT_COMPARATOR.compare(
                    firstPerson.getName().fullName, secondPerson.getName().fullName);
        };
    }

    private static int comparePrimaryValues(Person firstPerson, Person secondPerson,
            SortField field, SortOrder order) {
        int comparison = switch (field) {
            case NAME -> compareText(firstPerson.getName().fullName, secondPerson.getName().fullName);
            case PHONE -> comparePhone(firstPerson, secondPerson);
            case EMAIL -> compareText(firstPerson.getEmail().value, secondPerson.getEmail().value);
            case ADDRESS -> compareText(firstPerson.getAddress().value, secondPerson.getAddress().value);
            case TAG -> comparePrimaryTags(firstPerson, secondPerson, order);
        };

        if (field == SortField.TAG || order == SortOrder.ASCENDING) {
            return comparison;
        }
        return -comparison;
    }

    private static int compareText(String firstValue, String secondValue) {
        return TEXT_COMPARATOR.compare(firstValue, secondValue);
    }

    private static int comparePhone(Person firstPerson, Person secondPerson) {
        BigInteger firstPhone = new BigInteger(firstPerson.getPhone().value);
        BigInteger secondPhone = new BigInteger(secondPerson.getPhone().value);
        return firstPhone.compareTo(secondPhone);
    }

    private static int comparePrimaryTags(Person firstPerson, Person secondPerson, SortOrder order) {
        Optional<String> firstTag = getPrimaryTag(firstPerson);
        Optional<String> secondTag = getPrimaryTag(secondPerson);

        if (firstTag.isEmpty()) {
            return secondTag.isEmpty() ? 0 : 1;
        }
        if (secondTag.isEmpty()) {
            return -1;
        }

        int comparison = compareText(firstTag.get(), secondTag.get());
        return order == SortOrder.ASCENDING ? comparison : -comparison;
    }

    private static Optional<String> getPrimaryTag(Person person) {
        return person.getTags().stream()
                .map(tag -> tag.tagName)
                .min(TEXT_COMPARATOR);
    }
}

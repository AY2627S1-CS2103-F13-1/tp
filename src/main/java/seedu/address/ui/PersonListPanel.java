package seedu.address.ui;

import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personList.addListener((javafx.collections.ListChangeListener<Person>) change -> {
            Person selected = personListView.getSelectionModel().getSelectedItem();
            if (shouldClearSelection(selected, personList)) {
                personListView.getSelectionModel().clearSelection();
            }
        });
    }

    /** Returns whether the selected contact is no longer in the displayed list. */
    static boolean shouldClearSelection(Person selected, ObservableList<Person> persons) {
        return selected != null && !persons.contains(selected);
    }

    /** Registers a listener for contact selection changes. */
    public void addSelectionListener(ChangeListener<? super Person> listener) {
        personListView.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    /** Clears the currently selected contact. */
    public void clearSelection() {
        personListView.getSelectionModel().clearSelection();
    }

    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person).getRoot());
            }
        }
    }

}

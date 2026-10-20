package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seedu.address.model.person.Person;

public class PersonListPanelTest extends ApplicationTest {

    private PersonListPanel panel;
    private ObservableList<Person> persons;

    @Override
    public void start(Stage stage) {
        persons = FXCollections.observableArrayList(ALICE, BOB);
        panel = new PersonListPanel(persons);

        stage.setScene(new Scene(panel.getRoot(), 600, 400));
        stage.show();
    }

    @Test
    public void selectPerson_notifiesListener() {
        AtomicReference<Person> selected = new AtomicReference<>();

        interact(() -> {
            panel.addSelectionListener((observable, previous, current) -> selected.set(current));

            ListView<Person> listView = lookup(".list-view").query();
            listView.getSelectionModel().select(ALICE);
        });

        assertEquals(ALICE, selected.get());
    }

    @Test
    public void clearSelection_removesSelectedPerson() {
        interact(() -> {
            ListView<Person> listView = lookup(".list-view").query();
            listView.getSelectionModel().select(ALICE);

            panel.clearSelection();

            assertNull(listView.getSelectionModel().getSelectedItem());
        });
    }

    @Test
    public void removeSelectedPerson_doesNotRetainRemovedSelection() {
        interact(() -> {
            ListView<Person> listView = lookup(".list-view").query();
            listView.getSelectionModel().select(ALICE);

            persons.remove(ALICE);

            assertNotEquals(ALICE, listView.getSelectionModel().getSelectedItem());
        });
    }
}

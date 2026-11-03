package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class MainWindowTest extends ApplicationTest {

    private MainWindow mainWindow;

    @Override
    public void start(Stage stage) {
        ObservableList<Person> persons = FXCollections.observableArrayList(ALICE, BOB);

        Logic logic = new Logic() {
            private GuiSettings guiSettings = new UserPrefs().getGuiSettings();

            @Override
            public CommandResult execute(String commandText)
                    throws CommandException, ParseException {
                return new CommandResult(commandText);
            }

            @Override
            public ObservableList<Person> getFilteredPersonList() {
                return persons;
            }

            @Override
            public GuiSettings getGuiSettings() {
                return guiSettings;
            }

            @Override
            public void setGuiSettings(GuiSettings settings) {
                guiSettings = settings;
            }
        };

        mainWindow = new MainWindow(stage, logic, Paths.get("test-addressbook.json"));
        mainWindow.fillInnerParts();
        mainWindow.show();
    }

    @Test
    public void initialState_displaysEmptyContactDetails() {
        interact(() -> {
            VBox details = lookup("#contactDetailsPanel").query();
            ScrollPane help = lookup("#helpPanel").query();
            Label name = lookup("#detailName").query();
            Label prompt = lookup("#detailPrompt").query();

            assertTrue(details.isVisible());
            assertFalse(help.isVisible());
            assertTrue(prompt.isVisible());
            assertEquals("Your newsroom, organised.", name.getText());
        });
    }

    @Test
    public void selectContact_displaysContactDetails() {
        interact(() -> {
            ListView<Person> list = lookup(".list-view").query();
            list.getSelectionModel().select(ALICE);

            Label name = lookup("#detailName").query();
            Label phone = lookup("#detailPhone").query();
            Label email = lookup("#detailEmail").query();
            Label prompt = lookup("#detailPrompt").query();

            assertEquals(ALICE.getName().fullName, name.getText());
            assertEquals("PHONE  " + ALICE.getPhone().value, phone.getText());
            assertEquals("EMAIL  " + ALICE.getEmail().value, email.getText());
            assertFalse(prompt.isVisible());
        });
    }

    @Test
    public void handleHelp_displaysCommandGuide() {
        interact(() -> {
            mainWindow.handleHelp();

            VBox details = lookup("#contactDetailsPanel").query();
            ScrollPane help = lookup("#helpPanel").query();

            assertFalse(details.isVisible());
            assertFalse(details.isManaged());
            assertTrue(help.isVisible());
            assertTrue(help.isManaged());

            Label contactsGuide = lookup("#contactsGuide").query();
            Label applicationGuide = lookup("#applicationGuide").query();
            String contactsText = contactsGuide.getText().replaceAll("[ \\t]+(?=\\n)", "");
            String applicationText = applicationGuide.getText().replaceAll("[ \\t]+(?=\\n)", "");

            String correctSample1 = """
                    delete ID
                    Example: delete 3

                    clear
                    """;
            String correctSample2 = """
                    undo
                    Reverses the most recent change to contacts. Takes no parameters. Undo up to 20 changes.
                    Example: delete 3, then undo to bring the contact back.

                    redo
                    Reapplies the last change you undid. Takes no parameters.
                    Example: delete 3, then undo, then redo to delete the contact again.
                    """;
            assertTrue(contactsText.contains(correctSample1));
            assertTrue(applicationText.contains(correctSample2));

            assertTrue(contactsGuide.isWrapText());
            assertTrue(applicationGuide.isWrapText());
        });
    }

    @Test
    public void selectContactAfterHelp_restoresContactDetails() {
        interact(() -> {
            mainWindow.handleHelp();

            ListView<Person> list = lookup(".list-view").query();
            list.getSelectionModel().select(BOB);

            VBox details = lookup("#contactDetailsPanel").query();
            ScrollPane help = lookup("#helpPanel").query();
            Label name = lookup("#detailName").query();

            assertTrue(details.isVisible());
            assertTrue(details.isManaged());
            assertFalse(help.isVisible());
            assertEquals(BOB.getName().fullName, name.getText());
        });
    }
}

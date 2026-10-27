package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_PERSON_ID_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.parser.SortCommandParser.MESSAGE_INVALID_SORT_ORDER;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        logic = createLogic(model);
    }

    private Logic createLogic(Model model) {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        return new LogicManager(model, storage);
    }

    @Test
    public void guiSettings_readsAndUpdatesModelPreferences() {
        GuiSettings original = new GuiSettings(900, 700, 10, 20);
        model.setGuiSettings(original);
        assertEquals(original, logic.getGuiSettings());

        GuiSettings updated = new GuiSettings(1100, 800, 30, 40);
        logic.setGuiSettings(updated);
        assertEquals(updated, model.getGuiSettings());
        assertEquals(updated, logic.getGuiSettings());
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, String.format(MESSAGE_PERSON_ID_NOT_FOUND, 9));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_ACTIVE_SUCCESS, model);
    }

    @Test
    public void execute_sortCommand_success() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);

        CommandResult result = logic.execute("sort name desc");

        assertEquals("Sorted contacts by name in descending order.", result.getFeedbackToUser());
        assertEquals(List.of(GEORGE, FIONA, ELLE, DANIEL, CARL, BENSON, ALICE),
                logic.getFilteredPersonList());
        assertEquals(getTypicalPersons(), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidSortCommandAfterSort_throwsParseExceptionAndPreservesDisplayedOrder()
            throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);
        logic.execute("sort name desc");
        List<Person> expectedDisplayedOrder = new ArrayList<>(logic.getFilteredPersonList());

        assertThrows(ParseException.class, MESSAGE_INVALID_SORT_ORDER, () -> logic.execute("sort name upward"));

        assertEquals(expectedDisplayedOrder, logic.getFilteredPersonList());
        assertEquals(getTypicalPersons(), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_filterAndListCommands_updateDisplayedContactsWithoutChangingStorage() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);

        CommandResult partialFindResult = logic.execute("find t/fri");
        assertEquals(String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 3), partialFindResult.getFeedbackToUser());
        assertEquals(List.of(ALICE, BENSON, DANIEL), logic.getFilteredPersonList());

        CommandResult exactFilterResult = logic.execute("filter t/fri");
        assertEquals(String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 0), exactFilterResult.getFeedbackToUser());
        assertEquals(List.of(), logic.getFilteredPersonList());

        logic.execute("filter t/friends");
        assertEquals(List.of(ALICE, BENSON, DANIEL), logic.getFilteredPersonList());

        logic.execute("sort name desc");
        assertEquals(List.of(DANIEL, BENSON, ALICE), logic.getFilteredPersonList());

        logic.execute("list");
        assertEquals(getTypicalPersons(), logic.getFilteredPersonList());
        assertEquals(getTypicalPersons(), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidFilterAfterSort_preservesDisplayedOrder() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);
        logic.execute("filter t/friends");
        logic.execute("sort name desc");
        List<Person> expectedDisplayedOrder = new ArrayList<>(logic.getFilteredPersonList());

        String invalidFilter = "filter t/housing-policy";
        assertThrows(ParseException.class, Tag.MESSAGE_CONSTRAINTS, () -> logic.execute(invalidFilter));

        assertEquals(expectedDisplayedOrder, logic.getFilteredPersonList());
        assertEquals(getTypicalPersons(), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_undoAfterAdd_removesPersonAndRestoresIdCounter() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        logic.execute(addCommand);
        assertEquals(2, model.getAddressBook().getNextPersonId());

        CommandResult result = logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(new AddressBook(), model.getAddressBook());
        JsonAddressBookStorage savedFile = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(new AddressBook(), savedFile.readAddressBook().orElseThrow());

        // the ID of the undone contact is given to the next contact added
        logic.execute(addCommand);
        assertEquals(Optional.of(new PersonId(1)), model.getAddressBook().getPersonList().get(0).getId());
    }

    @Test
    public void execute_undoAfterViewCommands_nothingToUndo() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);
        logic.execute("list");
        logic.execute("find Alice");
        logic.execute("sort name desc");

        assertThrows(CommandException.class, UndoCommand.MESSAGE_NOTHING_TO_UNDO, ()
            -> logic.execute(UndoCommand.COMMAND_WORD));
    }

    @Test
    public void execute_undoAfterFailedCommand_nothingToUndo() {
        assertCommandException("delete 9", String.format(MESSAGE_PERSON_ID_NOT_FOUND, 9));

        assertCommandException(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_undoAfterFind_keepsSearchResults() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);
        logic.execute("delete 2");
        logic.execute("find Alice");
        assertEquals(List.of(ALICE, DANIEL), logic.getFilteredPersonList());

        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(List.of(ALICE, DANIEL), logic.getFilteredPersonList());
        assertTrue(model.hasPerson(BENSON));
    }

    @Test
    public void execute_redoAfterUndoneAdd_restoresPersonWithSameId() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        logic.execute(addCommand);
        Person addedPerson = model.getAddressBook().getPersonList().get(0);
        logic.execute(UndoCommand.COMMAND_WORD);

        CommandResult result = logic.execute(RedoCommand.COMMAND_WORD);

        assertEquals(RedoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(addedPerson), model.getAddressBook().getPersonList());
        assertEquals(2, model.getAddressBook().getNextPersonId());
        JsonAddressBookStorage savedFile = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(model.getAddressBook(), savedFile.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_redoAfterViewCommand_stillRedoes() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);
        logic.execute("delete 2");
        logic.execute(UndoCommand.COMMAND_WORD);
        logic.execute("list");

        logic.execute(RedoCommand.COMMAND_WORD);

        assertFalse(model.hasPerson(BENSON));
    }

    @Test
    public void execute_redoAfterNewChange_nothingToRedo() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = createLogic(model);
        logic.execute("delete 2");
        logic.execute(UndoCommand.COMMAND_WORD);
        logic.execute("delete 3");

        assertThrows(CommandException.class, RedoCommand.MESSAGE_NOTHING_TO_REDO, ()
            -> logic.execute(RedoCommand.COMMAND_WORD));
        assertTrue(model.hasPerson(BENSON));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}

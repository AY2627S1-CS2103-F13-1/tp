package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.commands.LoadCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.BackupStorage;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final BackupStorage backupStorage;
    private final AddressBookParser addressBookParser;

    /** Destination awaiting a Y/N response; null when no export confirmation is pending. */
    private Path pendingExportPath;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, new BackupStorage());
    }

    /** Allows backup failures to be tested independently of the operating system's file permissions. */
    LogicManager(Model model, Storage storage, BackupStorage backupStorage) {
        this.backupStorage = backupStorage;
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        if (pendingExportPath != null) {
            return respondToExportConfirmation(commandText);
        }

        Command command = addressBookParser.parseCommand(commandText);
        // Recovery is staged until persistence succeeds, keeping the displayed data intact on failure.
        Model executionModel = command instanceof LoadCommand
                ? new ModelManager(model.getAddressBook(), model.getUserPrefs()) : model;
        CommandResult commandResult;
        try {
            commandResult = command.execute(executionModel);
        } catch (CommandException e) {
            if (command instanceof ExportCommand exportCommand
                    && e.getCause() instanceof FileAlreadyExistsException
                    && Files.isRegularFile(exportCommand.getPath(), LinkOption.NOFOLLOW_LINKS)) {
                pendingExportPath = exportCommand.getPath().toAbsolutePath().normalize();
                return new CommandResult(exportWarning());
            }
            throw e;
        }
        if (command instanceof LoadCommand loadCommand) {
            Path backupPath = backupBeforeLoad(loadCommand.getPath());
            try {
                saveAddressBook(executionModel);
            } catch (CommandException e) {
                if (backupPath != null) {
                    throw new CommandException(e.getMessage() + " Previous data is backed up at " + backupPath, e);
                }
                throw e;
            }
            model.setAddressBook(executionModel.getAddressBook());
            model.commitAddressBook();
            return new CommandResult(commandResult.getFeedbackToUser() + (backupPath == null
                    ? " No backup needed: the previous address book was empty or unchanged sample data."
                    : " Previous data is backed up at " + backupPath));
        }
        if (command instanceof ExportCommand) {
            return commandResult;
        }
        // Unchanged data and undo/redo do not add duplicate history entries.
        model.commitAddressBook();
        saveAddressBook(executionModel);
        return commandResult;
    }

    /** Keeps the existing file untouched until the user explicitly answers Y. */
    private CommandResult respondToExportConfirmation(String response) throws CommandException {
        if (response.trim().equalsIgnoreCase("N")) {
            pendingExportPath = null;
            return new CommandResult("Export cancelled. The existing file has not been changed.");
        }
        if (!response.trim().equalsIgnoreCase("Y")) {
            return new CommandResult("Please enter Y or N.\n" + exportWarning());
        }
        Path destination = pendingExportPath;
        pendingExportPath = null;
        try {
            backupStorage.overwriteAddressBook(model.getAddressBook(), destination);
        } catch (IOException e) {
            throw new CommandException("Could not export contacts: " + e.getMessage(), e);
        }
        return new CommandResult("Exported all contacts to " + destination);
    }

    /** Shows the exact destination and the consequences of accepting replacement. */
    private String exportWarning() {
        return "Warning: " + pendingExportPath
                + " already exists. Overwriting it will permanently replace the previous backup. Proceed? (Y/N)";
    }

    /** Backs up non-default data before replacement; empty and unchanged sample books need no backup. */
    private Path backupBeforeLoad(Path sourcePath) throws CommandException {
        if (model.getAddressBook().getPersonList().isEmpty()
                || model.getAddressBook().equals(SampleDataUtil.getSampleAddressBook())) {
            return null;
        }
        try {
            return backupStorage.backupBeforeLoad(model.getAddressBook(), sourcePath);
        } catch (IOException e) {
            throw new CommandException("Load cancelled because the current contacts could not be backed up: "
                    + e.getMessage(), e);
        }
    }

    /** Saves staged data using the same error messages as ordinary command persistence. */
    private void saveAddressBook(Model executionModel) throws CommandException {
        try {
            storage.saveAddressBook(executionModel.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}

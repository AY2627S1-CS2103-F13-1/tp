package seedu.address.logic.commands;

import seedu.address.model.Model;

import org.junit.jupiter.api.Test;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

public class RemarkCommandTest {
    @Test
    public void execute() {
        Model model = null;
        final String MESSAGE_NOT_IMPLEMENTED_YET = "MESSAGE_NOT_IMPLEMENTED_YET";
        assertCommandFailure(new RemarkCommand(), model, MESSAGE_NOT_IMPLEMENTED_YET);
    }
}

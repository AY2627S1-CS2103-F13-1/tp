package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_validRemark_success() {
        Remark remark = new Remark("Met at a press conference");
        assertEquals("Met at a press conference", remark.value);
    }

    @Test
    public void toString_returnsValue() {
        Remark remark = new Remark("Technology source");
        assertEquals("Technology source", remark.toString());
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Technology source");

        assertEquals(remark, remark);
        assertEquals(remark, new Remark("Technology source"));
    }
}

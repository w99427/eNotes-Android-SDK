package io.enotes.sdk;

import org.ethereum.util.ByteUtil;
import org.junit.Test;

import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.card.TLVBox;

import static io.enotes.sdk.repository.card.Commands.TLVTag.Device_Certificate;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

}
package com.github.ultimate.ultimate32crop;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void mul1_isCorrect() {
        assertEquals(12, Main.imForTesting(3, 4));
    }

    @Test
    public void mul2_isCorrect() {
        assertEquals(12123*12399, Main.imForTesting(12123, 12399));
    }

    @Test
    public void mul3_isCorrect() {
        assertEquals(12123*12394, Main.imForTesting(12123, 12394));
    }
}
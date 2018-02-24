package com.xybank.chain.android.sdk;

import com.xybank.chain.android.sdk.encrypt.SM3Digest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testSM3() throws Exception {
        System.out.println(SM3Digest.SM3Hash("wuhuping"));
    }
}
package com.study.junit;

import org.junit.Test;

public class StringUtilTest {
    @Test
    public void testPrintNumber() {
        StringUtil.printNumber(" ");
        StringUtil.printNumber(null);
        StringUtil.printNumber("2");
    }
}

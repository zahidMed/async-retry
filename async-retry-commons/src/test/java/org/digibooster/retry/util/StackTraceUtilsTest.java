package org.digibooster.retry.util;

import lombok.extern.slf4j.Slf4j;
import org.digibooster.retry.manager.AsyncRetryableManager;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class StackTraceUtilsTest {

    @Test
    public void testExistsInCurrentStackTrace(){
        Assert.assertFalse(StackTraceUtils.existsInCurrentStackTrace(String.class));
        Assert.assertFalse(StackTraceUtils.existsInCurrentStackTrace(AsyncRetryableManager.class));
        Assert.assertTrue(StackTraceUtils.existsInCurrentStackTrace(StackTraceUtilsTest.class));
    }
}

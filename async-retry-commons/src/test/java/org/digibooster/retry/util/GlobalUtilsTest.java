package org.digibooster.retry.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public class GlobalUtilsTest {
	


    @Test
    public void test_check_retry_for(){
        TargetMethodInformation methodInformation= TargetMethodInformation
                .builder()
                .retryFor(new Class[]{IOException.class,NullPointerException.class})
                .build();
        Assert.assertTrue(GlobalUtils.instanceOf(new FileNotFoundException(),methodInformation.getRetryFor()));
        Assert.assertTrue(GlobalUtils.instanceOf(new IOException(),methodInformation.getRetryFor()));
        Assert.assertFalse(GlobalUtils.instanceOf(new ClassNotFoundException(),methodInformation.getRetryFor()));
        Assert.assertFalse(GlobalUtils.instanceOf(new Exception(),methodInformation.getNoRetryFor()));
    }

}

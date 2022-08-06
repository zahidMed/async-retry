package org.digibooster.retry.policy;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public class FixedWindowRetryableSchedulingPolicyTest {

    @Test
    public void test_next(){
        AsyncRetryableSchedulingPolicy asyncRetryableSchedulingPolicy = new FixedWindowRetryableSchedulingPolicy(5, 1000);
        Assert.assertTrue(asyncRetryableSchedulingPolicy.next(0)==0);
        Assert.assertTrue(asyncRetryableSchedulingPolicy.next(1)==1000);
        Assert.assertTrue(asyncRetryableSchedulingPolicy.next(4)==1000);
        Assert.assertTrue(asyncRetryableSchedulingPolicy.next(5)==1000);
        Assert.assertTrue(asyncRetryableSchedulingPolicy.next(6)<0);
        Assert.assertTrue(asyncRetryableSchedulingPolicy.next(10)<0);
    }
}

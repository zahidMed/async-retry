package org.digibooster.retry.util;

import org.junit.Assert;
import org.junit.Test;

public class HierarchyCallCheckerTest {

    @Test
    public void test_all_methods(){
        HierarchyCallChecker hierarchyCallChecker=HierarchyCallChecker.getInstance();
        Assert.assertFalse(hierarchyCallChecker.checkFlagExists());
        hierarchyCallChecker.setCallFlag();
        Assert.assertTrue(hierarchyCallChecker.checkFlagExists());
        hierarchyCallChecker.clear();
        Assert.assertFalse(hierarchyCallChecker.checkFlagExists());
    }
}

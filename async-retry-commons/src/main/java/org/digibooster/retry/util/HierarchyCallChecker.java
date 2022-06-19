package org.digibooster.retry.util;

/**
 * Singleton class based pattern that allow verifying if a method is called by another one.
 * This implementation is based on @{@link ThreadLocal}.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public class HierarchyCallChecker {

    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    private static HierarchyCallChecker instance;

    private HierarchyCallChecker(){}

    public static HierarchyCallChecker getInstance(){
        if(instance==null) instance = new HierarchyCallChecker();
        return instance;
    }

    /**
     * Store true value in the {@link ThreadLocal} as a flag
     */
    public void setCallFlag(){
        threadLocal.set(true);
    }

    /**
     * Check if there is a true value stored in the @{@link ThreadLocal}
     * @return
     */
    public boolean checkFlagExists(){
        return Boolean.TRUE.equals(threadLocal.get());
    }

    /**
     * Clear the @{@link ThreadLocal}
     */
    public void clear(){
        threadLocal.remove();
    }
}

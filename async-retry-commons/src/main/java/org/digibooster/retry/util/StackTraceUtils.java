package org.digibooster.retry.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mainly for internal use within the library.
 * This class delivers utility method for @{@link StackTraceElement}
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackTraceUtils {

    /**
     * Checks the existence of a class or interface in the currents stack trace.
     * It can be used to check if a method in called directly or indirectly by the class/interface given as parameter.
     * @param type class type
     * @return true if the type class belongs to the current thread stake traces
     */
    public static boolean existsInCurrentStackTrace(Class type){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        try {
            for (StackTraceElement element : stackTraceElements) {
                if (type.isAssignableFrom(Class.forName(element.getClassName())))
                    return true;
            }
        }
        catch (ClassNotFoundException e){
            log.error("Could not find class definition",e);
        }
        return false;
    }
}

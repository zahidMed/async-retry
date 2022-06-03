package org.digibooster.retry.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Mainly for internal use within the library.
 * This class delivers utility methods
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalUtils {


    /**
     * Checks if the given object is an instance of any of class types in the array
     * @param obj the object to check
     * @param types the class types array
     * @return true if the object is an instance of one of the types
     */
    public static boolean instanceOf(Object obj, Class[] types){
        if(isEmpty(types))
            return false;
        for(Class classType: types){
            if(classType.isInstance(obj))
                return true;
        }
        return false;
    }

    /**
     * Checks if the given array is null or empty
     * @param array input array
     * @return true if the array is empty
     */
    public static boolean isEmpty(Object[] array){
        return array==null || array.length==0;
    }

}

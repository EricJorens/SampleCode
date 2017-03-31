package com.sample.code.ericjorens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 1/1/2017
 * Time: 12:00 PM
 */

public class ClassComparator {

    private static final Logger log = LoggerFactory.getLogger(ClassComparator.class);


    public static void main(String[] args) {

        Bean1 b1 = new Bean1();
        Bean2 b2 = new Bean2();
        System.out.println(getPropertyNames(b1) + "\r\n");
        System.out.println(getPropertyNames(b1) + "\r\n");

        System.out.println("Number of alike properties by name: " + countCommonPropertiesByName(b1, b2) + "\r\n");
        System.out.println("Number of alike properties by name and value type: " + countCommonPropertiesByNameAndValueType(b1, b2) + "\r\n");
        System.out.println("Number of alike properties by name and property type: " + countCommonPropertiesByNameAndType(b1, b2) + "\r\n");

    }


    /**
     * Counts the number of similar properties, by name and type, of any two objects.
     * This method only equates properties with alike names and types, i.e. String "name" is not equal to Integer "name".
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static Integer countCommonPropertiesByNameAndType(final Object obj1, final Object obj2) {

        Map<String, String> prop1 = getPropertyAndType(obj1);
        Map<String, String> prop2 = getPropertyAndType(obj2);

        Integer count = 0;
        for (String p1 : prop1.keySet()) {
            for (String p2 : prop2.keySet()) {
                if ((p1.equalsIgnoreCase(p2)) && (prop1.get(p1).equalsIgnoreCase(prop2.get(p2)))) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }


    /**
     * Counts the number of similar properties, by name and value type, of any two objects.
     * This method assumes inequality of null values, i.e. if property "size" of object 1 is set to 10,
     * and "size" of object 2 is null, the equality will not be counted even though the property names are alike.
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static Integer countCommonPropertiesByNameAndValueType(final Object obj1, final Object obj2) {

        Map<String, String> prop1 = getPropertyAndValueType(obj1);
        Map<String, String> prop2 = getPropertyAndValueType(obj2);

        Integer count = 0;
        for (String p1 : prop1.keySet()) {
            for (String p2 : prop2.keySet()) {
                if ((p1.equalsIgnoreCase(p2)) && (prop1.get(p1).equalsIgnoreCase(prop2.get(p2)))) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }


    /**
     * Counts the number of similar properties, by name, of different objects of any class.
     * This method assumes equality of class properties regardless of type, i.e. String "size" is equal to Integer "size".
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static Integer countCommonPropertiesByName(final Object obj1, final Object obj2) {

        List<String> prop1 = getPropertyNames(obj1);
        List<String> prop2 = getPropertyNames(obj2);

        Integer count = 0;
        for (String p1 : prop1) {
            for (String p2 : prop2) {
                if (p1.equalsIgnoreCase(p2)) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }


    /**
     * Generates a list of the property names of an object.
     *
     * @param thingy
     * @return
     */
    public static List<String> getPropertyNames(final Object thingy) {
        final List<String> properties = new ArrayList<>();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(thingy.getClass());
            for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                try {
                    properties.add(descriptor.getName());
                } catch (final IllegalArgumentException e) {
                    log.debug("Illegal Argument: ", e);
                }
            }
        } catch (final IntrospectionException e) {
            log.debug("Introspection Exception: ", e);
        }
        return properties;
    }


    /**
     * Generates a map of the property names and value types of an object.
     * This method will take null property assignment into consideration.
     *
     * @param thingy
     * @return
     */
    public static Map<String, String> getPropertyAndValueType(final Object thingy) {
        final Map<String, String> nonNullProperties = new TreeMap<String, String>();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(thingy.getClass());
            for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                try {
                    final Object propertyValue = descriptor.getReadMethod().invoke(thingy);
                    if (propertyValue != null) {
                        nonNullProperties.put(descriptor.getName(), propertyValue.getClass().getName());
                    } else {
                        nonNullProperties.put(descriptor.getName(), "java.null");
                    }
                } catch (final IllegalArgumentException e) {
                    log.debug("Illegal Argument: ", e);
                } catch (final IllegalAccessException e) {
                    log.debug("Illegal Access: ", e);
                } catch (final InvocationTargetException e) {
                    log.debug("Illegal Target: ", e);
                }
            }
        } catch (final IntrospectionException e) {
            log.debug("Introspection Exception: ", e);
        }
        return nonNullProperties;
    }


    /**
     * Generates a map of the property names and types of an object.
     *
     * @param thingy
     * @return
     */
    public static Map<String, String> getPropertyAndType(final Object thingy) {
        final Map<String, String> nonNullProperties = new TreeMap<String, String>();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(thingy.getClass());
            for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                try {
                    final Object propertyType = descriptor.getPropertyType();
                    if (propertyType != null) {
                        nonNullProperties.put(descriptor.getName(), propertyType.getClass().getName());
                    } else {
                        nonNullProperties.put(descriptor.getName(), "java.null");
                    }
                } catch (final IllegalArgumentException e) {
                    log.debug("Illegal Argument: ", e);
                }
            }
        } catch (final IntrospectionException e) {
            log.debug("Introspection Exception: ", e);
        }
        return nonNullProperties;
    }


    /**
     * Generates a map of the property names and values of the non null values of an object.
     *
     * @param thingy
     * @return
     */
    public static Map<String, Object> getNonNullProperties(final Object thingy) {
        final Map<String, Object> nonNullProperties = new TreeMap<String, Object>();
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(thingy.getClass());
            for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                try {
                    final Object propertyValue = descriptor.getReadMethod().invoke(thingy);
                    if (propertyValue != null) {
                        nonNullProperties.put(descriptor.getName(), propertyValue);
                    }
                } catch (final IllegalArgumentException e) {
                    log.debug("Illegal Argument: ", e);
                } catch (final IllegalAccessException e) {
                    log.debug("Illegal Access: ", e);
                } catch (final InvocationTargetException e) {
                    log.debug("Illegal Target: ", e);
                }
            }
        } catch (final IntrospectionException e) {
            log.debug("Introspection Exception: ", e);
        }
        return nonNullProperties;
    }

}

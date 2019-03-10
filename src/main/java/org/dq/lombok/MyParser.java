package org.dq.lombok;

import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: duanqiong
 * @Date: 2019/3/10 22:58
 * @Version 1.0
 */
public class MyParser {
    private Element element;
    private Set<String> getMethodSet;
    private Set<String> setMethodSet;
    private Map<String, Field> fieldMap;

    public MyParser(Element element) throws ClassNotFoundException {
        this.element = element;
        getMethodSet = new HashSet<>();
        setMethodSet = new HashSet<>();
        fieldMap = new HashMap<>();
        findUndefinedGetOrSetField();
    }

    /**
     * 通过反射获取未生成的get和set方法的变量对应的get和set方法方法名称
     *
     * @throws ClassNotFoundException
     */
    private void findUndefinedGetOrSetField() throws ClassNotFoundException {
        TypeElement typeElement = (TypeElement) element;
        String className = typeElement.getQualifiedName().toString();
        Class<?> myClass = Class.forName(className);
        Field[] declaredFields = myClass.getDeclaredFields();
        Method[] declaredMethods = myClass.getDeclaredMethods();
        Arrays.stream(declaredFields).forEach(field -> {
            String fieldName = field.getName();
            String methodSimpleName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            getMethodSet.add("get" + methodSimpleName);
            setMethodSet.add("set" + methodSimpleName);
            fieldMap.put(fieldName, field);
        });
        Set<String> methodNames = Arrays.stream(declaredMethods).map(method -> method.getName()).collect(Collectors.toSet());
        String allMethods = StringUtils.join(methodNames, ",");
        getMethodSet = getMethodSet.stream().filter(getMethod -> !allMethods.matches(("^.*" + getMethod + ".*$"))).collect(Collectors.toSet());
        setMethodSet = setMethodSet.stream().filter(setMethod -> !allMethods.matches(("^.*" + setMethod + ".*$"))).collect(Collectors.toSet());
    }

    public void makeGenerate() {

    }
}

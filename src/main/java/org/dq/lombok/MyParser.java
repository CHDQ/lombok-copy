package org.dq.lombok;

import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: duanqiong
 * @Date: 2019/3/10 22:58
 * @Version 1.0
 */
public class MyParser {
    private Element element;
    private Set<String> needGenerateMethod;

    public MyParser(Element element) {
        this.element = element;
        needGenerateMethod = new HashMap<>();
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
        Set<String> getOrSetMethods = Arrays.stream(declaredFields).flatMap(field -> {
            String fieldName = field.getName();
            String methodSimpleName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return Stream.of("get" + methodSimpleName, "set" + methodSimpleName);
        }).collect(Collectors.toSet());
        Set<String> methodNames = Arrays.stream(declaredMethods).map(method -> method.getName()).collect(Collectors.toSet());
        String allMethods = StringUtils.join(methodNames, ",");
        needGenerateMethod = getOrSetMethods.stream().filter(getOrSetMethod -> !allMethods.matches(("^.*" + getOrSetMethod + ".*$"))).collect(Collectors.toSet());
    }
}

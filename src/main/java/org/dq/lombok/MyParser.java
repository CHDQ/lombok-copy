package org.dq.lombok;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

/**
 * @Author: duanqiong
 * @Date: 2019/3/10 22:58
 * @Version 1.0
 */
public class MyParser {
    private Class elementClass;
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
        elementClass = Class.forName(className);
        Field[] declaredFields = elementClass.getDeclaredFields();
        Method[] declaredMethods = elementClass.getDeclaredMethods();
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

    public byte[] makeGenerate() throws IOException {
        ClassReader classReader = new ClassReader(elementClass.getTypeName());
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        GMClassVistorAdapter gmClassVistorAdapter = new GMClassVistorAdapter(classWriter);
        classReader.accept(gmClassVistorAdapter, 0);
        Map<String, String> methodDescriptor = gmClassVistorAdapter.getGetOrSetMethodDescriptorMap();
        Map<String, String> fieldDescriptorMap = gmClassVistorAdapter.getFieldDescriptorMap();
        createGetMethod(classWriter, methodDescriptor, fieldDescriptorMap);//生成get方法
        createSetMethod(classWriter);//生成set方法
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private void createGetMethod(ClassWriter classWriter, Map<String, String> methodDescriptor, Map<String, String> fieldDescriptorMap) {
        getMethodSet.forEach(methodName -> {
            var fieldName = methodName.replaceFirst("get", "").toLowerCase();
            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, methodName, methodDescriptor.get(methodName), null, null);//简单实现，没有考虑泛型
            methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(elementClass), fieldName, fieldDescriptorMap.get(fieldName));
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(2, 0);
            methodVisitor.visitEnd();
        });
    }

    private void createSetMethod(ClassWriter classWriter) {

    }
}

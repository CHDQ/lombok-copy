package org.dq.lombok;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import java.util.HashMap;
import java.util.Map;

import static org.dq.lombok.Utils.toFirstCapital;
import static org.objectweb.asm.Opcodes.ASM7;

public class GMClassVistorAdapter extends ClassVisitor {
    private Map<String, String> getOrSetMethodDescriptorMap = new HashMap<>();
    private Map<String, String> fieldDescriptorMap = new HashMap<>();

    public GMClassVistorAdapter(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        fieldDescriptorMap.put(name, descriptor);
        getOrSetMethodDescriptorMap.put("get" + toFirstCapital(name), "()" + descriptor);
        getOrSetMethodDescriptorMap.put("set" + toFirstCapital(name), "(" + descriptor + ")V");
        return super.visitField(access, name, descriptor, signature, value);
    }

    Map<String, String> getGetOrSetMethodDescriptorMap() {
        return getOrSetMethodDescriptorMap;
    }

    public Map<String, String> getFieldDescriptorMap() {
        return fieldDescriptorMap;
    }
}

package org.dq.lombok;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

/**
 * @Author: duanqiong
 * @Date: 2019/3/10 16:46
 * @Version 1.0
 */
//不建议使用下面的注解去修改，支持的java版本和java的注解.该方式可能存在不兼容的问题
//@SupportedSourceVersion(SourceVersion.RELEASE_11)
//@SupportedAnnotationTypes("org.dq.lombok.Data")
public class Generator extends AbstractProcessor {
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Data.class);
        elements.forEach(element -> {
            try {
                ClassReader classReader = new ClassReader(element.getClass().getCanonicalName());
                ClassWriter classWriter = new ClassWriter(classReader, 0);
                classWriter.visitMethod(ACC_PUBLIC,)
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Data.class.getCanonicalName());
    }
}

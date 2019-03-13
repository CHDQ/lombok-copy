package org.dq.lombok;


import org.joda.time.DateTime;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @Author: duanqiong
 * @Date: 2019/3/10 16:46
 * @Version 1.0
 */
//不建议使用下面的注解去修改，支持的java版本和java的注解.该方式可能存在不兼容的问题
//@SupportedSourceVersion(SourceVersion.RELEASE_11)
//@SupportedAnnotationTypes("Data")
public class Generator extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "Generator process beginTime:" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));

        messager.printMessage(Diagnostic.Kind.NOTE, "Generator process endTime:" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        return true;//返回true的时候注解不会被后面process处理
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Data.class.getCanonicalName()).collect(Collectors.toSet());
    }
}

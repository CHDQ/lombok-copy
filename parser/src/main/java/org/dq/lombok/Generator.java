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
        messager.printMessage(Diagnostic.Kind.NOTE, "process beginTime:" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Data.class);
        elements.forEach(element -> {
            try {
                byte[] bytes = new MyParser(element).makeGenerate();
                JavaFileObject classFile = filer.createClassFile(element.getSimpleName(), element);
                OutputStream outputStream = classFile.openOutputStream();
                outputStream.write(bytes);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        messager.printMessage(Diagnostic.Kind.NOTE, "process endTime:" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        return true;//返回true的时候注解不会被后面process处理
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

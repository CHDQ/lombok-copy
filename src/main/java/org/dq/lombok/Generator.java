package org.dq.lombok;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @Author: duanqiong
 * @Date: 2019/3/10 16:46
 * @Version 1.0
 */
//不建议使用下面的注解去修改，支持的java版本和java的注解.该方式可能存在不兼容的问题
//@SupportedSourceVersion(SourceVersion.RELEASE_11)
//@SupportedAnnotationTypes("org.dq.lombok.Data")
public class Generator extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Data.class);
        elements.forEach(element -> {
            element.getClass().getDeclaredFields();
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

package org.dq.lombok;


import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import org.joda.time.DateTime;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Collections;
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
public class DataProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private JavacTrees javacTrees;
    private Names names;
    private TreeMaker treeMaker;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        javacTrees = JavacTrees.instance(processingEnv);//获取语法树
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        names = Names.instance(context);
        treeMaker = TreeMaker.instance(context);//用于创建和修改语法树
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "DataProcessor process beginTime:" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Data.class);
        elements.forEach(element -> {
            JCTree tree = javacTrees.getTree(element);//获取语法树
            tree.accept(new DataTreeTranslator(treeMaker, names));
        });
        messager.printMessage(Diagnostic.Kind.NOTE, "DataProcessor process endTime:" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        return true;//返回true的时候注解不会被后面process处理
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Data.class.getCanonicalName()).collect(Collectors.toSet());
    }

}

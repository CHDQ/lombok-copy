package org.dq.lombok;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.lang.model.element.Modifier;


public class ProcessUtils {
    static final String THIS = "this";
    static final String GET = "get";
    static final String SET = "set";
    static final String BUILDER_STATIC_METHOD_NAME = "builder";//静态方法
    static final String BUILD_METHOD_NAME = "build";
    static final String CONSTRUCTOR_NAME = "<init>";//构造方法

    /**
     * 获取所有的set方法
     * com.sun.tools.javac.util.List;不支持添加删除操作
     *
     * @param jcClassDecl
     * @return
     */
    static List<JCTree.JCMethodDecl> getSetJCMethods(JCTree.JCClassDecl jcClassDecl) {
        ListBuffer<JCTree.JCMethodDecl> setJCMethods = new ListBuffer();
        for (JCTree jcTree : jcClassDecl.defs) {
            if (isSetMethod(jcTree)) {
                setJCMethods.add((JCTree.JCMethodDecl) jcTree);
            }
        }
        return setJCMethods.toList();
    }

    /**
     * 判断是否是get方法
     *
     * @param jcTree
     * @return
     */
    private static boolean isSetMethod(JCTree jcTree) {
        if (jcTree.getKind() == JCTree.Kind.METHOD) {//比较是否是方法
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) jcTree;
            return jcMethodDecl.name.toString().startsWith(GET)
                    && jcMethodDecl.params.size() == 1
                    && !jcMethodDecl.mods.getFlags().contains(Modifier.STATIC);
        }
        return false;
    }

    /**
     * 获取所有的变量
     *
     * @param jcClassDecl
     * @return
     */
    static List<JCTree.JCVariableDecl> getJCField(JCTree.JCClassDecl jcClassDecl) {
        ListBuffer<JCTree.JCVariableDecl> jcVariableDecls = new ListBuffer<>();
        List<JCTree> members = jcClassDecl.getMembers();
        for (JCTree jcTree : members) {
            if (isValidField(jcTree)) {
                jcVariableDecls.add((JCTree.JCVariableDecl) jcTree);
            }
        }
        return jcVariableDecls.toList();
    }

    /**
     * 判断是否是有效的属性(非static)
     *
     * @param jcTree
     * @return
     */
    private static boolean isValidField(JCTree jcTree) {
        if (jcTree.getKind() == JCTree.Kind.VARIABLE) {
            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) jcTree;
            return !jcVariableDecl.mods.getFlags().contains(Modifier.STATIC);
        }
        return false;
    }

    /**
     * 验证是否属性对应的set方法已经存在
     *
     * @param jcVariableDecl
     * @param jcClassDecl
     * @return
     */
    static boolean hasSetMethod(JCTree.JCVariableDecl jcVariableDecl, JCTree.JCClassDecl jcClassDecl) {
        String methodName = formatSetMethodName(jcVariableDecl.name.toString());
        for (JCTree jcTree : jcClassDecl.getMembers()) {
            if (jcTree.getKind() == JCTree.Kind.METHOD) {
                JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) jcTree;
                return methodName.equals(jcMethodDecl.name.toString())
                        && jcMethodDecl.getParameters().size() == 1
                        && jcMethodDecl.getParameters().get(0).vartype.type == jcVariableDecl.vartype.type;
            }
        }
        return false;
    }

    /**
     * 验证是否get方法已经存在
     *
     * @param jcVariableDecl
     * @param jcClassDecl
     * @return
     */
    static boolean hasGetMethod(JCTree.JCVariableDecl jcVariableDecl, JCTree.JCClassDecl jcClassDecl) {
        String methodName = formatGetMethodName(jcVariableDecl.name.toString());
        for (JCTree jcTree : jcClassDecl.getMembers()) {
            if (jcTree.getKind() == JCTree.Kind.METHOD) {
                JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) jcTree;
                return methodName.equals(jcMethodDecl.name.toString())
                        && jcMethodDecl.getParameters().size() == 0;
            }
        }
        return false;
    }

    /**
     * 根据变量创建变量类型copy
     *
     * @param treeMaker
     * @param jcVariableDecl
     * @return
     */
    static JCTree.JCVariableDecl cloneJCVariableAsParam(TreeMaker treeMaker, JCTree.JCVariableDecl jcVariableDecl) {
        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PARAMETER),//访问标志
                jcVariableDecl.name,//名字
                jcVariableDecl.vartype,//类型
                null//初始化语句
        );
    }

    /**
     * 拼接get方法
     *
     * @param field
     * @return
     */
    static String formatGetMethodName(String field) {
        return GET + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    /**
     * 拼接set方法
     *
     * @param field
     * @return
     */
    static String formatSetMethodName(String field) {
        return SET + field.substring(0, 1).toUpperCase() + field.substring(1);
    }
}

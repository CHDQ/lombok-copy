package org.dq.lombok;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Modifier;

import static org.dq.lombok.ProcessUtils.*;

public class DataTreeTranslator extends TreeTranslator {
    private JCTree.JCClassDecl jcClassDecl;
    private List<JCTree.JCVariableDecl> fields;
    private TreeMaker treeMaker;
    private Names names;

    public DataTreeTranslator(TreeMaker treeMaker, Names names) {
        this.treeMaker = treeMaker;
        this.names = names;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
        init(jcClassDecl);
        jcClassDecl.defs = jcClassDecl.defs.appendList(
                createDataMethods()
        );
    }

    /**
     * 获取所有属性
     *
     * @param jcClassDecl
     */
    private void init(JCTree.JCClassDecl jcClassDecl) {
        this.jcClassDecl = jcClassDecl;
        this.fields = getJCField(jcClassDecl);
    }

    /**
     * 创建get和set方法
     *
     * @return
     */
    private List<JCTree> createDataMethods() {
        ListBuffer<JCTree> jcTrees = new ListBuffer<>();
        for (JCTree.JCVariableDecl jcVariableDecl : fields) {
            if (!jcVariableDecl.mods.getFlags().contains(Modifier.FINAL)
                    && !hasSetMethod(jcVariableDecl, jcClassDecl)) {//不存在set方法，创建set方法
                jcTrees.append(createSetMethod(jcVariableDecl));
            }
            if (!hasGetMethod(jcVariableDecl, jcClassDecl)) {//不存在get方法，创建get方法
                jcTrees.append(createGetMethod(jcVariableDecl));
            }
        }
        return jcTrees.toList();
    }

    /**
     * 创建get方法
     *
     * @param jcVariableDecl
     * @return
     */
    private JCTree.JCMethodDecl createSetMethod(JCTree.JCVariableDecl jcVariableDecl) {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        jcStatements.append(treeMaker.Exec(treeMaker.Assign(treeMaker.Select(treeMaker.Ident(names.fromString(THIS)),
                jcVariableDecl.name), treeMaker.Ident(jcVariableDecl.name))));
        JCTree.JCBlock jcBlock = treeMaker.Block(0, jcStatements.toList());//0为访问标志
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),//访问修饰符
                names.fromString(formatSetMethodName(jcVariableDecl.name.toString())),//方法名称
                treeMaker.TypeIdent(TypeTag.VOID),//返回值类型
                List.nil(),//泛型集列表
                List.of(cloneJCVariableAsParam(treeMaker, jcVariableDecl)),//参数列表
                List.nil(),//异常列表
                jcBlock,//方法体
                null//默认方法，可能是父方法
        );
    }

    private JCTree.JCMethodDecl createGetMethod(JCTree.JCVariableDecl jcVariableDecl) {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        jcStatements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString(THIS)),
                jcVariableDecl.name)));
        JCTree.JCBlock jcBlock = treeMaker.Block(0, jcStatements.toList());//0为访问标志
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),//访问修饰符
                names.fromString(formatSetMethodName(jcVariableDecl.name.toString())),//方法名称
                jcVariableDecl.vartype,//返回值类型
                List.nil(),//泛型集列表
                List.nil(),//参数列表
                List.nil(),//异常列表
                jcBlock,//方法体
                null//默认方法，可能是父方法
        );
    }
}
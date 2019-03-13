# lombok-copy
模拟lombok插件实现lombok自动生成get和set方法

## [调试方法(参考stack overflow)](https://stackoverflow.com/questions/31345893/debug-java-annotation-processors-using-intellij-and-maven)
1. First of all, [download and install Maven](https://maven.apache.org/download.cgi), then download and install IntelliJ IDEA (referred to as IDEA from here on). 
(If you don't know how to use Windows CMD, here is a short [tutorial](http://www.7tutorials.com/command-prompt-how-use-basic-commands) for it, also: [how to open the command prompt](http://www.7tutorials.com/7-ways-launch-command-prompt-windows-7-windows-8)
2. Create a Maven project in IDEA without any Archetype. Then create some some package in src > main > java
3. Create a Class which extends javax.annotation.processing.AbstractProcessor.
4. Insert some minimal code, just to make it work. (Don't forget the Annotation at the top of the class declaration!)
Assuming that the annotation full path is core.Factory, the code will look like
```java
@SupportedAnnotationTypes("core.Factory")
public class MyProcessor extends AbstractProcessor {
Messager messager;

    @Override
    public void init(ProcessingEnvironment env) {
        messager = env.getMessager();
        super.init(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,       RoundEnvironment roundEnv) {
        for (TypeElement te : annotations)
            for (Element e : roundEnv.getElementsAnnotatedWith(te))
                messager.printMessage(Diagnostic.Kind.NOTE, "Printing: " +   e.toString());
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
```
5. Create an annotation in the same package.
```java
public @interface Factory {

}
```
6. In the project there is probably a directory src > test > java, create there another package with the same name as the package you've created earlier. Then create a Class in it with a name ending with "Test" (for example: MyProcessorTest). Then annotate this class with the new annotation type you created earlier (@Factory).
```java
@Factory
public class MyProcessorTest {

}
```
7. Now, for annotation processors to work, they have to have some file in META-INF. To achieve that, we'll use another annotation processor called autoservice. So in the pom.xml file insert it's dependency.
```
<dependencies>
    <dependency>
        <groupId>com.google.auto.service</groupId>
        <artifactId>auto-service</artifactId>
        <version>1.0-rc2</version>
    </dependency>
</dependencies>
```
> Side-note: For some reason, if I don't specify it explicitly, the Maven project uses Java 1.5. To force it to work with Java 1.8, insert this into the pom.xml file.
```
<build>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.3</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```
8. Annotate our Processor Class with @AutoService(Processor.class).
9. Now, we have to set up a remote debugger configuration in IDEA. To do that, go to Run > Edit Configurations, click on the green + button on the top left, select remote. Name it something like "mvnDebug", set the Host to localhost and the Port to 8000, press ok and it's good to go.
10. Set a break point in the process method in our Processor.
11. Open up the Windows command prompt, navigate to your projects directory, where the pom.xmlresides. Then type in mvnDebug clean install.If everything has been set up right, it should say something like "Listening for transport dt_socket at address: 8000".
12. Go back to IDEA and execute the mvnDebug configuration we've just made. If everything has been set up right, it should say something like "Connected to the target VM, address: 'localhost:8000', transport: 'socket'".
13. Go back to the Command Prompt and if nothing is happening press some key to wake it up.
14. If everything was set up right, IDEA will stop at the breakpoint, suspending javac's (the Java compiler) execution.

## [mvn compiler plugin 参数](http://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html#forceJavacCompilerUse)
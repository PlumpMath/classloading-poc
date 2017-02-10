package org.openrepose.poc.corerunner;

import org.openrepose.commons.utils.classloader.EarClassProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;

/**
 * Created by adrian on 2/10/17.
 */
public class CoreRunner {
    public void initializeContext() throws Exception {

        AnnotationConfigApplicationContext coreContext = new AnnotationConfigApplicationContext();
        coreContext.scan("org.openrepose.poc.coreservice");

        EarClassProvider leftProvider = new EarClassProvider(new File("./build/ears/left-service-ear.ear"), new File("./build/exploded-ears"));
        ClassPathBeanDefinitionScanner leftScanner = new ClassPathBeanDefinitionScanner(coreContext);
        leftScanner.setResourceLoader(new DefaultResourceLoader(leftProvider.getClassLoader()));
        leftScanner.scan("org.openrepose.poc.leftservice");

        EarClassProvider rightProvider = new EarClassProvider(new File("./build/ears/right-service-ear.ear"), new File("./build/exploded-ears"));
        ClassPathBeanDefinitionScanner rightScanner = new ClassPathBeanDefinitionScanner(coreContext);
        rightScanner.setResourceLoader(new DefaultResourceLoader(rightProvider.getClassLoader()));
        rightScanner.scan("org.openrepose.poc.rightservice");

        coreContext.refresh();
    }
}

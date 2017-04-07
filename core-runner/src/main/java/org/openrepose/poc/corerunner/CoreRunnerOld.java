package org.openrepose.poc.corerunner;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.openrepose.commons.utils.classloader.EarClassProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by adrian on 2/10/17.
 */
public class CoreRunnerOld {
    public void initializeContext() throws Exception {

        AnnotationConfigApplicationContext coreContext = new AnnotationConfigApplicationContext();
        coreContext.scan("org.openrepose.poc.coreservice");

        File[] earFiles = new File("./build/ears").listFiles((file, name) -> name.endsWith(".ear"));
        for( File earFile : earFiles) {
            EarClassProvider earProvider = new EarClassProvider(earFile, new File("./build/exploded-ears"));

            Config servicesConfig = ConfigFactory.empty();
            Enumeration<URL> serviceDescriptors = ((URLClassLoader)earProvider.getClassLoader()).findResources("META-INF/org/openrepose/service.properties");
            while(serviceDescriptors.hasMoreElements()) {
                servicesConfig = servicesConfig.withFallback(ConfigFactory.parseURL(serviceDescriptors.nextElement()));
            }

            Config foundServices = servicesConfig.getConfig("org.openrepose.services.node");
            for(Map.Entry<String, ConfigValue> service : foundServices.entrySet()) {
                ClassPathBeanDefinitionScanner beanScanner = new ClassPathBeanDefinitionScanner(coreContext);
                beanScanner.setResourceLoader(new DefaultResourceLoader(earProvider.getClassLoader()));
                beanScanner.scan((String)service.getValue().unwrapped());
            }
        }

//        coreContext.refresh();
    }
}

package org.openrepose.poc.corerunner

import java.io.{File, FilenameFilter}
import java.net.{URL, URLClassLoader}
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.openrepose.commons.utils.classloader.EarClassProvider
import org.springframework.context.annotation.AnnotationConfigApplicationContext

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  * Created by adrian on 2/10/17.
  */
class CoreRunner {
  def initializeContext() {
    val coreContext: AnnotationConfigApplicationContext = new AnnotationConfigApplicationContext
    coreContext.scan("org.openrepose.poc.coreservice")
    coreContext.refresh()

    var serviceDefinitions: Set[ServiceDefinition] = Set.empty
    val earFiles: Array[File] = new File("./build/ears").listFiles(new FilenameFilter {
      override def accept(file: File, name: String) = name.endsWith(".ear")
    })
    for (earFile <- earFiles) {
      val earProvider: EarClassProvider = new EarClassProvider(earFile, new File("./build/exploded-ears"))
      var servicesConfig: Config = ConfigFactory.empty
      val serviceDescriptors: java.util.Enumeration[URL] = (earProvider.getClassLoader.asInstanceOf[URLClassLoader]).findResources("META-INF/org/openrepose/service.properties")
      while (serviceDescriptors.hasMoreElements) {
        {
          servicesConfig = servicesConfig.withFallback(ConfigFactory.parseURL(serviceDescriptors.nextElement))
        }
      }
      val foundServices: Config = servicesConfig.getConfig("org.openrepose.services.node")
      serviceDefinitions =  serviceDefinitions ++ foundServices.root().asScala.entrySet.map { entry =>
        val serviceDetails = entry.getValue.unwrapped.asInstanceOf[util.Map[String,String]]

        ServiceDefinition(entry.getKey, serviceDetails.get("package"), Option(serviceDetails.get("uses")).map(_.split(",").toSet).getOrElse(Set.empty))
      }

//      for (service <- foundServices.entrySet) {
//        val beanScanner: ClassPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(coreContext)
//        beanScanner.setResourceLoader(new DefaultResourceLoader(earProvider.getClassLoader))
//        beanScanner.scan(service.getValue.unwrapped.asInstanceOf[String])
//      }
    }
    //        coreContext.refresh();
  }
}

case class ServiceDefinition(name: String, packageName: String, usedServices: Set[String])
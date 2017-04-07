package org.openrepose.poc.corerunner

import java.io.{File, FilenameFilter}
import java.net.{URL, URLClassLoader}
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.openrepose.commons.utils.classloader.EarClassProvider
import org.springframework.context.annotation.AnnotationConfigApplicationContext

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

/**
  * Created by adrian on 2/10/17.
  */
class CoreRunner {
  def initializeContext() {
    //build the core context
    val coreContext: AnnotationConfigApplicationContext = new AnnotationConfigApplicationContext
    coreContext.scan("org.openrepose.poc.coreservice")
    coreContext.refresh()

    //find all the services
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
    }

    //build the service dependency tree
    val serviceTree: mutable.Map[String, ServiceNode] = mutable.Map.empty
    for(serviceDefinition <- serviceDefinitions) {
      val serviceNode = serviceTree.getOrElse(serviceDefinition.name, new ServiceNode(serviceDefinition.name))
      serviceNode.packageName = serviceDefinition.packageName
      serviceDefinition.usedServices.foreach { serviceName =>
        val dependentNode = serviceTree.get(serviceName) match {
          case Some(node) =>
            node.checkParents(serviceDefinition.name)
            node
          case None =>
            val newNode = new ServiceNode(serviceName)
            serviceTree.put(serviceName, newNode)
            newNode
        }
        serviceNode.usedServices.add(dependentNode)
      }
      serviceTree.put(serviceDefinition.name, serviceNode)
    }

    //chain the service contexts into a working overall context
    val servicesInOrder = serviceTree.values.toList.sortBy(_.depth)
    val finalContext = servicesInOrder.foldLeft(coreContext) { (currentContext, serviceNode) =>
      Option(serviceNode.packageName) match {
        case Some(name) =>
          val newContext: AnnotationConfigApplicationContext = new AnnotationConfigApplicationContext()
          newContext.setParent(currentContext)
          newContext.scan(name)
          newContext.refresh()
          newContext
        case None =>
          currentContext
      }
    }
  }
}

case class ServiceDefinition(name: String, packageName: String, usedServices: Set[String])
class ServiceNode(name: String) {
  var packageName: String = null
  var usedServices: mutable.Set[ServiceNode] = mutable.Set.empty
  lazy val depth: Int = Try(usedServices.map(_.depth).max + 1).getOrElse(0)

  def checkParents(queriedName: String): Unit = {
    if(name == queriedName) {
      throw new Exception("Circular dependency")
    } else {
      usedServices.foreach( _.checkParents(queriedName) )
    }
  }
}

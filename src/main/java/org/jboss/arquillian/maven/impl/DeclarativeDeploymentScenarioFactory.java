/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.maven.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.arquillian.impl.client.deployment.AnnotationDeploymentScenarioGenerator;
import org.jboss.arquillian.maven.annotation.Deployments;
import org.jboss.arquillian.maven.spi.DeclarativeDeploymentScenarioGenerator;
import org.jboss.arquillian.spi.ServiceLoader;
import org.jboss.arquillian.spi.TestClass;
import org.jboss.arquillian.spi.client.deployment.DeploymentScenario;
import org.jboss.arquillian.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.spi.core.Instance;
import org.jboss.arquillian.spi.core.annotation.Inject;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public class DeclarativeDeploymentScenarioFactory implements DeploymentScenarioGenerator
{

   private Logger log = Logger.getLogger(DeclarativeDeploymentScenarioFactory.class.getName());

   @Inject
   private Instance<ServiceLoader> serviceLoader;

   @Inject
   private Instance<DeploymentsScenarioRegistry> registry;

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.arquillian.spi.client.deployment.DeploymentScenarioGenerator#generate(org.jboss.arquillian.spi.TestClass)
    */
   @SuppressWarnings("unchecked")
   public DeploymentScenario generate(TestClass testClass)
   {
      DeploymentScenario scenario = new DeploymentScenario();

      List<Annotation> annotations = getDeploymentsAnnotation(testClass);

      if (annotations.isEmpty())
      {
         log.fine("DeploymentsBasedScenarioGenerator was registered, but not @Deployments were specified. Passed control to AnnotationDeploymentScenarioGenerator");
         return chainAnnotatedDeploymentGenerator(testClass);
      }

      for (Annotation a : annotations)
      {
         @SuppressWarnings("rawtypes")
         DeclarativeDeploymentScenarioGenerator generator = registry.get().getGeneratorFor(a.annotationType());

         if (generator == null)
         {
            log.warning("Unable to process " + a + ", no DeploymentsScenarioGenerator registered");
            continue;
         }

         generator.generateDeploymentDescriptions(a, scenario, testClass);
      }

      return scenario;
   }

   /**
    * Gets all annotations on the test class which have {@code Deployments} meta annotation
    * 
    * @param testClass
    * @return
    */
   private List<Annotation> getDeploymentsAnnotation(TestClass testClass)
   {

      List<Annotation> annotations = new ArrayList<Annotation>();
      for (Annotation a : testClass.getJavaClass().getAnnotations())
      {
         if (a.annotationType().isAnnotationPresent(Deployments.class))
         {
            annotations.add(a);
         }
      }
      return annotations;
   }

   /**
    * @param testClass
    * @return
    */
   private DeploymentScenario chainAnnotatedDeploymentGenerator(TestClass testClass)
   {
      DeploymentScenarioGenerator generator = serviceLoader.get().onlyOne(AnnotationDeploymentScenarioGenerator.class);

      return generator.generate(testClass);
   }

}

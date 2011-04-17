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

import java.util.Collection;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.ShouldThrowException;
import org.jboss.arquillian.maven.annotation.MavenDeployment;
import org.jboss.arquillian.maven.annotation.MavenDeployments;
import org.jboss.arquillian.maven.spi.DeclarativeDeploymentScenarioGenerator;
import org.jboss.arquillian.spi.TestClass;
import org.jboss.arquillian.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.spi.client.deployment.DeploymentScenario;
import org.jboss.arquillian.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.spi.client.test.TargetDescription;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public class MavenDeploymentsScenarioGenerator implements DeclarativeDeploymentScenarioGenerator<MavenDeployments>
{
   // fully qualified name of exception which act as uninitialized value
   private static final String UNUSED_EXCEPTION_CHECK = "org.jboss.arquillian.maven.annotation.MavenDeployment.UNUSED_EXCEPTION_CHECK";

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.arquillian.maven.spi.DeploymentsScenarioGenerator#getPrecedence()
    */
   public int getPrecedence()
   {
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.arquillian.maven.spi.DeploymentsScenarioGenerator#generateDeploymentDescriptions(java.lang.annotation.Annotation, org.jboss.arquillian.spi.client.deployment.DeploymentScenario, org.jboss.arquillian.spi.TestClass)
    */
   public void generateDeploymentDescriptions(MavenDeployments configuration, DeploymentScenario scenario, TestClass testClass)
   {
      for (MavenDeployment maven : configuration.value())
      {
         scenario.addDeployment(generateDeployment(maven));
      }
   }

   private DeploymentDescription generateDeployment(MavenDeployment maven)
   {
      Deployment deploymentAnnotation = maven.deployment();
      Archive<?> archive = resolveArchive(maven.value(), maven.settings());
      DeploymentDescription deployment = new DeploymentDescription(deploymentAnnotation.name(), archive);

      deployment.setTarget(new TargetDescription(maven.target().value()));
      deployment.setProtocol(new ProtocolDescription(maven.protocol().value()));
      deployment.shouldBeTestable(deploymentAnnotation.testable());
      deployment.shouldBeManaged(deploymentAnnotation.managed());
      deployment.setOrder(deploymentAnnotation.order());

      if (shouldExpectException(maven.expected()))
      {
         deployment.setExpectedException(maven.expected().value());
         deployment.shouldBeTestable(false); // can't test against failing deployments
      }

      return deployment;
   }

   private boolean shouldExpectException(ShouldThrowException shouldThrowException)
   {
      Class<? extends Exception> exception = shouldThrowException.value();
      if (UNUSED_EXCEPTION_CHECK.equals(exception.getCanonicalName()))
      {
         return false;
      }

      return true;
   }

   private Archive<?> resolveArchive(String coordinates, String mavenSettings)
   {

      // configure Maven resolver from settings.xml file, e.g. retrieve repositories
      MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class);
      if (mavenSettings != null && mavenSettings.length() != 0)
      {
         resolver.configureFrom(mavenSettings);
      }

      // determine type of the archive
      Class<? extends Archive<?>> type = MavenTokenizer.shrinkWrapType(coordinates);

      Collection<? extends Archive<?>> archives = resolver
            .artifact(coordinates).exclusion("*:*").resolveAs(type);

      assert archives.size() == 1 : "Exactly one archive is expected as resolution result";

      return archives.iterator().next();

   }
}

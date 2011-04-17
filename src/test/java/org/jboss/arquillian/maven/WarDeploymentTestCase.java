/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.maven;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.maven.annotation.MavenDeployment;
import org.jboss.arquillian.maven.annotation.MavenDeployments;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class shares deployment method for all available tests.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
@MavenDeployments(@MavenDeployment(value = "org.jboss.weld.examples.jsf:weld-jsf-login:war:1.1.1.Final",
            settings = "src/test/resources/settings.xml"))
@RunWith(Arquillian.class)
public class WarDeploymentTestCase
{

   /**
    * Creates a WAR of a Weld based application using ShrinkWrap
    * 
    * @return WebArchive to be tested
    */
   @Deployment
   public static WebArchive createDeployment()
   {
      throw new AssertionError("Should be using different Deployment Scenario");
   }

   @Test
   public void archiveWasDeployed()
   {
   }

}

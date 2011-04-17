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
package org.jboss.arquillian.maven;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.arquillian.maven.impl.DeploymentsScenarioRegistrar;
import org.jboss.arquillian.spi.Profile;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public class DeclarativeDeploymentScenarioGeneratorProfile implements Profile
{

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.arquillian.spi.Profile#getClientProfile()
    */
   public Collection<Class<?>> getClientProfile()
   {
      List<Class<?>> list = new ArrayList<Class<?>>();
      list.add(DeploymentsScenarioRegistrar.class);

      return list;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.arquillian.spi.Profile#getContainerProfile()
    */
   public Collection<Class<?>> getContainerProfile()
   {
      List<Class<?>> list = new ArrayList<Class<?>>();
      list.add(DeploymentsScenarioRegistrar.class);

      return list;
   }

}

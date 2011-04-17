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
import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.maven.spi.DeclarativeDeploymentScenarioGenerator;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public class DeploymentsScenarioRegistry
{
   private Map<Class<?>, DeclarativeDeploymentScenarioGenerator<?>> registry = new HashMap<Class<?>, DeclarativeDeploymentScenarioGenerator<?>>();

   @SuppressWarnings("unchecked")
   public <A extends Annotation> DeclarativeDeploymentScenarioGenerator<A> getGeneratorFor(Class<A> type)
   {
      System.err.println("Size " + registry.size());
      for(Map.Entry<Class<?>, DeclarativeDeploymentScenarioGenerator<?>> foo: registry.entrySet()) {
         System.err.println(foo.getKey() + " <>" + type);
      }
      
      return (DeclarativeDeploymentScenarioGenerator<A>) registry.get(type);
   }

   public DeploymentsScenarioRegistry registerGeneratorFor(Class<? extends Annotation> type, DeclarativeDeploymentScenarioGenerator<?> generator)
   {
      registry.put(type, generator);

      return this;
   }
}

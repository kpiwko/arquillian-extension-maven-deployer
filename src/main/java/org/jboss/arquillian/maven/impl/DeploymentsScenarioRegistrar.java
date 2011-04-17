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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.arquillian.maven.annotation.Deployments;
import org.jboss.arquillian.maven.spi.DeclarativeDeploymentScenarioGenerator;
import org.jboss.arquillian.spi.ServiceLoader;
import org.jboss.arquillian.spi.core.Instance;
import org.jboss.arquillian.spi.core.InstanceProducer;
import org.jboss.arquillian.spi.core.annotation.Inject;
import org.jboss.arquillian.spi.core.annotation.Observes;
import org.jboss.arquillian.spi.core.annotation.SuiteScoped;
import org.jboss.arquillian.spi.event.suite.BeforeSuite;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
public class DeploymentsScenarioRegistrar
{
   @Inject
   @SuiteScoped
   private InstanceProducer<DeploymentsScenarioRegistry> registry;

   @Inject
   private Instance<ServiceLoader> serviceLoader;

   public void register(@Observes BeforeSuite event)
   {
      registry.set(new DeploymentsScenarioRegistry());
      registerGenerators();
   }

   private void registerGenerators()
   {
      @SuppressWarnings("rawtypes")
      List<DeclarativeDeploymentScenarioGenerator> list = new ArrayList<DeclarativeDeploymentScenarioGenerator>(serviceLoader.get().all(DeclarativeDeploymentScenarioGenerator.class));
      Collections.sort(list, GENERATOR_COMPARATOR);

      for (DeclarativeDeploymentScenarioGenerator<?> generator : list)
      {
         Class<? extends Annotation> type = getGeneratorType(generator.getClass(), DeclarativeDeploymentScenarioGenerator.class);
         if (type != null)
         {
            registry.get().registerGeneratorFor(type, generator);
         }
      }
   }

   private static Class<?> getFirstGenericParameterType(Class<?> clazz, Class<?> rawType)
   {
      for (Type interfaceType : clazz.getGenericInterfaces())
      {
         if (interfaceType instanceof ParameterizedType)
         {
            ParameterizedType ptype = (ParameterizedType) interfaceType;
            if (rawType.isAssignableFrom((Class<?>) ptype.getRawType()))
            {
               return (Class<?>) ptype.getActualTypeArguments()[0];
            }
         }
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   private static Class<? extends Annotation> getGeneratorType(Class<?> clazz, Class<?> rawType)
   {
      Class<?> candidate = getFirstGenericParameterType(clazz, rawType);

      if (Annotation.class.isAssignableFrom(candidate) && candidate.isAnnotationPresent(Deployments.class))
      {
         return (Class<? extends Annotation>) candidate;
      }

      throw new IllegalArgumentException("Unable to register " + clazz.getName() + " as DeploymentsScenarioGenerator service, it does not operate on annotation type with @Deployments meta annotation");
   }

   // comparator
   @SuppressWarnings("rawtypes")
   private static final Comparator<DeclarativeDeploymentScenarioGenerator> GENERATOR_COMPARATOR = new Comparator<DeclarativeDeploymentScenarioGenerator>()
   {
      public int compare(DeclarativeDeploymentScenarioGenerator o1, DeclarativeDeploymentScenarioGenerator o2)
      {
         return new Integer(o1.getPrecedence()).compareTo(new Integer(o2.getPrecedence()));
      }
   };

}

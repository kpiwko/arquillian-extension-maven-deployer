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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
class MavenTokenizer
{
   private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?(:([^: ]+))?");

   private static final int DEPENDENCY_GROUP_ID = 1;
   private static final int DEPENDENCY_ARTIFACT_ID = 2;
   private static final int DEPENDENCY_TYPE_ID = 4;
   private static final int DEPENDENCY_CLASSIFIER_ID = 6;
   private static final int DEPENDENCY_VERSION_ID = 8;

   // disable instantiation
   private MavenTokenizer()
   {
      throw new AssertionError("Utility class MavenTokenizer cannot be instantiated.");
   }

   static Class<? extends Archive<?>> shrinkWrapType(String coordinates)
   {
      String type = type(coordinates);
      if (type == null || type.length() == 0 || "jar".equals(type))
      {
         return JavaArchive.class;
      }
      else if ("war".equals(type))
      {
         return WebArchive.class;
      }
      else if ("ear".equals(type))
      {
         return EnterpriseArchive.class;
      }

      // unknown type
      return GenericArchive.class;
   }

   static String groupId(String coordinates)
   {
      return splitCoordinates(coordinates).group(DEPENDENCY_GROUP_ID);
   }

   static String artifactId(String coordinates)
   {
      return splitCoordinates(coordinates).group(DEPENDENCY_ARTIFACT_ID);
   }

   static String version(String coordinates)
   {
      return splitCoordinates(coordinates).group(DEPENDENCY_VERSION_ID);
   }

   static String type(String coordinates)
   {
      return splitCoordinates(coordinates).group(DEPENDENCY_TYPE_ID);
   }

   static String classifier(String coordinates)
   {
      return splitCoordinates(coordinates).group(DEPENDENCY_CLASSIFIER_ID);
   }

   static Matcher splitCoordinates(String coordinates)
   {
      Matcher m = DEPENDENCY_PATTERN.matcher(coordinates);
      if (!m.matches())
      {
         throw new ResolutionException("Bad artifact coordinates" + ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]");
      }

      return m;
   }

}

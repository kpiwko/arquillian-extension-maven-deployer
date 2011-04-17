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
package org.jboss.arquillian.maven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.OverProtocol;
import org.jboss.arquillian.api.ShouldThrowException;
import org.jboss.arquillian.api.TargetsContainer;

/**
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MavenDeployment
{
   Deployment deployment() default @Deployment;

   OverProtocol protocol() default @OverProtocol("_DEFAULT_");

   TargetsContainer target() default @TargetsContainer("_DEFAULT_");

   ShouldThrowException expected() default @ShouldThrowException(UNUSED_EXCEPTION_CHECK.class);

   String settings() default "";
   
   String value();

   // Annotations can't hold null values, so this class acts as type-safe uninitialized holder
   static final class UNUSED_EXCEPTION_CHECK extends Exception
   {
      private static final long serialVersionUID = 1L;
   };

}

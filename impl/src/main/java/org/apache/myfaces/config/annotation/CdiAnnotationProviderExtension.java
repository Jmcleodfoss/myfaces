/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.config.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.FacesBehavior;
import javax.faces.convert.FacesConverter;
import javax.faces.render.FacesBehaviorRenderer;
import javax.faces.render.FacesRenderer;
import javax.faces.validator.FacesValidator;
import javax.faces.view.facelets.FaceletsResourceResolver;
import org.apache.myfaces.config.element.NamedEvent;

public class CdiAnnotationProviderExtension implements Extension
{
    private Map<Class<? extends Annotation>, Set<Class<?>>> map;
    private Class<? extends Annotation>[] annotationsToScan;

    public CdiAnnotationProviderExtension()
    {
        annotationsToScan = new Class[] {
            FacesComponent.class,
            FacesBehavior.class,
            FacesConverter.class,
            FacesValidator.class,
            FacesRenderer.class,
            NamedEvent.class,
            FacesBehaviorRenderer.class,
            FaceletsResourceResolver.class
        };
        map = new HashMap<>(annotationsToScan.length, 1f);
    }

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat)
    {
        AnnotatedType<T> type = pat.getAnnotatedType();

        for (Class<? extends Annotation> annotation : annotationsToScan)
        {
            if (type.isAnnotationPresent(annotation))
            {
                Set<Class<?>> set = map.computeIfAbsent(annotation, k -> new HashSet<>());
                set.add(type.getJavaClass());
            }
        }
    }

    public Map<Class<? extends Annotation>, Set<Class<?>>> getMap()
    {
        return map;
    }
    
    public void release()
    {
        if (map != null)
        {
            map.clear();
            map = null;
        }

        annotationsToScan = null;
    }
}

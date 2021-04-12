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
package org.apache.myfaces.view.facelets.tag.faces.core;

import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.ComponentConfig;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TextHandler;

import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFFaceletTag;
import org.apache.myfaces.view.facelets.tag.TagHandlerUtils;

/**
 * Handler for f:verbatim
 * 
 * @author Adam Winer
 * @version $Id$
 */
@JSFFaceletTag(name = "f:verbatim", bodyContent = "empty")
public final class VerbatimHandler extends ComponentHandler
{
    public VerbatimHandler(ComponentConfig config)
    {
        super(config);
    }

    @Override
    public void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent)
    {
        StringBuilder content = new StringBuilder();
        for (TextHandler handler : TagHandlerUtils.findNextByType(nextHandler, TextHandler.class))
        {
            content.append(handler.getText(ctx));
        }

        c.getAttributes().put("value", content.toString());
        c.getAttributes().put("escape", Boolean.FALSE);
        c.setTransient(true);
    }

    @Override
    public void applyNextHandler(FaceletContext ctx, UIComponent c)
    {
    }
}

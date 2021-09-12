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
package org.apache.myfaces.application;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;

import jakarta.faces.application.StateManager;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIOutput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.render.RenderKitFactory;
import jakarta.faces.render.ResponseStateManager;
import jakarta.faces.view.StateManagementStrategy;
import jakarta.faces.view.ViewDeclarationLanguage;

import org.apache.myfaces.renderkit.html.HtmlResponseStateManager;
import org.apache.myfaces.application.viewstate.StateUtils;
import org.apache.myfaces.spi.impl.DefaultSerialFactory;
import org.apache.myfaces.test.base.junit.AbstractJsfConfigurableMultipleRequestsTestCase;
import org.apache.myfaces.test.mock.MockRenderKit;
import org.apache.myfaces.test.mock.MockResponseWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StateManagerImplTest extends AbstractJsfConfigurableMultipleRequestsTestCase
{

    public StateManagerImplTest()
    {
        super();
    }

    @Test
    public void testWriteAndRestoreState() throws Exception
    {
        String viewId = "/root";
        
        StateManager stateManager = null;
        String viewStateParam = null;
        
        //renderKit.setResponseStateManager(new HtmlResponseStateManager());
        //StateUtils.initSecret(servletContext);
        //servletContext.setAttribute(StateUtils.SERIAL_FACTORY, new DefaultSerialFactory());
        servletContext.addInitParameter(StateManager.STATE_SAVING_METHOD_PARAM_NAME, StateManager.STATE_SAVING_METHOD_CLIENT);
        
        try
        {
            setupRequest();
            
            facesContext.setResponseWriter(new MockResponseWriter(new BufferedWriter(new CharArrayWriter()), null, null));
    
            UIViewRoot viewRoot = facesContext.getViewRoot();
            viewRoot.setViewId(viewId);
            stateManager = new StateManagerImpl();
    
            UIOutput output = new UIOutput();
            output.setValue("foo");
            output.setId("foo");
    
            ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
            ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(facesContext, viewId);
            StateManagementStrategy sms = vdl.getStateManagementStrategy(facesContext, viewId);
            
            stateManager.writeState(facesContext, sms.saveView(facesContext));
            
            viewStateParam = stateManager.getViewState(facesContext);
        }
        finally
        {
            tearDownRequest();
        }

        try
        {
            setupRequest();
            
            request.addParameter(ResponseStateManager.VIEW_STATE_PARAM, viewStateParam);
    
            ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
            ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(facesContext, viewId);
            StateManagementStrategy sms = vdl.getStateManagementStrategy(facesContext, viewId);
            
            UIViewRoot restoredViewRoot = sms.restoreView(facesContext, viewId, RenderKitFactory.HTML_BASIC_RENDER_KIT);
            
            Assert.assertNotNull(restoredViewRoot);
        }
        finally
        {
            tearDownRequest();
        }
    }

    @Test
    public void testWriteAndRestoreStateWithMyFacesRSM() throws Exception
    {
        String viewId = "/root";
        StateManager stateManager = null;
        String viewStateParam = null;

        setupRequest();
        
        ((MockRenderKit)renderKit).setResponseStateManager(new HtmlResponseStateManager());
        StateUtils.initSecret(servletContext);
        servletContext.setAttribute(StateUtils.SERIAL_FACTORY, new DefaultSerialFactory());
        
        tearDownRequest();
        
        try
        {
            setupRequest();
            
            facesContext.setResponseWriter(new MockResponseWriter(new BufferedWriter(new CharArrayWriter()), null, null));
    
            UIViewRoot viewRoot = facesContext.getViewRoot();
            viewRoot.setViewId("/root");
            stateManager = new StateManagerImpl();
    
            UIOutput output = new UIOutput();
            output.setValue("foo");
            output.setId("foo");
    
            ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
            ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(facesContext, viewRoot.getId());
            StateManagementStrategy sms = vdl.getStateManagementStrategy(facesContext, viewRoot.getId());
            stateManager.writeState(facesContext, sms.saveView(facesContext));
            
            viewStateParam = stateManager.getViewState(facesContext);
        }
        finally
        {
            tearDownRequest();
        }

        try
        {
            setupRequest();
            
            request.addParameter(ResponseStateManager.VIEW_STATE_PARAM, viewStateParam);
    
            ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
            ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(facesContext, viewId);
            StateManagementStrategy sms = vdl.getStateManagementStrategy(facesContext, viewId);

            UIViewRoot restoredViewRoot = sms.restoreView(facesContext, viewId, RenderKitFactory.HTML_BASIC_RENDER_KIT);
            
            Assert.assertNotNull(restoredViewRoot);
        }
        finally
        {
            tearDownRequest();
        }
    }
}

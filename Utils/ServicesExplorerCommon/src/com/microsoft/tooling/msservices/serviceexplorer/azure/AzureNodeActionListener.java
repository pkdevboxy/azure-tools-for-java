/**
 * Copyright (c) Microsoft Corporation
 * <p/>
 * All rights reserved.
 * <p/>
 * MIT License
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.microsoft.tooling.msservices.serviceexplorer.azure;

import com.microsoft.tooling.msservices.helpers.NotNull;
import com.microsoft.tooling.msservices.helpers.azure.AzureCmdException;
import com.microsoft.tooling.msservices.helpers.azure.AzureManagerImpl;
import com.microsoft.tooling.msservices.serviceexplorer.EventHelper;
import com.microsoft.tooling.msservices.serviceexplorer.EventHelper.EventHandler;
import com.microsoft.tooling.msservices.serviceexplorer.EventHelper.EventStateHandle;
import com.microsoft.tooling.msservices.serviceexplorer.EventHelper.EventWaitHandle;
import com.microsoft.tooling.msservices.serviceexplorer.Node;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionListenerAsync;

import java.util.concurrent.Callable;

public abstract class AzureNodeActionListener extends NodeActionListenerAsync {
    protected Node azureNode;

    public AzureNodeActionListener(@NotNull Node azureNode,
                                   @NotNull String progressMessage) {
        super(progressMessage);
        this.azureNode = azureNode;
    }

    @NotNull
    @Override
    protected Callable<Boolean> beforeAsyncActionPerfomed() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        };
    }

    @Override
    protected void actionPerformed(final NodeActionEvent e) throws AzureCmdException {
        EventHelper.runInterruptible(new EventHandler() {
            @Override
            public EventWaitHandle registerEvent()
                    throws AzureCmdException {
                return AzureManagerImpl.getManager(azureNode.getProject()).registerSubscriptionsChanged();
            }

            @Override
            public void unregisterEvent(@NotNull EventWaitHandle waitHandle)
                    throws AzureCmdException {
                AzureManagerImpl.getManager(azureNode.getProject()).unregisterSubscriptionsChanged(waitHandle);
            }

            @Override
            public void interruptibleAction(@NotNull EventStateHandle stateHandle)
                    throws AzureCmdException {
                azureNodeAction(e, stateHandle);
            }

            @Override
            public void eventTriggeredAction()
                    throws AzureCmdException {
                onSubscriptionsChanged(e);
            }
        });
    }

    protected abstract void azureNodeAction(NodeActionEvent e, @NotNull EventStateHandle stateHandle)
            throws AzureCmdException;

    protected abstract void onSubscriptionsChanged(NodeActionEvent e)
            throws AzureCmdException;
}
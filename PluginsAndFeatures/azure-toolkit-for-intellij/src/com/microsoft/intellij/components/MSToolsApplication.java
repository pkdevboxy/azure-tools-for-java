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
package com.microsoft.intellij.components;

import com.google.gson.Gson;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.microsoft.azure.hdinsight.common.HDInsightHelperImpl;
import com.microsoft.azure.hdinsight.common.HDInsightLoader;
import com.microsoft.intellij.common.CommonConst;
import com.microsoft.intellij.helpers.IDEHelperImpl;
import com.microsoft.intellij.helpers.UIHelperImpl;
import com.microsoft.intellij.serviceexplorer.NodeActionsMap;
import com.microsoft.intellij.util.PluginUtil;
import com.microsoft.tooling.msservices.components.AppSettingsNames;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.components.PluginComponent;
import com.microsoft.tooling.msservices.components.PluginSettings;
import com.microsoft.tooling.msservices.helpers.StringHelper;
import com.microsoft.tooling.msservices.serviceexplorer.Node;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.microsoft.intellij.ui.messages.AzureBundle.message;

public class MSToolsApplication extends AbstractProjectComponent {
    protected MSToolsApplication(Project project) {
        super(project);
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "MSOpenTechTools";
    }

    @Override
    public void initComponent() {
        cleanTempData();
    }

    private void cleanTempData() {
        // check the plugin version stored in the properties; if it
        // doesn't match with the current plugin version then we clear
        // all stored options
        // TODO: The authentication tokens are stored with the subscription id appended as a
        // suffix to AZURE_AUTHENTICATION_TOKEN. So clearing that requires that we enumerate the
        // current subscriptions and iterate over that list to clear the auth tokens for those
        // subscriptions.

        String currentPluginVersion = DefaultLoader.getIdeHelper().getProperty(AppSettingsNames.CURRENT_PLUGIN_VERSION, myProject);

        if (StringHelper.isNullOrWhiteSpace(currentPluginVersion) ||
                !DefaultLoader.getPluginComponent().getSettings().getPluginVersion().equals(currentPluginVersion)) {

            String[] settings = new String[]{
                    AppSettingsNames.AAD_AUTHENTICATION_RESULTS,
                    AppSettingsNames.O365_USER_INFO,
                    AppSettingsNames.AZURE_SUBSCRIPTIONS,
                    AppSettingsNames.AZURE_USER_INFO,
                    AppSettingsNames.AZURE_USER_SUBSCRIPTIONS
            };

            for (String setting : settings) {
                DefaultLoader.getIdeHelper().unsetProperty(setting, myProject);
            }
        }

        // save the current plugin version
        DefaultLoader.getIdeHelper().setProperty(AppSettingsNames.CURRENT_PLUGIN_VERSION, DefaultLoader.getPluginComponent().getSettings().getPluginVersion(), myProject);
    }
}
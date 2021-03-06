/*
 * Copyright (C) 2020 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.microservices.endpoints

import com.intellij.microservices.endpoints.EndpointType
import com.intellij.microservices.endpoints.EndpointsProvider
import com.intellij.microservices.endpoints.EndpointsViewProvider
import com.intellij.microservices.endpoints.VisibilityScope
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import javax.swing.JComponent

@Suppress("UnstableApiUsage")
abstract class EndpointsProvider :
    EndpointsProvider<EndpointsGroup, Endpoint>,
    EndpointsViewProvider<EndpointsGroup, Endpoint>,
    EndpointsFramework {
    // region EndpointsProvider

    private var cachedEndpointGroups: List<EndpointsGroup> = listOf()

    override val endpointType: EndpointType = EndpointType.SERVER

    abstract override val id: String

    override val viewProvider: EndpointsViewProvider<EndpointsGroup, Endpoint>
        get() = this

    override fun getEndpointGroups(project: Project, scope: VisibilityScope): List<EndpointsGroup> {
        return cachedEndpointGroups
    }

    override fun getEndpoints(group: EndpointsGroup): List<Endpoint> = group.endpoints.toList()

    override fun hasEndpointGroups(project: Project, scope: VisibilityScope): Boolean {
        cachedEndpointGroups = groups(project)
        return cachedEndpointGroups.isNotEmpty()
    }

    override fun isAvailable(project: Project): Boolean = true

    // endregion
    // region EndpointsViewProvider

    override val frameworkPresentation: ItemPresentation
        get() = this

    override val frameworkTag: String
        get() = id

    override fun getEndpointData(group: EndpointsGroup, endpoint: Endpoint, dataId: String): Any? {
        return (endpoint as? DataProvider)?.getData(dataId)
    }

    override fun getEndpointDetails(
        group: EndpointsGroup, endpoint: Endpoint, parentDisposable: Disposable
    ): JComponent? {
        return JBScrollPane(endpoint.details)
    }

    override fun getEndpointPresentation(group: EndpointsGroup, endpoint: Endpoint): ItemPresentation {
        return EndpointPresentation(endpoint)
    }

    override fun getGroupData(group: EndpointsGroup, dataId: String): Any? {
        return (group as? DataProvider)?.getData(dataId)
    }

    override fun getGroupPresentation(group: EndpointsGroup): ItemPresentation = group.presentation

    override fun isValidEndpoint(group: EndpointsGroup, endpoint: Endpoint): Boolean = true

    override fun isValidGroup(group: EndpointsGroup): Boolean = true

    // endregion
}

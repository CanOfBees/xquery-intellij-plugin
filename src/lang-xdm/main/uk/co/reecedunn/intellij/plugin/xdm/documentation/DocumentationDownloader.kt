/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.documentation

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.*
import com.intellij.util.io.HttpRequests
import com.intellij.util.xmlb.XmlSerializerUtil
import uk.co.reecedunn.intellij.plugin.core.progress.TaskManager
import uk.co.reecedunn.intellij.plugin.intellij.resources.XdmBundle
import java.io.File

enum class XdmDocumentationDownloadStatus(val label: String) {
    NotDownloaded(XdmBundle.message("download-status.not-downloaded")),
    Downloading(XdmBundle.message("download-status.downloading")),
    Downloaded(XdmBundle.message("download-status.downloaded"))
}

@State(name = "XdmDocumentationDownloader", storages = [Storage("xijp_settings.xml")])
class XdmDocumentationDownloader : PersistentStateComponent<XdmDocumentationDownloader> {
    var basePath: String? = null
        get() = field ?: "${PathManager.getSystemPath()}/xdm-cache/documentation"

    private val tasks = TaskManager<XdmDocumentationSource>()

    fun download(source: XdmDocumentationSource): Boolean {
        return tasks.backgroundable(XdmBundle.message("documentation-source.download.title"), source) { indicator ->
            val file = File("$basePath/${source.path}")
            HttpRequests.request(source.href).saveToFile(file, indicator)
        }
    }

    fun status(source: XdmDocumentationSource): XdmDocumentationDownloadStatus {
        return when {
            tasks.isActive(source) -> XdmDocumentationDownloadStatus.Downloading
            else -> XdmDocumentationDownloadStatus.NotDownloaded
        }
    }

    // region PersistentStateComponent

    override fun getState(): XdmDocumentationDownloader? = this

    override fun loadState(state: XdmDocumentationDownloader) = XmlSerializerUtil.copyBean(state, this)

    // endregion

    companion object {
        fun getInstance(): XdmDocumentationDownloader {
            return ServiceManager.getService(XdmDocumentationDownloader::class.java)
        }
    }
}

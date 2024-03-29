package cf.thebone.ddctoolbox.file

import android.content.Context
import android.net.Uri
import android.os.Environment
import cf.thebone.ddctoolbox.R
import cf.thebone.ddctoolbox.file.io.ProjectExporter
import cf.thebone.ddctoolbox.file.io.ProjectParser
import cf.thebone.ddctoolbox.file.io.ProjectWriter
import cf.thebone.ddctoolbox.model.FilterItem
import cf.thebone.ddctoolbox.model.instance.ProjectManagerDataInstance
import cf.thebone.ddctoolbox.utils.StringUtils
import java.io.File


class ProjectManager(private val context: Context){
    var currentProjectName = context.getString(R.string.untitled)

    var currentDirectoryName = Environment.getExternalStorageDirectory().toString()
        private set
    var isFileLoaded = false

    var isModified = false

    fun saveInstance(): ProjectManagerDataInstance {
        return ProjectManagerDataInstance(
            currentProjectName, currentDirectoryName,
            isFileLoaded, isModified
        )
    }

    fun restoreInstance(instance: ProjectManagerDataInstance){
        currentDirectoryName = instance.currentDirectoryName
        currentProjectName = instance.currentProjectName
        isFileLoaded = instance.isFileLoaded
        isModified = instance.isModified
    }

    fun load(path: String): ArrayList<FilterItem>?{
        isModified = false
        isFileLoaded = true
        val spath = Uri.parse(path).lastPathSegment ?: return null
        currentProjectName = spath.substring(spath.lastIndexOf("/") + 1)
        currentDirectoryName = File(path).parentFile?.absolutePath.toString()

        return ProjectParser(context).parseFile(path)
    }
    fun save(list: ArrayList<FilterItem>): Boolean?{
        isModified = false
        if(!isFileLoaded || currentDirectoryName == "" || currentProjectName == "" ||
            currentProjectName == "Untitled" || !File(currentDirectoryName).exists())
            return null

        val stripped = StringUtils.stripExtension(currentProjectName,".vdcprj")
        return ProjectWriter().writeFile(File(currentDirectoryName, "$stripped.vdcprj").toString(),list)
    }
    fun saveAs(path: String, list: ArrayList<FilterItem>){
        isModified = false
        isFileLoaded = true

        val spath = Uri.parse(path).lastPathSegment!!
        currentProjectName = spath.substring(spath.lastIndexOf("/") + 1)
        currentDirectoryName = File(path).parentFile?.absolutePath.toString()

        val stripped = StringUtils.stripExtension(currentProjectName,".vdcprj")
        ProjectWriter().writeFile(File(currentDirectoryName, "$stripped.vdcprj").toString(),list)
    }
    fun exportVDC(path: String, list: ArrayList<FilterItem>){
        val spath = Uri.parse(path).lastPathSegment!!
        val exportName = spath.substring(spath.lastIndexOf("/") + 1)
        val exportDirName = File(path).parentFile?.absolutePath.toString()

        val stripped = StringUtils.stripExtension(exportName,".vdc")
        ProjectExporter().writeFile(File(exportDirName, "$stripped.vdc").toString(),list)
    }
    fun close(){
        isFileLoaded = false
        currentProjectName = context.getString(R.string.untitled)
        currentDirectoryName = Environment.getExternalStorageDirectory().toString()
        isModified = false
    }
}
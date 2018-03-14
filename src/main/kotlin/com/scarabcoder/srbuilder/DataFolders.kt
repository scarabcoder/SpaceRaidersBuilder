package com.scarabcoder.srbuilder

import java.io.File

enum class DataFolders(private val _file: File) {
    PLUGIN(SRBuilderPlugin.plugin.dataFolder),
    DEFAULTS(File(SRBuilderPlugin.plugin.dataFolder, "defaults/")),
    PLAYER_PARTS(File(SRBuilderPlugin.plugin.dataFolder, "parts/")),
    DEFAULT_HULLS(File(DEFAULTS.folder, "hull/")),
    DEFAULT_ENGINES(File(DEFAULTS.folder, "engine/"));


    val folder: File
        get() {
            if(!_file.exists()){
                _file.mkdirs()
            }
            return _file
        }

}
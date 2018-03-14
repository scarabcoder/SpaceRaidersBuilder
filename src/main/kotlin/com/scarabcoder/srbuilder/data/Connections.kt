package com.scarabcoder.srbuilder.data

import com.scarabcoder.srbuilder.DataFolders
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

object Connections {
    fun grabConnection(): Connection {
        val dbFile = File(DataFolders.PLUGIN.folder, "parts.db")
        return DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
    }
}
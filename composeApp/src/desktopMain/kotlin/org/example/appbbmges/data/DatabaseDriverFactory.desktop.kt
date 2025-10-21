package org.example.appbbmges.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.appbbmges.AppDatabaseBaby
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val appDir = File(System.getProperty("user.home"), "BabySystem")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val databasePath = File(appDir, "AppDatabaseBaby.db").absolutePath

        try {
            Class.forName("org.sqlite.JDBC")
        } catch (e: ClassNotFoundException) {
            println("Error cargando SQLite driver: ${e.message}")
        }

        val driver = JdbcSqliteDriver(url = "jdbc:sqlite:$databasePath")

        val databaseFile = File(databasePath)
        if (!databaseFile.exists()) {
            try {
                AppDatabaseBaby.Schema.create(driver)
                println("Base de datos creada en: $databasePath")
            } catch (e: Exception) {
                println("Error creando base de datos: ${e.message}")
            }
        }

        return driver
    }
}
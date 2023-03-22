package com.example.agenda

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AgendaApplication: Application() {
    //Singleton para acceder a la base de datos
    companion object{
        lateinit var database: AppDatabase
    }

    /*La primera migración fue para agregar las columnas de fecha, telefono, nota y ruta imagen,
    * ya que la primera vez que se creo fue con el ID y el nombre*/
    override fun onCreate() {
        super.onCreate()

        val Migration_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE AgendaEntity ADD COLUMN fecha TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE AgendaEntity ADD COLUMN telefono TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE AgendaEntity ADD COLUMN nota TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE AgendaEntity ADD COLUMN rutaImagen TEXT NOT NULL DEFAULT ''")
            }
        }
        /*La segunda migración se modifico para que los campos de fecha y nota no sean obligatorios,
        * por lo que se tuvo que crear una tabla temporal y llenarla de los datos ya existentes, para
        * posteriormente eliminar la tabla original y renombrar la tabla temporal con el nombre original de la primera tabla*/
        val Migration_2_3 = object : Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS AgendaEntityTem (ID_agenda INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "nombre TEXT NOT NULL, fecha TEXT, " +
                        "telefono TEXT NOT NULL, " +
                        "nota TEXT, rutaImagen TEXT NOT NULL)")
                database.execSQL("INSERT INTO AgendaEntityTem (ID_agenda, nombre, telefono, nota, rutaImagen) SELECT ID_agenda, nombre, telefono, nota, rutaImagen FROM AgendaEntity")
                database.execSQL("DROP TABLE AgendaEntity")
                database.execSQL("ALTER TABLE AgendaEntityTem RENAME TO AgendaEntity")
            }
        }

        database = Room.databaseBuilder(this, AppDatabase::class.java, "Agenda").createFromAsset("database/Agenda.db").addMigrations(Migration_2_3).build()
    }
}
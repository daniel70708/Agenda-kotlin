package com.example.agenda

import androidx.room.Database
import androidx.room.RoomDatabase

//La version de la base de datos es 3 por que ha sido modificada (migrada) dos veces
@Database(entities = [AgendaEntity::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun agendaDao(): AgendaDao
}
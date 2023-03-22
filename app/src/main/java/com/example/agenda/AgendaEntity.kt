package com.example.agenda

import androidx.room.Entity
import androidx.room.PrimaryKey

//Los únicos campos que pueden ser nulos son fecha y nota, los démas son obligatorios
@Entity
data class AgendaEntity(
    @PrimaryKey(autoGenerate = true)
    var ID_agenda: Int = 0,
    val nombre: String,
    val fecha: String?,
    val telefono: String,
    val nota: String?,
    val rutaImagen: String)

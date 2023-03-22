package com.example.agenda

import androidx.room.*

@Dao
interface AgendaDao {
    @Query("Select * from AgendaEntity")
    suspend fun obteenerTodos(): MutableList<AgendaEntity>

    @Query("Select * from AgendaEntity where ID_agenda = :ID")
    suspend fun obtenerAgenda(ID: Int): AgendaEntity

    @Insert
    suspend fun agregarAgenda(agendaEntity: AgendaEntity)

    @Update
    suspend fun actualizarAgenda(agendaEntity: AgendaEntity)

    @Delete
    suspend fun eliminarAgenda(agendaEntity: AgendaEntity)
}
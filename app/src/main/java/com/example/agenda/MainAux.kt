package com.example.agenda

interface MainAux {
    /*Ambas funciones conectan el fragment Contacto con el adaptador para que podamos notar los cambios que hemos
    * hecho a la base de datos, ya sea cuando agregamos o actualizamos un registro*/
    fun agregarContacto(agendaEntity: AgendaEntity)
    fun actualizarContacto(agendaEntity: AgendaEntity)
}
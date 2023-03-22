package com.example.agenda

interface OnClickListener {
    /*La primera función es para pasar el ID_agenda al fragmento de contacto para poder cargar la información
    * a los campos de la vista para que puedan ser actualizados y la segunda función es para actualzar la lista del adaptador
    * despues de haber eliminado el registro en la base de datos (actualizado el recyclerview)*/
    fun onClick(ID_agenda: Int)
    fun eliminarContacto(agendaEntity: AgendaEntity)
}
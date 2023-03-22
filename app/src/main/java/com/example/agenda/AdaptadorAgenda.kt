package com.example.agenda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.agenda.databinding.ItemAgendaBinding

class AdaptadorAgenda(
    /*Recibimos una lista mutable de tipo AgendaEntity, y un clickListener para poder eliminar un registro y
    *para poder actualizar con el ID */
    private var listaAgenda: MutableList<AgendaEntity>,
    private var listener: com.example.agenda.OnClickListener
): RecyclerView.Adapter<AdaptadorAgenda.AgendaViewHolder>(){

    //Contexto que vamos a necesitar para poder colocar la imagen con glide
    private lateinit var mContext: Context

    /*Clase que trabaja junto al adaptador y define como se muestran los datos individualmente*/
    inner class AgendaViewHolder(view: View): RecyclerView.ViewHolder(view){
        //
        val binding = ItemAgendaBinding.bind(view)

        fun setListener(agendaEntity: AgendaEntity){
            with(binding.root){
                //clickListener para porder visualizar los datos a actualizar en el fragment de Contacto
                setOnClickListener { listener.onClick(agendaEntity.ID_agenda) }
                //clickListener para poder eliminar el registro con un click largo
                setOnLongClickListener {
                    listener.eliminarContacto(agendaEntity)
                    true
                }
            }
        }
    }

    //Inflamos la vista de item_agenda sin personalizar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_agenda, parent, false)
        return AgendaViewHolder(view)
    }

    //Regresa el tamaño de la listaAgenda
    override fun getItemCount() = listaAgenda.size

    //Remplazamos el contenido de la vista item_agenda de acuerdo a al contenido de listaAgenda de acuerdo a su posición
    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        val agenda = listaAgenda[position]
        with(holder){
            binding.itemTvNombre.text = agenda.nombre
            binding.itemTvFecha.text = agenda.fecha
            binding.itemTvTelefono.text = agenda.telefono
            binding.itemTvNotas.text = agenda.nota
            Glide.with(mContext)
                .load(agenda.rutaImagen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.itemImgPhoto)
            holder.setListener(agenda)
        }
    }
    //Función que le pasa la lista de contactos para que pueda comenzar a cargar los datos
    fun setContactos(listaAgenda: MutableList<AgendaEntity>){
        this.listaAgenda = listaAgenda
        notifyDataSetChanged()
    }
    /*Funciones con la que vamos a modificar a listaAgenda, de acuerdo si se agrego un
    * nuevo contacto, modifico o elimino para que se actualice la lista en el recyclerview */
    fun agregarContacto(agendaEntity: AgendaEntity){
        if (!listaAgenda.contains(agendaEntity)){
            listaAgenda.add(agendaEntity)
            notifyItemInserted(listaAgenda.size-1)
        }
    }

    fun actualizarContacto(agendaEntity: AgendaEntity){
        val indice = listaAgenda.indexOf(agendaEntity)
        if (indice != -1){
            listaAgenda.set(indice, agendaEntity)
            notifyItemChanged(indice)
        }
    }

    fun eliminarContacto(agendaEntity: AgendaEntity){
        val indice = listaAgenda.indexOf(agendaEntity)
        if(indice != -1){
            listaAgenda.removeAt(indice)
            notifyItemRemoved(indice)
        }
    }
}

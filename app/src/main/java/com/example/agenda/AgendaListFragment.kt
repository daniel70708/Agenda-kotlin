package com.example.agenda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.agenda.databinding.FragmentAgendaListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgendaListFragment : Fragment(), MainAux, OnClickListener {
    private var _binding: FragmentAgendaListBinding? = null
    private val binding get() = _binding!!
    //Variable para el adaptador de la agenda
    private lateinit var adaptador: AdaptadorAgenda

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAgendaListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarRecyclerview()

        //Accedemos a la action bar del main activity y agreamos el titulo de lista de contactos
        val activity = activity as? MainActivity
        activity?.supportActionBar?.title = getString(R.string.lista_agenda)

        binding.btnAgregar.setOnClickListener(View.OnClickListener {
            mostrarEditarFragment()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*Muestra el fragmento de contacto, puede pasar el ID_agenda (en caso de que sea una actualización) pero
    * por defult el bundle es nulo para el caso de que solo sea para agregar un nuevo contacto*/
    private fun mostrarEditarFragment(args: Bundle? = null){
        val newFragment = ContactoFragment()
        if(args != null) newFragment.arguments = args
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedorFragment, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    /*Iniciamos el recyclerview con todos los contactos que posea la base de datos y definimos que solo va
    * a mostrar una solo item en vertical*/
    private fun cargarRecyclerview(){
        adaptador = AdaptadorAgenda(mutableListOf(), this)
        binding.recyclerviewLista.layoutManager = GridLayoutManager(requireContext(), 1)
        obtenerContactos()
        binding.recyclerviewLista.adapter = adaptador
    }
    /*Obtenemos todos los registros y se los enviamos al adaptador para que pueda cargar todos los contactos*/
    private fun obtenerContactos(){
        lifecycleScope.launch{
            val contactos = AgendaApplication.database.agendaDao().obteenerTodos()
            adaptador.setContactos(contactos)
        }
    }

    /*MainAux (le enviamos en contacto al adaptador desde la el fragmento contecto para que pueda actualizar la lista)*/
    override fun agregarContacto(agendaEntity: AgendaEntity) {
        adaptador.agregarContacto(agendaEntity)
    }

    override fun actualizarContacto(agendaEntity: AgendaEntity) {
        adaptador.actualizarContacto(agendaEntity)
    }

    /*OnClickListener*/
    //Función que le pasa ID_agenda al fragment de contacto para que pueda cargar la información del contato para ser actualizado
    override fun onClick(ID_agenda: Int) {
        val args = Bundle()
        args.putInt(getString(R.string.arg_id), ID_agenda)
        mostrarEditarFragment(args)
    }

    /*Cuando le damos un click largo a un elemento de la lista, nos muestra un dialog indicando que si deseas eliminar
    * el registro, cuando damos click en confirmar. Eliminamos el registro y actualizamos la lista del adaptador*/
    override fun eliminarContacto(agendaEntity: AgendaEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.tituloDialog))
            .setMessage(resources.getString(R.string.mensajeDialog))
            .setPositiveButton(R.string.confirmarDialog) { dialog, which ->
                lifecycleScope.launch{
                    AgendaApplication.database.agendaDao().eliminarAgenda(agendaEntity)
                    adaptador.eliminarContacto(agendaEntity)
                }
            }
            .setNegativeButton(resources.getString(R.string.cancelarDialog), null)
            .show()
    }

}


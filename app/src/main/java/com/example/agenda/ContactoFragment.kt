package com.example.agenda

import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.agenda.databinding.FragmentContactoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch


class ContactoFragment : Fragment() {
    //bindig
    private var _binding: FragmentContactoBinding? = null
    private val binding get() = _binding!!
    //variable para acceder a las funciones del activityMain y la agendaListaFragment
    private var mActivity: MainActivity? = null
    private var mAgendaLista: AgendaListFragment? = null
    //variables para saber si se esta editando o guardando
    private var esModoEdicion: Boolean = false
    private var contacto: AgendaEntity? = null
    private var ID_contacto: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Tomamos los argumentos del fragment lista
        ID_contacto = arguments?.getInt(getString(R.string.arg_id), 0)
        //Verificamos si es modo actualizar o guardar
        if(ID_contacto != null && ID_contacto != 0){
            esModoEdicion = true
            obtenerContacto(ID_contacto!!)
        }else{
            esModoEdicion = false
            contacto = AgendaEntity(nombre = "", fecha = "", telefono = "", nota = "", rutaImagen = "")
        }
        //Modificamos el nombre en la barra de acciones según el modo en el que estemos
        iniciarlizarBarraDeAcciones()
        //Cuando haya cambios en los editText verificamos que no se encuentre vacíos
        configurarCampos()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //Al destruir la vista eliminamos el icono de regresar, así como el menú de guardar y regresamos el titulo del fragment anterior
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.lista_agenda)
        setHasOptionsMenu(false)
        _binding = null
    }

    //Inflamos el menú de guardar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_guardar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //Acciones cuando el usuario de click en algunas de las opciones de la action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Cuando de click en el bóton de regresar
        return when(item.itemId){
            android.R.id.home->{
                mActivity?.onBackPressed()
                true
            }
            //Cuando de click en guardar
            R.id.action_guardar->{
                guardarRegistro()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*Para guardar o actualizar un registro en la base de datos verificamos que los campos no esten vacíos, para
    * posteriormente crear un objeto de tipo Agenda y almacenarlo. En caso de que se actualice el registro le
    * agremos el ID del objeto antes de actualizarlo*/
    private fun guardarRegistro(){
        if (contacto != null && validarCampos(binding.itImagen, binding.itTelefono, binding.itNombre)){
            //Cramos un objeto agenda que vamos a almacenar en room
            contacto = AgendaEntity(nombre = binding.etNombre.text.toString().trim(),
                fecha = binding.etBirthday.text.toString().trim(),
                telefono = binding.etTelefono.text.toString().trim(),
                nota = binding.etNotas.text.toString().trim(),
                rutaImagen = binding.etImagen.text.toString().trim())

            if (esModoEdicion == true){
                contacto!!.ID_agenda = ID_contacto!!
                lifecycleScope.launch {
                    AgendaApplication.database.agendaDao().actualizarAgenda(contacto!!)
                    mAgendaLista?.actualizarContacto(contacto!!)
                }
                Snackbar.make(binding.root, getString(R.string.exito_actualizar), Snackbar.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch {
                    AgendaApplication.database.agendaDao().agregarAgenda(contacto!!)
                    mAgendaLista?.agregarContacto(contacto!!)
                }
                Snackbar.make(binding.root, getString(R.string.exito_guardar), Snackbar.LENGTH_SHORT).show()
            }

            mActivity?.onBackPressed()
        }
    }

    private fun iniciarlizarBarraDeAcciones(){
        //Accedemos a la action bar del main activity main, modificando el nombre y el botón de regresar
        mActivity = activity as? MainActivity
        mAgendaLista = parentFragment as? AgendaListFragment

        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title= if (esModoEdicion) (getString(R.string.actualizar_agenda))
            else (getString(R.string.agregar_agenda))
        //Creamos el menú de guardar
        setHasOptionsMenu(true)
    }

    /*Obtenemos el valor del contacto pasando el ID para poder actualizar el registro*/
    private fun obtenerContacto(ID_contacto: Int){
        lifecycleScope.launch{
           contacto = AgendaApplication.database.agendaDao().obtenerAgenda(ID_contacto)
            llenarCampos()
        }
    }
    /*Una vez que tenemos el contacto, debemos de cargar esa información en sus respectivos editText, para que el
    * usuario pueda actualizar el registro*/
    private fun llenarCampos(){
        with(binding){
            etNombre.text = contacto?.nombre?.editable()
            etBirthday.text = contacto?.fecha?.editable()
            etTelefono.text = contacto?.telefono?.editable()
            etNotas.text = contacto?.nota?.editable()
            etImagen.text= contacto?.rutaImagen?.editable()
        }
        contacto?.let { cargarImagen(it.rutaImagen) }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    /*Cada vez que se modifique el contenido de los campos oblogatorios (nombre, telefono e imagen) verificaremos que
    * no se encuentren vacíos. En el caso de la imagen cuando se introduzca una nueva ruta, el recuado de junto cargará
    * la nueva imagen y si nos encontramos en ese mismo campo al dar click en el botón de guardar del taclado ejecutará
    * esta acción como si fuera el menú de guardar */
    private fun configurarCampos(){
        with(binding){
            etNombre.addTextChangedListener { validarCampos(itNombre) }
            etTelefono.addTextChangedListener { validarCampos(itTelefono) }
            etImagen.addTextChangedListener {
                validarCampos(itImagen)
                cargarImagen(it.toString().trim())
            }
            etImagen.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    guardarRegistro()
                    true
                }else false
            }
        }
    }

    private fun cargarImagen(rutaImagen: String){
        Glide.with(requireContext())
            .load(rutaImagen)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.imgFoto)

    }
    /*Validamos que los campos oblogatorios no sean vacíos*/
    private fun validarCampos(vararg campos: TextInputLayout): Boolean{
        var esCorrecto = true
        for(campo in campos){
            if(campo.editText?.text.toString().trim().isEmpty()){
                campo.error = getString(R.string.obigatorio)
                esCorrecto = false
            }else campo.error = null
        }

        if (!esCorrecto) Snackbar.make(binding.root, getString(R.string.camposObligatorios), Snackbar.LENGTH_SHORT).show()

        return esCorrecto
    }
}

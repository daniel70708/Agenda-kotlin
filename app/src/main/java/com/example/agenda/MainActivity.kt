package com.example.agenda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agenda.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mostrarListaFragment()
    }

    fun mostrarListaFragment(){
        val newFragment = AgendaListFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedorFragment, newFragment)
        transaction.commit()
    }
}
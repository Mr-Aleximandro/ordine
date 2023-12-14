package com.example.ordine.view.pantallas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ordine.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Sesión iniciada
        val idUsuario = intent.getStringExtra("idUsuario")
        //val tvInfoAmistad = findViewById<TextView>(R.id.tvInfoAmistad)
        //tvInfoAmistad.setText(amistad)
        Toast.makeText(this, "Bienvenido ${idUsuario}", Toast.LENGTH_LONG).show()
    }
}
//[LOGIN]
package com.example.ordine.view.pantallas

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.example.ordine.R
import com.example.ordine.databinding.ActivityMainBinding
import com.example.ordine.model.SolicitudApi
import com.example.ordine.model.IVolley
import com.example.ordine.model.dataclass.IniciarSesion
import com.example.ordine.view.widgets.LoadingDialog
import com.example.ordine.view.widgets.ValidarCodigoActivity
import com.example.ordine.viewmodel.ControlResponse
import com.example.ordine.viewmodel.Internet
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.util.regex.Pattern


class MainActivity : AppCompatActivity(), IVolley {

    //Creando custom Dialog
    val dialog = LoadingDialog(this)

    var _etCorreo:TextInputLayout?=null
    var _etContrasena:TextInputLayout?=null
    var _btnIniciarSesion:Button?=null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Ordine) //Pantalla de carga
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _btnIniciarSesion = binding.btnIniciarSesion

        //Iniciar sesión -> Button
        binding.btnIniciarSesion.setOnClickListener(){
            //Validar que los TextInputLayout no esten vacíos y posteriormente iniciar sesión
            _etCorreo = binding.etCorreo
            _etContrasena = binding.etContrasena
            //validarCampos(etCorreo, etContrasena)
            validarCampos()
        }

        //Crear cuenta -> TextView
        binding.tvCrearCuenta.setOnClickListener(){
            //Desabilitando Button para evitar errores de solicitud
            binding.tvCrearCuenta.isClickable=false
            crearCuenta()
            //Habilitando Button
            Handler().postDelayed({
                binding.tvCrearCuenta.isClickable=true
            }, 1000)
        }
    }
    //:::::::::::::::::::::::Metodos:::::::::::::::::::::::
    private fun crearCuenta(){
        //Intent para pasar a la pantalla de crear cuenta
        val intent = Intent(this, CrearCuentaActivity::class.java)
        //Se envia a la pantalla para crear cuenta
        startActivity(intent)
    }
    private fun mensaje(mensaje: String){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun validarCampos() {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        if(_etCorreo?.getEditText()?.getText().isNullOrEmpty()) {
            if (!_etContrasena?.getEditText()?.getText().isNullOrEmpty())
                _etContrasena?.setError(null)
            else
                _etContrasena?.setError("Ingrese su contraseña")
            _etCorreo?.setError("Ingrese su correo electrónico")
        }else if(_etContrasena?.getEditText()?.getText().isNullOrEmpty()){
            if (!_etCorreo?.getEditText()?.getText().isNullOrEmpty())
                if(pattern.matcher(_etCorreo?.getEditText()?.getText()).matches()) {
                    _etCorreo?.setError(null)
                }else
                _etCorreo?.setError("Correo electrónico no válido")
            _etContrasena?.setError("Ingrese su contraseña")
        }else if(!_etCorreo?.getEditText()?.getText().isNullOrEmpty()){
            //Validar el correo
            if(pattern.matcher(_etCorreo?.getEditText()?.getText()).matches()) {
                //Validar si el correo y la contraseña son correctos en la BD usando la ApiREST para iniciar sesión
                validarUsuario()
            }else{
                if (!_etContrasena?.getEditText()?.getText().isNullOrEmpty())
                    _etContrasena?.setError(null)
                _etCorreo?.setError("Correo electrónico no válido")
            }
        }
    }
    private fun validarUsuario() {
        //Obtener correo y contraseña del usuario
        val correo = _etCorreo?.getEditText()?.getText().toString()
        val contrasena = _etContrasena?.getEditText()?.getText().toString()
        //Desabilitando Button para evitar errores de solicitud
        _btnIniciarSesion?.isClickable=false
        //Solicitud apiOrdine
        iniciarSesion(correo, contrasena)
        //Habilitando Button
        Handler().postDelayed({
            _btnIniciarSesion?.isClickable=true
        }, 3500)
    }

    //Solicitud POST ApiOrdine
    private fun iniciarSesion(correo:String, contrasena:String){
        //Quitando focus de los input text
        _etCorreo?.clearFocus()
        _etContrasena?.clearFocus()
        //Hide keyword
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(_etCorreo?.getWindowToken(), 0)
        imm.hideSoftInputFromWindow(_etContrasena?.getWindowToken(), 0)

        //Iniciando Custom dialog
        dialog.startLoadingDialog()
        //Comprobando conexión a Internet
        if(Internet.conexion(this)){
            //Haciendo peticion al ApiOrdine
            SolicitudApi.getInstance(this, this)
                .iniciarSesion("iniciarSesion", correo, contrasena)
            //indicando la solicitud ApiOrdine de la respuesta que se espera
            ControlResponse.response="iniciarSesion"
            //eliminando instancia de la solicitud
            SolicitudApi.destroyInstance()
        }else{
            //cerrando custom dialog50-40-5-1.5
            dialog.dismissDialog()
            mensaje("Revisa tu conexión a internet e inténtalo de nuevo.")
        }
    }
    private fun comprobandoDatos(response: String){
        //Tag e Intent para pasar al menu principal con datos
        val intent = Intent(this, InicioActivity::class.java)

        //Sacando datos de response (Json)
        var gson = Gson()
        var jsonResponse = gson?.fromJson(response, IniciarSesion.datos::class.java)

        //Comprobando que la respuesta del api sea diferente de -1
        if(jsonResponse.idUsuario != "-1"){
            //cerrando custom dialog
            dialog.dismissDialog()
            //Inicio de sesión exitoso
            _etCorreo?.setError(null)
            _etContrasena?.setError(null)

            //Se envia a la pantalla principal con sus idUsuario
            intent.putExtra("idUsuario", jsonResponse.idUsuario)
            startActivity(intent)
        }else{
            //cerrando custom dialog
            dialog.dismissDialog()
            _etCorreo?.setError("Correo incorrecto")
            _etContrasena?.setError("Contraseña incorrecta")
        }
    }


    //Respuestas apiOrdine
    override fun onResponse(response: String) {
        when (ControlResponse.response) {
            "iniciarSesion" -> {
                comprobandoDatos(response)
            }
        }
    }
    override fun onErrorResponse(response: Class<VolleyError>) {
        //cerrando custom dialog
        dialog.dismissDialog()
        when (ControlResponse.response) {
            "iniciarSesion" -> {
                _etCorreo?.setError(null)
                _etContrasena?.setError(null)
                //cerrando custom dialog
                dialog.dismissDialog()
                //response = [class com.android.volley.TimeoutError]
                mensaje("Servidor no disponible")
            }
        }
    }
}
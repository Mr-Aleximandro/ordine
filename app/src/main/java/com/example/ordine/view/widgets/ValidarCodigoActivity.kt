package com.example.ordine.view.widgets

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.example.ordine.databinding.ActivityValidarCodigoBinding
import com.example.ordine.model.IVolley
import com.example.ordine.model.SolicitudApi
import com.example.ordine.view.pantallas.MainActivity
import com.example.ordine.viewmodel.ControlResponse
import com.example.ordine.viewmodel.Internet

class ValidarCodigoActivity : AppCompatActivity(), IVolley{

    //Creando custom Dialog
    val dialog = LoadingDialog(this)

    //Text inputs
    private var thisEtCodigo1: EditText?=null
    private var thisEtCodigo2: EditText?=null
    private var thisEtCodigo3: EditText?=null
    private var thisCodigo: String?=null
    private var thisDatos: Array<String>?=null

    private lateinit var binding: ActivityValidarCodigoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidarCodigoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datos: Array<String>? = intent.getStringArrayExtra("datos")
        thisDatos = datos
        var tvSubtitulo: String = binding.tvSubtitulo.text.toString()
        tvSubtitulo += " ${datos?.get(1)}"
        binding.tvSubtitulo.setText(tvSubtitulo)

        //Transformando Id sexo
        if(thisDatos?.get(4)!!.equals("Hombre")){thisDatos?.set(4,"1")}else{thisDatos?.set(4,"2")}
        //Formateando fecha de dd/mm/yyyy -> yyyy/mm/dd
        var fecha = thisDatos!![3].split("/")
        thisDatos?.set(3, "${fecha[2]}/${fecha[1]}/${fecha[0]}")

        binding.btnValidarCodigo.setOnClickListener{
            thisEtCodigo1 = binding.etCodigo1
            thisEtCodigo2 = binding.etCodigo2
            thisEtCodigo3 = binding.etCodigo3
            validarCampos()
        }
    }
    private fun mensaje(mensaje: String){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
    private fun validarCampos(){
        if(thisEtCodigo1?.text.isNullOrEmpty() || thisEtCodigo1?.text?.length!! < 2){
            thisEtCodigo1?.setError("Código incompleto")
            if(!thisEtCodigo2?.text.isNullOrEmpty()){thisEtCodigo2?.setError(null)}
            if(!thisEtCodigo3?.text.isNullOrEmpty()){thisEtCodigo3?.setError(null)}
        }else if(thisEtCodigo2?.text.isNullOrEmpty() || thisEtCodigo2?.text?.length!! < 3){
            thisEtCodigo2?.setError("Código incompleto")
            if(!thisEtCodigo1?.text.isNullOrEmpty()){thisEtCodigo1?.setError(null)}
            if(!thisEtCodigo3?.text.isNullOrEmpty()){thisEtCodigo3?.setError(null)}
        }else if(thisEtCodigo3?.text.isNullOrEmpty() || thisEtCodigo3?.text?.length!! < 2){
            thisEtCodigo3?.setError("Código incompleto")
            if(!thisEtCodigo1?.text.isNullOrEmpty()){thisEtCodigo1?.setError(null)}
            if(!thisEtCodigo2?.text.isNullOrEmpty()){thisEtCodigo2?.setError(null)}
        }else{
            thisEtCodigo1?.setError(null)
            thisEtCodigo2?.setError(null)
            thisEtCodigo3?.setError(null)
            guardarCodigo()
            comprobarCodigo()
        }
    }

    private fun guardarCodigo(){
        thisCodigo = "${thisEtCodigo1?.text.toString()}-${thisEtCodigo2?.text.toString()}-${thisEtCodigo3?.text.toString()}"
    }

    private fun comprobarCodigo(){
        //Desabilitando Button para evitar errores de solicitud
        binding.btnValidarCodigo.isClickable=false
        //Iniciando Custom dialog
        dialog.startLoadingDialog()
        //Comprobando conexión a Internet
        if(Internet.conexion(this)){
            SolicitudApi.getInstance(this, this)
                .comprobarCodigo("comprobarCodigo", thisCodigo!!, thisDatos?.get(1)!!)
            //indicando la solicitud ApiOrdine de la respuesta que se espera
            ControlResponse.response="comprobarCodigo"
            //eliminando instancia de la solicitud
            SolicitudApi.destroyInstance()
        }else{
            //cerrando custom dialog
            dialog.dismissDialog()
            mensaje("Revisa tu conexión a internet e inténtalo de nuevo.")
        }
        //Habilitando Button
        Handler().postDelayed({
            binding.btnValidarCodigo.isClickable=true
        }, 500)
    }

    private fun registrarUsuario(response: String){
        if(response.equals("\"true\"")){
            //Comprobando conexión a Internet
            if(Internet.conexion(this)){
                SolicitudApi.getInstance(this, this)
                    .registrarUsuario("registrarUsuario", thisDatos?.get(0)!!, thisDatos?.get(3)!!, thisDatos?.get(1)!!, thisDatos?.get(2)!!, thisDatos?.get(4)!!)
                //indicando la solicitud ApiOrdine de la respuesta que se espera
                ControlResponse.response="registrarUsuario"
                //eliminando instancia de la solicitud
                SolicitudApi.destroyInstance()
            }else{
                //cerrando custom dialog
                dialog.dismissDialog()
                mensaje("Revisa tu conexión a internet e inténtalo de nuevo.")
            }
        }else{
            //cerrando custom dialog
            dialog.dismissDialog()
            mensaje("Código incorrecto.")
        }
    }

    private fun cambiarPantalla(response: String){
        if(response.equals("\"true\"")){
            val intent = Intent(this, MainActivity::class.java)
            //cerrando custom dialog
            dialog.dismissDialog()
            mensaje("Se ha registrado correctamente")
            //Cambiar de pantalla
            startActivity(intent)
        }else{
            //cerrando custom dialog
            dialog.dismissDialog()
            mensaje("No se pudo completar el registro, inténtelo más tarde")
        }
    }

    //Respuestas apiOrdine
    override fun onResponse(response: String) {
        when (ControlResponse.response) {
            "comprobarCodigo" -> {
                registrarUsuario(response)
            }
            "registrarUsuario" -> {
                cambiarPantalla(response)
            }
        }
    }

    override fun onErrorResponse(response: Class<VolleyError>) {
        when (ControlResponse.response) {
            "comprobarCodigo" -> {
                dialog.dismissDialog()
                //response = [class com.android.volley.TimeoutError]
                mensaje("Servidor no disponible")
            }
        }
    }
}
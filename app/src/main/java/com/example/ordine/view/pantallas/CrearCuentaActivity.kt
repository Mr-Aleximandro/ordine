package com.example.ordine.view.pantallas

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.example.ordine.R
import com.example.ordine.databinding.ActivityCrearCuentaBinding
import com.example.ordine.model.IVolley
import com.example.ordine.model.SolicitudApi
import com.example.ordine.model.dataclass.IniciarSesion
import com.example.ordine.view.widgets.LoadingDialog
import com.example.ordine.view.widgets.ValidarCodigoActivity
import com.example.ordine.viewmodel.ControlResponse
import com.example.ordine.viewmodel.Internet
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class CrearCuentaActivity : AppCompatActivity(), IVolley {

    //Creando custom Dialog
    val dialog = LoadingDialog(this)

    //Text inputs
    private var thisEtNombre: TextInputLayout?=null
    private var thisEtCorreo: TextInputLayout?=null
    private var thisEtContrasena: TextInputLayout?=null
    private var thisBtnFecha:Button?=null
    private var thisBtnSexo:Button?=null
    private var thisBtnCrearCuenta:Button?=null

    //Declarando el formato e idioma de la fecha
    var formatDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat("dd/MM/YYYY", Locale.US)
    } else {
        TODO("VERSION.SDK_INT < N")
    }
    //Declarando dia, mes y año
    val getDate: Calendar = Calendar.getInstance()
    var dia: Int = getDate.get(Calendar.DAY_OF_MONTH) //[0...31]
    var mes: Int = getDate.get(Calendar.MONTH)        //[0...11]
    var anio: Int = getDate.get(Calendar.YEAR)        //[1999]

    private lateinit var binding: ActivityCrearCuentaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Button Fecha
        binding.btnDatePicker.setOnClickListener(){
            //Desabilitando Button para evitar errores de solicitud-
            binding.btnDatePicker.isClickable=false
            //Mostrando datePicker
            dataPicker(binding.btnDatePicker)
            //Habilitando Button
            Handler().postDelayed({
                binding.btnDatePicker.isClickable=true
            }, 500)
        }
        //Button Sexo
        binding.btnSexo.setOnClickListener(){
            //Desabilitando Button para evitar errores de solicitud
            binding.btnSexo.isClickable=false
            //Mostrando dialogSexo
            sexPicker(binding.btnSexo)
            //Habilitando Button
            Handler().postDelayed({
                binding.btnSexo.isClickable=true
            }, 500)
        }
        //Button Crear cuenta
        binding.btnCrearCuenta.setOnClickListener(){
            thisEtNombre = binding.etNombre
            thisEtCorreo = binding.etCorreo
            thisEtContrasena = binding.etContrasena
            thisBtnFecha = binding.btnDatePicker
            thisBtnSexo = binding.btnSexo
            validarCampos()
        }
    }

    //Métodos
    private fun mensaje(mensaje: String){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
    private fun dataPicker(btnDatePicker: Button){
        val datepicker = DatePickerDialog(this, R.style.DatePicker, DatePickerDialog.OnDateSetListener{ datePicker, i, i2, i3 ->
            val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR,i)
                selectDate.set(Calendar.MONTH,i2)
                selectDate.set(Calendar.DAY_OF_MONTH,i3)
            val date: String  = formatDate.format(selectDate.time)
            dia = (date.get(0).toString()+date.get(1).toString()).toInt()
            mes = (date.get(3).toString()+date.get(4).toString()).toInt()-1
            anio = (date.get(6).toString()+date.get(7).toString()+date.get(8).toString()+date.get(9).toString()).toInt()
            //mensaje("Usuario: Dia:$dia Mes:$mes Año:$anio")
            //mensaje("Sistema: Dia:$i3 Mes:$i2 Año:$i")
            btnDatePicker.setText(date)
        },anio, mes, dia)
        datepicker.setCancelable(false)
        datepicker.show()
    }
    private fun sexPicker(btnSexo: Button){
        var alertDialog: AlertDialog? = null
        var values = arrayOf<CharSequence>("Hombre", "Mujer")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.SexPicker)
        builder.setCancelable(false)
        builder.setTitle("Sexo")
        var checkedItem: Int = -1
        if(btnSexo.text=="Hombre"){
            checkedItem=0
        }else if(btnSexo.text=="Mujer"){
            checkedItem=1
        }
        builder.setSingleChoiceItems(values, checkedItem,
            DialogInterface.OnClickListener { dialog, item ->
                when (item) {
                    0 -> btnSexo.setText("Hombre")
                    1 -> btnSexo.setText("Mujer")
                }
            })
        builder.setPositiveButton("ACEPTAR"){
                alertDialog, item -> alertDialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog.show()
    }
    private fun validarCampos(){
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        if(thisEtNombre?.editText?.text.isNullOrEmpty()){
            thisEtNombre?.error = "Ingrese su nombre"
            if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = null}else if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && !pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = "Correo electrónico no válido"}
            if(!thisEtContrasena?.editText?.text.isNullOrEmpty()){thisEtContrasena?.error = null}
            if(thisBtnFecha?.text != "Seleccionar"){thisBtnFecha?.error = null}
            if(thisBtnSexo?.text != "Seleccionar"){thisBtnSexo?.error = null}
        }else if(thisEtCorreo?.editText?.text.isNullOrEmpty()){
            thisEtCorreo?.error = "Ingrese su correo electrónico"
            if(!thisEtNombre?.editText?.text.isNullOrEmpty()){thisEtNombre?.error = null}
            if(!thisEtContrasena?.editText?.text.isNullOrEmpty()){thisEtContrasena?.error = null}
            if(thisBtnFecha?.text != "Seleccionar"){thisBtnFecha?.error = null}
            if(thisBtnSexo?.text != "Seleccionar"){thisBtnSexo?.error = null}
        }else if(thisEtContrasena?.editText?.text.isNullOrEmpty()){
            thisEtContrasena?.error = "Ingrese una contraseña"
            if(!thisEtNombre?.editText?.text.isNullOrEmpty()){thisEtNombre?.error = null}
            if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = null}else if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && !pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = "Correo electrónico no válido"}
            if(thisBtnFecha?.text != "Seleccionar"){thisBtnFecha?.error = null}
            if(thisBtnSexo?.text != "Seleccionar"){thisBtnSexo?.error = null}
        }else if(thisBtnFecha?.text == "Seleccionar"){
            thisBtnFecha?.error = ""
            if(!thisEtNombre?.editText?.text.isNullOrEmpty()){thisEtNombre?.error = null}
            if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = null}else if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && !pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = "Correo electrónico no válido"}
            if(!thisEtContrasena?.editText?.text.isNullOrEmpty()){thisEtContrasena?.error = null}
            if(thisBtnSexo?.text != "Seleccionar"){thisBtnSexo?.error = null}
        }else if(thisBtnSexo?.text == "Seleccionar"){
            thisBtnSexo?.error = ""
            if(!thisEtNombre?.editText?.text.isNullOrEmpty()){thisEtNombre?.error = null}
            if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = null}else if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && !pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){thisEtCorreo?.error = "Correo electrónico no válido"}
            if(!thisEtContrasena?.editText?.text.isNullOrEmpty()){thisEtContrasena?.error = null}
            if(thisBtnFecha?.text != "Seleccionar"){thisBtnFecha?.error = null}
        }else{
            thisEtNombre?.error = null
            thisEtContrasena?.error = null
            thisBtnFecha?.error = null
            thisBtnSexo?.error = null
            if(!thisEtCorreo?.editText?.text.isNullOrEmpty() && pattern.matcher(thisEtCorreo?.getEditText()?.getText()).matches()){
                thisEtCorreo?.error = null
                //Validar si existe una cuenta con el correo electrónico
                validarCorreo()
            }else{thisEtCorreo?.error = "Correo electrónico no válido"}
        }
    }

    //Solicitud POST ApiOrdine
    private fun validarCorreo(){
        //Desabilitando Button para evitar errores de solicitud
        binding.btnCrearCuenta.isClickable=false
        //Iniciando Custom dialog
        dialog.startLoadingDialog()
        //Comprobando conexión a Internet
        if(Internet.conexion(this)){
            val correo = thisEtCorreo?.editText?.text.toString()
            SolicitudApi.getInstance(this, this)
                .comprobarCorreo("comprobarCorreo", correo)
            //indicando la solicitud ApiOrdine de la respuesta que se espera
            ControlResponse.response="comprobarCorreo"
            //eliminando instancia de la solicitud
            SolicitudApi.destroyInstance()
        }else{
            //cerrando custom dialog
            dialog.dismissDialog()
            mensaje("Revisa tu conexión a internet e inténtalo de nuevo.")
        }
        //Habilitando Button
        Handler().postDelayed({
            binding.btnCrearCuenta.isClickable=true
        }, 500)
    }
    private fun comprobarCorreo(response: String){
        if(response.equals("\"true\"")){
            //cerrando custom dialog
            dialog.dismissDialog()
            mensaje("Ya existe una cuenta asociada a ${thisEtCorreo?.editText?.text.toString()} Ingrese otro correo")
        }else if (response.equals("\"false\"")){
            //Enviar código de validación
            enviarCodigo()
        }else{
            mensaje("Ocurrió un problema")
        }
    }

    private fun enviarCodigo(){

        //Comprobando conexión a Internet
        if(Internet.conexion(this)) {
            //Hacer peticion POST al ApiOrdine
            SolicitudApi.getInstance(this, this)
                .enviarCorreo(
                    "enviarCorreo",
                    thisEtCorreo?.editText?.text.toString(),
                    thisEtNombre?.editText?.text.toString(),
                    "0"
                )
            //indicando la solicitud ApiOrdine de la respuesta que se espera
            ControlResponse.response = "enviarCorreo"
            //eliminando instancia de la solicitud
            SolicitudApi.destroyInstance()
        }else{
            //cerrando custom dialog
            dialog.dismissDialog()
            mensaje("Revisa tu conexión a internet e inténtalo de nuevo.")
        }
    }

    private fun cambiarPantalla(response: String) = if(response.equals("\"true\"")){
        //Preparar datos para enviar a la pantalla de validar código
        var datos = arrayOf(
            thisEtNombre?.editText?.text.toString(),
            thisEtCorreo?.editText?.text.toString(),
            thisEtContrasena?.editText?.text.toString(),
            thisBtnFecha?.text.toString(),
            thisBtnSexo?.text.toString()
        )

        val intent = Intent(this, ValidarCodigoActivity::class.java)
        //Array de datos
        intent.putExtra("datos", datos)
        //cerrando custom dialog
        dialog.dismissDialog()
        //Cambiar de pantalla
        startActivity(intent)
    }else{
        //cerrando custom dialog
        dialog.dismissDialog()
        mensaje("Se ha producido un error inténtelo más tarde")
        mensaje(response)
    }


    //Respuestas apiOrdine
    override fun onResponse(response: String) {
        when (ControlResponse.response) {
            "comprobarCorreo" -> {
                comprobarCorreo(response)
            }
            "enviarCorreo" -> {
                cambiarPantalla(response)
            }
        }
    }
    override fun onErrorResponse(response: Class<VolleyError>) {
        //cerrando custom dialog
        dialog.dismissDialog()
        when (ControlResponse.response) {
            "comprobarCorreo" -> {
                thisEtNombre?.error = null
                thisEtContrasena?.error = null
                thisEtContrasena?.error = null
                thisBtnFecha?.error = null
                thisBtnSexo?.error = null
                //cerrando custom dialog
                dialog.dismissDialog()
                //response = [class com.android.volley.TimeoutError]
                mensaje("Servidor no disponible")
            }
            "enviarCorreo" -> {

            }
        }
    }
}
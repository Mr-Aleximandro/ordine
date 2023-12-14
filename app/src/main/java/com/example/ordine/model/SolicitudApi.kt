package com.example.ordine.model
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class SolicitudApi {
    private var mRequestQueue: RequestQueue?=null
    private var context: Context?=null
    private var iVolley: IVolley?=null
    val requestQueue: RequestQueue
        get(){
            if(mRequestQueue == null)
                mRequestQueue = Volley.newRequestQueue(context!!.applicationContext)
            return mRequestQueue!!
        }
    private constructor(context: Context, iVolley: IVolley){
        this.context = context
        this.iVolley = iVolley
        mRequestQueue = requestQueue
    }
    fun <T> addToRequestQueue(req: Request<T>){
        requestQueue.add(req)
    }

    companion object{
        private var mInstance: SolicitudApi?=null
        @Synchronized
        fun getInstance(context: Context, iVolley: IVolley): SolicitudApi{
            if(mInstance == null){
                mInstance = SolicitudApi(context, iVolley)
            }
            return mInstance!!
        }

        fun destroyInstance(){
            mInstance = null
        }
    }

    //Url base
    val urlApi: String = "http://192.168.0.5:8081/rest/index.php/ordine/" //"http://10.0.0.4:8081/rest/index.php/ordine/" //

    //Solicitudes POST al servidor (Api Ordine)
    //comprobarCorreo
    fun comprobarCorreo(url:String, correo:String){
        val URL = urlApi + url
        val comprobarCorreo = object : StringRequest(Request.Method.POST, URL,
            Response.Listener { response ->
                iVolley!!.onResponse(response.toString())
            }, Response.ErrorListener { error ->
                iVolley!!.onErrorResponse(
                    //error.message!!
                    error.javaClass!!
                )
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["correoUsuario"] = correo
                return params
            }
        }
        addToRequestQueue(comprobarCorreo)
    }
    //enviarCorreo
    fun enviarCorreo(url:String, correo:String, nombre:String, tipo:String){
        val URL = urlApi + url
        val enviarCorreo = object : StringRequest(Request.Method.POST, URL,
            Response.Listener { response ->
                iVolley!!.onResponse(response.toString())
            }, Response.ErrorListener { error ->
                iVolley!!.onErrorResponse(
                    //error.message!!
                    error.javaClass!!
                )
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["correoUsuario"] = correo
                params["nombreUsuario"] = nombre
                params["tipoCorreo"] = tipo
                return params
            }
        }
        addToRequestQueue(enviarCorreo)
    }
    //comprobarCodigo
    fun comprobarCodigo(url:String, codigo:String, correo:String){
        val URL = urlApi + url
        val comprobarCodigo = object : StringRequest(Request.Method.POST, URL,
            Response.Listener { response ->
                iVolley!!.onResponse(response.toString())
            }, Response.ErrorListener { error ->
                iVolley!!.onErrorResponse(
                    //error.message!!
                    error.javaClass!!
                )
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["codigo"] = codigo
                params["correo"] = correo
                return params
            }
        }
        addToRequestQueue(comprobarCodigo)
    }
    //registrarUsuario
    fun registrarUsuario(url:String, nombre:String, fecha:String, correo:String, contrasena:String, sexo:String){
        val URL = urlApi + url
        val registrarUsuario = object : StringRequest(Request.Method.POST, URL,
            Response.Listener { response ->
                iVolley!!.onResponse(response.toString())
            }, Response.ErrorListener { error ->
                iVolley!!.onErrorResponse(
                    //error.message!!
                    error.javaClass!!
                )
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombreUsuario"] = nombre
                params["fechaNacimientoUsuario"] = fecha
                params["correoUsuario"] = correo
                params["contrasenaUsuario"] = contrasena
                params["idSexo"] = sexo
                return params
            }
        }
        addToRequestQueue(registrarUsuario)
    }
    //Iniciar Sesion
    fun iniciarSesion(url:String, correo:String, contrasena:String){
        val URL = urlApi + url
        val iniciarSesion = object : StringRequest(Request.Method.POST, URL,
            Response.Listener { response ->
                iVolley!!.onResponse(response.toString())
            }, Response.ErrorListener { error ->
                iVolley!!.onErrorResponse(
                    //error.message!!
                    error.javaClass!!
                )
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["correoUsuario"] = correo
                params["contrasenaUsuario"] = contrasena
                return params
            }
        }
        addToRequestQueue(iniciarSesion)
    }
    //mostrarDatosUsuario();
    //mostrarAmigos();
    //mostrarUsuarios();
    //mostrarDatosAmigo();
    //setNombre();
    //setApodo();
    //setFechaNacimiento();
    //setFoto();
    //setDescripcion();
    //setEstadoCivil();
    //setSexo();
    //setContrasena();
    //setAmistad();
    //setTiempoConocido();
    //setConfianza();
    //agregarAmigo();
    //eliminarAmigo();
}
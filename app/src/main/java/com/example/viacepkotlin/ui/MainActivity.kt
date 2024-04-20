package com.example.viacepkotlin.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viacepkotlin.R
import com.example.viacepkotlin.api.Api
import com.example.viacepkotlin.databinding.ActivityMainBinding
import com.example.viacepkotlin.model.Endereco
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        window.statusBarColor = Color.parseColor("#4CAF50")
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#4CAF50")))

        //Configurar o Retrofit
        val retrofit = Retrofit.Builder() //Aciona o Retrofit na Activity
            .addConverterFactory(GsonConverterFactory.create()) //Faz a conversão do arquivo Json
            .baseUrl("https://viacep.com.br/ws/") //url de base da nossa API
            .build()
            .create(Api::class.java) //Associar configuração do Retrofit com a API


        binding.btBuscarCep.setOnClickListener {

            val cep = binding.TxtCep.text.toString()


            if (cep.isEmpty()) {

                Toast.makeText(this, "Preencha o campo de CEP!", Toast.LENGTH_SHORT).show()

            } else {

                retrofit.setEndereco(cep).enqueue(object : Callback<Endereco> {
                    override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {

                        if (response.code() == 200) {

                            val logradouro = response.body()?.logradouro.toString()
                            val bairro = response.body()?.bairro.toString()
                            val cidade = response.body()?.localidade.toString()
                            val estado = response.body()?.uf.toString()

                            setFormularios(logradouro, bairro, cidade, estado)

                        } else {

                            Toast.makeText(applicationContext, "Cep Inválido!", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Endereco>, t: Throwable) {

                        Toast.makeText(applicationContext, "Erro no servidor!", Toast.LENGTH_LONG)
                            .show()
                    }
                })
            }
        }
    }

private fun setFormularios(logradouro: String, bairro: String, cidade: String, estado: String){

    binding.TxtLogradouro.setText(logradouro)
    binding.TxtBairro.setText(bairro)
    binding.TxtCidade.setText(cidade)
    binding.TxtEstado.setText(estado)

    }
}
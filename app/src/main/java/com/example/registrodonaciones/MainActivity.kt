//UNIVERSIDAD TECNOLOGICA INDOAMERICA
//INGENIERIA EN TECNOLOGIAS DE LA INFORMACIÓN
//DESARROLLO DE APLICCACIONES MOVILS
// Autor: Mateo Miño
// Fecha: 17/10/2026
// Pantalla principal para registrar bienes donados.

package com.example.registrodonaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.registrodonaciones.ui.theme.RegistroDonacionesTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {  // Inicializa la actividad y carga la interfaz principal con Jetpack Compose.
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            RegistroDonacionesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRegistro(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Presenta el formulario y administra el estado de sus campos.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(modifier: Modifier = Modifier) {

    // Variables de estado para conservar los datos ingresados.
    var nombreBien by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Alimentos") }
    var cantidadTexto by remember { mutableStateOf("") }
    var nombreDonante by remember { mutableStateOf("") }

    // Estados para mostrar los mensajes del registro.
    var resumen by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var menuExpandido by remember { mutableStateOf(false) }

    // Opciones disponibles para clasificar los bienes donados.
    val categorias = listOf("Alimentos", "Ropa", "Higiene", "Otros")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Control de Inventarios de la Fundación Familia de Familias",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Fundación Familia de Familias",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Ingresa los datos del bien recibido."
        )

        // Campo para ingresar el nombre del bien.
        OutlinedTextField(
            value = nombreBien,
            onValueChange = {
                nombreBien = it
                mensajeError = ""
            },
            label = { Text("Nombre del bien") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Selector desplegable de categorías.
        ExposedDropdownMenuBox(
            expanded = menuExpandido,
            onExpandedChange = { menuExpandido = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuExpandido
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { menuExpandido = false }
            ) {
                categorias.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            categoria = opcion
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        // Campo para ingresar la cantidad de unidades.
        OutlinedTextField(
            value = cantidadTexto,
            onValueChange = {
                cantidadTexto = it
                mensajeError = ""
            },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Campo para ingresar el nombre de quien realiza la donación.
        OutlinedTextField(
            value = nombreDonante,
            onValueChange = {
                nombreDonante = it
                mensajeError = ""
            },
            label = { Text("Nombre del donante") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Procesa los datos cuando el usuario presiona Registrar.
        Button(
            onClick = {
                val cantidad = cantidadTexto.toIntOrNull()

                when {
                    nombreBien.isBlank() ||
                            nombreDonante.isBlank() ||
                            cantidadTexto.isBlank() -> {
                        mensajeError = "Completa todos los campos obligatorios."
                        resumen = ""
                    }

                    cantidad == null || cantidad <= 0 -> {
                        mensajeError = "Ingresa una cantidad entera mayor que cero."
                        resumen = ""
                    }

                    else -> {
                        val stock = clasificarStock(cantidad)

                        resumen = construirResumen(
                            nombreBien = nombreBien.trim(),
                            categoria = categoria,
                            cantidad = cantidad,
                            nombreDonante = nombreDonante.trim(),
                            stock = stock
                        )

                        mensajeError = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        // Muestra los errores de validación cuando existen.
        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error
            )
        }

        // Presenta el resumen cuando los datos son válidos.
        if (resumen.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Bien registrado correctamente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(text = resumen)
                }
            }
        }
    }
}

// Clasifica el inventario usando la cantidad de unidades recibidas.
// Los rangos son una propuesta de implementación para este taller.
fun clasificarStock(cantidad: Int): String {
    return when {
        cantidad <= 0 -> "Cantidad inválida"
        cantidad <= 5 -> "Stock bajo"
        cantidad <= 20 -> "Stock medio"
        else -> "Stock alto"
    }
}

// Construye un resumen legible con la información del bien donado.
fun construirResumen(
    nombreBien: String,
    categoria: String,
    cantidad: Int,
    nombreDonante: String,
    stock: String
): String {
    return """
        Bien: $nombreBien
        Categoría: $categoria
        Cantidad: $cantidad unidades
        Donante: $nombreDonante
        Clasificación: $stock
    """.trimIndent()
}

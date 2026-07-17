//MARIA MADRID TALLER 1
fun main() {
    //VARIABLES PARA EL PASAJERO
    var nombre =""
    var apellido =""
    var edad =0
    var sexo =""
    var pasajeroRegistrado =false
    var boletoPagado =false
    var tipoPago =""
    val precioBoleto =20.00

    //BUCLE PRINCIPAL DEL MENU Y PERMANECE ACTIVO
    while (true) {
        println("\n===== TRANSPORTE UTP S.A. =====")
        println("1-registrar pasajero")
        println("2-comprar boleto")
        println("3-salir")
        print("seleccione una opción: ")
        when (readLine()?.trim()) {
            //OPCION REGISTRAR PASAJERO
            "1"-> {
                print("ingrese nombre: ")
                nombre = readLine()?.trim() ?: ""

                print("ingrese apellido: ")
                apellido = readLine()?.trim() ?: ""

                print("ingrese edad: ")
                val edadInput = readLine()?.trim() ?: ""

                print("ingrese su género (M/F): ")
                sexo = readLine()?.trim()?.uppercase() ?: ""

                //VALIDACIONDE NOMBRE Y APELLIDO
                val nombreValido = nombre.isNotBlank() && nombre.all { it.isLetter() || it.isWhitespace() }
                val apellidoValido = apellido.isNotBlank() && apellido.all { it.isLetter() || it.isWhitespace() }

                //VALIDACION EDAD
                val edadConvertida = edadInput.toIntOrNull()
                val edadValida = edadConvertida != null && edadConvertida > 0

                //VALIDACION GENERO
                val sexoValido = sexo.length == 1 && sexo[0].isLetter() && (sexo == "M" || sexo == "F")

                //MENSAJES POR SI FALLA ALGUNA VALIDADCION
                if (!nombreValido) {
                    println("\nnombre invalido.no debe estar vacio ni contener numeros")
                } else if (!apellidoValido) {
                    println("\napellido invalido.no debe estar vacio ni contener numeros")
                } else if (!edadValida) {
                    println("\nedad invalida.debe ser un numero entero mayor que cero")
                } else if (!sexoValido) {
                    println("\ngenero invalido.solo se acepta M o F")
                } else {
                    //SI ESTA BIEN GUARDA LA EDAD Y SE REGISTRA EL PASAJERO
                    edad = edadConvertida!!
                    pasajeroRegistrado = true
                    println("\npasajero registrado correctamente")
                    println("  nombre completo: $nombre $apellido")
                }
            }
            //OPCION COMPRAR BOLETO
            "2"-> {
                //VERIFICAR QUE EXISTA UN PASAJERO REGISTRADO ANTES DE CONTINUAR
                if (!pasajeroRegistrado) {
                    println("\n por favor primero registrar un pasajero (opción 1 del menu)")
                } else {
                    // VARIABLES PARA CALCULAR DESCUENTO
                    val descuento: Double
                    val motivoDescuento: String

                    //DESCUENTO SEGUN EDAD Y GENERO
                    when {
                        edad <12-> {
                            descuento =0.05
                            motivoDescuento = "menor de edad (5%)"
                        }
                        (sexo == "F" && edad>57)-> {
                            descuento =0.15
                            motivoDescuento= "adulta mayor (15%)"
                        }
                        (sexo == "M" && edad > 62)-> {
                            descuento =0.15
                            motivoDescuento ="adulto mayor (15%)"
                        }
                        else -> {
                            descuento =0.0
                            motivoDescuento="descuento no aplicable"
                        }
                    }

                    //MONTO FINAL
                    val montoDescuento = precioBoleto * descuento
                    val totalAPagar = precioBoleto - montoDescuento

                    //RESUMEN ANTES DE PAGAR Y METODO DE PAGI
                    println("\n--- RESUMEN DE COMPRA ---")
                    println("precio del boleto : B/ %.2f".format(precioBoleto))
                    println("descuento         : $motivoDescuento")
                    println("monto descontado  : -B/ %.2f".format(montoDescuento))
                    println("TOTAL A PAGAR     : B/ %.2f".format(totalAPagar))

                    println("\nmetodos de pago disponibles:")
                    println("  1-visa")
                    println("  2-clave")
                    println("  3-cheque")
                    println("  4-efectivo")
                    println("  5-transferencia bancaria")
                    println("  6-yappy")
                    print("seleccione metodo de pago: ")

                    tipoPago = when (readLine()?.trim()) {
                        "1"-> "visa"
                        "2"-> "clave"
                        "3"-> "cheque"
                        "4"-> "efectivo"
                        "5"-> "transferencia bancaria"
                        "6"-> "yappy"
                        else-> "no especificado"
                    }
                    boletoPagado = true

                    //RECIBO FIANL
                    println("\n")
                    println("========================================")
                    println("       --- TRANSPORTE UTP S.A. ---      ")
                    println("            RUC: 01-2531-4507              ")
                    println("           TERMINAL PRINCIPAL            ")
                    println("========================================")
                    println("  CLIENTE : $nombre $apellido")
                    println("  EDAD    : $edad años")
                    println("  GÉNERO  : ${if (sexo == "F") "Femenino" else "Masculino"}")
                    println("  DESCUENTO: $motivoDescuento")
                    println("  COSTO   : B/ %.2f".format(totalAPagar))
                    println("  PAGO    : $tipoPago")
                    println("========================================")
                    println("              BUEN VIAJE!               ")
                    println("========================================")
                }
            }

            // OPCION SALIR
            "3"-> {
                println("\ngracias por usar TRANSPORTE UTP S.A.")
                break
            }

            // OPCION INVALIDA
            else -> println("\nopcion invalida. intendte nuevamente con una opcion valida:")
        }
    }
}
#!/bin/bash

instancia=$1
solucion=$2

# Compilar el archivo VerificadorTSP.kt
kotlinc VerificadorTSP.kt -include-runtime -d VerificadorTSP.jar

# Ejecutar el programa VerificadorTSP.jar
java -jar VerificadorTSP.jar "$instancia" "$solucion"

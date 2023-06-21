#!/bin/bash

archivoEntrada=$1
archivoSalida=$2

# Compilar el archivo DCLSTSP.kt
kotlinc DCLSTSP.kt -include-runtime -d DCLSTSP.jar

# Ejecutar el programa DCLSTSP.jar
java -jar DCLSTSP.jar "$archivoEntrada" "$archivoSalida"
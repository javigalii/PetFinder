VintedClone – Marketplace de Segunda Mano

PARA VERLO BUSCA EN GOOGLE http://217.154.185.97:9000/

Plataforma web basada en PHP y MySQL que replica las funciones esenciales de Vinted, permitiendo a usuarios comprar y vender artículos de segunda mano mediante un flujo completo de comercio electrónico, autenticación y pagos con PayPal.

📌 Overview

Este proyecto implementa un sistema completo de compra-venta:

Autenticación de usuarios

Navegación de productos

Gestión de carrito

Publicación y edición de productos

Procesos de compra mediante PayPal

Toda la lógica se organiza en páginas PHP tradicionales con recarga completa, sin frameworks de JavaScript.

Archivos principales

vinted/index.php – Catálogo principal

vinted/conexion.php – Conexión y creación de tablas

Scripts de acciones (carrito, compra, venta)

🎯 Propósito del Sistema

La plataforma funciona como un marketplace de doble cara:

Compradores

Navegar productos subidos por otros usuarios

Añadir artículos al carrito

Completar pagos vía PayPal

Vendedores

Subir productos con imágenes

Editar sus publicaciones

Gestionar su inventario

Los productos propios no aparecen en el catálogo del comprador, evitando autocompras.

🧱 Technology Stack
Capa	Tecnología	Función
Frontend	HTML5, Bootstrap 5.3.2	Interfaz y diseño responsive
Backend	PHP	Lógica servidor, sesiones
Base de datos	MySQL	Usuarios y productos
Almacenamiento	uploads/	Archivos de imagen
Pagos	PayPal Standard	Procesamiento externo
Sesiones	$_SESSION	Estado del usuario

Arquitectura LAMP clásica, sin AJAX ni frameworks.

🗂️ Estructura de la Aplicación

El proyecto se divide en tres capas:

1. Entry Points

Páginas PHP que renderizan la interfaz (index, login, vender, carrito).

2. Action Scripts

Scripts pequeños que modifican estado y redirigen (alcarrito.php, vaciarcarrito.php).

3. Infraestructura

conexion.php: conexión y creación de tablas

funciones.php: cabecera, contador del carrito

🔧 Componentes Principales
Conexión a la base de datos (conexion.php)

Crea las tablas javiusers y javiproductos si no existen

Inserta usuario admin si la tabla está vacía

Usa credenciales locales y base de datos vinted

Funciones compartidas (funciones.php)

cabezera(): genera menú superior

escribirCantidadCarrito(): suma de items en el carrito

Sesiones

Variables relevantes:

$_SESSION["logged"] – estado de login

$_SESSION["id"] – ID del usuario

$_SESSION["carrito"] – productos añadidos

$_SESSION["totalprecio"] – total calculado

🗄️ Modelo de Datos
Tablas principales
javiusers

Usuario, email, contraseña (sin hash)

Rol: usuario o admin

javiproductos

Nombre, precio, imagen, descripción

vendedor → FK a javiusers.id

vendido (no utilizado en la lógica actual)

🔁 Flujo de Peticiones

session_start()

Carga de conexion.php

Carga de utilidades

Lógica específica de la página

Render de HTML

Los scripts de acción nunca muestran HTML: solo modifican datos y redirigen.

🔐 Modelo de Autenticación

Sistema binario: logueado o anónimo.

Páginas como vender, modificar, carrito requieren login.

index.php y detalle.php muestran avisos si el usuario no está autenticado.

📄 Mapa de Archivos
Archivo	Propósito
index.php	Catálogo principal
login.php	Inicio de sesión
registro.php	Registro de usuarios
detalle.php	Ficha de producto
vender.php	Panel del vendedor
modificar.php	Edición de productos
carrito.php	Vista del carrito
comprarcarrito.php	Integración con PayPal
alcarrito.php	Añadir producto
vaciarcarrito.php	Vaciar carrito
conexion.php	BD + creación de tablas
funciones.php	Cabecera y utilidades
🧭 Navegación del Usuario
Flujo del comprador

Catálogo → index.php

Login / registro

Detalle de producto

Añadir a carrito

Ver carrito

Comprar → PayPal

Flujo del vendedor

Login

Página vender.php

Crear producto

Editarlo en modificar.php

🌐 Dependencias Externas

Bootstrap 5.3.2 (CDN)

PayPal Website Payments Standard

⚠️ Consideraciones de Seguridad

El sistema contiene vulnerabilidades:

Contraseñas en texto plano

SQL Injection

Subidas sin validación

Sin CSRF tokens

Sesiones sin endurecimiento

Requiere mejoras para producción.

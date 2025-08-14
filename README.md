# Weather App

## 📦 Compilación
Para poder compilar la aplicación únicamente tendremos que meter en la raíz del proyecto el archivo `secret.properties`, con esto ya podríamos sincronizar el `build.gradle` sin que de error y poder compilar la app en nuestro dispositivo sin ningún problema.

Un posible error que puede dar al sincronizar el proyecto son problemas de la cache de Android Studio del ordenador que se debería de solucionar con la opción **"Invalidate Caches"** o cerrando el Android Studio y volviéndolo a abrir.

---

## 📱 Requisitos
- Dispositivo/emulador Android **8.0+**  
- SDK mínimo **26**

---

## 🏗 Arquitectura implementada: Clean architecture
Se separa en varias capas:

### **Data**
Donde se hace las llamadas al servicio web y se mapean los DTOs.

### **DI**
En este caso en los requisitos no se pedía inyección de dependencias y empecé a hacerlo sin usarlas pero hubiera sido una mejora bastante significativa para tener el código limpio.

### **Domain**
Tenemos las entidades, interfaces y usecases separadas de la red.

### **Core**
Tendríamos utilidades y tipado de errores.

### **Presentation**
Estaría la navegación, diferentes pantallas y viewModels.

---

## ⚙️ Decisiones técnicas
- Uno de los principales cambios ha sido la librería **LocationManager** en la que había métodos deprecados para API de +30 y he visto que era una mejora sustancial usar **LocationServices** que es la forma actual en la que se obtiene la ubicación en las aplicaciones.
- He implementado **enableEdgeToEdge** para que sea compatible las vistas tanto para SDK 35+ como para anteriores debido a que en el SDK 35 ahora es la aplicación la que maneja dar espacios en las systemBar.
- En la documentación de la API encontré que se podía añadir dos parámetros de metric para que viniesen los datos en la métrica europea de grados en vez de kelvins, y también para poder elegir el idioma a español dado que venía en inglés.
- He centralizado tanto estilos de texto como resources de string y colores.
- He sacado fuera de la aplicación en un archivo `secrets.properties` la clave de la API y metido la base de la URL para las llamadas web para no exponer la API a gente externa.

---

## 🚀 Puntos faltantes o posibles mejoras
- Me ha faltado hacer los test unitarios que eran opcionales, me he centrado más en dejar la aplicación y el código lo más limpio y centralizado posible.
- No he conseguido poner el sol de la brújula apuntando correctamente a los grados correspondientes.
- Aunque no se pedía en la prueba he echado en falta un botón de recarga o una animación de recarga ya que no me ha dado tiempo.
- Hubiera implementado con más tiempo Hilt o desde un principio me hubiera gustado.

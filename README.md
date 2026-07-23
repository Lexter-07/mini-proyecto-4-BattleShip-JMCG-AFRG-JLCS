# BattleShip

Una implementación del clásico juego **BattleShip (Batalla Naval)** desarrollada en **Java** utilizando **JavaFX** y **FXML** como parte del Mini Proyecto 4.

## Descripción

BattleShip es un juego de estrategia para dos jugadores donde cada participante debe ubicar su flota en un tablero y tratar de hundir todos los barcos enemigos antes de que el oponente haga lo mismo.

Este proyecto implementa una interfaz gráfica moderna con JavaFX, utilizando componentes FXML personalizados para representar los diferentes barcos y efectos visuales del juego.

---

## Características

-  Interfaz gráfica desarrollada con JavaFX.
-  Posicionamiento de barcos antes del inicio de la partida.
-  Sistema de disparos.
-  Efectos visuales para impactos.
-  Animaciones para disparos fallidos en el agua.
-  Barcos representados mediante archivos FXML independientes.
-  Interfaz inspirada en juegos modernos de BattleShip.
-  Arquitectura organizada siguiendo el patrón MVC.

---

## Tecnologías utilizadas

- Java 17
- JavaFX
- FXML
- CSS
- Maven
- IntelliJ IDEA

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/example/battleship/
|   |       ├──controller
|   |       ├──model
|   |       ├──view
│   ├── resources/
│   │   ├── FXML/
│   │   ├── css/
│   │   ├── images/
│   │   └── sounds/
└── pom.xml
```

---

## Cómo ejecutar

### Requisitos

- Java JDK 17 o superior
- Maven
- IntelliJ IDEA (recomendado)

### Clonar el repositorio

```bash
git clone https://github.com/Lexter-07/mini-proyecto-4-BattleShip-JMCG-AFRG-JLCS.git
```

### Ejecutar

Desde IntelliJ:

1. Abrir el proyecto.
2. Esperar a que Maven descargue las dependencias.
3. Ejecutar la clase:

```
Main.java
```

O mediante Maven:

```bash
mvn javafx:run
```

---

## Mecánica del juego

1. Cada jugador coloca su flota en el tablero.
2. Los jugadores se turnan para realizar disparos.
3. Un impacto genera un efecto visual de explosión.
4. Un disparo al agua genera una animación de salpicadura.
5. El juego termina cuando todos los barcos de un jugador han sido hundidos.

---

## Capturas

Puedes agregar aquí imágenes de la interfaz del juego.

```
docs/
    menu.png
    gameplay.png
    positioning.png
```

---

## Integrantes

- **Jose Manuel Cardona**
- **Andres Felipe Rodriguez**
- **Jorge Luis Castro Escarpeta**

---

## Proyecto académico

Mini Proyecto 4 – BattleShip

Tecnología en Desarrollo de Software

Universidad del Valle

---

## Licencia

Este proyecto fue desarrollado con fines académicos.
package com.tetris;

/**
 * Lanzador independiente para compatibilidad con empaquetado JAR
 * y ejecución sin requerir banderas modulares explícitas en el classpath.
 */
public class Launcher {
    public static void main(String[] args) {
        TetrisApp.main(args);
    }
}

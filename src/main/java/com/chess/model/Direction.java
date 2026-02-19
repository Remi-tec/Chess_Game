package com.chess.model;

public enum Direction {
    // Diagonales
    DIAGONAL(new int[][] {
            {1, 1},   // Haut-droite
            {1, -1},  // Haut-gauche
            {-1, 1},  // Bas-droite
            {-1, -1}  // Bas-gauche
    }),

    // Horizontales et verticales
    HORIZONTAL(new int[][] {
            {0, 1},   // Droite
            {0, -1},  // Gauche
            {1, 0},   // Haut
            {-1, 0}   // Bas
    }),

    // En L (mouvement du cavalier)
    L(new int[][] {
            {2, 1},   // 2 haut, 1 droite
            {2, -1},  // 2 haut, 1 gauche
            {-2, 1},  // 2 bas, 1 droite
            {-2, -1}, // 2 bas, 1 gauche
            {1, 2},   // 1 haut, 2 droite
            {1, -2},  // 1 haut, 2 gauche
            {-1, 2},  // 1 bas, 2 droite
            {-1, -2}  // 1 bas, 2 gauche
    }),

    WHITEPAWN(new int[][] {
            // Blancs
            {0, 2}, // First move double step
            {0, 1}, // Move forward
            {1, 1}, // Capture left
            {-1, 1}
    }),

    BLACKPAWN(new int[][] {
            // Noirs
            {0, -2}, // First move double step
            {0, -1}, // Move forward
            {-1, -1}, // Capture left
            {1, -1} // Capture right
    });



    private final int[][] directions;

    Direction(int[][] directions) {
        this.directions = directions;
    }

    public int[][] getDirections() {
        return directions;
    }
}


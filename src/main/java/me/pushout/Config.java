package me.pushout;

public class Config {
    public static double KB_MULTIPLIER = 16.0; // multiplicateur dans la formule (exemple dans KOManager)
    public static double KB_BASE = 0.5;         // valeur de base (exemple dans KOManager)
    public static boolean JUMP_BOOST_ENABLED = true;
    public static double HIT_PERCENT_GAIN = 2.3;
    public static double GRAPPLING_FORCE = 1.0;   // valeur utilisée dans propelPlayer
    public static double TP_RADIUS = 8.0;         // rayon de spawn dans GameManager.startGame()
    public static double MAP_MAX_RADIUS = 12;
    public static boolean OBJECTS_ENABLED = true;
}

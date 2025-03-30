package me.pushout;

public class Config {
    public static double KB_MULTIPLIER = 20.0; // multiplicateur dans la formule (exemple dans KOManager)
    public static double KB_BASE = 0.4;         // valeur de base (exemple dans KOManager)
    public static boolean JUMP_BOOST_ENABLED = true;
    public static double HIT_PERCENT_GAIN = 2.3;
    public static double GRAPPLING_FORCE = 0.8;   // valeur utilisée dans propelPlayer
    public static double TP_RADIUS = 8.0;         // rayon de spawn dans GameManager.startGame()
    public static double MAP_MAX_RADIUS = 26;
    public static boolean OBJECTS_ENABLED = true;
}

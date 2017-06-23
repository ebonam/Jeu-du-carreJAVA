import javafx.scene.paint.Color;
/**
 * Code donné dans le sujet
 */
public enum Etat {
	LIBRE(Color.GRAY), J1(Color.BLUE), J2(Color.RED);
		  Color couleur;
		  Etat(Color _couleur){couleur = _couleur;}
		  public Color getCouleur(){return couleur;}
		}
	


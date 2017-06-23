import javafx.scene.shape.Rectangle;

public class Carre extends Rectangle {
/**
 * @deprecated mais on ne sait jamais
 * @param h
 * @param G
 * @param B
 * @param D
 */
	public Carre(Ligne h, Ligne G, Ligne B, Ligne D) {
		super();
		Haut = h;
		Bas = B;
		Gauche = G;
		Droit = D;
	}
/** Ligne du carres **/	
	Ligne Haut, Bas, Gauche, Droit;
/**  Etat du carré*/
	Etat e;
/**
 * Constructeur
 * 
 * 
 */
	public Carre() {
		super();
		e=Etat.LIBRE;
	}
/**
 * fonction qui donne l'instance de l'objet aux lignes pour le pattern observer
 */
	public void lol() {
		Haut.r.add(this);
		Bas.r.add(this);
		Gauche.r.add(this);
		Droit.r.add(this);

	}
/**
 * Test si le carré est plein 
 * si c'est le cas peint le carré avec la couleur du joueur
 * @param e1 couleur du joueur 
 * @return si le carré a ete peint
 */
	public boolean Test(Etat e1) {
		if (Haut.et != Etat.LIBRE && Bas.et != Etat.LIBRE && Gauche.et != Etat.LIBRE && Etat.LIBRE != Droit.et) {
			e = e1;
			setX(Haut.getStartX());
			setY(Haut.getStartY());
			setWidth(Haut.getEndX() - Haut.getStartX());
			setHeight(100);
			setFill(e1.couleur);
			return true;
		}
		return false;
	}
	
	
		
/**
 * Pour l'ia donne le nombre de ligne non libre 
 * @return le nb de ligne deja occupé
 */
	public int CptLigne() {
		int cpt = 0;
		if (Haut.et == Etat.LIBRE)
			cpt++;
		if (Bas.et == Etat.LIBRE)
			cpt++;
		if (Droit.et == Etat.LIBRE)
			cpt++;
		if (Gauche.et == Etat.LIBRE)
			cpt++;
		return cpt;

	}

}

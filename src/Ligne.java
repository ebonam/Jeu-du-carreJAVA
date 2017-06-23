import java.util.ArrayList;
import javafx.scene.shape.Line;

/**
 * l'interface est clairement inutile juste pour respecter le patern observeur
 * 
 * @author antoine
 *
 */
public class Ligne extends Line implements Obs {

	/**
	 * Constructeur
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @param x
	 * @param y
	 */
	public Ligne(int i, int j, int k, int l, int x, int y, boolean b) {

		super(i, j, k, l);
		et = Etat.LIBRE;
		r = new ArrayList<>();
		x1 = x;
		y1 = y;
		bool = b;
	}
/** Donne la position x de la ligne*/
	int x1;
	/** Donne la position y de la ligne*/
	int y1;
	/** Dis si la ligne est verticale*/
	boolean bool;
	/** Donne l'etat de la ligne*/
	public Etat et;
	/**colection d'element observeur*/
	ArrayList<Carre> r;
	/**
	 * permet de modifier la couleur de la ligne,son etat, et notife ses carrés
	 * 
	 * @param e
	 * @return  dit si repeint
	 */
	public boolean repeindre(Etat e) {
		if (et == Etat.LIBRE) {
				setStroke(e.couleur);
				et = e;
				System.out.println(e);
			}
		return	notifieAll(e);
			
		}


	/** permet d'actualiser les carrés
	 * 
	 * @param j l'etat a potentiellement affecter
	 * @return si un carre au moins a ete colorié
	 */
	@Override
	public boolean notifieAll(Etat j) {
		boolean flag=false;
		for (Carre d : r) {
			if(d.Test(j))
			{flag=true;}
		}
	
		return flag;
	}

	/**
	 * 
	 * @return le nombre le possibilité possible pour l'ia
	 */
	public int notify2() {
		int max = 4;

		for (Carre d : r) {
			int tmp = d.CptLigne();
			if (tmp < max)
				max = tmp;
		}
		return max;
	}

}

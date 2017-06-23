import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AppliYote extends Application implements EventHandler<MouseEvent> {

	/** taille d'une case en pixels */
	int tailleCase;
	/** scene de jeu */
	Scene scene;
	/** Savoir si la partie est finie pour le run de la classe Threa*/
	boolean finish;
	/** Pour les socket ...*/
	ServerSocket socketserver;
	Socket socketduserveur;
	BufferedReader in;
	PrintWriter out;
	
	/** Si mode ia */
	boolean ia;
	/** Points pour un eventuel affichage des scores */
	int pointJ1, pointJ2;
	/** Collection qui regroupe toutes les lignes */
	ArrayList<Ligne> vert = new ArrayList<Ligne>();
	/** Collection qui regroupe toutes les lignes */
	ArrayList<Ligne> Hori = new ArrayList<Ligne>();
	/** Collection pour garder les carrés **/
	ArrayList<Carre> r;
	/** information sur le joueur de l'instance actuel */
	Etat JoueurPartie;// Static
	/** information sur le joueur du tour */
	Etat JoueurActuel;// savoir qui doit jouer

	/** lancement automatique de l'application graphique 
	 * Demande au joueur son mode de jeu puis prepare la partie Serveur socket,
	 * socket client, les bufferReader etc,...
	 * 
	 * @param primaryStage
	 */
	public void start(Stage primaryStage) {
		finish = true;
		tailleCase = 100;
		construirePlateauJeu(primaryStage);
		this.JoueurActuel = Etat.J1;

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Choissisez le mode de jeu ");
		alert.setHeaderText("Pour le multijoueur d'abord choissir Serveur + nouvelle instance avec Client");

		ButtonType buttonTypeOne = new ButtonType("ia");
		ButtonType buttonTypeTwo = new ButtonType("Serveur");
		ButtonType buttonTypeThree = new ButtonType("Client");

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

		Optional<ButtonType> result = alert.showAndWait();
		primaryStage.setOnCloseRequest(e -> this.quit());

		if (result.get() == buttonTypeOne) {
			this.JoueurPartie = Etat.J1;
			ia = true;
			/*
			 * Skynet skynet = new Skynet(); skynet.start()
			 */;
		} else if (result.get() == buttonTypeTwo) {
			this.JoueurPartie = Etat.J1;
			try {

				socketserver = new ServerSocket(2009);
				socketduserveur = socketserver.accept();
				out = new PrintWriter(socketduserveur.getOutputStream());
				in = new BufferedReader(new InputStreamReader(socketduserveur.getInputStream()));
				threa t = new threa();
				t.start();
			} catch (Exception e) {
				System.err.println("Server exception: " + e.toString());
				e.printStackTrace();
			}

		} else if (result.get() == buttonTypeThree) {
			this.JoueurPartie = Etat.J2;
			try {
				socketduserveur = new Socket(InetAddress.getLocalHost(), 2009);
				in = new BufferedReader(new InputStreamReader(socketduserveur.getInputStream()));
				out = new PrintWriter(socketduserveur.getOutputStream());

				threa t = new threa();
				t.start();
				// socketduserveur.connect();
			} catch (Exception e) {
				System.err.println("Client exception: " + e.toString());
				e.printStackTrace();
			}
		} else {
			System.exit(0);
		}

	}

	/** construction du théatre et de la scène */
	void construirePlateauJeu(Stage primaryStage) {
		Group troupe = new Group();
		scene = new Scene(troupe, tailleCase + 6 * tailleCase + 6 * tailleCase + tailleCase,
				tailleCase + 6 * tailleCase, Color.ANTIQUEWHITE);
		dessinEnvironnement(troupe);
		primaryStage.setTitle("Jeu du carré");
		primaryStage.setScene(scene);
		primaryStage.setWidth(750);
		primaryStage.setHeight(750);

		primaryStage.show();
	}
	/**
	 * Compte les points et fais un affichage si le jeu est terminé
	 * 
	 * @return true si le jeu est fini
	 */
	public boolean finJeu() {
		boolean b1 = true;
		pointJ1 = 0;
		pointJ2 = 0;

		for (Carre c : r) {

			if (c.e == Etat.LIBRE) {
				b1 = false;
			} else if (c.e == this.JoueurPartie)
				pointJ1++;
			else
				pointJ2++;

		}
		if (b1) {
			if (this.JoueurPartie == Etat.J1) {
				// finish=true;
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Choissisez le mode de jeu ");
				alert.setHeaderText("Pour le multijoueur d'abord choissir Serveur + nouvelle instance avec Client");
				finish = true;
				alert.setTitle("Fin du game");
				if (pointJ1 > pointJ2)
					alert.setHeaderText("Gagné");
				else
					alert.setHeaderText("Perdu");
				alert.setContentText("Vous avez " + pointJ1 + "points, l'adversaire a:" + pointJ2);
				alert.showAndWait();
				System.exit(0);
			}
		}
		return b1;

	}
	/**
	 * Cree les lignes et prepare les carré pour etre observable par les bonnes
	 * lignes
	 * 
	 * @param troupe
	 */
	void dessinEnvironnement(Group troupe) {
		int decalage = tailleCase / 2;
		// Creation des lignes verticales
		for (int j = 0; j < 6; j++) {
			for (int i = 0; i < 7; i++) {

				Ligne l = new Ligne(decalage + tailleCase * i, decalage + tailleCase * j, decalage + tailleCase * i,
						decalage + tailleCase * (j + 1), i, j, true);
				l.setStrokeWidth(6);
				l.setStroke(Color.GREEN);
				l.setOnMouseClicked(this);
				troupe.getChildren().add(l);

				vert.add(l);
			}
		}

		// Creation des lignes horizontales
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				Ligne l2 = new Ligne(decalage + (tailleCase * j), decalage + tailleCase * i,
						decalage + tailleCase * (j + 1), decalage + tailleCase * i, i, j, false);
				l2.setStrokeWidth(6);

				l2.setStroke(Color.GREEN);
				l2.setOnMouseClicked(this);

				troupe.getChildren().add(l2);
				Hori.add(l2);
			}
		}

		r = new ArrayList<>(); // container des carré

		for (int i = 0; i < 6 * 6; i++) {
			r.add(new Carre());
		}
		int ctr = 0;
		ArrayList<Ligne> ver2 = new ArrayList<Ligne>();
		ver2.addAll(vert);

		while (ctr != 36) { // assigner les lignes verticales aux carrés
			// #bonam seal of quality approved
			if (ctr != 0 && ctr % 6 == 0)
				ver2.remove(0);
			r.get(ctr).Gauche = ver2.get(0);
			r.get(ctr).Droit = ver2.get(1);
			ctr++;
			ver2.remove(0);
		}
		for (int i = 0; i < 6 * 6; i++) { // assigne les lignes horizontales aux
											// carré et prepare les observeurs
			r.get(i).Haut = Hori.get(i);
			r.get(i).Bas = Hori.get(i + 6);
			r.get(i).lol();
			troupe.getChildren().add(r.get(i));

		}
	}

	/**
	 * @deprecated je ne pense pas quelle ai une utilité
	 * @param event
	 */
	public void handle(WindowEvent event) {
		Platform.exit();
		System.exit(0);
	}

	/**
	 * @deprecated Je ne pense pas quelle ai une utilité
	 */
	public void quit() {
		Platform.exit();
		System.exit(0);
	}

	/**
	 * Permet a l'ia de jouer Tri lineaire des possibilitées, Ferme les carré,
	 * regard recursif(fermera les carré des carré precedants) Ajout d'aleatoire
	 * pour etre moins previsible
	 * 
	 * <br>
	 * <h1>exception handled, si l'ia finit la partie, la collection est vide,
	 * impossible de get</h1>
	 */
	public void IaJoue() {
		ArrayList<Ligne> Total = null;
		ArrayList<Ligne> coup0 = null;
		ArrayList<Ligne> coup1 = null;
		ArrayList<Ligne> coup2 = null;
		boolean changed = true;
		while (changed) {
			changed = false;
			Total = new ArrayList<>();
			coup0 = new ArrayList<>();
			coup1 = new ArrayList<>();
			coup2 = new ArrayList<>();
			for (Ligne l : vert) {
				if (l.et == Etat.LIBRE) {
					switch (l.notify2()) {
					case 4:
						coup0.add(l);
						break;
					case 3:
						coup1.add(l);
						break;
					case 2:
						coup2.add(l);
						break;
					case 1:
						JoueCoup(l.x1, l.y1, l.bool);
						changed = true;
						break;
					}
				}
			}
			for (Ligne l : Hori) {
				if (l.et == Etat.LIBRE)
					switch (l.notify2()) {
					case 4:
						coup0.add(l);
						break;
					case 3:
						coup1.add(l);
						break;
					case 2:
						coup2.add(l);
						break;
					case 1:
						JoueCoup(l.x1, l.y1, l.bool);
						changed = true;
						break;
					}
			}

		}
		int i = 0;
		/**
		 * Pour rendre l'ia moins previsible
		 */
		if (coup1.size() != 0 && coup0.size() != 0) {
			if (Math.random() > 0.80)
				i = (int) (Math.random() * coup1.size()) + coup0.size();
			else
				i = (int) (Math.random() * coup0.size());
		} else if (coup0.size() != 0) {
			i = (int) (Math.random() * coup0.size());
		} else if (coup1.size() != 0) {
			i = (int) (Math.random() * coup1.size());
		} else
			i = (int) (Math.random() * coup2.size());
		Total.addAll(coup0);
		Total.addAll(coup1);
		Total.addAll(coup2);
		try {
			Ligne l = Total.get(i);
			JoueCoup(l.x1, l.y1, l.bool);
			switchPlayer();
			Thread.sleep(100);
		} catch (Exception e) {// si l'ia fini le jeu}
			finJeu();
		}
	}
	/**
	 * fonction pour le joueur distant,ou l'ia le faire jouer un coup remplace
	 * la simulation du clic souris
	 * 
	 * @param x
	 *            le x de la ligne
	 * @param y
	 *            le y de la ligne
	 * @param b
	 *            si la ligne est vertical ou pas
	 */
	public boolean JoueCoup(int x, int y, boolean b) {
		if (b) {// case Verticale
			for (Ligne l : vert) {
				if (l.x1 == x && l.y1 == y) {
					return l.repeindre(JoueurActuel);
				}
			}
		} else {
			for (Ligne l : Hori) {
				if (l.x1 == x && l.y1 == y) {
					return l.repeindre(JoueurActuel);
				}
			}
		}
System.exit(-1);// dead code pour etre sur
		return false;// Dead code pour compiler
	}
	/** reponse aux evenements de souris */
	public void handle(MouseEvent me) {
		Object o = me.getSource();
		if (o instanceof Ligne) {
			Ligne d = (Ligne) o;
			if (d.et == Etat.LIBRE && JoueurPartie == JoueurActuel) {
				if (!ia)
					this.sendMessage(d);
				if (!d.repeindre(JoueurActuel)) {
					finJeu();
					switchPlayer();
					if (ia) {
						IaJoue();
					 }
				}
				finJeu();
			}
		}
	}
	/**
	 * Change le joueur actuel
	 */
	private void switchPlayer() {
		if (this.JoueurActuel == Etat.J1) {
			JoueurActuel = Etat.J2;
		} else {
			JoueurActuel = Etat.J1;
		}
	}
	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	/**
	 * Envoi la ligne l par le buffer reader
	 * 
	 * @param l
	 *            ligne a envoyé
	 */
	public void sendMessage(Ligne l) {
		out.println(l.x1);
		out.flush();
		out.println(l.y1);
		out.flush();
		out.println(l.bool);
		out.flush();
	}
	/**
	 * Cette classe devait gerer l'ia, mais en cas de fin de partie, ne peut
	 * afficher le pop up de fin (pas le thread principal) mais tout de même
	 * fonctionnel.
	 * 
	 * @author antoine
	 *
	 */
	class Skynet extends Thread {

		@Override
		public void run() {
			finish = false;

			while (!finish) {

				if (!finish && JoueurActuel == Etat.J2) {
					IaJoue();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	/**
	 * 
	 * Classe qui permet d'ecouter le joueur distant via un thread
	 *
	 */
	class threa extends Thread {

		/**
		 * Lis la ligne l pour faire joueur le joueur distant
		 */
		@Override
		public void run() {
			while (!finish) {

				try {
					int x = Integer.parseInt(in.readLine());
					int y = Integer.parseInt(in.readLine());
					boolean bo = Boolean.parseBoolean(in.readLine());
					if (!JoueCoup(x, y, bo))
						switchPlayer();
					finJeu();
				} catch (NumberFormatException e) {
					// ne peut se produire
				
				} catch (IOException e) {
					// se produit si l'un des client ou serveur est deconnecté
					System.exit(0);
				}
			}
		}
	}
}

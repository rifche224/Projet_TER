/**
* @author Alhadj adam Mamadou && Cherif Sy
* Programme pour établir une connection client/serveur, 
*/

import java.io.*;
import java.net.*;

public class Serveur {

	private ServerSocket serveur = null;
	private Socket client = null;
	private BufferedReader in;
	private PrintWriter out;

	/** Contructeur qui specifie le port écoute, qui doit être au dessus de 1024
	  * @param port le numero du port
	  */
	public Serveur(int port) {
		if (port < 1023) {// ports reserves
			System.err.println("erreur de choix du port ! 5555 par defaut");
			port =5555;
		}
		try{
			// on crée un serveur sur le port specifié
			serveur = new ServerSocket(port);
		}
		catch(IOException e){
			System.err.println("Impossible d'écouter le port "+port);
            System.exit(1);
        }
		System.out.println("J'ecoute sur le port: "+port+"...");
		try{
			serveur.setSoTimeout(100000); // attendre au max 10s
			// s'il y a une requête sur le port
			// on crée un Socket pour communiquer avec le client
			// On attend jusqu'a ce qu'il y ait une requête
			client = serveur.accept(); //"client" est le Socket
		}
		catch (SocketTimeoutException e){
			System.err.println("On quitte : TimeOut");
			System.exit(1);
		}
		catch (IOException e) {
				System.err.println("Client refusé !.");
				System.exit(1);
        }
		System.out.println("client accepté !");
		try {
			// on recupère les canaux de communication avec des filtres de lecture ecriture de données
			in = new BufferedReader(new InputStreamReader(client.getInputStream() ) );
			out = new PrintWriter(client.getOutputStream() );
		}
		catch (IOException e) {
            	System.err.println("Erreur lors de création des streams");
           		System.exit(1);
        }
		if (in == null)
			System.out.println("pas d'entrée !!!");
		if (out == null)
			System.out.println("pas de sortie !!!");
	}

	/** lit les caractères envoyés par le client.
		* @return un objet String qui contient l'ensemble des caractères lus
		*/
	public String lireClient(){
		String ligne=null;
		try{
		 	ligne =in.readLine();
		}
		catch (IOException e) {
            System.err.println("rien a lire");
		}
		return ligne;
	}

	/** Envoie des données au client.
		* @param ligne les caractères à envoyer
		*/
	public void ecrireClient(String ligne){
		if (out == null)
			System.out.println("pas de sortie !!! : ecrire ?");
		else{
			out.println(ligne);
			out.flush();
		}
	}
	
	/** teste la connexion.
		*@return un booléen notifiant l'état de la connexion
		*/
	public boolean clientOK(){
		return client.isConnected();
	}

	/** Fermeture du socket.
		*/
	public void fermer(){
	// il faut fermer "proprement" les streams avant les Sockets
		try{
			in.close();
			out.close();
			if (client != null)
				client.close();
			if (serveur != null)
				serveur.close();
			System.out.println ("Au revoir tout est Fermé!");
		}
		catch(IOException e){
			System.err.println("Erreur à la fermeture des flux !");
		}
	}

	protected void finalize(){
		// méthode executée par le ramasse miettes avant de liberer la mémoire
		// pb : on ne sait jamais trop quand !!!
		fermer();
	}

	

	public static void main(String[] args){
		// Pour tester un protocole simple de communication
		// Bien entendu il faut que serveur et client soient compatibles

		// initialisation du serveur
			Serveur  srv;
			if (args.length >0)
				srv = new Serveur(Integer.parseInt(args[0]));
			else
				srv =new Serveur(0);
			System.out.println("Client bla bla");

		// Envoie un message d'accueil
			srv.ecrireClient("Bienvenue sur le Serveur - Pour quitter : taper \"fin\" ");
		
		// Ecoute du client
			boolean continuer =true;
			String ligne;
			while(continuer && srv.clientOK()) {
				ligne = srv.lireClient();
				if (ligne.equalsIgnoreCase("fin")) {//peu importe la casse
					continuer = false;
					srv.ecrireClient("Au revoir");
				}
				System.out.println("Client : "+ligne);
				// envoies au client la taille demander le code-----
				srv.ecrireClient("Bien recu votre requête!");
			}
			System.out.println("On termine");
			srv.fermer();
	}//Fin du main

}

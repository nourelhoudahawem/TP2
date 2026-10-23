package clientPackage;

import common.Operation;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    private static final Pattern OPERATION = Pattern.compile(
            "^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$");

    public static void main(String[] args) {
        String hote = "localhost";
        int port = 1234;
        try (Socket socket = new Socket(hote, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connecté au serveur.");
            while (true) {
                System.out.print("Entrez une opération (ex. 55 * 25) ou FIN pour quitter : ");
                String ligne = sc.nextLine();

                if (ligne.trim().equalsIgnoreCase("FIN")) {
                    out.writeObject("FIN");
                    out.flush();
                    break;
                }

                Matcher m = OPERATION.matcher(ligne);
                if (!m.matches()) {
                    System.out.println("Syntaxe incorrecte. Format attendu : <nombre> <+|-|*|/> <nombre>");
                    continue;
                }

                Operation op = new Operation(
                        Double.parseDouble(m.group(1)),
                        m.group(2).charAt(0),
                        Double.parseDouble(m.group(3)));

                out.writeObject(op);
                out.flush();

                String resultat = (String) in.readObject(); // lecture du résultat
                System.out.println("Résultat : " + ligne.trim() + " = " + resultat);
            }
            System.out.println("Communication terminée. Fermeture du socket.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}
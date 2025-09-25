package com.example;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Named
@SessionScoped
public class CalculBean implements Serializable {

    private double a;
    private double b;
    private double c;
    private String resultat;
    private List<Operation> historique = new ArrayList<>();

    // Getters et setters
    public double getA() { return a; }
    public void setA(double a) { this.a = a; }
    public double getB() { return b; }
    public void setB(double b) { this.b = b; }
    public double getC() { return c; }
    public void setC(double c) { this.c = c; }
    public String getResultat() { return resultat; }
    public List<Operation> getHistorique() { return historique; }

    // Réinitialiser seulement les champs de saisie et le résultat
    public void effacer() {
    a = 0;
    b = 0;
    c = 0;
    resultat = "";
    // historique.clear();  <-- on ne touche plus à l'historique
}

    // Appel de l'API pour effectuer le calcul
    public void calculer(String op) {
        try {
            String urlStr = "http://backend:8080/calcul/" + op + "?a=" + a + "&b=" + b;
            if(op.equals("eq1") || op.equals("eq2")) {
                urlStr += "&c=" + c;
            }

            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            Scanner sc = new Scanner(con.getInputStream());
            if(sc.hasNextLine()) {
                resultat = sc.nextLine();  // On lit le résultat simple
            }
            sc.close();

            // Ajouter à l'historique
            String inputStr = "a=" + a + ", b=" + b + (op.equals("eq1")||op.equals("eq2") ? ", c=" + c : "");
            historique.add(new Operation(op, inputStr, resultat));

        } catch (Exception e) {
            resultat = "Erreur : " + e.getMessage();
        }
    }

    // Classe interne pour stocker l'historique
    public static class Operation {
        private String type;
        private String input;
        private String result;
        private String createdAt;

        public Operation(String type, String input, String result) {
            this.type = type;
            this.input = input;
            this.result = result;
            this.createdAt = java.time.LocalDateTime.now().toString();
        }

        public String getType() { return type; }
        public String getInput() { return input; }
        public String getResult() { return result; }
        public String getCreatedAt() { return createdAt; }
    }
}
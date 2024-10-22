package agent;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentGuide extends Agent {
    private int currentTableau = 0;
    private int totalTableaux = 5;  // Nombre de tableaux dans l'exposition
    private List<String> tourists = new ArrayList<>(); // Liste des touristes pris en charge
    private Map<Integer, String> exhibition = new HashMap<>(); // Tableau avec descriptions

    @Override
    protected void setup() {
        System.out.println("Agent-guide " + getLocalName() + " est prêt.");

        exhibition.put(1, "Tableau 1: La Joconde de Léonard de Vinci.");
        exhibition.put(2, "Tableau 2: La Nuit étoilée de Vincent van Gogh.");
        exhibition.put(3, "Tableau 3: Le Cri d'Edvard Munch.");
        exhibition.put(4, "Tableau 4: La Jeune Fille à la perle de Vermeer.");
        exhibition.put(5, "Tableau 5: Guernica de Pablo Picasso.");

        addBehaviour(new GuideBehaviour());
        addBehaviour(new GuideCommunicationBehaviour());
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent-guide " + getLocalName() + " termine.");
    }


    private class GuideBehaviour extends Behaviour {
        @Override
        public void action() {
            // Si tous les tableaux sont vus, terminer la visite
            if (currentTableau >= totalTableaux) {
                System.out.println("Visite terminée.");
                myAgent.doDelete(); // Terminer l'agent à la fin de la visite
            } else {
                // Fournir des explications pour le tableau courant
                System.out.println("Agent-guide " + getLocalName() + ": Fournir des explications pour le tableau " + (currentTableau + 1));
                System.out.println(exhibition.get(currentTableau + 1));

                // Simuler un délai avant de passer au tableau suivant (par exemple, 3 secondes)
                block(3000);
                currentTableau++;
            }
        }

        @Override
        public boolean done() {
            return currentTableau >= totalTableaux;
        }
    }

    private class GuideCommunicationBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                System.out.println("Agent-guide " + getLocalName() + ": Message reçu de " + msg.getSender().getLocalName() + ": " + msg.getContent());

                ACLMessage reply = msg.createReply();
                reply.setContent("Voici plus d'informations sur l'exposition.");
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }


}

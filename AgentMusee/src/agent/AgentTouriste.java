package agent;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentTouriste extends AgentGuide{
    @Override
    protected void setup() {
        System.out.println("Touriste " + getLocalName() + " est prêt.");

        addBehaviour(new AskGuideBehaviour());
    }

    @Override
    protected void takeDown() {
        System.out.println("Touriste " + getLocalName() + " quitte le musée.");
    }

    private class AskGuideBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent("Pouvez-vous m'en dire plus sur ce tableau ?");
            send(msg);
        }
    }
}

package agent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentTouriste extends AgentGuide{

    private AID[] guideAgents;
    private Boolean hasChosen = false;

    protected void  setup() {
        System.out.println("je suis l'agent touriste " + this.getLocalName());

        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("guide");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.println("Found the following guide agents:");
                    guideAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        guideAgents[i] = result[i].getName();
                        System.out.println(guideAgents[i].getName());
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }

                // Perform the request
                myAgent.addBehaviour(new RequestPerformer());
            }
        });
    }

    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Tourist-agent "+getAID().getName()+" terminating.");
    }

    private class RequestPerformer extends Behaviour {
        private AID bestGuide;
        private int bestPrice;
        private int repliesCnt = 0;
        private MessageTemplate mt;
        private int step = 0;

        @Override
        public void action() {
            System.out.println(guideAgents.length);
            System.out.println("step is :" + step);
            if(hasChosen) {
                System.out.println("guide chose");
                doDelete();
            } else {
                System.out.println("guide not chosen");
            }
            switch (step) {
                case 0:
                    // Send the cfp to all guides
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (AID guideAgent : guideAgents) {
                        cfp.addReceiver(guideAgent);
                    }
                    cfp.setContent("5");
                    cfp.setConversationId("group-choice");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("group-choice"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    System.out.println("???");
                    step = 1;
                    break;
                case 1:
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    System.out.println("reply is : " + reply);
                    if (reply != null) {
                        // Reply received
                        System.out.println("reply is : " + reply);
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
                            int price = Integer.parseInt(reply.getContent());
                            if (bestGuide == null || price < bestPrice) {
                                // This is the best offer at present
                                bestPrice = price;
                                bestGuide = reply.getSender();

                            }
                        }
                        repliesCnt++;
                        if (repliesCnt >= guideAgents.length) {
                            // We received all replies
                            hasChosen = true;
                            step++;
                        }
                    }
                    else {
                        block();
                    }
                    break;
            }

            }

        @Override
        public boolean done() {
            if(step == 2) {
                System.out.println("Best guide for tourist " + myAgent.getLocalName() + " is " + bestGuide.getLocalName() + " with price " + bestPrice);
            }
            return (step == 2);
        }
    }
}

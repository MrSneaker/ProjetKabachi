import agent.AgentGuide;
import agent.AgentTouriste;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class MainContainer {
    public static void main(String[] args) {
        // Initialisation de la plateforme JADE
        Properties properties = new Properties();
        properties.setProperty(Profile.GUI, "true");  // Pour afficher l'interface graphique de JADE
        Profile profile = new ProfileImpl(properties);
        profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
        profile.setParameter(Profile.MAIN_HOST, "localhost");

        // Création du Main Container
        Runtime rt = Runtime.instance();
        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            // Création et démarrage de l'agent-guide
            AgentController guideAgent = mainContainer.createNewAgent("guide", AgentGuide.class.getName(), null);
            guideAgent.start();

            // Création et démarrage des agents-touristes
            AgentController tourist1 = mainContainer.createNewAgent("tourist1", AgentTouriste.class.getName(), null);
            AgentController tourist2 = mainContainer.createNewAgent("tourist2", AgentTouriste.class.getName(), null);

            tourist1.start();
            tourist2.start();

            String[] agentsToMonitor = {"guide", "tourist1", "tourist2"};
            AgentController snifferAgent = mainContainer.createNewAgent("sniffer", "jade.tools.sniffer.Sniffer", agentsToMonitor);
            snifferAgent.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
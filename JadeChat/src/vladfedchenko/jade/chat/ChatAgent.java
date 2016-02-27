package vladfedchenko.jade.chat;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

/**
 * Created by vladfedchenko on 22.02.16.
 */
public class ChatAgent extends GuiAgent {

    private ChatAgentGui myGui;

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
    }

    @Override
    protected void setup() {
        super.setup();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat-reciever");
        sd.setName("resieve-message");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        this.myGui = new ChatAgentGui(this);
        this.myGui.setVisible(true);

        this.addBehaviour(new RecieveMessageBehaviour());

        this.sendMessageToChat("Joined the chat!");
    }

    @Override
    protected void takeDown() {
        this.sendMessageToChat("Left the chat!");
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        if (this.myGui != null)
        {
            this.myGui.dispose();
            this.myGui = null;
        }


        super.takeDown();
    }

    public void sendMessageToChat(String message)
    {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat-reciever");
        sd.setName("resieve-message");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(message);
            for (int i = 0; i < result.length; ++i) {
                if (!result[i].getName().equals(this.getAID()))
                {
                    msg.addReceiver(result[i].getName());
                }
            }
            send(msg);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public ChatAgentGui getGui()
    {
        return this.myGui;
    }

    private class RecieveMessageBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {

                ChatAgent agent = (ChatAgent)this.getAgent();
                if(null != agent)
                {
                    agent.getGui().addMessageToTextArea(msg.getSender().getName() + ": " + msg.getContent());
                }
            }
            block();
        }
    } // End of inner class OfferRequestsServer
}

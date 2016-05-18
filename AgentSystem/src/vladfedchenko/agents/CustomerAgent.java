package vladfedchenko.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import vladfedchenko.agentsgui.CustomerGui;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vladfedchenko on 4/19/16.
 */
public class CustomerAgent extends GuiAgent{

    private double money;

    private CustomerGui myGui;

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void setup() {
        super.setup();

        this.money = 3000.0;

        this.myGui = new CustomerGui(this);

        this.myGui.setVisible(true);
        this.repaint();

        this.addBehaviour(new CustomerDelayBehaviour(this, 2500));

    }

    @Override
    protected void takeDown() {

        super.takeDown();
    }

    public double getMoney() { return this.money; }

    public void bankrupt()
    {
        this.doDelete();
    }

    public void repaint()
    {
        this.myGui.repaint();
    }

    private class CustomerDelayBehaviour extends TickerBehaviour
    {
        private final MessageTemplate msgTemplate;
        private final MessageTemplate buyTemplate;
        private int ticks = 1;

        public CustomerDelayBehaviour(CustomerAgent ag, long period)
        {
            super(ag, period);
            this.buyTemplate = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
            this.msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        }

        @Override
        protected void onTick()
        {
            //Logger.getGlobal().log(Level.INFO, "Customer tick");
            if (this.ticks % 5 == 0)
            {
                this.getRealAgent().money += 3000.0;
            }
            this.ticks++;

            int toBuyMeat = this.getCount();
            this.tryBuyMeat(toBuyMeat);

            if (this.getRealAgent().money < 0)
            {
                this.getRealAgent().bankrupt();
            }

            int toBuyWheat = this.getCount();
            this.tryBuyWheat(toBuyWheat);

            if (this.getRealAgent().money < 0)
            {
                this.getRealAgent().bankrupt();
            }

            this.getRealAgent().repaint();
        }

        private int tryBuyMeat(int toBuyMeat) {
            int startValue = toBuyMeat;
            while (startValue > 0)
            {
                //Logger.getGlobal().log(Level.INFO, "Customer buy meat :" + startValue);

                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("meatMarketProvider");
                sd.setName("provideMeat");
                template.addServices(sd);

                try
                {
                    DFAgentDescription[] result = DFService.search(this.getRealAgent(), template);
                    AID bestSeller = null;
                    int bestCount = -1;
                    double lastBestPrice = Double.MAX_VALUE;

                    for (DFAgentDescription agentDesc : result)
                    {
                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("getMeatInfo");
                        msg.addReceiver(agentDesc.getName());
                        this.getRealAgent().send(msg);

                        ACLMessage reply = this.getRealAgent().receive(MessageTemplate.and(this.msgTemplate, MessageTemplate.MatchSender(agentDesc.getName())));
                        while(reply == null) {
                            //block();
                            reply = this.getRealAgent().receive(MessageTemplate.and(this.msgTemplate, MessageTemplate.MatchSender(agentDesc.getName())));
                        }

                        if (reply != null) {
                            double price = Double.parseDouble(reply.getUserDefinedParameter("price"));
                            int count = Integer.parseInt(reply.getUserDefinedParameter("count"));
                            if (count > 0 && price < lastBestPrice) {
                                bestSeller = reply.getSender();
                                bestCount = count;
                                lastBestPrice = price;
                            }
                        }
                    }
                    if (bestSeller != null)
                    {
                        int toBuy = startValue >= bestCount ? bestCount : startValue;

                        //Logger.getGlobal().log(Level.INFO, "Customer best meat provider found: " + bestSeller.getName());
                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("buyMeat");
                        msg.addUserDefinedParameter("count", toBuy + "");
                        msg.addReceiver(bestSeller);
                        this.getRealAgent().send(msg);

                        ACLMessage reply = null;
                        while(reply == null) {
                            //block();
                            //Logger.getGlobal().log(Level.INFO, "Waiting for message from: " + bestSeller.getName());
                            reply = this.getRealAgent().receive(MessageTemplate.and(this.buyTemplate, MessageTemplate.MatchSender(bestSeller)));
                        }

                        if (reply != null) {
                            int realCount = Integer.parseInt(reply.getUserDefinedParameter("count"));
                            //Logger.getGlobal().log(Level.INFO, "Message recieved: " + realCount);
                            startValue -= realCount;
                            this.getRealAgent().money -= realCount * lastBestPrice;
                        }
                    }
                    else
                    {
                        break;
                    }

                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }

            return toBuyMeat - startValue;
        }

        private int tryBuyWheat(int toBuyWheat) {
            int startValue = toBuyWheat;
            while (startValue > 0)
            {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("wheatMarketProvider");
                sd.setName("provideWheat");
                template.addServices(sd);

                try
                {
                    DFAgentDescription[] result = DFService.search(this.getRealAgent(), template);
                    AID bestSeller = null;
                    int bestCount = -1;
                    double lastBestPrice = Double.MAX_VALUE;

                    for (DFAgentDescription agentDesc : result)
                    {
                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("getFirstClassInfo");
                        msg.addReceiver(agentDesc.getName());
                        this.getRealAgent().send(msg);
                        //block();
                        ACLMessage reply = this.getRealAgent().receive(MessageTemplate.and(this.msgTemplate, MessageTemplate.MatchSender(agentDesc.getName())));
                        while(reply == null) {
                            //block();
                            reply = this.getRealAgent().receive(MessageTemplate.and(this.msgTemplate, MessageTemplate.MatchSender(agentDesc.getName())));
                        }

                        if (reply != null) {
                            double price = Double.parseDouble(reply.getUserDefinedParameter("price"));
                            int count = Integer.parseInt(reply.getUserDefinedParameter("count"));
                            if (count > 0 && price < lastBestPrice) {
                                bestSeller = reply.getSender();
                                bestCount = count;
                                lastBestPrice = price;
                            }
                        }
                    }
                    if (bestSeller != null)
                    {
                        int toBuy = startValue >= bestCount ? bestCount : startValue;

                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("buyFirstClass");
                        msg.addUserDefinedParameter("count", toBuy + "");
                        msg.addReceiver(bestSeller);
                        this.getRealAgent().send(msg);

                        ACLMessage reply = null;
                        while(reply == null) {
                            //block();
                            reply = this.getRealAgent().receive(MessageTemplate.and(this.buyTemplate, MessageTemplate.MatchSender(bestSeller)));
                        }

                        if (reply != null) {
                            int realCount = Integer.parseInt(reply.getUserDefinedParameter("count"));
                            startValue -= realCount;
                            this.getRealAgent().money -= realCount * lastBestPrice;
                        }
                    }
                    else
                    {
                        break;
                    }

                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }

            return toBuyWheat - startValue;
        }

        private int getCount()
        {
            Random rand = new Random();
            return rand.nextInt(3) + 2;
        }

        private CustomerAgent getRealAgent()
        {
            return (CustomerAgent)this.getAgent();
        }
    }

}

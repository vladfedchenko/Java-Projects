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
import vladfedchenko.agentsgui.FarmerGui;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vladfedchenko on 4/17/16.
 */
public class FarmerAgent extends GuiAgent {

    private double money;

    private int meat;
    private double meatPrice;
    private int seasonStartMeat;

    private FarmerGui myGui;

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void setup() {
        super.setup();

        this.money = 15000.0;
        this.meat = 400;
        this.meatPrice = 45.0;
        this.seasonStartMeat = this.meat;

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("meatProvider");
        sd.setName("provideMeat");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        this.myGui = new FarmerGui(this);

        this.myGui.setVisible(true);
        this.repaint();

        this.addBehaviour(new FarmerParallelBehaviour(this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        super.takeDown();
    }

    public double getMoney() { return this.money; }

    public int getMeatCount() { return this.meat; }

    public double getMeatPrice() { return this.meatPrice; }

    public int getSeasonStartMeat() { return seasonStartMeat; }

    public int sellMeat(int count)
    {
        int realCount = this.meat >= count ? count : this.meat;
        double priceRiseCoof = 1.0 + realCount * 0.1 / 340.0;
        this.meat -= realCount;
        this.money += this.meatPrice * realCount;
        this.meatPrice *= priceRiseCoof;
        return realCount;
    }

    public void bankrupt()
    {
        this.doDelete();
    }

    public void repaint()
    {
        this.myGui.repaint();
    }

    private class FarmerParallelBehaviour extends ParallelBehaviour {

        public FarmerParallelBehaviour(FarmerAgent agent)
        {
            super(agent, ParallelBehaviour.WHEN_ALL);

            this.addSubBehaviour(new MeatDelayBehaviour(agent, 25000));
            this.addSubBehaviour(new RecieveOrderBehaviour(agent));
        }
    }

    private class RecieveOrderBehaviour extends CyclicBehaviour {

        private final MessageTemplate template;

        public RecieveOrderBehaviour(FarmerAgent agent)
        {
            super(agent);

            this.template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(this.template);
            if (msg != null) {
                if (msg.getContent().equals("getMeatInfo"))
                {
                    this.replyGetMeat(msg);
                } else if (msg.getContent().equals("buyMeat")) {
                    this.sellMeat(msg);
                }
                else
                {
                    System.out.println("Farmer does not support this operation! " + msg.getContent() + ", send by: " + msg.getSender());
                    //throw new UnsupportedOperationException("Farmer does not support this operation! " + msg.getContent());
                }
            }
            //block();
        }

        private void replyGetMeat(ACLMessage msg)
        {
            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());

            FarmerAgent agent = (FarmerAgent)this.getAgent();
            reply.addUserDefinedParameter("count", agent.getMeatCount() + "");
            reply.addUserDefinedParameter("price", agent.getMeatPrice() + "");

            send(reply);
        }

        private void sellMeat(ACLMessage msg)
        {
            int count = Integer.parseInt(msg.getUserDefinedParameter("count"));
            FarmerAgent agent = (FarmerAgent)this.getAgent();
            int realCount = agent.sellMeat(count);
            ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
            reply.addReceiver(msg.getSender());
            reply.addUserDefinedParameter("count", realCount + "");
            agent.send(reply);
            agent.repaint();
        }
    }

    private class MeatDelayBehaviour extends TickerBehaviour
    {
        private final MessageTemplate msgTemplate;
        private final MessageTemplate buyTemplate;

        public MeatDelayBehaviour(FarmerAgent ag, long period)
        {
            super(ag, period);

            this.msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            this.buyTemplate = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
        }

        @Override
        protected void onTick()
        {
            //Logger.getGlobal().log(Level.INFO, "Farmer tick");
            this.getRealAgent().money -= this.getRealAgent().meat * 3;

            int needMeat = (int)((this.getRealAgent().seasonStartMeat - this.getRealAgent().meat) * 1.3);
            int toProduceMeat = needMeat - this.getRealAgent().meat;

            if (toProduceMeat > 0)
            {
                this.tryProduceMeat(toProduceMeat);
                this.getRealAgent().seasonStartMeat = this.getRealAgent().meat;
            }
            else
            {
                this.getRealAgent().seasonStartMeat = this.getRealAgent().meat;
            }

            if (seasonStartMeat == 0)
            {
                this.tryProduceMeat(100);
                this.getRealAgent().seasonStartMeat = this.getRealAgent().meat;
            }

            this.getRealAgent().meatPrice = 45.0;

            if (this.getRealAgent().money < 0)
            {
                this.getRealAgent().bankrupt();
            }

            this.getRealAgent().repaint();
        }

        private int tryProduceMeat(int toProduceMeat) {
            int startValue = toProduceMeat;
            while (startValue > 0)
            {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("wheatProvider");
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
                        msg.setContent("getSecondClassInfo");
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
                        int toBuy = startValue * 2 >= bestCount ? bestCount : startValue * 2;


                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("buySecondClass");
                        msg.addUserDefinedParameter("count", toBuy + "");
                        msg.addReceiver(bestSeller);
                        this.getRealAgent().send(msg);

                        ACLMessage reply = this.getRealAgent().receive(MessageTemplate.and(this.buyTemplate, MessageTemplate.MatchSender(bestSeller)));
                        while(reply == null) {
                            //block();
                            reply = this.getRealAgent().receive(MessageTemplate.and(this.buyTemplate, MessageTemplate.MatchSender(bestSeller)));
                        }

                        if (reply != null) {
                            int realCount = Integer.parseInt(reply.getUserDefinedParameter("count"));
                            startValue -= realCount / 2;
                            this.getRealAgent().money -= realCount * (lastBestPrice + 5);
                            this.getRealAgent().meat += (int)(realCount / 2 * this.getProductivity());
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

            return toProduceMeat - startValue;
        }

        private double getProductivity()
        {
            Random rand = new Random();
            return (rand.nextDouble() * 10.0 - 5.0) / 100.0 + 1.0;
        }

        private FarmerAgent getRealAgent()
        {
            return (FarmerAgent)this.getAgent();
        }
    }
}

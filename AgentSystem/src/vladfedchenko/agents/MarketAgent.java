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
import vladfedchenko.agentsgui.MarketGui;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vladfedchenko on 4/19/16.
 */
public class MarketAgent extends GuiAgent {

    private double money;

    private int meat;
    private double meatPrice;
    private int seasonStartMeat;

    private int firstClass;
    private double firstClassPrice;
    private int lastSeasonNeedFirst;
    private MarketGui myGui;

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void setup() {
        super.setup();

        this.firstClass = 100;
        this.firstClassPrice = 35.0;
        this.lastSeasonNeedFirst = this.firstClass;

        this.money = 8000.0;
        this.meat = 100;
        this.meatPrice = 55.0;
        this.seasonStartMeat = this.meat;

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("meatMarketProvider");
        sd.setName("provideMeat");
        dfd.addServices(sd);
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("wheatMarketProvider");
        sd1.setName("provideWheat");
        dfd.addServices(sd1);

        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        this.myGui = new MarketGui(this);

        this.myGui.setVisible(true);
        this.repaint();

        this.addBehaviour(new MarketParallelBehaviour(this));
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
        double priceRiseCoof = 1.0 + realCount * 0.1 / 70.0;
        this.meat -= realCount;
        this.money += this.meatPrice * realCount;
        this.meatPrice *= priceRiseCoof;
        return realCount;
    }

    public int getFirstClassCount()
    {
        return this.firstClass;
    }

    public double getFirstClassPrice()
    {
        return this.firstClassPrice;
    }

    public int getFirstClassNeed() { return this.lastSeasonNeedFirst; }

    public int sellFirstClass(int count)
    {
        int realCount = this.firstClass >= count ? count : this.firstClass;
        double priceRiseCoof = 1.0 + realCount * 0.1 / 70.0;
        this.firstClass -= realCount;
        this.money += this.firstClassPrice * realCount;
        this.firstClassPrice *= priceRiseCoof;
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

    private class MarketParallelBehaviour extends ParallelBehaviour {

        public MarketParallelBehaviour(MarketAgent agent)
        {
            super(agent, ParallelBehaviour.WHEN_ALL);

            this.addSubBehaviour(new MarketDelayBehaviour(agent, 12000));
            this.addSubBehaviour(new RecieveOrderBehaviour(agent));
        }
    }

    private class RecieveOrderBehaviour extends CyclicBehaviour {

        private final MessageTemplate template;

        public RecieveOrderBehaviour(MarketAgent agent)
        {
            super(agent);

            this.template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(this.template);
            if (msg != null) {
                if (msg.getContent().equals("getFirstClassInfo"))
                {
                    this.replyGetFirstClass(msg);
                }
                else if (msg.getContent().equals("getMeatInfo"))
                {
                    this.replyGetMeat(msg);
                } else if (msg.getContent().equals("buyMeat")) {
                    this.sellMeat(msg);
                }
                else if (msg.getContent().equals("buyFirstClass"))
                {
                    this.sellFirstClass(msg);
                }
                else
                {
                    throw new UnsupportedOperationException("Market does not support this operation!");
                }
            }
            block();
        }

        private void replyGetMeat(ACLMessage msg)
        {
            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());

            MarketAgent agent = (MarketAgent)this.getAgent();
            reply.addUserDefinedParameter("count", agent.getMeatCount() + "");
            reply.addUserDefinedParameter("price", agent.getMeatPrice() + "");

            send(reply);
        }

        private void sellMeat(ACLMessage msg)
        {
            int count = Integer.parseInt(msg.getUserDefinedParameter("count"));
            MarketAgent agent = (MarketAgent)this.getAgent();
            int realCount = agent.sellMeat(count);
            //Logger.getGlobal().log(Level.INFO, agent.getName() + " selling meat: " + realCount + ". To :" +  msg.getSender().getName());
            ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
            reply.addReceiver(msg.getSender());
            reply.addUserDefinedParameter("count", realCount + "");
            agent.send(reply);
            agent.repaint();
        }

        private void replyGetFirstClass(ACLMessage msg)
        {
            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());

            MarketAgent agent = (MarketAgent)this.getAgent();
            reply.addUserDefinedParameter("count", agent.getFirstClassCount() + "");
            reply.addUserDefinedParameter("price", agent.getFirstClassPrice() + "");

            send(reply);
        }

        private void sellFirstClass(ACLMessage msg)
        {
            int count = Integer.parseInt(msg.getUserDefinedParameter("count"));
            MarketAgent agent = (MarketAgent)this.getAgent();
            int realCount = agent.sellFirstClass(count);
            ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
            reply.addReceiver(msg.getSender());
            reply.addUserDefinedParameter("count", realCount + "");
            agent.send(reply);
            agent.repaint();
        }
    }

    private class MarketDelayBehaviour extends TickerBehaviour
    {
        private final MessageTemplate msgTemplate;
        private final MessageTemplate buyTemplate;

        public MarketDelayBehaviour(MarketAgent ag, long period)
        {
            super(ag, period);

            this.msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            this.buyTemplate = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
        }

        @Override
        protected void onTick()
        {
            //Logger.getGlobal().log(Level.INFO, "Market tick");
            int needMeat = (int)((this.getRealAgent().seasonStartMeat - this.getRealAgent().meat) * 1.3);
            int toBuyMeat = needMeat - this.getRealAgent().meat;

            if (toBuyMeat > 0)
            {
                this.tryBuyMeat(toBuyMeat);
                this.getRealAgent().seasonStartMeat = this.getRealAgent().meat;
            }
            else
            {
                this.getRealAgent().seasonStartMeat = this.getRealAgent().meat;
            }

            if (seasonStartMeat == 0)
            {
                this.tryBuyMeat(50);
                this.getRealAgent().seasonStartMeat = this.getRealAgent().meat;
            }

            this.getRealAgent().meatPrice = 55.0;

            if (this.getRealAgent().money < 0)
            {
                this.getRealAgent().bankrupt();
            }

            int needWheat = (int)((this.getRealAgent().lastSeasonNeedFirst - this.getRealAgent().firstClass) * 1.3);
            int toBuyWheat = needWheat - this.getRealAgent().firstClass;

            if (toBuyWheat > 0)
            {
                this.tryBuyWheat(toBuyWheat);
                this.getRealAgent().lastSeasonNeedFirst = this.getRealAgent().firstClass;
            }
            else
            {
                this.getRealAgent().lastSeasonNeedFirst = this.getRealAgent().firstClass;
            }

            if (lastSeasonNeedFirst == 0)
            {
                this.tryBuyWheat(50);
                this.getRealAgent().lastSeasonNeedFirst = this.getRealAgent().firstClass;
            }

            this.getRealAgent().firstClassPrice = 35.0;

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
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("meatProvider");
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

                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("buyMeat");
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
                            startValue -= realCount;
                            this.getRealAgent().money -= realCount * lastBestPrice;
                            this.getRealAgent().meat += realCount;
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
                        msg.setContent("getFirstClassInfo");
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

                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("buyFirstClass");
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
                            startValue -= realCount;
                            this.getRealAgent().money -= realCount * lastBestPrice;
                            this.getRealAgent().firstClass += realCount;
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

        private MarketAgent getRealAgent()
        {
            return (MarketAgent)this.getAgent();
        }
    }
}

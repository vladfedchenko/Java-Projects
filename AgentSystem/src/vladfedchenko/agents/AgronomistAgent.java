package vladfedchenko.agents;

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
import jade.tools.testagent.ReceiveCyclicBehaviour;
import vladfedchenko.agentsgui.AgronomistGui;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vladfedchenko on 4/14/16.
 */
public class AgronomistAgent extends GuiAgent {

    private int firstClass;
    private double firstClassPrice;
    private int lastSeasonNeedFirst;

    private int secondClass;
    private double secondClassPrice;
    private int lastSeasonNeedSecond;

    private double money;
    private AgronomistGui myGui;

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void setup() {
        super.setup();

        this.firstClass = 2000;
        this.firstClassPrice = 30.0;
        this.lastSeasonNeedFirst = this.firstClass;

        this.secondClass = 4100;
        this.secondClassPrice = 15.0;
        this.lastSeasonNeedSecond = this.secondClass;

        this.money = 14500.0;

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("wheatProvider");
        sd.setName("provideWheat");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        this.myGui = new AgronomistGui(this);
        this.myGui.setVisible(true);
        this.repaint();

        this.addBehaviour(new AgroParallelBehaviour(this));
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

    public int getFirstClassCount()
    {
        return this.firstClass;
    }

    public double getFirstClassPrice()
    {
        return this.firstClassPrice;
    }

    public int getFirstClassNeed() { return this.lastSeasonNeedFirst; }

    public int getSecondClassCount()
    {
        return this.secondClass;
    }

    public double getSecondClassPrice()
    {
        return this.secondClassPrice;
    }

    public int getSecondClassNeed() { return this.lastSeasonNeedSecond; }

    public double getMoney() { return this.money; }

    public int sellFirstClass(int count)
    {
        int realCount = this.firstClass >= count ? count : this.firstClass;
        double priceRiseCoof = 1.0 + realCount * 0.1 / 2000.0;
        this.firstClass -= realCount;
        this.money += this.firstClassPrice * realCount;
        this.firstClassPrice *= priceRiseCoof;
        return realCount;
    }

    public int sellSecondClass(int count)
    {
        int realCount = this.secondClass >= count ? count : this.secondClass;
        double priceRiseCoof = 1.0 + realCount * 0.1 / 4100.0;
        this.secondClass -= realCount;
        this.money += this.secondClassPrice * realCount;
        this.secondClassPrice *= priceRiseCoof;
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

    private class AgroParallelBehaviour extends ParallelBehaviour{

        public AgroParallelBehaviour(AgronomistAgent agent)
        {
            super(agent, ParallelBehaviour.WHEN_ALL);

            this.addSubBehaviour(new WheatDelayBehaviour(agent, 60000));
            this.addSubBehaviour(new RecieveOrderBehaviour(agent));
        }
    }

    private class RecieveOrderBehaviour extends CyclicBehaviour{

        public RecieveOrderBehaviour(AgronomistAgent agent)
        {
            super(agent);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getContent().equals("getFirstClassInfo"))
                {
                    this.replyGetFirstClass(msg);
                }
                else if (msg.getContent().equals("getSecondClassInfo"))
                {
                    this.replyGetSecondClass(msg);
                } else if (msg.getContent().equals("buyFirstClass"))
                {
                    this.sellFirstClass(msg);
                } else if (msg.getContent().equals("buySecondClass"))
                {
                    this.sellSecondClass(msg);
                }
                else
                {
                    System.out.println("Agronomist does not support this operation! " + msg.getContent() + ", send by: " + msg.getSender());
                    //throw new UnsupportedOperationException("Agronomist does not support this operation! " + msg.getContent());
                }
            }
            //block();
        }

        private void replyGetFirstClass(ACLMessage msg)
        {
            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());

            AgronomistAgent agroAgent = (AgronomistAgent)this.getAgent();
            reply.addUserDefinedParameter("count", agroAgent.getFirstClassCount() + "");
            reply.addUserDefinedParameter("price", agroAgent.getFirstClassPrice() + "");

            send(reply);
        }

        private void replyGetSecondClass(ACLMessage msg)
        {
            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());

            AgronomistAgent agroAgent = (AgronomistAgent)this.getAgent();
            reply.addUserDefinedParameter("count", agroAgent.getSecondClassCount() + "");
            reply.addUserDefinedParameter("price", agroAgent.getSecondClassPrice() + "");

            send(reply);
        }

        private void sellFirstClass(ACLMessage msg)
        {
            int count = Integer.parseInt(msg.getUserDefinedParameter("count"));
            AgronomistAgent agent = (AgronomistAgent)this.getAgent();
            int realCount = agent.sellFirstClass(count);
            ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
            reply.addReceiver(msg.getSender());
            reply.addUserDefinedParameter("count", realCount + "");
            agent.send(reply);
            agent.repaint();
        }

        private void sellSecondClass(ACLMessage msg)
        {
            int count = Integer.parseInt(msg.getUserDefinedParameter("count"));
            AgronomistAgent agent = (AgronomistAgent)this.getAgent();
            int realCount = agent.sellSecondClass(count);
            ACLMessage reply = new ACLMessage(ACLMessage.AGREE);
            reply.addReceiver(msg.getSender());
            reply.addUserDefinedParameter("count", realCount + "");
            agent.send(reply);
            agent.repaint();
        }
    }

    private class WheatDelayBehaviour extends TickerBehaviour
    {
        public WheatDelayBehaviour(AgronomistAgent ag, long period)
        {
            super(ag, period);
        }

        @Override
        protected void onTick()
        {
            //Logger.getGlobal().log(Level.INFO, "Agro tick");
            this.getRealAgent().money -= this.getRealAgent().firstClass + this.getRealAgent().secondClass;

            int needFirst = (int)((this.getRealAgent().lastSeasonNeedFirst - this.getRealAgent().firstClass) * 1.3);
            this.getRealAgent().firstClass /= 2;
            int toProduceFirst = needFirst - this.getRealAgent().firstClass;

            if (toProduceFirst > 0)
            {
                this.getRealAgent().money -= toProduceFirst * 25.0;
                this.getRealAgent().firstClass = (int)(this.getRealAgent().firstClass + toProduceFirst * this.getProductivity());
                this.getRealAgent().lastSeasonNeedFirst = this.getRealAgent().firstClass;
            }
            else
            {
                this.getRealAgent().lastSeasonNeedFirst = this.getRealAgent().firstClass;
            }

            if (lastSeasonNeedFirst == 0)
            {
                this.getRealAgent().money -= 200 * 25.0;
                this.getRealAgent().firstClass = (int)(this.getRealAgent().firstClass + 200 * this.getProductivity());
                this.getRealAgent().lastSeasonNeedFirst = this.getRealAgent().firstClass;
            }

            this.getRealAgent().firstClassPrice = 30.0;

            int needSecond = (int)((this.getRealAgent().lastSeasonNeedSecond - this.getRealAgent().secondClass) * 1.3);
            this.getRealAgent().secondClass /= 2;
            int toProduceSecond = needSecond - this.getRealAgent().secondClass;

            if (toProduceSecond > 0)
            {
                this.getRealAgent().money -= toProduceSecond * 12.0;
                this.getRealAgent().secondClass = (int)(this.getRealAgent().secondClass + toProduceSecond * this.getProductivity());
                this.getRealAgent().lastSeasonNeedSecond = this.getRealAgent().secondClass;
            }
            else
            {
                this.getRealAgent().lastSeasonNeedSecond = this.getRealAgent().secondClass;
            }

            if (lastSeasonNeedSecond == 0)
            {
                this.getRealAgent().money -= 200 * 12.0;
                this.getRealAgent().secondClass = (int)(this.getRealAgent().secondClass + 200 * this.getProductivity());
                this.getRealAgent().lastSeasonNeedSecond = this.getRealAgent().secondClass;
            }

            this.getRealAgent().secondClassPrice = 15.0;

            if (this.getRealAgent().money < 0)
            {
                this.getRealAgent().bankrupt();
            }

            this.getRealAgent().repaint();
        }

        private double getProductivity()
        {
            Random rand = new Random();
            return (rand.nextDouble() * 10.0 - 5.0) / 100.0 + 1.0;
        }

        private AgronomistAgent getRealAgent()
        {
            return (AgronomistAgent)this.getAgent();
        }
    }
}

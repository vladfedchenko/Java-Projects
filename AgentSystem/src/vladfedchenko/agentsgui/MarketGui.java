package vladfedchenko.agentsgui;

import vladfedchenko.agents.MarketAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by vladfedchenko on 4/19/16.
 */
public class MarketGui extends JFrame {

    private JLabel meatWasVal;
    private JLabel meatPriceVal;
    private JLabel meatVal;

    private JLabel moneyVal;

    private JLabel firstClassWasVal;
    private JLabel firstClassPriceVal;
    private JLabel firstClassVal;

    private MarketAgent agent;

    public MarketGui(MarketAgent agent)
    {
        super(agent.getName());

        this.agent = agent;

        this.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel money = new JLabel("Money: ");
        this.moneyVal = new JLabel("-1");

        this.add(money);
        this.add(moneyVal);

        JLabel meat = new JLabel("Meat: ");
        this.meatVal = new JLabel("-1");

        this.add(meat);
        this.add(meatVal);

        JLabel meatPrice = new JLabel("Meat Price: ");
        this.meatPriceVal = new JLabel("-1");

        this.add(meatPrice);
        this.add(meatPriceVal);

        JLabel meatWas = new JLabel("Meat Was:");
        this.meatWasVal = new JLabel("-1");

        this.add(meatWas);
        this.add(meatWasVal);

        JLabel firstClass = new JLabel("First class: ");
        this.firstClassVal = new JLabel("-1");

        this.add(firstClass);
        this.add(firstClassVal);

        JLabel firstClassPrice = new JLabel("FC Price: ");
        this.firstClassPriceVal = new JLabel("-1");

        this.add(firstClassPrice);
        this.add(firstClassPriceVal);

        JLabel firstClassWas = new JLabel("FC Was:");
        this.firstClassWasVal = new JLabel("-1");

        this.add(firstClassWas);
        this.add(firstClassWasVal);

        this.addWindowListener(new MarketGuiWindowListener());
        this.setSize(230, 175);
        this.setVisible(true);
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.agent != null)
        {
            this.agent.doDelete();
            this.agent = null;
        }
        super.finalize();
    }

    @Override
    public void repaint()
    {
        this.meatVal.setText(this.agent.getMeatCount() + "");
        this.meatPriceVal.setText(this.agent.getMeatPrice() + "");
        this.meatWasVal.setText(this.agent.getSeasonStartMeat() + "");

        this.firstClassVal.setText(this.agent.getFirstClassCount() + "");
        this.firstClassPriceVal.setText(this.agent.getFirstClassPrice() + "");
        this.firstClassWasVal.setText(this.agent.getFirstClassNeed() + "");

        this.moneyVal.setText(this.agent.getMoney() + "");
        super.repaint();
    }

    private class MarketGuiWindowListener extends WindowAdapter
    {

        @Override
        public void windowClosing(WindowEvent e) {
            MarketGui source = (MarketGui)e.getSource();
            try {
                if (null != source)
                {
                    source.finalize();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            super.windowClosing(e);
        }
    }

}

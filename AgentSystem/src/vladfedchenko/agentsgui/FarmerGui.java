package vladfedchenko.agentsgui;

import vladfedchenko.agents.FarmerAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by vladfedchenko on 4/18/16.
 */
public class FarmerGui extends JFrame
{
    private JLabel meatWasVal;
    private JLabel meatPriceVal;
    private JLabel meatVal;
    private JLabel moneyVal;
    private FarmerAgent agent;

    public FarmerGui(FarmerAgent agent)
    {
        super(agent.getName());

        this.agent = agent;

        this.setLayout(new GridLayout(4, 2, 10, 10));

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

        this.addWindowListener(new FarmerGuiWindowListener());
        this.setSize(230, 100);
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

        this.moneyVal.setText(this.agent.getMoney() + "");
        super.repaint();
    }

    private class FarmerGuiWindowListener extends WindowAdapter
    {

        @Override
        public void windowClosing(WindowEvent e) {
            FarmerGui source = (FarmerGui)e.getSource();
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

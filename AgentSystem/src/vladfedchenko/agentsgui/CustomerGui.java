package vladfedchenko.agentsgui;

import vladfedchenko.agents.CustomerAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by vladfedchenko on 4/19/16.
 */
public class CustomerGui extends JFrame {

    private JLabel moneyVal;

    private CustomerAgent agent;

    public CustomerGui(CustomerAgent agent)
    {
        super(agent.getName());

        this.agent = agent;

        this.setLayout(new GridLayout(1, 2, 10, 10));

        JLabel money = new JLabel("Money: ");
        this.moneyVal = new JLabel("-1");

        this.add(money);
        this.add(moneyVal);

        this.addWindowListener(new MarketGuiWindowListener());
        this.setSize(230, 60);
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
        this.moneyVal.setText(this.agent.getMoney() + "");
        super.repaint();
    }

    private class MarketGuiWindowListener extends WindowAdapter
    {

        @Override
        public void windowClosing(WindowEvent e) {
            AgronomistGui source = (AgronomistGui)e.getSource();
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

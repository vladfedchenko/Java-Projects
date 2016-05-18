package vladfedchenko.agentsgui;

import vladfedchenko.agents.AgronomistAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by vladfedchenko on 4/17/16.
 */
public class AgronomistGui extends JFrame {

    private JLabel firstClassWasVal;
    private JLabel firstClassPriceVal;
    private JLabel firstClassVal;
    private JLabel secondClassWasVal;
    private JLabel secondClassPriceVal;
    private JLabel secondClassVal;
    private JLabel moneyVal;
    private AgronomistAgent agent;

    public AgronomistGui(AgronomistAgent agent){

        super(agent.getName());

        this.agent = agent;
        this.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel money = new JLabel("Money: ");
        this.moneyVal = new JLabel("-1");

        this.add(money);
        this.add(moneyVal);

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

        JLabel secondClass = new JLabel("Second class: ");
        this.secondClassVal = new JLabel("-1");

        this.add(secondClass);
        this.add(secondClassVal);

        JLabel secondClassPrice = new JLabel("SC Price: ");
        this.secondClassPriceVal = new JLabel("-1");

        this.add(secondClassPrice);
        this.add(secondClassPriceVal);

        JLabel secondClassWas = new JLabel("SC Was:");
        this.secondClassWasVal = new JLabel("-1");

        this.add(secondClassWas);
        this.add(secondClassWasVal);

        this.addWindowListener(new AgronomistGuiWindowListener());
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
        this.firstClassVal.setText(this.agent.getFirstClassCount() + "");
        this.firstClassPriceVal.setText(this.agent.getFirstClassPrice() + "");
        this.firstClassWasVal.setText(this.agent.getFirstClassNeed() + "");

        this.secondClassVal.setText(this.agent.getSecondClassCount() + "");
        this.secondClassPriceVal.setText(this.agent.getSecondClassPrice() + "");
        this.secondClassWasVal.setText(this.agent.getSecondClassNeed() + "");

        this.moneyVal.setText(this.agent.getMoney() + "");
        super.repaint();
    }

    private class AgronomistGuiWindowListener extends WindowAdapter
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

package vladfedchenko.jade.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by vladfedchenko on 24.02.16.
 */
public class ChatAgentGui extends JFrame implements KeyListener {

    private JTextArea textArea;
    private JTextField textField;
    private ChatAgent agent;

    public ChatAgentGui(ChatAgent agent)
    {
        super(agent.getName());

        this.agent = agent;

        BorderLayout layout = new BorderLayout(10, 10);
        this.setLayout(layout);

        this.textArea = new JTextArea(250, 500);
        this.add(this.textArea, BorderLayout.CENTER);
        this.textArea.setEditable(false);

        this.textField = new JTextField(250);
        this.add(textField, BorderLayout.SOUTH);
        this.textField.addKeyListener(this);

        this.addWindowListener(new ChatAgentGuiWindowListener());
        this.setSize(800, 600);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if (this.textField.getText() != "")
            {
                this.agent.sendMessageToChat(this.textField.getText());
                this.addMessageToTextArea("Me: " + this.textField.getText());
                this.textField.setText("");
                this.repaint();
            }
        }
    }


    public void addMessageToTextArea(String mes)
    {
        this.textArea.setText(this.textArea.getText() + "\n" + mes);
        this.repaint();
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

    private class ChatAgentGuiWindowListener extends WindowAdapter
    {

        @Override
        public void windowClosing(WindowEvent e) {
            ChatAgentGui source = (ChatAgentGui)e.getSource();
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

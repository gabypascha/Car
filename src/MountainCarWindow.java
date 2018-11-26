import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class MountainCarWindow extends JFrame {

    private MountainCar mc;
    private MountainCarViewer view;

    public MountainCarWindow(MountainCar mc) {
        super("Pendulum Environment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mc = mc;
        //Add a MountainCarViewer
        view = new MountainCarViewer(mc);
        add(view, BorderLayout.CENTER);
        //Add a button to reset the car
        JButton randomButton = new JButton();
        randomButton.setText("Random");
        randomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setRandomAngle();
            }
        });
        //This is ugly, but easy ...
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(randomButton, null);
        add(buttonPanel, BorderLayout.SOUTH);
        //Open in center of screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = view.getPreferredSize();
        int left = (screenSize.width - frameSize.width) / 2;
        int top = (screenSize.height - frameSize.height) / 2;
        setLocation(left, top);

        pack();
    }

    public void paintCar() {
        setVisible(true);
        view.repaint();
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
        }
    }

    private void setRandomAngle() {
        mc.randomInit();
    }

    public static void main(String[] args) {
        MountainCar mc = new MountainCar();
        MountainCarWindow pw = new MountainCarWindow(mc);
//        for (int i=0; i<10; i++) {
//            mc.randomInit();
//            int stepcounter = 0;
//            while (!mc.done()) {
//                pw.paintCar();
//                //mc.apply(0);
//                mc.apply((int)(Math.random()*3));
//                stepcounter++;
//            }
//            System.out.println("Episode " + i + " took " + stepcounter + " steps.");
//        }
//        pw.dispose();

        int n_states = 10;
        int n_actions = 12;
        double lambda=1.0;
//        i no longer remember what this is for


        double gamma=0.95; // discounting factor
// * Discount factor for the expected summary reward. The value serves as
//     * multiplier for the expected reward. So if the value is set to 1,
//     * then the expected summary reward is not discounted. If the value is getting
//                * smaller, then smaller amount of the expected reward is used for actions'
//                * estimates update.



        double alpha=0.25; //learning rate
//        * The value determines the amount of updates Q-function receives
//                * during learning. The greater the value, the more updates the function receives.
//                * The lower the value, the less updates it receives.


        double[][] Quality = new double[n_states][n_actions]; //initalize randomly
        for (int i = 0; i < n_states; i++){
            for (int j = 0; j < n_actions; j++)     {
                    Quality[i][j] = (double)(Math.random()/10); // dont know if too big or small
                }
            }

        double[][] e = new double[n_states][n_actions]; // initalize to 0
        Arrays.fill(e, 0);


        for (int iterations = 0; iterations < 10; iterations++) {
            mc.randomInit();
            double[] state = mc.getState();
            int state_num ; /// this depends on definations
            double action;
            int action_num;
            for (int i = 0; i < n_actions; i++) {
                double q = 0;
                if (Quality[state_num][i]>q) { // greedy for inialization
                    q = Quality[state_num][i];
                    action_num = i;
                }
            }


            while (!mc.done()) { // while the final state is not reached
                pw.paintCar();
                mc.apply((int) action);
                double reward = mc.getReward();
                double[] state2 = mc.getState();
                int state_num2;/// this depends on definations
                double action2;
                int action_num2;
                for (int i = 0; i < n_actions; i++) {
                    double q = 0;
                    if (Quality[state_num][i]>q) { // greedy can change exploration policy
                        q = Quality[state_num][i];
                        action_num2 = i;
                    }
                }
                double delta = reward +gamma*Quality[state_num2][action_num2]-Quality[state_num][action_num];
                e[state_num][action_num]++;
                for (int i = 0; i < n_states; i++) {
                    for (int j = 0; j < n_actions; j++) {
                        Quality[i][j] += alpha*delta*e[i][j];
                        e[i][j] *= gamma*lambda;

                    }
                }
                state=state2;
                action=action2;

            }


        }
    }

}
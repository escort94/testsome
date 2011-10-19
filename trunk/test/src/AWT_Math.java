
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class AWT_Math {
    public static void main(String[] args){
      new TFrame().launchFrame();
    }
}

class TFrame extends JFrame {
  TextField num1,num2,num3;
  public void launchFrame(){
    num1 = new TextField(10);
    num2 = new TextField(10);
    num3 = new TextField(15);
    Label LaPlus = new Label("+");
    Button buqual = new Button("=");
    buqual.addActionListener(new MyMonitor(this));
    setLayout(new FlowLayout());
    add(num1);
    add(LaPlus);
    add(num2);
    add(buqual);
    add(num3);
    pack();
    setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}

class MyMonitor implements ActionListener{
	TFrame tf = null;
    public  MyMonitor(TFrame tf){
      this.tf = tf;
    }
    
  public void actionPerformed(ActionEvent e){
    int n1 = Integer.parseInt(tf.num1.getText());
    int n2 = Integer.parseInt(tf.num2.getText());
    tf.num3.setText(""+(n1+n2));
  }
}
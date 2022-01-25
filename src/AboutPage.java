/*

Jared Dyreson
CWID: 889546529
AboutPage.java -> Some information about ZipViewer

*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AboutPage extends JFrame implements ActionListener{
        // Auto generated with caffine and ureadahead.service

	private final int FRAME_HEIGHT = 424, FRAME_WIDTH = 373;
        // https://stackoverflow.com/questions/878573/java-multiline-string
        private String about_message = String.join("\n"
         , "Zip Viewer"
         , "1.0.0"
         , "A zip manager written in pure Java"
         , "This is direct clone of Archive Manager for GNOME"
         , "Copyright....none"
         , "Absolutely no warranty."
        );
        private JTextArea center_label = new JTextArea(about_message);

        private JButton close_button = new JButton("Close");

        private JPanel sub_panel = new JPanel();

        public AboutPage(){
                super("About Zip Viewer");

                this.setSize(FRAME_HEIGHT, FRAME_WIDTH);
                this.setLocationRelativeTo(null);
                this.setLayout(new FlowLayout());
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                this.close_button.addActionListener(this);

                this.sub_panel.setLayout(new GridLayout(2, 0));
                this.center_label.setEditable(false);

                this.add(this.center_label, BorderLayout.CENTER);
                this.add(this.close_button, BorderLayout.SOUTH);
        }
        @Override
        public void actionPerformed(ActionEvent event){
                Object source = event.getSource();
                if(source == close_button){ this.dispose(); }
        }
}

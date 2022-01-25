/*

Jared Dyreson
CWID: 889546529
ContentsOfZip.java -> List the contents of the zip file

*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*; 
import java.util.*; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 

public class ContentsOfZip extends JFrame implements ActionListener{
	private final int FRAME_HEIGHT = 700, FRAME_WIDTH = 700;
        private JButton close_operation_button = new JButton("Close");
        private JPanel panel = new JPanel();
        private JPanel button_panel = new JPanel();
        private Vector<String> column_names = new Vector<String>(Arrays.asList("Name", "Size", "Type", "Modified"));
        private Vector<Vector<String>> files =  new Vector<Vector<String>>();

        public ContentsOfZip(Vector<Vector<String>> file_manifest) throws IOException{
                // display the contents of the archive
                super("Contents of Archive");

                this.setSize(FRAME_HEIGHT, FRAME_WIDTH);
                this.setLocationRelativeTo(null);
                this.setLayout(new GridLayout());
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                this.close_operation_button.addActionListener(this);
                this.panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                this.button_panel.setLayout(new FlowLayout());
                this.add(panel);

                this.files = file_manifest;

                JTable table = new JTable(file_manifest, column_names);
                JScrollPane scroll = new JScrollPane(table);
                
                // allows for the whole space to be filled by the JTable
                table.setFillsViewportHeight(true);
                this.panel.add(scroll);
                this.panel.add(this.close_operation_button, BorderLayout.WEST);
        }

        public JPanel export_table() throws IOException{
               // allow for this functionality to be used outside of just this class
               // think of this as a getter method
               JPanel panel = new JPanel();
               JTable t = new JTable(this.files, this.column_names);
               JScrollPane scroll = new JScrollPane(t);
               t.setFillsViewportHeight(true);
               panel.add(scroll);
               return panel;
        }

        // other getter methods
        public Vector<Vector<String>> get_file_manifest(){ return files; }
        public Vector<String> get_column_names(){ return column_names; }
        
        // close the window when we're done
        @Override
        public void actionPerformed(ActionEvent event){
                Object source = event.getSource();
                if(source == close_operation_button){ this.dispose(); }
        }
}

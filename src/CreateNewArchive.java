/*

Jared Dyreson
CWID: 889546529
CreateNewArchive.java -> GUI Window to aid in creating a zip archive

*/

import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import java.io.*; 
import java.util.*; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 
import javax.swing.border.EmptyBorder;



public class CreateNewArchive extends JFrame implements ActionListener{
        // Auto generated with caffine and pppd-dns.service

        private final int FRAME_HEIGHT = 522, FRAME_WIDTH = 193;
        private JButton create = new JButton("Create");
        private JButton close = new JButton("Close");
        private JButton add_files = new JButton("Add Files");
        private JButton show_contents = new JButton("List Contents");

        private JLabel location_label = new JLabel("Location");
        private JLabel archive_name_label = new JLabel("Name:");
        private JLabel creation_message = new JLabel("", SwingConstants.CENTER);
        private JLabel file_counter = new JLabel("File(s): ");

        private JTextField archive_name_field = new JTextField(20);

        private JPanel bottom_elements = new JPanel();
        private JPanel center_elements = new JPanel();

        private ZipBackend zipper = new ZipBackend();
        private Vector<String> file_manifest = new Vector<String>();
        private Vector<Vector<String>> table_data = new Vector<Vector<String>>();

        public CreateNewArchive(){
                super("Create New Archive");

                this.setSize(FRAME_HEIGHT, FRAME_WIDTH);
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                this.setLocationRelativeTo(null);

                this.create.addActionListener(this);
                this.close.addActionListener(this);
                this.add_files.addActionListener(this);
                this.show_contents.addActionListener(this);

                this.creation_message.setOpaque(true);

                this.bottom_elements.setLayout(new FlowLayout());
                this.bottom_elements.add(create);
                this.bottom_elements.add(add_files);
                this.bottom_elements.add(close);
                this.bottom_elements.add(show_contents);
                this.bottom_elements.add(file_counter, BorderLayout.EAST);

                this.center_elements.setLayout(new GridLayout(3, 0));
                this.center_elements.add(archive_name_label);
                this.center_elements.add(archive_name_field);
                this.center_elements.add(creation_message);

                this.add(bottom_elements, BorderLayout.SOUTH);
                this.add(center_elements, BorderLayout.CENTER);
        }

	public static void main(String[] args){
                CreateNewArchive n = new CreateNewArchive();
                n.setVisible(true);
	}
        public Vector<Vector<String>> export_vector(Vector<String> example_vector) throws IOException{
                // get file information about each file in the vector
                // this is used in the JTable object data field
                Vector<Vector<String>> export_me = new Vector<Vector<String>>();
                for(String path : example_vector){ export_me.add(new FileHandler(path).info()); }
                return export_me;
        }
        public String get_file(){
                // using a builtin file choosing menu
                String file_path = "";
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int return_value = jfc.showOpenDialog(null);
                if(return_value == JFileChooser.APPROVE_OPTION) {
                        File selected_file = jfc.getSelectedFile();
                        file_path = selected_file.getAbsolutePath();
		}
                // we return the absolute path to the file we chose
                return file_path;
                
        }
        public void show_contents_of_zip() throws IOException{
                // treated as main function
                ContentsOfZip contents = new ContentsOfZip(this.export_vector(this.file_manifest));
                contents.setVisible(true);
        }
        @Override
        public void actionPerformed(ActionEvent event){
                Object action_performed = event.getSource();

                if(action_performed == close){ this.dispose(); }
                else if(action_performed == create){
                        String path_to_empty = archive_name_field.getText();
                        if(path_to_empty.length() == 0){
                                // if nothing selected, alert
                                String html_string = "<html><font color='red'>{0}</font></html>";
                                String error_message = MessageFormat.format(html_string, "Please specify an archive name");
                                creation_message.setText(error_message);
                                return;
                        }
                        try{

                                File p = new File(MessageFormat.format("{0}.zip", path_to_empty));
                                if(p.exists()){
                                        // check if the archive already is present
                                        String m = MessageFormat.format("{0}.zip already exists", path_to_empty);
                                        creation_message.setText(MessageFormat.format("<html><font color='red'>{0}</font></html>", m));
                                }
                                else{
                                        // create if not present
                                        zipper.create_empty_archive(MessageFormat.format("{0}.zip", path_to_empty));
                                        zipper.zip_contents(file_manifest, path_to_empty+".zip");
                                        String m = MessageFormat.format("Successfully created {0}.zip", path_to_empty);
                                        creation_message.setText(MessageFormat.format("<html><font color='green'>{0}</font></html>", m));
                                }
                        }
                        catch(Exception error){}
                }
                else if(action_performed == add_files){
                        String path_to_file = get_file();
                        if(new File(path_to_file).exists()){ file_manifest.add(path_to_file); }
                        // show a little counter by how many files are currently loaded
                        file_counter.setText(MessageFormat.format("File(s): {0}", String.valueOf(file_manifest.size())));
                }
                // display contents of the archive that will be created
                else if(action_performed == show_contents){
                        try{ this.show_contents_of_zip(); }
                        catch(Exception error){}
                }
        }
}

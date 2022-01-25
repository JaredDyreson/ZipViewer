/*

Jared Dyreson
CWID: 889546529
ZipWindow.java -> Main driver code for the Zip Viewer application

*/

import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

import java.io.*; 
import java.util.*; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 
import java.nio.file.Path;

// README:
// Driver code for the ZipViewer program
// Open an archive and make edits to it
// Extract the archive to a directory in $PWD


public class ZipWindow extends JFrame implements ActionListener{
        // Auto generated with caffine and cups.service
        private final int FRAME_WIDTH = 959, FRAME_HEIGHT = 1052;
        
        private JPanel menu_panel = new JPanel();
        private JPanel exported_panel = new JPanel();

        private JTextField path_in_archive = new JTextField();

        private JToolBar toolbar = new JToolBar();

        private JMenu file_menu = new JMenu("File");
        private JMenu edit_menu = new JMenu("Edit");
        private JMenu about_menu = new JMenu("About");
        private JMenuItem about_item = new JMenuItem("About");

        private String[] file_menu_options = {"New Archive", "Open", "Extract Files"};
        private String[] edit_menu_options = {"Add Files", "Delete Files", "Rename"};

        private JMenuBar menu_bar = new JMenuBar();
        private ZipBackend zipper = new ZipBackend();
        private CreateNewArchive archive_builder = new CreateNewArchive();
        private String loaded_zip_file_path = "";
        private String show_loaded_html = MessageFormat.format(
                "<html>Archive loaded:<font color='green'>{0}</font></html>", 
                this.loaded_zip_file_path
        );
        private JLabel show_loaded_label = new JLabel(show_loaded_html);

        public String get_loaded(){ return this.loaded_zip_file_path; }

        public ZipWindow(){
                super("Zip Viewer");

                this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                this.setLocationRelativeTo(null);

                this.toolbar.setRollover(true);

                this.menu_bar.add(file_menu);
                this.menu_bar.add(edit_menu);
                this.menu_bar.add(about_menu);
                this.about_item.addActionListener(this);

                this.about_menu.add(about_item);

                for(int i = 0; i < file_menu_options.length; ++i){
                        JMenuItem file_item = new JMenuItem(file_menu_options[i]);
                        JMenuItem edit_item = new JMenuItem(edit_menu_options[i]);

                        file_item.addActionListener(this);
                        edit_item.addActionListener(this);

                        file_menu.add(file_item);
                        edit_menu.add(edit_item);
                }

                this.toolbar.add(menu_bar);
                this.menu_panel.setLayout(new GridLayout(2, 2));
                this.menu_panel.add(toolbar, BorderLayout.WEST);
                this.menu_panel.add(show_loaded_label);
                this.add(menu_panel, BorderLayout.NORTH);

        }

	public static void main(String[] args){
                // code that will power the whole application
                ZipWindow zipper = new ZipWindow();
                zipper.setVisible(true);
	}

        public int find_pos(String[] arr, String value){
                // find where the string is in an array of strings
                for(int i = 0; i < arr.length; ++i){
                        if(arr[i] == value){ return i; }
                }
                // if not found, return -1
                return -1;
        }

        public void new_archive(){
                CreateNewArchive n = new CreateNewArchive();
                n.setVisible(true);
        }

        public void open_archive(){
                try{
                        String path = archive_builder.get_file();
                        if(!zipper.is_zip(path)){
                                String message = MessageFormat.format("{0} is not a zip archive", new File(path).getAbsolutePath());
                                JOptionPane.showMessageDialog(null, message, "Fail", JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        this.loaded_zip_file_path = path;
                        Vector<Vector<String>> fh_manifest = zipper.list_contents(path, true);
                        this.refresh_jframe(new ContentsOfZip(fh_manifest));
                }
                catch(Exception error){}
        }
        // well Java does not support default parameters
        // I was forced to make two, nearly identical functions 
        public void open_archive(String path){
                this.loaded_zip_file_path = path;
                try{
                        Vector<Vector<String>> fh_manifest = zipper.list_contents(path, true);
                        this.refresh_jframe(new ContentsOfZip(fh_manifest));
                }
                catch(Exception error){}
        }

        public void refresh_jframe(ContentsOfZip coz){
                // this code is very important because I am able to make edits to an Object inside the
                // JFrame and refresh it when it needs to be
                // also we need to updated loaded archive tag at the top of the screen
                Container current_container = this.getContentPane();

                if(this.exported_panel != null){ current_container.remove(this.exported_panel); }
                else if (this.show_loaded_label != null){ current_container.remove(this.show_loaded_label); }

                this.show_loaded_html = MessageFormat.format(
                        "<html>Archive loaded:    <font color='green'>{0}</font></html>", 
                        this.loaded_zip_file_path
                );
                this.show_loaded_label.setText(this.show_loaded_html);
                this.exported_panel = get_table_contents(coz.get_file_manifest(), coz.get_column_names());
                this.add(this.exported_panel);
                this.menu_panel.add(this.show_loaded_label);

                current_container.invalidate();
                current_container.revalidate();
                current_container.repaint();
        }

        public JPanel get_table_contents(Vector<Vector<String>> manifest, Vector<String> col_names){
                // this function returns a JPanel with an embedded JTable
                // this allows for a nice view of the contents of the archive
                JPanel exported_panel = new JPanel();
                JTable t = new JTable(manifest, col_names);
                JScrollPane scroll = new JScrollPane(t);
                t.setFillsViewportHeight(true);
                exported_panel.add(scroll);
                exported_panel.setLayout(new BoxLayout(exported_panel, BoxLayout.Y_AXIS));
                return exported_panel;
        }
        public void extract_archive(){
                // wrapper function around the list_contents function with
                // GUI guiders....I like that phrase
                try{

                        String archive_path = archive_builder.get_file();
                        if(zipper.is_zip(archive_path)){
                                // yay valid zip file
                                zipper.list_contents(archive_path, true);
                                String extraction_success_message =  MessageFormat.format("Successfully extracted {0}", archive_path);
                                JOptionPane.showMessageDialog(null, extraction_success_message, "Success", JOptionPane.INFORMATION_MESSAGE);
                                this.loaded_zip_file_path = archive_path;
                        }
                        else{
                                // try again plz
                                String extraction_fail =  MessageFormat.format("{0} is not a valid zip file", archive_path);
                                JOptionPane.showMessageDialog(null, extraction_fail, "Fail", JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                }
                catch(Exception error){}

        }
        public Vector<String> contents_of_dumped(){
                // list the contens of where we extracted our archive, with full paths
                Vector<String> adding_files_vector = new Vector<String>();
                String cwd = new File("").getAbsolutePath();
                String archive_dir = MessageFormat.format("{0}/{1}_extracted/", cwd, zipper.file_name(this.loaded_zip_file_path));
                File p = new File(archive_dir);
                ArrayList<String> names = new ArrayList<String>(Arrays.asList(p.list()));


                for(String a : names){
                        adding_files_vector.add(MessageFormat.format("{0}/{1}", archive_dir, a));
                }
                return adding_files_vector;

        }
        public void add_files() throws IOException{
                // functions that directly work with File objects need to have
                // the ability to throw an exception
                Vector<String> adding_files_vector = this.contents_of_dumped();
                String file_path = archive_builder.get_file();
                adding_files_vector.add(file_path);
                zipper.zip_contents(adding_files_vector, loaded_zip_file_path);
                // remove the contents of the archive we just had extracted
                zipper.delete_dir_contents(extract_dir());
                open_archive(this.loaded_zip_file_path);
        }
        public File extract_dir(){
                // construct a file object pointing to our extract dir
                String cwd = new File("").getAbsolutePath();
                String archive_dir = MessageFormat.format("{0}/{1}_extracted/", cwd, zipper.file_name(this.loaded_zip_file_path));
                return new File(archive_dir);
        }
        public void remove_entry() throws IOException{
                if(this.loaded_zip_file_path.length() == 0){
                        this.no_archive_error();
                        return;
                }
                else{
                        JFrame frame = new JFrame("Remove File");
                        String path = JOptionPane.showInputDialog(frame, "File name:");

                        String archive_name = zipper.file_name(this.loaded_zip_file_path);
                        String path_to_delete = MessageFormat.format("{0}_extracted/{1}", archive_name, path);
                        File delete_entry = new File(path_to_delete);
                        if(!delete_entry.exists()){
                                String message = MessageFormat.format("Cannot remove {0}, does not exist!", path);
                                JOptionPane.showMessageDialog(null, message, "Fail", JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        else{
                                // basically remove the selected file, re-zip the file and then open it again...
                                File del = new File(path_to_delete);
                                delete_entry.delete();
                                Vector<String> adding_files = this.contents_of_dumped();
                                zipper.zip_contents(adding_files, loaded_zip_file_path);
                                zipper.delete_dir_contents(extract_dir());
                                open_archive(this.loaded_zip_file_path);
                                String message = MessageFormat.format("Succesfully removed {0}", path);
                                JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                }
        }

        public void rename_archive(File src) throws IOException{
                JFrame frame = new JFrame("Remove File");
                String dst = JOptionPane.showInputDialog(frame, "Destination");
                // this will be used in the GUI
                dst = MessageFormat.format("{0}.zip", dst);
                // to allow for the use outside of the relative path
                String long_dst = MessageFormat.format("{0}/{1}", src.getParent(), dst);
                if(src.renameTo(new File(long_dst))){
                        //good
                        String message = MessageFormat.format("{0} has been successfully renamed to {1}", src.getName(), dst);
                        JOptionPane.showMessageDialog(null, message, "Rename Success", JOptionPane.INFORMATION_MESSAGE);
                        this.loaded_zip_file_path = long_dst;
                        Vector<Vector<String>> fh_manifest = zipper.list_contents(long_dst, true);
                        this.refresh_jframe(new ContentsOfZip(fh_manifest));
                }
                else{
                        // bad
                        String message = MessageFormat.format("{0} has not been successfully renamed to {1}", src.getName(), dst);
                        JOptionPane.showMessageDialog(null, message, "Rename Failed", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                // some clean up
                String old_name = zipper.file_name(src.getName());
                String old_archive_dump = MessageFormat.format("{0}_extracted", old_name);
                File delete_old_dump = new File(old_archive_dump);
                if(delete_old_dump.isDirectory()){
                        zipper.delete_dir_contents(delete_old_dump);
                        delete_old_dump.delete();
                }
        }
        public void no_archive_error(){
                JOptionPane.showMessageDialog(null, "Cannot add to archive, no file loaded", "Fail", JOptionPane.ERROR_MESSAGE);
        }
        @Override
        public void actionPerformed(ActionEvent event){
                Object source = event.getSource();
                JMenuItem item_source = (JMenuItem)event.getSource();
                
                // allows us to navigate our menu items efficiently
                // essentially a map from number -> function

                int file_position = find_pos(file_menu_options, item_source.getText());
                int edit_position = find_pos(edit_menu_options, item_source.getText());
                int about_position = find_pos(new String[] {"About"}, item_source.getText());

                if(file_position >= 0){ 
                        // 0 -> New Archive
                        // 1 -> Open Archive
                        // 2 -> Extract Archive
                        switch(file_position){
                                case 0:
                                        this.new_archive();
                                        break;
                                case 1:
                                        this.open_archive();
                                        break;
                                case 2:
                                        this.extract_archive();
                                        break;
                        }
                }
                else if(edit_position >= 0){
                        // 0 -> Add Files
                        // 1 -> Delete Files
                        // 2 -> Rename
                        switch(edit_position){
                                case 0:
                                        if(loaded_zip_file_path.length() == 0){
                                                this.no_archive_error();
                                        }
                                        else{
                                                try{ this.add_files(); }
                                                catch(Exception error){}
                                        }
                                        break;
                                case 1:
                                        try {this.remove_entry();}
                                        catch(Exception error){}
                                        break;
                                case 2:
                                        
                                        if(loaded_zip_file_path.length() == 0){
                                                this.no_archive_error();
                                        }
                                        else{
                                                try{ this.rename_archive(new File(this.loaded_zip_file_path)); }
                                                catch(Exception error){}
                                        }
                                        break;
                        }
                }
                else if(about_position >= 0){
                         AboutPage about_page = new AboutPage();
                         about_page.setVisible(true);
                }
        }
	
}

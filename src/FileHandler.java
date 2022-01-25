/*

Jared Dyreson
CWID: 889546529
FileHandler.java -> A class that handles gathering information about files on disk

*/
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.MessageFormat;

import java.io.*; 
import java.util.*; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 

// CONTENTS
// Get specific information about files in the zip buffer
// file name (relative and absolute)
// modification date
// mime type (what kind of file is it)
// current file size
// return the following information into a string that can
// it can be used in a Vector<String> object


public class FileHandler {
        // Auto generated with caffine and networking.service

        private String path = "";
        private File file; 

        public FileHandler(String path_to_file){
                this.path = path_to_file;
                this.file = new File(this.path);
        }

        public FileHandler(File file_path_object){
                this.file = file_path_object;
                this.path = this.get_path();
        }

        public Vector<String> info() throws IOException{
                // generates information about the file and store it as Vector<String>
                return new Vector<String>(Arrays.asList(
                this.get_path(), this.get_file_size(), this.get_mime_type(), this.get_last_modified()
                ));
        }

        public String get_path(){
                // path displayed in the JTable
                String path = this.file.getName();
                try{ 
                        // make sure it is not too long
                        String sub = path.substring(0, 15);
                        if(sub == path){ return path; }
                        return MessageFormat.format("{0}....",  sub); 
                }
                catch(Exception error){ return path; }
        }
        public String get_absolute_path(){
                // get full path of file
                if(this.file.exists()){ return this.file.getAbsolutePath(); }
                return "";
        }
        public String get_last_modified(){
                // form the datetime into human readable
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                return formatter.format(this.file.lastModified());
        }
        public String get_mime_type() throws IOException{
                // what kind of file is it?
                if(this.file.isDirectory()){ return "dir"; }
                Path source = Paths.get(this.path);
                return String.valueOf(Files.probeContentType(source));
        }

        public String get_file_size(){
                // how big is it in bytes
                return String.valueOf(this.file.length());
        }
}

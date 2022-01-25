/*

Jared Dyreson
CWID: 889546529
ZipBackend.java -> Backend component to the Zip Viewer Application

*/

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.*;
import java.util.zip.ZipFile;
import java.text.MessageFormat;

public class ZipBackend {

        // Auto generated with caffine and accounts-daemon.service
        public Vector<String> convert_nested(Vector<Vector<String>> nested){
                // unpack Vector<Vector<String>> -> Vector<String>
                Vector<String> exported = new Vector<String>();
                for(Vector<String> unnested: nested){
                        Collections.copy(exported, unnested);
                }
                return exported;
        }
        public boolean is_zip(String path) throws IOException{
                // https://coderanch.com/t/381509/java/check-file-zip-file-java
                RandomAccessFile raf = new RandomAccessFile(path, "r");
                long n = raf.readInt();
                raf.close();
                return (n == 0x504B0304) ? true : false;
        }
        public void zip_contents(Vector<String> file_contents, String path_to_file) throws IOException{
                FileOutputStream file_stream = new FileOutputStream(path_to_file);
                // write to this stream
                ZipOutputStream zip_out = new ZipOutputStream(file_stream);
                File destination_zip = new File(path_to_file);

                for(String path : file_contents){
                        File zip_this_file = new File(path);
                        // construct a stream of bytes that will be read into a buffer
                        FileInputStream input_stream = new FileInputStream(zip_this_file);
                        ZipEntry zip_entry = new ZipEntry(zip_this_file.getName());
                        // tell the zip archive where the file will be placed in the file's memory map
                        zip_out.putNextEntry(zip_entry);

                        byte[] bytes = new byte[1024];
                        int len;
                        // reading in chunks of the file
                        while((len = input_stream.read(bytes)) >= 0){
                                zip_out.write(bytes, 0, len);
                        }
                        input_stream.close();
                }
                zip_out.close();
                file_stream.close();
                // close all streams
        }

        public void create_empty_archive(String path_to_file) throws IOException{
                // NOTE
                // this function is interesting and I am still trying to figure
                // out how it actually works

                // file descriptor to base file object
                File zip_file = new File(path_to_file);
                // make the file "writable"
                FileOutputStream stream = new FileOutputStream(zip_file);
                // this allows for the Zip Library to write to the output stream
                // without needing to call the "underlying system for each byte written"
                BufferedOutputStream buffered = new BufferedOutputStream(stream);
                
                // this writes a special header to the file object
                // in doing so, the header becomes "4b50"
                ZipOutputStream zip_stream = new ZipOutputStream(stream);
                // by not adding any entries, we create an empty zip file that can be used
                zip_stream.close();
        }
        public String file_name(String path){
                // return the basename of a path without the .zip extension
                int n_elements = path.split("/").length;
                return path.split("/")[n_elements-1].split(".zip")[0];
        }

        public Vector<Vector<String>> list_contents(String file_path, boolean unpack) throws IOException{
                // get the name of the archive

                // this will only print with depth of 1

                String archive_name = file_name(file_path);
                Vector<Vector<String>> file_handler_vector = new Vector<Vector<String>>();
                String unpack_destination = "";

                ZipFile zip_file = new ZipFile(file_path);
                // if we want to preserve the content of the unpacked zip, we store it somewhere other than /tmp
                if(unpack){
                        unpack_destination = MessageFormat.format("./{0}_extracted", archive_name);
                }else{
                        // Linux only feature
                        unpack_destination = MessageFormat.format("/tmp/{0}_extracted", archive_name);
                }

                File destination = new File(unpack_destination);
                if(!destination.isDirectory()){ destination.mkdirs(); }
                
                // creates a way for the Enumeration feature to be used on ZipEntry objects
                // this can be used on any type "T"
                Enumeration<? extends ZipEntry> enumeration = zip_file.entries();

                while(enumeration.hasMoreElements()){
                        // this is the equivalent of a generator in Python
                        ZipEntry entry = enumeration.nextElement();
                        String file_name = entry.getName();
                        InputStream input_stream = zip_file.getInputStream(entry);
                        String file_out = MessageFormat.format("{0}/{1}", unpack_destination, file_name);
                        if(entry.isDirectory()){
                                File tree_structure = new File(entry.getName());
                                tree_structure.mkdirs();
                                break;
                        }
                        try{ 
                                // unpack to the correct place
                                System.out.println(MessageFormat.format("inflating: {0}", file_out));
                                Files.copy(input_stream, Paths.get(file_out)); 
                        }
                        catch(Exception error){}
                        // note down the information about each file and return that information as
                        // a Vector<Vector<String>>
                        file_handler_vector.add(new FileHandler(file_out).info());

                }

                return file_handler_vector;
        }
        public void delete_dir_contents(File path){
                // remove the files in folder of depth 1
                String[] entries = path.list();
                for(String p : entries){ new File(p).delete();
                }
        }
}

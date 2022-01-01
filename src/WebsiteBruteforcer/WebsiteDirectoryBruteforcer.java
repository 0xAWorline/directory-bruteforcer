/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebsiteBruteforcer;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
/**
 *
 * @author Austin Worline
 */
public class WebsiteDirectoryBruteforcer extends JPanel implements ActionListener {
    
    static File file;
    static FileOutputStream fileOutput;
    static ArrayList <String> slugArray;
    
    static private JLabel websiteLabel;
    static private JLabel protocolLabel;
    static private JLabel outputLabel;
    static private JTextField websiteField;
    static private JComboBox protocolBoxes;
    static private JButton submitButton;
    
    //constructor that sets up all elements inside GUI
    public WebsiteDirectoryBruteforcer() {
        super(new GridBagLayout());
        setSize(250,300);
        
        protocolLabel = new JLabel("Choose Protocol: ");
     
        String[] protocolList = { "HTTP", "HTTPS" };
        protocolBoxes = new JComboBox(protocolList);
        protocolBoxes.setSelectedIndex(0);
         
        websiteLabel = new JLabel("Enter website: ");
        websiteLabel.setBounds(10,20, 50, 80);
        websiteField = new JTextField(15);
        websiteField.setToolTipText("Ex. www.google.com");
        
        outputLabel = new JLabel("READY");
        submitButton = new JButton("START");
        submitButton.setToolTipText("Sends requests");
        submitButton.addActionListener(this);
        
        add(protocolLabel, getConstraints(0, 0));
        add(protocolBoxes, getConstraints(1, 0));
        add(websiteLabel, getConstraints(0, 1));
        add(websiteField, getConstraints(1, 1));
        add(submitButton, getConstraints(1, 2));
        add(outputLabel, getConstraints(0, 2));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
    //ActionEvent for when button is pressed
    @Override
    public void actionPerformed(ActionEvent button) {
        if(websiteField.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "ENTER A WEBSITE");
        }
        else {
            try {
                System.out.println("URL: " + (String) websiteField.getText());
                //spawns new thread to prevent freezing of GUI
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            outputLabel.setText("REQUESTING");
                            multiThreading();
                            outputLabel.setText("DONE");
                            JOptionPane.showMessageDialog(null, "Finished, Check " + System.getProperty("user.dir") + "\\" + file.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }                            
                    }
                };
                thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
            }                  
        }
    }
    //creates main part of GUI and shows
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Website Directory Bruteforcer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new WebsiteDirectoryBruteforcer();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    //returns constraints for GridBagLayout
    public GridBagConstraints getConstraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 5);
        c.gridx = x;
        c.gridy = y;
        return c;
    }
    //sets up files and prepares threadpool for work
    public static void multiThreading() throws IOException {
        file = new File("test" + "-" + "output" + ".txt");
	fileOutput = new FileOutputStream(file); 
	//Opens a threadpool that has 4 threads ready for work
	ExecutorService executor = Executors.newFixedThreadPool(12);
        System.out.println("---Threadpool created---\n");
	  
	//constructor that sets slug as string, and submits it to the run method
        class ThreadTask implements Runnable {
            String finalSlug;
            ThreadTask (String slug) { finalSlug = slug; }
                public void run() {
                    threadWork(finalSlug);
                }
            }
	//takes each element from slugArray and submits it as a new threadTask for executor
	for(int i = 0; i < slugArray.size(); i++) {
            executor.submit(new ThreadTask(slugArray.get(i)));
	}
	        
	executor.shutdown();
	//awaits termination for all threads, gives exception for any issues
	try {
	    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	} catch (InterruptedException e) {
	        e.printStackTrace();
	    }
        System.out.println("---Finished sending requests---");
	fileOutput.flush();
        fileOutput.close();
    }
    //each thread sends a slug to be requested with the full URL
    public static void threadWork(String slug) {
            String protocol = (String) protocolBoxes.getSelectedItem();
            //new instance of requester class to prevent any race conditions for the threads
            Requester requester = new Requester(websiteField.getText(), protocol.toLowerCase());
            try {
	        requester.sendRequest(slug);
	        print(requester.getResponse());
	    } 
            catch (Exception e) {
	            e.getStackTrace();
	        }
	    }
    //parses dictionary file and returns it as an ArrayList
    public static ArrayList parseDictionaryFile(String fileName) throws IOException {
	        BufferedReader reader;
	        ArrayList <String> slugArray = new ArrayList<String>();
                System.out.println("--Parsing Dictionary--");
	        try {
	            reader = new BufferedReader(new FileReader(fileName));   
	            String line = reader.readLine();
	            //adds each slug from every new line to the ArrayList till it reaches end of file 
	            while (line != null) {
	                line = reader.readLine();
	                slugArray.add(line);
	            }
	            reader.close();
		} 
	        catch (IOException e) {
	            e.printStackTrace();
	        }
                System.out.println("--Parsed Dictionary--");
	        return slugArray;        
    }
    //writes response to output file
    public static synchronized void print(String response) {
        try {
	    if (response == "") {
	        return;
	    }
	    fileOutput.write(String.format("%s\n", response).getBytes());
	    } 
	    catch (IOException e) {
                e.printStackTrace();
	    }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        try {
        slugArray = parseDictionaryFile("small.txt");
        } catch (IOException e) {
            e.printStackTrace();
            }
    }    
}
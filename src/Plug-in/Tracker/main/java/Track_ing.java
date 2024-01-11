
/******************************************************************************
# Author:           NadeauLabGroup
# Date:             November 20, 2023
# Description:      Executes the MHI micro and calls upon a Track Algo and gets them side by side
# Input:            Data
# Output:           MHI and Track side by side 
# Sources:          https://imagej.net/develop/plugins
******************************************************************************/
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.plugin.*;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Track_ing implements PlugIn {
    @Override
    public void run(String arg) {
        // Unchanged image
        ImagePlus unchanged = IJ.getImage();

        // MHI Script
        MHI_Script mhiScript = new MHI_Script();
        mhiScript.run("");

        // Get the MHI image for later references
        ImagePlus mhiImage = null;

        // Placeholder for tracking algorithm
        ImagePlus trackImage = null; // TODO: Implement the tracking algorithm

        // Combine MHI and tracking images
        combinedResult(mhiImage, trackImage);
    }

    // TODO: Implement the tracking algorithm
    // private ImagePlus runTrack(ImagePlus unchanged) {
    // ...
    // }

    // Interface creation and combining the two results
    public void combinedResult(ImagePlus mhiImage, ImagePlus trackImage) {
        // Convert images to BufferedImage
        BufferedImage MHI = null;
        BufferedImage TRACK = null; // TODO: Initialize with tracking image

        // Main window frame
        JFrame frame = new JFrame("MHI & Track");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Split pane to hold two images
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JLabel label1 = new JLabel(new ImageIcon(MHI));
        JLabel label2 = new JLabel("Tracking Image Not Available"); // Placeholder label

        // Add labels to the split pane
        splitPane.setLeftComponent(new JScrollPane(label1));
        splitPane.setRightComponent(new JScrollPane(label2));

        // Split divider
        splitPane.setDividerLocation(frame.getWidth() / 2);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        // Pack and display the frame
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}

/*
 * This plugin runs a script to produce an MHI for an active image.
 * Fiji commands and functions are redefined as Java methods for sake
 * of simplicity and readability
 */
class MHI_Script implements PlugIn {
    
    // Main method to run all privately defined methods
    public void run(String arg) {
        // Get the active image ID
        //int imageID = IJ.getImage().getID();

        // Perform the operations
        //deltaFUp();
        //autoThreshold();
        //zCodeStack();
        //depthCodedStack();
        //zProject();
    }

    // Cookbook > T-Functions > Delta F Up
    //private void deltaFUp() {
       // IJ.run("Delta F Up");
    //}

    // Image > Adjust > Threshold
    /*private void autoThreshold() {
        IJ.setAutoThreshold(IJ.getImage(), "MaxEntropy dark");
        IJ.run("Convert to Mask", "method=MaxEntropy background=Dark calculate");

        /*
         * For some reason, this line caused an error in the console,
         * however final desired product is produced. Come back to edit
         * if needed
         */
        // IJ.setOption("BlackBackground", false);

    //}

    // Cookbook > Z-Functions > Z Code Stack
    //private void zCodeStack() {
    //IJ.run("Z Code Stack");
    //}

    // Cookbook > Z-Functions > Depth Coded Stack
    //private void depthCodedStack() {
    //    IJ.selectWindow("Depth Coded Stack");
    //}

    // Image > Stacks > Z-project
    //private void zProject() {
    //    IJ.run("Z Project...", "projection=[Max Intensity]");
    //}
}
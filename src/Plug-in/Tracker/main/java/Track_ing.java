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

        if (WindowManager.getImageCount() == 0) {
            IJ.log("No image open in ImageJ");
            return;
        }

        // Unchanged image
        ImagePlus unchanged = IJ.getImage();

        // MHI Script
        MHI_Script mhiScript = new MHI_Script();
        mhiScript.run("");

        // Get the MHI image for later references
        ImagePlus mhiImage = mhiScript.getMHIImage();

        // Placeholder for tracking algorithm
        ImagePlus trackImage = runTrack(unchanged); // TODO: Implement the tracking algorithm

        if (mhiImage== null) {
            IJ.log("MHI Image is null in Track_ing");
        }
        if (trackImage == null) {
            IJ.log("Track Image is null in Track_ing");
        }
        
        // Combine MHI and tracking images
        combinedResult(mhiImage, trackImage);
    }

    // TODO: Implement the tracking algorithm
    private ImagePlus runTrack(ImagePlus unchanged) {
        return unchanged;
    }
    // Method to convert ImagePlus to BufferedImage
    private BufferedImage convertToBufferedImage(ImagePlus imp) {
        //check if the image null
        if (imp == null) {
            return createPlaceholderImage("ImagePlus is null");
        }
    
        ImageProcessor ip = imp.getProcessor();
        if (ip == null) {
            return createPlaceholderImage("ImageProcessor is null");
        }
    
        BufferedImage bi = new BufferedImage(ip.getWidth(), ip.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(ip.createImage(), 0, 0, null);
        g2d.dispose();
        return bi;
    }

    // Interface creation and combining the two results
    public void combinedResult(ImagePlus mhiImage, ImagePlus trackImage) {
        // Convert images to BufferedImage
        BufferedImage MHI = (mhiImage != null) ? convertToBufferedImage(mhiImage) : createPlaceholderImage("MHI Image Not Available");
        BufferedImage TRACK = (trackImage != null) ? convertToBufferedImage(trackImage) : createPlaceholderImage("Tracking Image Not Available");
    
        // Main window frame
        JFrame frame = new JFrame("MHI & Track");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Split pane to hold two images
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JLabel label1 = new JLabel(new ImageIcon(MHI));
        JLabel label2 = new JLabel(new ImageIcon(TRACK));

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
    // Method to create a placeholder image
    private BufferedImage createPlaceholderImage(String text) {
        BufferedImage img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(Color.WHITE);
        g2d.fillRect(0, 0, 300, 300);
        g2d.setPaint(Color.BLACK);
        g2d.drawString(text, 10, 150);
        g2d.dispose();
        return img;
    }

}

/*
 * This plugin runs a script to produce an MHI for an active image.
 * Fiji commands and functions are redefined as Java methods for sake
 * of simplicity and readability
 */
class MHI_Script implements PlugIn {
    // Get the active image ID
    private ImagePlus mhiImage;
    // Main method to run all privately defined methods
    public void run(String arg) {
        // Perform the operations
        deltaFUp();
        checkImage("After Delta F Up");

        autoThreshold();
        checkImage("After Auto Threshold");

        zCodeStack();
        checkImage("After Z Code Stack");

        depthCodedStack();
        checkImage("After Depth Coded Stack");

        zProject();
        checkImage("After Z Project");
    }
    public ImagePlus getMHIImage() {
        return mhiImage;
    }

    // Cookbook > T-Functions > Delta F Up
    private void deltaFUp() {
       IJ.run("Delta F Up");
    }

    // Image > Adjust > Threshold
    private void autoThreshold() {
        IJ.setAutoThreshold(IJ.getImage(), "MaxEntropy dark");
        IJ.run("Convert to Mask", "method=MaxEntropy background=Dark calculate");
    }

    // Cookbook > Z-Functions > Z Code Stack
    private void zCodeStack() {
    IJ.run("Z Code Stack");
    }

    // Cookbook > Z-Functions > Depth Coded Stack
    private void depthCodedStack() {
       IJ.selectWindow("Depth Coded Stack");
    }

    // Image > Stacks > Z-project
    private void zProject() {
        IJ.run("Z Project...", "projection=[Max Intensity]");
        mhiImage = IJ.getImage();;
        if (mhiImage == null) {
            IJ.log("MHI Image is null after Z Project");
        }
    }
    private void checkImage(String stage) {
        if (WindowManager.getCurrentImage() == null) {
            IJ.log("Image is null at " + stage);
        }
    }
}
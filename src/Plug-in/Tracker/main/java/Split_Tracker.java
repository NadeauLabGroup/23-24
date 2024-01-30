/******************************************************************************
# Author:           NadeauLabGroup
# Date:             November 20, 2023
# Description:      Generates MHI and TrackMate (subject to change) images side by side
# Input:            Data
# Output:           MHI and Track side by side 
# Sources:          https://imagej.net/develop/plugins
******************************************************************************/
import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import javafx.scene.paint.Color;
import ij.plugin.*;
import ij.plugin.PlugIn;
import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferedImage;

/** TODO: 
 *  - Implement a basic script/macro/plugin for Trackmate 
 *    so we can import it to this plugin.
 *      - Currently experiencing a lot of trouble getting TrackMate's
 *        packages and libraries to be accessed by the compiler; 
 *        'classpath' flag not working as expected.  
 *          - Implement as a macro for now using:
 *              IJ.run("TrackMate..., "some_settings")
 *  - Write method for reactive resizing of windows. 
 *      - Allow for zoom in and out, and scrolling
 * 
 * Changes: 
 *  - Changed the name of the plugin to keep it neat in the Plugins'
 *    dropdown menu.
 *  - MHI script extra windows are automatically closed after running
 *    the plugin. Only the JFrame window remains.
 *  - Fixed scaling and zooming aspects when loading the JFrame.
 *  - JFrame now loads an automatic divider down center
 *  - Fixed a bug that would cause JFrame to refuse to close until
 *    user force-quit Fiji.
 */


public class Split_Tracker implements PlugIn {
    @Override
    public void run(String arg) {

        if (WindowManager.getImageCount() == 0) {
            IJ.log("No image open in ImageJ");
            return;
        }

        // Unchanged image; save this one so both processes can use it
        ImagePlus unchanged = IJ.getImage();
        
        // MHI Script
        MHI_Script mhiScript = new MHI_Script();
        mhiScript.run("");
        
        // Hold the MHI image for later references
        ImagePlus mhiImage = mhiScript.getMHIImage();
        
        /** Have to fix code so window isn't deleted before JFrame */
        // // Close excess windows before running TrackMate
        // if(0 == closeExtraWindows(unchanged))
        //     IJ.log("No windows to close.");

        // Placeholder for tracking algorithm
        ImagePlus trackImage = runTrack(unchanged); 
        
        if (mhiImage== null)
            IJ.log("MHI Image is null in Track_ing");
        if (trackImage == null) 
            IJ.log("Track Image is null in Track_ing");
        
        // Combine MHI and tracking images
        combinedResult(mhiImage, trackImage);

    }
    
    /** TODO: Implement the tracking algorithm */
    private ImagePlus runTrack(ImagePlus unchanged) {
            return unchanged;
    }
    
    // Method to convert ImagePlus to BufferedImage
    private BufferedImage convertToBufferedImage(ImagePlus imp) {
        //check if the image null
        if (imp == null) 
            return createPlaceholderImage("ImagePlus is null");
        
        ImageProcessor ip = imp.getProcessor();
        if (ip == null) 
            return createPlaceholderImage("ImageProcessor is null");
        
        BufferedImage bi = new BufferedImage(ip.getWidth(), ip.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(ip.createImage(), 0, 0, null);
        g2d.dispose();
        return bi;
    }
    
    
    // Generating interface and displaying MHI and Tracker side by side
    public void combinedResult(ImagePlus mhiImage, ImagePlus trackImage) {
        
        // Convert images to BufferedImage
        BufferedImage MHI = (mhiImage != null) ? convertToBufferedImage(mhiImage) : createPlaceholderImage("MHI Image Not Available");
        BufferedImage TRACK = (trackImage != null) ? convertToBufferedImage(trackImage) : createPlaceholderImage("Tracking Image Not Available");
        
        // Main window frame
        JFrame frame = new JFrame("MHI & Track");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //EXIT_ON_CLOSE won't close application, not sure why
        
        // Split pane to hold two images
        JSplitPane mainSplittedPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplittedPane.setOneTouchExpandable(true);
        mainSplittedPane.setResizeWeight(0.5);
        
        JLabel label1 = new JLabel(new ImageIcon(MHI));
        JLabel label2 = new JLabel(new ImageIcon(TRACK));
        
        // Add labels to the split pane
        mainSplittedPane.setLeftComponent(new JScrollPane(label1));
        mainSplittedPane.setRightComponent(new JScrollPane(label2));
        
        frame.getContentPane().add(mainSplittedPane, BorderLayout.CENTER);
        
        // Calculate frame size
        int frameWidth = MHI.getWidth() + TRACK.getWidth();
        int frameHeight = Math.max(MHI.getHeight(), TRACK.getHeight());
        frame.setSize(frameWidth, frameHeight);
        
        // Ensure the frame is not too small
        frame.setMinimumSize(new Dimension(500, 400));
        
        // Pack and display the frame
        frame.pack();
        frame.setVisible(true);
    }

    // Method to create a placeholder image; RIGHT SIDE
    /** setPaint was causing compile error due to setOneTouchExpandable() and setResizeWeight() */
    private BufferedImage createPlaceholderImage(String text) {
        BufferedImage img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        // g2d.setPaint(Color.WHITE);
        g2d.fillRect(0, 0, 300, 300);
        // g2d.setPaint(Color.BLACK); 
        g2d.drawString(text, 10, 150);
        //g2d.dispose();
        return img;
    }

    /** Iterate through list of active windows, closing all active 
     * windows. The JFrame window is automatically excluded in this 
     * list. */
    private static int closeExtraWindows() {

        int[] windowList = WindowManager.getIDList();

        if (windowList == null) {
            return 0;
        } else {
            for (int i = 0; i < windowList.length; i++) {
                ImagePlus imp = WindowManager.getImage(windowList[i]);
                if (imp != null)
                    imp.close();
            }

            IJ.run("Collect Garbage");
            return 1;
        }
    }  

    /** Iterate through list of active windows, closing all except 
     * for window passed in as a String parameter.
     * Implementation ideas: 
     *  - grab ID of window in run() method from ij package.
     *  - have user manually enter in name of window they want 
     *    excluded from closing. */
    private static int closeExtraWindows(ImagePlus exception) {

        int[] windowList = WindowManager.getIDList();

        if (windowList == null) {
            return 0;
        } else {
            for (int i = 0; i < windowList.length; i++) {

                ImagePlus imp = WindowManager.getImage(windowList[i]);

                if (imp != null && imp != exception)
                    imp.close();
            }

            IJ.run("Collect Garbage");
            return 1;
        }
    }  
    
}

/** This plugin runs a script to produce an MHI for an active image.
 *  Fiji commands and functions are redefined as Java methods for sake
 *  of simplicity and readability. */
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
        mhiImage = IJ.getImage();
        if (mhiImage == null) 
            IJ.log("MHI Image is null after Z Project");
    }

    // Null check
    private void checkImage(String stage) {
        if (WindowManager.getCurrentImage() == null)
            IJ.log("Image is null at " + stage);
    }
}

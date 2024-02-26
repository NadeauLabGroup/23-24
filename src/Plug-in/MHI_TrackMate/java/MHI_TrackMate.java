/******************************************************************************
# Author:           NadeauLabGroup
# Date:             November 20, 2023
# Description:      Generates MHI and TrackMate (subject to change) images side by side
# Input:            Data
# Output:           MHI and Track side by side 
# Sources:          https://imagej.net/develop/plugins
******************************************************************************/
import ij.*;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.plugin.*;
import ij.plugin.PlugIn;
import javax.swing.*;
import ij.io.FileSaver;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MHI_TrackMate implements PlugIn {
    private ImagePlus original;
    private ImagePlus mhiImage;
    private MHI_Script mhiScript = new MHI_Script();

    @Override
    public void run(String arg) {
        if (WindowManager.getImageCount() == 0) {
            IJ.log("No image open in ImageJ");
            return;
        }

        // Original image
        original = IJ.getImage();

        // Create a deep copy
        mhiImage = original.duplicate();
        closeAllExcept(mhiImage,original);
        
        ImagePlus mhi = null;

        //MHI
        mhiScript.setImage(mhiImage);
        mhiScript.run("");
        mhiImage = mhiScript.getMHIImage();
        if (mhiImage== null) {
            IJ.log("MHI Image is null");
            return; 
        }
        closeAllExcept(mhiScript.getMHIImage(), original);
                
        // Tracking algorithm and the calls on combined 
        launchTrackMate(original);
    }
    private void closeAllExcept(ImagePlus keepOpen1, ImagePlus keepOpen2) {
        int[] windowList = WindowManager.getIDList();
        if (windowList != null) {
            for (int id : windowList) {
                ImagePlus imp = WindowManager.getImage(id);
                if (imp != null && (keepOpen1 == null || !imp.equals(keepOpen1)) && (keepOpen2 == null || !imp.equals(keepOpen2))) {
                    imp.changes = false; // Prevent "Save changes" 
                    imp.close();
                }
            }
        }
    }
    /** TODO: Implement the tracking algorithm */
    private void launchTrackMate(ImagePlus image) {
        if (!image.isVisible()) {
            image.show();
        }
        IJ.selectWindow(image.getTitle());

        // Launch TrackMate
        IJ.run("TrackMate", "");

        //finish tracking button
        displayContinueInstructions();
    }
    private void displayContinueInstructions() {
        JFrame frame = new JFrame("TrackMate Completion");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JButton continueButton = new JButton("I have completed TrackMate tracking");
    
        // Action to perform after TrackMate completion
        continueButton.addActionListener(e -> {
            frame.dispose(); 
            postTrackMateProcessing(); 
        });
    
        frame.getContentPane().add(continueButton, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); 
    }
    private void postTrackMateProcessing() {
        // User has indicated they've finished with TrackMate
        ImagePlus trackResult = getUserSelectedImage(); 
        
        if (trackResult == null) {
            IJ.log("TrackMate result was not selected or generated.");
            return;
        }

        // Proceed with further processing using the trackResult
        combinedResult(mhiScript.getMHIImage(), trackResult);
    }

    private ImagePlus getUserSelectedImage() {
        int[] ids = WindowManager.getIDList();
        if (ids == null) {
            IJ.noImage();
            return null;
        }

        String[] titles = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            ImagePlus imp = WindowManager.getImage(ids[i]);
            titles[i] = imp != null ? imp.getTitle() : "";
        }

        String selectedTitle = (String) JOptionPane.showInputDialog(null,
                "Select the TrackMate result image:",
                "Image Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                titles,
                titles[0]);

        if (selectedTitle == null) {
            IJ.log("No image selected.");
            return null;
        }

        return WindowManager.getImage(selectedTitle);
    }
    // Generating interface and displaying MHI and Tracker side by side
    public void combinedResult(ImagePlus mhiImage, ImagePlus trackImage) {
     // Ensure both images are not null
    if (mhiImage == null || trackImage == null) {
        IJ.log("One of the images is null. MHI: " + (mhiImage == null ? "Yes" : "No") + ", Track Image: " + (trackImage == null ? "Yes" : "No"));
        return;
    }



    // Hide all other windows
    closeAllExcept(mhiImage, trackImage);
    // Ensure thread-safety when modifying GUI components
    SwingUtilities.invokeLater(() -> {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        
        // Show both images first to ensure their windows are created

        mhiImage.show();
        trackImage.show();

        // Now safely position the windows side by side
        ImageWindow windowMHI = mhiImage.getWindow();
        ImageWindow windowTrack = trackImage.getWindow();

    if (windowMHI != null && windowTrack != null) {
            int width = windowMHI.getWidth() + windowTrack.getWidth();
            int height = Math.max(windowMHI.getHeight(), windowTrack.getHeight());
            
            // Adjust if combined width is greater than screen width
            int xPositionMHI = (screenWidth - width) / 2;
            int yPosition = (screenHeight - height) / 2; 

            windowMHI.setLocation(xPositionMHI, yPosition);
            windowTrack.setLocation(xPositionMHI + windowMHI.getWidth(), yPosition);
        }
    });

}

/**
 * This plugin runs a script to produce an MHI for an active image.
 * Fiji commands and functions are redefined as Java methods for sake
 * of simplicity and readability.
 */
class MHI_Script implements PlugIn {
    private ImagePlus image;

    public void setImage(ImagePlus img) {
        this.image = img;
    }

    @Override
    public void run(String arg) {
        try {
            if (image == null) {
                IJ.log("MHI_Script: Image is null at the beginning");
                return;
            }
            image.show();
            IJ.selectWindow(image.getTitle());

            // Perform the operations
            performOperation("Delta F Up", () -> deltaFUp());
            performOperation("Auto Threshold", () -> autoThreshold());
            performOperation("Z Code Stack", () -> zCodeStack());
            performOperation("Depth Coded Stack", () -> depthCodedStack());
            performOperation("Z Project", () -> zProject());

        } catch (RuntimeException e) {
            IJ.log("Runtime exception occurred in MHI_Script: " + e.getMessage());
        } catch (Exception e) {
            IJ.log("An unexpected error occurred in MHI_Script: " + e.getMessage());
        }
    }

    private void performOperation(String operationName, Runnable operation) {
        operation.run();
        updateImageToLatest();
        MHI_TrackMate.this.closeAllExcept(this.image, MHI_TrackMate.this.original);
    }

    public ImagePlus getMHIImage() {
        return this.image;
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
    }
    private void updateImageToLatest() {
        ImagePlus latestImage = WindowManager.getCurrentImage();
        if (latestImage != null && !latestImage.equals(this.image)) {
            this.image = latestImage;
        }
    }
}

}

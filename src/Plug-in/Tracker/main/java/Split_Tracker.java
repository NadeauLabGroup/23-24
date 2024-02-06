
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
import javafx.scene.paint.Color;
import java.awt.image.BufferedImage;

public class Split_Tracker implements PlugIn {
    @Override
    public void run(String arg) {
        if (WindowManager.getImageCount() == 0) {
            IJ.log("No image open in ImageJ");
            return;
        }

        // Original image
        ImagePlus original = IJ.getImage();

        // Create a deep copy
        ImagePlus mhiImage = original.duplicate();

        ImagePlus trackImage = null;
        ImagePlus mhi = null;

        //MHI
        MHI_Script mhiScript = new MHI_Script();
        mhiScript.setImage(mhiImage);
        mhiScript.run("");
        mhi = mhiScript.getMHIImage();
                
        // Tracking algorithm using the original original image
        trackImage = runTrack(original);

        //Null checks
        if (mhi == null) {
            IJ.log("MHI Image is null");
            return; 
        }
        if (trackImage == null) {
            IJ.log("Track Image is null");
            return; 
        }

        // Combine MHI and tracking images
        combinedResult(mhi, trackImage);

    }

    /** TODO: Implement the tracking algorithm */
    private ImagePlus runTrack(ImagePlus original) {
        if (!original.isVisible()) {
            original.show();
        }
        IJ.selectWindow(original.getTitle());

        // Launch TrackMate
        IJ.run("TrackMate", "");

        // Polling loop with user confirmation
        boolean isTrackMateDone = false;
        while (!isTrackMateDone) {
            IJ.wait(15000); // Wait for 15 second

            // Ask the user if they have finished with TrackMate
            int userResponse = JOptionPane.showConfirmDialog(null,
                    "Have you completed the tracking in TrackMate?",
                    "TrackMate Confirmation",
                    JOptionPane.YES_NO_OPTION);

            // Check the user's response
            if (userResponse == JOptionPane.YES_OPTION) {
                isTrackMateDone = true;
            }
        }

        ImagePlus trackResult = getUserSelectedImage();
        if (trackResult == null) {
            IJ.log("TrackMate result was not selected.");
            return null;
        }

        return trackResult;
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

    // Show both images first to ensure their windows are created
    mhiImage.show();
    trackImage.show();

    // Hide all other windows
    int[] ids = WindowManager.getIDList();
    if (ids != null) {
        for (int id : ids) {
            ImagePlus imp = WindowManager.getImage(id);
            if (imp != null && !imp.equals(mhiImage) && !imp.equals(trackImage)) {
                ImageWindow win = imp.getWindow();
                if (win != null) { // Ensure window exists before hiding
                    win.setVisible(false);
                }
            }
        }
    }

    // Now safely position the windows side by side
    ImageWindow windowMHI = mhiImage.getWindow();
    ImageWindow windowTrack = trackImage.getWindow();
    if (windowMHI != null && windowTrack != null) {
        windowMHI.setLocation(0, 0);
        windowTrack.setLocation(windowMHI.getWidth(), 0);
    }

}

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

        } catch (Exception e) {
            IJ.log("An error occurred in MHI_Script: " + e.getMessage());
        }
    }

    private void performOperation(String operationName, Runnable operation) {
        IJ.log("Performing: " + operationName);
        operation.run();
        updateImageToLatest();
        checkImage(operationName);
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

    // Null check
    private void checkImage(String stage) {
        if (this.image == null) {
            IJ.log("Image became null after " + stage);
        } else {
            IJ.log(stage + " completed successfully on " + this.image.getTitle());
        }
    }
    private void updateImageToLatest() {
        ImagePlus latestImage = WindowManager.getCurrentImage();
        if (latestImage != null && !latestImage.equals(this.image)) {
            this.image = latestImage;
        }
    }
}
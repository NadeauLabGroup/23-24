import ij.*;
import ij.gui.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.plugin.*;
import java.awt.*;

/* This plugin runs a script to produce an MHI for an active image. 
Fiji commands and functions are redefined as Java methods for sake 
of simplicity and readability */
public class MHI_Script1 implements PlugIn {

    //Main method to run all privately defined methods
    public void run(String arg) {
        // Get the active image ID
        int imageID = IJ.getImage().getID();

        // Perform the operations
        deltaFUp();
        autoThreshold();
        zCodeStack();
        depthCodedStack();
        zProject();
    }

    /* IJ contains the Fiji/ImageJ commands, parameters 
    are defined similar to ImageJ's macro language run
    commands */

    // Cookbook > T-Functions > Delta F Up
    private void deltaFUp() {
        IJ.run("Delta F Up");
    }

    // Image > Adjust > Threshold
    private void autoThreshold() {
        IJ.setAutoThreshold(IJ.getImage(), "MaxEntropy dark");
        IJ.run("Convert to Mask", "method=MaxEntropy background=Dark calculate");

        /* For some reason, this line caused an error in the console, 
        however final desired product is produced. Come back to edit 
        if needed */
        //IJ.setOption("BlackBackground", false);
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
}

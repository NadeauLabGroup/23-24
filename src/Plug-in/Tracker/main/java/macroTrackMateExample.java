

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

/** Scratch code to test running a TrackMate Macro */
public class macroTrackMateExample implements PlugIn {
    
    public void run(String arg) {

        //write code to duplicate and id two images so we can perform on them
        ImagePlus unchanged = IJ.getImage(); //hold onto original
        IJ.run("Duplicate...", "Duplicate stack=true");
        
        int[] windowList = WindowManager.getIDList();

        if (windowList == null || windowList.length > 2) {
            IJ.log("Cannot perform operations. Too many windows to ID");
        } else {
            ImagePlus trackmate_component = WindowManager.getImage(windowList[0]);
            ImagePlus MHI_component = WindowManager.getImage(windowList[1]);

            if (trackmate_component == null || MHI_component == null)
                IJ.log("Error accessing window list");

            //IJ.run("Collect Garbage");
        }

        //IJ.run(trackmate_component, "TrackMate", "");

        return;
    }  
    
}

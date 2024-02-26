import ij.*;
import ij.*;
import ij.plugin.*;

/* This plugin runs a script to perform a DHM reconstruction on an 
active image. */
public class DHM_Reconstruction implements PlugIn {

    public void run(String arg) {
        // Get the active image
        ImagePlus hologram = IJ.getImage();

        // Perform DHM reconstruction
        ImagePlus reconstructedPhase = reconstructDHM(hologram);

        // Display the reconstructed phase image
        reconstructedPhase.show();
    }

    
    private ImagePlus reconstructDHM(ImagePlus hologram) {
        // Implement DHM reconstruction logic here
        IJ.run("Reconstruction", 
               "p_hologram=edu.pdx.imagej.dynamic_parameters.ImageParameter@33f560f p_wavelength=edu.pdx.imagej.dynamic_parameters.DoubleParameter@274174ce p_width=edu.pdx.imagej.dynamic_parameters.DoubleParameter@41564876 p_height=edu.pdx.imagej.dynamic_parameters.DoubleParameter@3129e95c p_ts=edu.pdx.imagej.reconstruction.TParameter@48df5e58 p_zs=edu.pdx.imagej.reconstruction.ZParameter@6494a489 p_plugins=edu.pdx.imagej.reconstruction.plugin.AllPluginsParameter@17724595");


        // For demonstration purposes, simply duplicate the hologram
        ImagePlus reconstructedPhase = hologram.duplicate();
        reconstructedPhase.setTitle("Reconstructed Phase (Duplicate)");

        return reconstructedPhase;
    }
}

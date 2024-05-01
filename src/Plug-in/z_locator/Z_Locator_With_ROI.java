// package edu.pdx.imagej.reconstruction.filter;

import ij.IJ;
import ij.ImagePlus;

import ij.plugin.filter.PlugInFilter;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.measure.ResultsTable;

import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;

import javafx.scene.effect.Light.Point;
import javafx.scene.shape.Rectangle;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;


/** Need to configure machine library to access the following packages
 * 
 */
// import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
// import edu.pdx.imagej.reconstruction.plugin.AbstractReconstructionPlugin;
// import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
// import edu.pdx.imagej.reconstruction.ReconstructionField;
// import edu.pdx.imagej.reconstruction.ConstReconstructionField;

// import org.scijava.Priority;
// import org.scijava.plugin.Parameter;
// import org.scijava.plugin.Plugin;


public class Z_Locator_With_ROI implements PlugInFilter {

    @Override
    public void run(ImageProcessor ip) {
        
        ImagePlus imp = IJ.getImage();
        ResultsTable table = new ResultsTable();

        // Get ROI from the ROI Manager
        Roi roi = imp.getRoi();

        if (roi == null) {
            IJ.error("No ROI selected!");
            return;
        }

        // Get dimensions of the selected ROI
        int roiX = roi.getBounds().x;
        int roiY = roi.getBounds().y;
        int roiWidth = roi.getBounds().width;
        int roiHeight = roi.getBounds().height;

        // Get image sequence information
        int numFrames = imp.getNFrames();
        int numSlices = imp.getNSlices();
        
        // Iterate through all time points and z slices
        for (int t = 1; t <= numFrames; t++) {
            for (int z = 1; z <= numSlices; z++) {
                imp.setPosition(1, z, t);

                // Duplicate the ROI for each slice and time point
                Roi sliceRoi = (Roi) roi.clone();
                sliceRoi.setLocation(roiX, roiY);

                // Crop the image to the selected ROI
                ImageProcessor croppedIp = ip.crop();
                croppedIp.setRoi(sliceRoi);

                // Detect particles within the cropped ROI
                detectParticles(croppedIp, table, t, z, roiX, roiY, roiWidth, roiHeight);
            }
        }

        // Display the ResultsTable
        table.show("Detected Particles");
    }

    private void detectParticles(ImageProcessor ip, ResultsTable table, int timePoint, int focalPoint, int roiX, int roiY, int roiWidth, int roiHeight) {
        // Iterate through the selected ROI
        for (int y = 0; y < roiHeight; y++) {
            for (int x = 0; x < roiWidth; x++) {
                // Check if the pixel is fully white (pixel value == 255)
                if (ip.getPixelValue(x, y) == 255) {
                    // Add the coordinates, time point, and z slice to the ResultsTable
                    table.incrementCounter();
                    table.addValue("Time Point", timePoint);
                    table.addValue("Z Slice", focalPoint);
                    table.addValue("X", x + roiX); // Adjust x coordinate with ROI offset
                    table.addValue("Y", y + roiY); // Adjust y coordinate with ROI offset
                }
            }
        }
    }

        // NEEDS CONFIGURATION
        // /** Manually set the filter.  Use this if you don't want the filter to be
        //  * selected through the gui.
        //  *
        //  * @param roi The roi to set the filter to be.
        //  */
        // public void setFilter(Roi roi)
        // {
        //     M_roi = roi;
        //     M_filtered = true;
        // }
    
        // public void setDefaultMessage(String message)
        // {
        //     M_message = message;
        // }
        // /** Get the filter from the user through the gui, if a filter hasn't been
        //  * set already.
        //  *
        //  * @param field The field to acquire the filter from
        //  */
        // @Override
        // public void processOriginalHologram(ConstReconstructionField field)
        // {
        //     if (!M_filtered) {
        //         getFilter(field, M_message);
        //         M_filtered = true;
        //     }
        // }
        // /** Get the filter from a field.
        //  *
        //  * @param field The field to acquire the filter from
        //  * @param message The message to display to the user describing what's going
        //  *                on.
        //  */
        // public void getFilter(ConstReconstructionField field, String message)
        // {
        //     double[][] fourier = field.fourier().getAmp();
        //     float[][] array = new float[fourier.length][fourier[0].length];
        //     for (int x = 0; x < fourier.length; ++x) {
        //         for (int y = 0; y < fourier[0].length; ++y) {
        //             array[x][y] = (float)fourier[x][y];
        //         }
        //     }
        //     FloatProcessor proc = new FloatProcessor(array);
        //     proc.log();
        //     ImagePlus imp = new ImagePlus("FFT", proc);
        //     imp.show();
        //     WaitForUserDialog dialog = new WaitForUserDialog(message);
        //     dialog.show();
        //     if (dialog.escPressed()) {
        //         imp.hide();
        //         M_error = true;
        //         return;
        //     }
        //     M_roi = imp.getRoi();
        //     imp.hide();
        // }
        // /** Filter a field (just calls {@link filterField filterField}.
        //  *
        //  * @param field The field to filter.
        //  * @param t Unused.
        //  */
        // @Override
        // public void processFilteredField(ReconstructionField field, int t)
        // {
        //     filterField(field);
        // }
        // /** Filter a field.  This is separate from {@link processFilteredField
        //  * processFilteredField} so that other plugins can filter by the same roi.
        //  *
        //  * @param field The field to filter.
        //  */
        // public void filterField(ReconstructionField field)
        // {
        //     if (M_roi == null) return; // If the user didn't select any roi
        //     double[][] fourier = field.fourier().getField();
        //     double[][] filtered = new double[fourier.length][fourier[0].length];
        //     Rectangle rect = M_roi.getBounds();
        //     int centerX = (int)rect.getCenterX();
        //     int centerY = (int)rect.getCenterY();
        //     int xp = field.fourier().width() / 2 - centerX;
        //     int yp = field.fourier().height() / 2 - centerY;
        //     for (Point p : M_roi) {
        //         if (p.x < 0 || p.x >= fourier.length) continue;
        //         if (p.y < 0 || p.y >= fourier[0].length / 2) continue;
        //         filtered[p.x + xp][(p.y + yp) * 2] = fourier[p.x][p.y * 2];
        //         filtered[p.x + xp][(p.y + yp) * 2 + 1] = fourier[p.x][p.y * 2 + 1];
        //     }
        //     field.fourier().setField(filtered);
        // }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_8G + STACK_REQUIRED + ROI_REQUIRED;
    }

    // @Override public boolean hasError() {return M_error;}
    // @Override public Filter duplicate() {return new Filter();}
    // private Roi M_roi;
    // private boolean M_error = false;
    // private boolean M_filtered = false;
    // private String M_message = "Please select the ROI and then press OK.";
}

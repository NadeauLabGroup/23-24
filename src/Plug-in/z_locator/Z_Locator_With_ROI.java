import ij.IJ;
import ij.ImagePlus;

import ij.plugin.filter.PlugInFilter;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.measure.ResultsTable;

import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;
import ij.gui.GenericDialog;

import javafx.scene.effect.Light.Point;
import javafx.scene.shape.Rectangle;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/*  Takes a window of a processed image (DHM reconstruction), using 
 *  an ROI. Scans all t and z slices and stores detected max value 
 *  pixels. User is given option to save data to CSV file with either
 *  a custom name or default generated one with date and how the data
 *  is sorted. 
 * 
 *  At the moment it's hard to tell *where* one should select their
 *  ROI. Manually, it's a helpful visual guide to use a z-projection
 *  separately to see where the paths are going. From there, I want 
 *  the user to be able to make sense of where the path is traveling
 *  between z-slices. 
 * 
 *  I want to take the CSV file and be able to do a scan to either
 *  recreate the path within Fiji or take it to Python using their
 *  cv2 library. A CSV file containing the dimensions of the ROI is 
 *  saved as well to be utilized later for this purpose.
 * 
 */
public class Z_Locator_With_ROI implements PlugInFilter {

    private ResultsTable table;

    @Override
    public void run(ImageProcessor ip) {
        
        ImagePlus imp = IJ.getImage();
        table = new ResultsTable();

        // Get ROI from the ROI Manager
        Roi roi = imp.getRoi();

        if (roi == null) {
            IJ.error("No ROI selected!");
            return;
        }

        // Get dimensions of the selected ROI
        int[] RoiDimensions;
        RoiDimensions = new int[4];

        int roiX = RoiDimensions[0] = roi.getBounds().x;
        int roiY = RoiDimensions[1] = roi.getBounds().y;
        int roiWidth = RoiDimensions[2] = roi.getBounds().width;
        int roiHeight = RoiDimensions[3] = roi.getBounds().height;

        // Get image sequence information
        int numFrames = imp.getNFrames();
        int numSlices = imp.getNSlices();

        //Prompt user to choose sorting options
        String userSelection = new String();
        userSelection = promptSortOption();

        if (null == userSelection) {
            return; // user canceled process
        } 

        //
        else if("time" == userSelection) {
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
        } else {
            for (int z = 1; z <= numSlices; ++z){
                // ImageProcessor binaryIp = imp.getProcessor().duplicate();
                for (int t = 1; t <= numFrames; ++t) {
                    imp.setPosition(1, z, t);

                    Roi sliceRoi = (Roi) roi.clone();
                    sliceRoi.setLocation(roiX, roiY);

                    ImageProcessor croppedIp = ip.crop();
                    croppedIp.setRoi(sliceRoi);

                    detectParticles(croppedIp, table, t, z, roiX, roiY, roiWidth, roiHeight);
                }
            }
        }

        // Display the ResultsTable
        table.show("Detected Particles");
        
        promptSaveToCSV(userSelection, RoiDimensions);
    }

    public static String promptSortOption() {
        GenericDialog gd = new GenericDialog("Sort Option");
        gd.addRadioButtonGroup("Sort by:", new String[]{"Time", "Z Slices"}, 1, 2, "Time");
        gd.showDialog();

        if (gd.wasCanceled()) {
            return null;
        } else if (gd.getNextRadioButton().equals("Time")) {
            return "time";
        } else {
            return "z slices";
        }
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

    public void saveResultsTableToCSV(ResultsTable table, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            writer.writeNext(table.getHeadings());

            // Write data
            for (int i = 0; i < table.getCounter(); i++) {
                String[] row = new String[table.getHeadings().length];
                for (int j = 0; j < row.length; j++) {
                    row[j] = table.getStringValue(j, i);
                }
                writer.writeNext(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // private void promptSaveToCSV(String userSelection) {

    //     String sortingFlag = new String();

    //     if("time" == userSelection) {
    //         sortingFlag = "sorted_time_";
    //     } else {
    //         sortingFlag = "sorted_z_slices_";
    //     }

    //     GenericDialog gd = new GenericDialog("Save to CSV");
    //     gd.addStringField("File name (leave blank for default):", "");
    //     gd.showDialog();

    //     if (gd.wasOKed()) {
    //         String fileName = gd.getNextString();
    //         if (fileName.isEmpty()) {
    //             // Generate default file name based on current date and time
    //             SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
    //             Date now = new Date();
    //             fileName = "detected_particles_" + sortingFlag + formatter.format(now) + ".csv";
    //         } else {
    //             fileName += ".csv";
    //         }

    //         String directoryPath = IJ.getDirectory("Choose a Directory");
    //         if (directoryPath != null) {
    //             String filePath = directoryPath + File.separator + fileName;
    //             saveResultsTableToCSV(table, filePath);
    //             IJ.showMessage("Results saved to " + filePath);
    //         }
    //     }
    // }
    
    public void promptSaveToCSV(String userSelection, int [] RoiDimensions) {

        String sortingFlag = new String();

        if("time" == userSelection) {
            sortingFlag = "sorted_time_";
        } else {
            sortingFlag = "sorted_z_slices_";
        }

        GenericDialog gd = new GenericDialog("Save to CSV");
        gd.addStringField("File name (leave blank for default):", "");
        gd.showDialog();

        if (gd.wasOKed()) {
            String fileName = gd.getNextString();
            if (fileName.isEmpty()) {
                // Generate default file name based on current date and time
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date now = new Date();
                fileName = formatter.format(now);
            } 

            String machineDirectory = IJ.getDirectory("Select a Location to save CSV data");
            if(machineDirectory != null) {
                // Create subfolder
                String subfolderPath = machineDirectory + File.separator + "results_" + fileName;
                File subfolder = new File(subfolderPath);
                if (!subfolder.exists()) { 
                    subfolder.mkdir();
                }

                // Define paths for both CSV files
                String filePath1 = subfolderPath + File.separator + fileName + "detected_particles_" + sortingFlag + ".csv";
                String filePath2 = subfolderPath + File.separator + fileName + "_roi_dimensions.csv";

                // Save to CSV files
                saveResultsTableToCSV(table, filePath1);
                saveROIDimensionsToCSV(RoiDimensions[0], RoiDimensions[1], RoiDimensions[2], RoiDimensions[3], filePath2);

                IJ.showMessage("Results saved to " + subfolderPath);
            }
        }
    }

    public static void saveROIDimensionsToCSV(int roiX, int roiY, int roiWidth, int roiHeight, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header
            String[] header = {"ROI X", "ROI Y", "ROI Width", "ROI Height"};
            writer.writeNext(header);

            // Write ROI dimensions
            String[] row = {String.valueOf(roiX), String.valueOf(roiY), String.valueOf(roiWidth), String.valueOf(roiHeight)};
            writer.writeNext(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_8G + STACK_REQUIRED + ROI_REQUIRED;
    }

}

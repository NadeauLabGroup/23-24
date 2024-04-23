import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.measure.ResultsTable;
import ij.gui.GenericDialog;

public class Z_Locator implements PlugInFilter {

    @Override
    public void run(ImageProcessor ip) {

        ImagePlus imp = IJ.getImage();

        ResultsTable table = new ResultsTable();

        // Get image sequence information
        int numFrames = imp.getNFrames(); //t
        int numSlices = imp.getNSlices(); //z
        int width = imp.getWidth();
        int height = imp.getHeight();

        // Prompt sorting options
        String userSelection = new String();
        userSelection = promptSortOption();

        // If time is selected or prompt window is closed, default is sort points by time
        // If z is selected, sort points by slices
        // If prompt window was closed, return 0 and stop process
        if ("time" == userSelection) {
            for (int t = 1; t <= numFrames; ++t) {
                ImageProcessor binaryIp = imp.getProcessor().duplicate();
    
                for (int z = 0; z < numSlices; ++z){
                    imp.setPosition(1, z, t);
                    detectParticles(binaryIp, table, t, z, width, height);
                }
            }

            table.show("Detected Particles");

        } else if ("z slices" == userSelection) {
            for (int z = 0; z < numSlices; ++z){
                ImageProcessor binaryIp = imp.getProcessor().duplicate();
                
                for (int t = 1; t <= numFrames; ++t) {
                    imp.setPosition(1, z, t);
                    detectParticles(binaryIp, table, t, z, width, height);
                }
            }

            table.show("Detected Particles");

        } else {
            return;
        }

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

    private void detectParticles(ImageProcessor ip, ResultsTable table, int timePoint, int focalPoint, int width, int height) {
        // Traverse over each row (downwards)
        for (int y = 0; y < height; ++y) {

            //Traverse over each element in each row (left to right)
            for (int x = 0; x < width; ++x) {
            	
                // Check if the pixel is fully white (pixel value == 255)
                if (ip.getPixelValue(x, y) == 255) {

                    // IJ.log("Time Point: " + timePoint + "\nFocal Point: " + focalPoint + "\nX: " + x + "\nY: " + y);
                    // Add the coordinates, time point, and z slice to the ResultsTable
                    table.incrementCounter();
                    table.addValue("Time Point", timePoint);
                    table.addValue("Z Slice", focalPoint+1);
                    table.addValue("X", x);
                    table.addValue("Y", y);
                }
            }
        }
    }


    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_8G + STACK_REQUIRED; // Process grayscale 8-bit images and require a stack (sequence of images)
    }
}

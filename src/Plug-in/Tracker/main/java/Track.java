/******************************************************************************
# Author:           NadeauLabGroup
# Date:             November 20, 2023
# Description:      Executes the MHI micro and calls upon a Track Algo and gets them side by side
# Input:            Data
# Output:           MHI and Track side by side 
# Sources:          https://imagej.net/develop/plugins
#******************************************************************************/
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

Public class Track implements PlugIn{
  @Override
  public void run() {
    //Unchanged image 
    ImagePlus unchanged = IJ.getImage();

    // Call the MHI macro
    IJ.runMacroFile("MHI [f1]");

    //Get the MHI image for later references 
    ImagePlus mhiImage = IJ.getImage();
    
    //Run the tracking algo function, passes in the unchanged image
    ImagePlus TrackImage = null;

    //Get the tracking and the tracking algo images togather, Problem with this because the tracking algo creats a stack.
    combinedResult(mhiImage, TrackImage);
}

 //Calls upon a tracking algo and pass in the unchanged images
  //private ImagePlus runTrackMate(unchanged){



  //}


  //Interface creation and combining the two results
  public void combinedResult(ImagePlus mhiImage, ImagePlus TrackImage) {

    //makes mhi into a buffered image
    BufferedImage MHI = mhiImage.getBufferedImage();
    //makes TrackImage into a buffered image
    BufferedImage TRACK = null;


    // main window frame
    JFrame frame = new JFrame("MHI & Track");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //split pane to hold two images
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    
    //labels to hold the images
    JLabel label1 = new JLabel(new ImageIcon(MHI));
    //JLabel label2 = new JLabel(new ImageIcon(TRACK));
    
    // Add labels to the split pane
    splitPane.setLeftComponent(new JScrollPane(label1));
    //splitPane.setRightComponent(new JScrollPane(label2));
    
    // split divider 
    splitPane.setDividerLocation(frame.getWidth() / 2);
    frame.getContentPane().add(splitPane, BorderLayout.CENTER);
    
    //packs the frame
    frame.pack();
    
    // Set the frame size and make it visible
    frame.setSize(800, 600);
    frame.setVisible(true);
}
 
}
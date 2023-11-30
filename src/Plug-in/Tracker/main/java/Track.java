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

Public class Track {
  public void run() {
    //Get the active unchanged image 
    ImagePlus unchanged = IJ.getImage();

    // Call the MHI macro
    IJ.runMacroFile("MHI [f1]");

    //Get the MHI image for later references 
    //ImagePlus mhiImage = IJ.getImage();
    
    //Get stack of all the images and pass it in 
    //Call upon the tracking algo and pass in the unchanged image stack

    //get the stack of the image produced by the tracker and store them


    //Get the tracking and the tracking algo images togather, Problem with this because the tracking algo creats a stack.
    ///ImagePlus combinedResult = combineImagesSideBySide(mhiImage, ......);

    // Show the combined result in a interfaces
    //combinedResult.show();
}

 //Calls upon a tracking algo and pass in a stack of the unchanged images
  //private TrackMate...(Image stack){

  //}

  


  //Interface creation and combining the two results
  //private ImagePlus combineImagesSideBySide(mhiImage, ......){

  //}

 
}
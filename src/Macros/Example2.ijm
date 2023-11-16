macro "MHI [f1]"{
    
    avi = getImageID;
    //run("32-bit");
    
    // Cookbook > T- Functions > Delta F Up
    run("Delta F Up");
    
    setAutoThreshold("MaxEntropy dark");
    //run("Threshold...");
    
    run("Convert to Mask", "method=MaxEntropy background=Dark calculate");
    setOption("BlackBackground", false);
    
    run("Z Code Stack");
    selectImage("Depth Coded Stack");
    
    run("Z Project...", "projection=[Max Intensity]");
    //selectImage("Depth Coded Stack");   
}
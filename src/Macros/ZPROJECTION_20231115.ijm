// Macro for a Preliminary Z-Projection Process
macro "MHI [f1]"{
	
	// Grab ID of active image 
    avi = getImageID;
    
    // Cookbook > T- Functions > Delta F Up
    run("Delta F Up");
    
    //run [Image > Adjust > Threshold]
    setAutoThreshold("MaxEntropy dark");
    
    //Apply these setting to threshold; TURN BLACKBACKGROUND OFF
    run("Convert to Mask", "method=MaxEntropy background=Dark calculate");
    setOption("BlackBackground", false);
    
    // run [ Cookbook > Z-Functions > Z Code Stack ] 
    run("Z Code Stack");
    selectImage("Depth Coded Stack");
    
    //run Z Project with Max Intensity Projection
    run("Z Project...", "projection=[Max Intensity]");
}
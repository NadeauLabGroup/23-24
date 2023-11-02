macro "DeltaF [f1]"{
    avi = getImageID;
    run("32-bit");
    run("Delta F Up");
    run("Threshold...");
    setOption("BlackBackground", true);
    run("Convert to Mask", "method=MaxEntropy background=Dark calculate");
    run("Z Code Stack");
    selectImage("Depth Coded Stack");
    run("Z Project...", "projection=Median");
    run("Find Maxima...", "noise=50 output=[Point Selection] light");
}
macro DeltaF[f1]{
    avi = getImageID;
    run("32-bit");
    run("Delta F Up");
    run("Find Maxima...", "noise=50 output=[Point Selection] light");
}
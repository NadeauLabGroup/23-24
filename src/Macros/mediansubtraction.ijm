macro MeanSubtract{
avi = getImageID;
run("32-bit");
run("Z Project...", "projection=Median");
median = getImageID;
imageCalculator("Subtract stack", avi, median);
run("8-bit");
}


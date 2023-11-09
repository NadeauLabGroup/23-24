// Define your settings: Unit is nanometers
wavelength = 405;
width = 365;
height = 365;
ts = "All";  // --> t slice
zs = "Range";    // z plane selection
zStart = -10;
zEnd = 10;
zStep = 5;

polynomialDegree = 1;
lineSelectionType = "Auto";
amplitude = "Amplitude";
outputImageType = "8-bit";

// Set the ROI before calling the plugin
makeRectangle(1200, 1030, 6, 4);

// Run the Reconstruction plugin with the defined settings
run("Reconstruction", "hologram=1 holograms wavelength=" + wavelength + " width=" + width +
    " height=" + height + " ts=" + ts + " zs=" + zs +
    " z_start=" + zStart + " z_end=" + zEnd + " z_step=" + zStep +
    " polynomial_degree=" + polynomialDegree +
    " line_selection_type=" + lineSelectionType +
    " " + amplitude + " output_image_type=" + outputImageType);








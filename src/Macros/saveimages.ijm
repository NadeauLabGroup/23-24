  
  
if (nImages==0)
     exit("No images are open");
dir = getDirectory("Choose a Directory");
for (n=1; n<=nImages; n++) {
    selectImage(n);
    title = getTitle;
    saveAs("tiff", dir+title.replace("/","_"));
} 
//close("*");
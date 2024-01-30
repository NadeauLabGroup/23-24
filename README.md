# Tracking Software

## Nadeau Group: Installing plugins to use with Fiji

- Download/pull the `.class` files from GitHub corresponding to the desired plugin(s).

There are two ways to import the `.class` files into Fiji:

**Drag and dropping**
This method allows you to keep the original files while automatically making duplicates to store in the `plugins` folder under `Fiji.app`.
1. Drag and drop one or all files into Fiji's GUI

     <img src="./images/FIJI_GUI.png"
          alt="Fiji User Interface"
          style="width:75vh"
     />
     
     <img src="./images/REPLACE_PLUGIN_MESSAGE.png"
          alt="Replace .class file in Fiji"
          style="height:30vh;"
     />


**Directly importing into Fiji's `plugins` Library**
This method moves the original files into `Fiji.app` under its Plugins folder, so it may be best to make duplicates to hold onto before moving files to Fiji.

1. Close/Quit Fiji if it's not closed already (it will have to be closed regardless in order to properly update its `plugins` library)

2. Locate Fiji's `plugins` folder. Right click on Fiji.app in your systems files (typically located under Applications), and select 'Show Package Contents':

     <img src="./images/RIGHTCLICK_PKG_CONTENTS.png"
          alt="Dropdown menu for Right Clicking on Fiji.app"
          style="height:40vh;"       
     />

     <img src="./images/FIJI_PKG_CONTENTS.png"
             alt="Dropdown menu for Right Clicking on Fiji.app"
             style="height:40vh;"       
        />
5. Restart Fiji


Plugins should now be ready to run and located in the dropdown menu under **Plugins**
    - In this example I imported `MHI Script` and `Track ing` which are now located here.

<img src="./images/PLUGIN_DROPDOWN_MENU.png"
     alt="Fiji's Plugin Dropdown Menu"
     style="height:75vh;"


//two different micro
macro "Macro 1" {
    print("This is Macro 1");
}

macro "Macro 2" {
    print("This is Macro 2");
}


//communicate using global variblas
var s = "a string";
macro "Enter String..." {
    s = getString("Enter a String:", s);
}
macro "Print String" {
    print("s=" + s);
}


//Keyboard Shortcuts --> runs the micro if a button is clicked

macro "Macro 1 [f1]" {
    print("The user pressed 'f1'");
    
}

macro "Macro 2 [f2]" {
    print("The user pressed 'f2'");
}
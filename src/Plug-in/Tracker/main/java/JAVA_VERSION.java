import java.util.Properties;

public class JAVA_VERSION {
    
    public static void main(String[] arg) {
        Properties props = System.getProperties();
        String javaVersion = props.getProperty("java.version");
        
        System.out.println("Java version: " + javaVersion);
    }
}

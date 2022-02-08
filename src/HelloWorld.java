import io.jbotsim.core.Topology;
import io.jbotsim.ui.JViewer;

public class HelloWorld{
    public static void main(String[] args){
        Topology tp = new Topology();
        tp.setDefaultNodeModel(TreeNodeV1.class);
        tp.setTimeUnit(1000); // 1 ronde = 1000ms = 1s
        new JViewer(tp);
        tp.start();
    }
}
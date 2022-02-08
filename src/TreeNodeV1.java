import io.jbotsim.core.Message;
import io.jbotsim.core.Color;
import io.jbotsim.core.Node;

public class TreeNodeV1 extends Node {

    Node parent;

    @Override
    public void onStart() { // Initialisation par défaut
        parent = null; // Pas de parent au démarrage
        setColor(Color.green); // Non informé
    }

    @Override
    public void onSelection() { // Noeud sélectionné
        parent = this; // On est notre propre parent (premier noeud à avoir l'information)
        setColor(Color.red); // Informé
        sendAll(new Message("Mon message"));
    }

    @Override
    public void onMessage(Message message) {
        if (getColor() == Color.green) { // Si non-informé
            parent = message.getSender(); // On récupère notre parent
            getCommonLinkWith(parent).setWidth(3); // On se lie avec notre parent en gras
            setColor(Color.red); // Devient informé
            sendAll(new Message(message.getContent()));
        }
    }
}
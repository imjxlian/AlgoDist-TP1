import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;

import java.util.ArrayList;

/**
 * Correspondance des couleurs :
 *
 * BLACK  : La couleur par défaut de notre racine (pour la reconnaitre parmis les autres noeuds)
 * GREEN  : Le noeud ne possède pas l'information
 * RED    : Le noeud possède l'information
 * YELLOW : Le noeud n'aura pas d'enfant
 * BLUE   : La racine sait que l'arbre a fini sa construction
 */

public class TreeNodeV6 extends Node {

    Node parent;
    ArrayList<Node> children;
    int round = 0;
    /**
     * nbChildrenEnded (int) - le nombre d'enfant complétement construit
     */
    int nbChildrenEnded = 0;
    /**
     * nbNodes (int) - le nombre de noeud que l'on connait (nous-même (1 par défaut), enfants, petit-enfants...)
     */
    int nbNodes = 1;

    @Override
    public void onStart() { // Initialisation par défaut
        parent = null; // Pas de parent au démarrage
        children = new ArrayList<>(); // On créé notre liste d'enfant (vide au démarrage)
        setColor(Color.green); // Non informé
    }

    @Override
    public void onSelection() { // Noeud sélectionné
        parent = this; // On est notre propre parent (premier noeud à avoir l'information)
        setColor(Color.BLACK); // Informé
        sendAll(new Message("Mon message"));
    }

    @Override
    public void onMessage(Message message) {

        round = getTime();

        // Informe l'enfant
        if (getColor() == Color.green) { // Si non-informé
            parent = message.getSender(); // On récupère notre parent
            getCommonLinkWith(parent).setWidth(3); // On se lie avec notre parent en gras
            setColor(Color.red); // Devient informé
            sendAll(new Message(message.getContent()));
            send(parent, new Message(this, "CHILDREN")); // On dit a notre parent qu'on est son fils
        }

        // Si on apprend qu'on a un enfant
        if(message.getFlag().equals("CHILDREN")) {
            // Si on ne l'a pas déjà dans notre liste d'enfants
            if(!children.contains(message.getSender())){
                children.add(message.getSender()); // On ajoute notre enfant à notre liste d'enfants
            }
        }

        // Si on reçoit un message de fin de construction de notre enfant
        if(message.getFlag().equals("ENDED")){
            nbChildrenEnded++;
            nbNodes += (int) message.getContent(); // On ajoute au nombre de noeud qu'on connait, les noeuds que contient notre fils
            // Si tout nos fils se sont construits
            if(children.size() == nbChildrenEnded){
                // Si on est la racine
                if(this == parent){
                    setColor(Color.blue); // On change de couleur
                    setLabel("nbNodes : " + nbNodes); // On change le label avec le nombre de noeuds total de l'arbre
                    // On met à jour nos enfants
                    for(Node child : children){
                        send(child, new Message(nbNodes, "MAJ LABEL"));
                    }
                } else {
                    send(parent, new Message(nbNodes, "ENDED")); // On envoie à notre parent qu'on a terminé notre construction (et donc de nos enfants)
                }
            }
        }

        // Si notre parent nous dit de mettre à jour le nombre de noeuds total de l'arbre
        if(message.getFlag().equals("MAJ LABEL")){
            setLabel("nbNodes : " + message.getContent()); // On se met à jour
            // On met à jour nos enfants
            for (Node child : children) {
                send(child, new Message(message.getContent(), "MAJ LABEL"));
            }
        }
    }

    @Override
    public void onClock() {
        // Si on a reçu au moins un message, mais pas de message pendant 2 rounds et qu'on a pas d'enfant, alors on ne peut plus en avoir
        if(round != 0 && round <= getTime() - 2 && children.isEmpty() && getColor() != Color.yellow){
            this.setColor(Color.yellow);
            send(parent, new Message(nbNodes, "ENDED")); // On envoie à notre parent qu'on est un noeud sans enfant
        }
    }
}
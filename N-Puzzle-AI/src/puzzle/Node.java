package puzzle;
import java.util.Vector;
public class Node { // tạo nút của cây
    public State state;
    public int f;
    public int h;
    public int g;
    public boolean check =false;
    public int cost;
    public Node Parent;
    public Node(State s, int cost) {
        this.state = s;
        this.cost = cost;
    }
    public boolean equals(Node n) { //Kiểm tra 2 nút có trùng nhau ko
        if (state.equals(n.state)) return true;
        else return false;
    }
    public int Blank() {//trả về vị trí ô trống
        return state.findBlank();
    }
    public Vector<Node> successors() { //chuyển State thành Node
        Vector<Node> nodes = new Vector<Node>();
        Vector<State> states = state.successors();
        for (int i = 0; i < states.size(); i++) {
            nodes.add(0, new Node(states.elementAt(i), 1));
        }
        return nodes;
    }
    public int length(){
        Vector<State> states = state.successors();
        return states.size();
    }
    public int estimate(Node goalnode) { // trả về giá trị heuristic
        return state.chooseheuristic(goalnode.state);
    }
}
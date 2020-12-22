package puzzle;
import java.util.*;
public class Astar {
    public  Node startnode;  // nút ban đầu
    public  Node goalnode;     // nút đích
    private Node n;            
    private Node tempNode;
    private Vector<Node> FRINGE; // tập trạng thái đã sinh ra chưa được xét   
    private Vector<Node> M;      // tập trạng thái con của 1 trạng thái bất kì   
    public Vector<Node> KQ;     // tập trạng thái từ trạng thái hiện tại tới trạng thái đích
    public Vector<Node> VISIT;  // tập trạng thái có f vượt ngưỡng cutOff
    private int fmin;          
    private int lowIndex; // vị trí Node có fmin
    private int number;
    protected String Stop;
    protected long time_solve; //thời gian giải quyết
    protected int total_nodes; // tổng nút trên cây
    protected int count = 0;    // tổng nút đã duyệt
    public Astar() {
        FRINGE = new Vector<Node>(); 
        M = new Vector<Node>();
        KQ = new Vector<Node>();
        VISIT = new Vector<Node>();
    }
     public void solveAstar() { //Giải quyết vấn đề: thuật toán A*
        KQ.clear();
        long startTime = System.currentTimeMillis();
         startnode.f = startnode.h = startnode.estimate(goalnode);
         startnode.g = 0;
        FRINGE.add(0, startnode); // cho nút đầu đầu tiên vào FRINGE
        total_nodes = 0;
        count = 0;
        while (true) {
            if (FRINGE.isEmpty() || !(Main.window.issolu)) //điều kiện dừng
            {
                FRINGE.clear();
                M.clear();
                Stop = "stop";   
                return;
            }
            if (System.currentTimeMillis()- startTime > 180000) //điều kiện dừng
            {
                FRINGE.clear();
                M.clear();
                Stop = " Hàm heuristic trên với bài toán này quá tốn thời gian!!  \n";
                return;
            }
            lowIndex = 0;
            fmin = FRINGE.elementAt(0).f;
            for (int i = 0; i < FRINGE.size(); i++) {// tìm nút có f nhỏ nhất trong FRINGE
                number = FRINGE.elementAt(i).f;
                if (number < fmin) {
                    lowIndex = i;   //vị trí nút có fmin trong FRINGE
                    fmin = number;
                }
            }
            n = FRINGE.elementAt(lowIndex);  //xét nút n có fmin
            FRINGE.removeElement(n);    //xóa nút đã xét trong FRINGE
            if (n.h == 0)   //if (n.equals(goalnode))   //kiểm tra nút đang xét có phải là đích
                {
                    long endTime = System.currentTimeMillis();
                    time_solve = endTime - startTime;
                    total_nodes = count + FRINGE.size();
                    AddKQ(n);       //đưa kết quả vào trong KQ
                    FRINGE.clear();
                    M.clear();
                    System.out.println("Tổng số các node đã duyệt : " + total_nodes);
                    System.out.println("khoảng cách đã duyệt: " + (KQ.size() - 1));
                    return;
                }
            M = n.successors(); // sinh tâp trạng thái con của n
            if (n.Parent != null) {
                for (int i = 0; i < M.size(); i++) {
                    if (isKT(n.Parent, M.elementAt(i)))  // xóa trạng thái con của n trùng với trạng thái Cha(n)
                        M.remove(i);
                }
            }
            for (int i = 0; i < M.size(); i++){ // tính thông số của các trạng thái con
                Node s = M.elementAt(i);
                s.g = n.g + s.cost;
                s.h = s.estimate(goalnode);
                s.f = s.g + s.h;
                tempNode = (Node) M.elementAt(i);
                tempNode.Parent = n;  // đặt n là cha các trạng thái con
                FRINGE.add(0, M.elementAt(i)); // thêm các trạng thái con vào FRINGE
            }
            count++;  //tăng số nút đã duyệt

        }        
    }
     public void solveBFS(){ // Giải quyết bằng BFS
         Queue<Node> Q = new LinkedList<Node>();
         KQ.clear();
         Map <Node , Node > map = new HashMap<Node, Node>();
         long startTime = System.currentTimeMillis();// Bắt đầu đếm thời gian
         Q.add(startnode);
         KQ.add(startnode);
         startnode.check=true;
         total_nodes = 0;
         try {
             while (true) {
                 Node nx = Q.poll();
                 if (System.currentTimeMillis() - startTime > 18000) { //điều kiện dừng
                     M.clear();
                     Stop = "BFS Algorithms với bài toán này quá tốn thời gian!!  \n";
                     System.out.println("Tổng số các node đã duyệt : " + total_nodes);
                     return;
                 }
                 M = nx.successors();
                 total_nodes += nx.length();
                 for (int i = 0; i < M.size(); i++) {// tính thông số của các trạng thái con
                     Node s = M.elementAt(i);
                     if (isKT(s, goalnode)) {
                         long endTime = System.currentTimeMillis();
                         time_solve = endTime - startTime;
                         Stack<Node> stacks = new Stack<Node>();
                         map.put(s, nx);
                         Node rut = s;
                         while (!isKT(rut, startnode)) {
                             stacks.push(rut);
                             rut = map.get(rut);
                         }
                         while (stacks.size() != 0) {
                             KQ.add(stacks.pop());
                         }
                         M.clear();
                         System.out.println("Tổng số các node đã duyệt : " + total_nodes);
                         System.out.println("khoảng cách đã duyệt: " + (KQ.size() - 1));
                         return;
                     }
                     if (!s.check) {
                         s.check = true;
                         Q.add(s);
                         map.put(s, nx);
                     }
                 }
             }
         }
         catch (OutOfMemoryError e){
             Stop ="Tràn Bộ Nhớ!!";
             System.out.println("Tràn Bộ Nhớ!!");
             return;
         }
     }
     public boolean isKT(Node n, Node v){ //kiểm tra hai nút có trùng nhau không
         if(n.equals(v)) return true;
         return false;
     }

    public void AddKQ(Node n){//đưa kết quả vào KQ
        if(n.Parent!=null) {
            AddKQ(n.Parent);
            KQ.add(n);
        }
        else KQ.add(n);
    }
}
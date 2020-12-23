package puzzle;
import java.util.*;
public class State {
    static int heuristic = 0;
    public int[] Value;
    private int Size = 3;
    private int Length;
    private int blank;
    private int count = 0;
    public State(int m) { //truyền vào kích thước của puzzle
        this.Size=m;
        this.Length = Size*Size;
        this.Value=new int[Length];
        this.blank = 0;
    }
    public State(int[] v, int size) {//truyền vào trạng thái và kích thước của puzzle
        Value = v;
        Size=size;
        Length=Size*Size;
    }
    public void Init() {
        for (int i=0; i < Length; i++) {
            Value[i]=i;
        }
    }
    public int[] createArrayRandom() {
        Init();
        Random rand = new Random();
        int ri;
        for (int i=0; i< Length ; i++) {
            while ((ri = rand.nextInt(Length)) == i);
            int tmp = Value[i];
            Value[i] = Value[ri];
            Value[ri] = tmp;
        }
        return Value;
    }
    public int[] ArrayTronHinh() {
        int t = 65;
        Init();
        this.blank=0;
        count = 0;
        int a = 1, b = 0;
        Random rand1 = new Random();
        while(true) {
            switch(a) {
                case 1: DOWN();break;
                case 2: RIGHT();break;
                case 3: LEFT();break;
                case 4: UP();break;
            }
            while(true) {
                b = rand1.nextInt(4)+1;
                if((a == 1 && b != 4) || (a == 4 && b != 1) || (a == 2 && b != 3) || (a == 3 && b != 2) )
                {
                    a = b;
                    break;
                }
            }
            if(count == t) break;
        }
        return Value;
    }
    public boolean Test(int[] val){ //kiểm tra trạng thái có hợp lệ không
        int row = 0;
        int Count =0;
        int posBlank = 0;
        for(int i = 0; i < Length; i++)
            if(val[i] == 0)
            {
                posBlank = i;
                break;
            }
        row = posBlank / Size + 1;
        for(int i=0; i < Length; i++)
        {
            int t = val[i];
            if(t > 0 && t < Length)
            {
                for(int j = i + 1; j < Length; j++)
                    if(val[j] < t && val[j] > 0)
                        Count++;
            }
        }

        if( Size % 2 ==1)
        {
            return Count % 2 == 0;
        }
        else
        {
            return Count % 2 == (row+1) % 2;
        }
    }
    public boolean equals(State state) {  // kiểm tra xem 2 trạng thái có trùng nhau ko
        State s = (State)state;
        boolean flag = true;
        for (int i = 0; i < Length; i++)
            if (Value[i] != s.Value[i]) {
                flag = false;
                break;
            }
        return flag;
    }

    public int findBlank()  // tìm vị trí ô trống
    {
        int i=0;
        for(i=0; i< Length; i++)
            if(Value[i]==0) break;
        return i;
    }
    public Vector<State> successors() // tạo tập trạng thái mới
    {
        Vector<State> s = new Vector<State>();
        int blank = 0;
        for (int i = 0; i < Length; i++)
            if (Value[i] == 0) {
                blank = i;
                break;
            }
        if ((blank / Size) > 0) {
            addSuccessor(blank, blank - Size, s, Value); //UP
        }
        if ((blank / Size) < Size-1) {
            addSuccessor(blank, blank + Size, s, Value); //DOWN
        }
        if ((blank % Size) > 0) {
            addSuccessor(blank, blank - 1, s, Value); //LEFT
        }
        if ((blank % Size) < Size-1) {
            addSuccessor(blank, blank + 1, s, Value);//RIGHT
        }
        return s;
    }
    private void addSuccessor(int old_loc, int new_loc, Vector<State> v, int[] old){  // thêm trạng thái con vào vector <state>
        int[] val = (int[])old.clone();
        val[old_loc] = val[new_loc];
        val[new_loc] = 0;
        v.add(0, new State(val, Size));
    }
    public int chooseheuristic(State goalstate) { // lựa chọn hàm heuristic
        int est = 0;
        if (heuristic == 1) est = heuristic1(goalstate);
        else if (heuristic == 2) est = heuristic2(goalstate);
        else if (heuristic == 3) est = heuristic3(goalstate);
        else if (heuristic == 4) est = heuristic4(goalstate);
        else if (heuristic == 5) est = heuristic5(goalstate);
        else if (heuristic == 6) est = heuristic6(goalstate);
        else if (heuristic == 7) est = heuristic7(goalstate);
        return est;
    }
    public int heuristic1(State goalstate){ //Tổng số các ô sai vị trí:
        int[] goal = goalstate.Value;
        int distance = 0;
        for(int i=0;i<Length;i++){
            if(goal[i]!=Value[i] ) distance++;
        }
        return distance;
    }
    public int heuristic2(State goalstate) { //Tổng khoảng cách dịch chuyển ngắn nhất để dịch chuyển các ô sai về vị trí đúng của nó:
        int[] goal = goalstate.Value;
        int distance = 0;
        for (int i = 0; i < Length; i++) {
            int c = Value[i];
            int v = 0;
            for (int j = 0; j < Length; j++)
                if (c == goal[j]) {
                    v = j;
                    break;
                }
            if (c != 0) {
                distance += Math.abs((i % Size) - (v % Size)) +Math.abs((i / Size) - (v / Size));
            }
        }
        return distance;
    }
    public int heuristic3(State goalstate) {// Tổng khoảng cách Euclide của các ô so với ô mục tiêu
        int[] goal = goalstate.Value;
        int distance = 0;
        for(int i=0;i<Length;i++){
            int c= Value[i];
            int v=0;
            for(int j=0;j<Length;j++){
                if(goal[j]==c){
                    v=j;
                    break;
                }
            }
            if(c!=0){
                int xd = Math.abs(v % Size - i%Size);
                int yd = Math.abs(v/Size -i/Size);
                int xy = (int) Math.sqrt(Math.pow((double) xd,2)+Math.pow((double)yd,2));
                distance+=xy;
            }
        }
        return distance;
    }
    public int heuristic4(State goalstate) { //Tổng số các ô nằm sai hàng cộng với số các ô nằm sai cột
        int[] goal = goalstate.Value;
        int distance = 0;
        int col =0;
        int row =0;
        for(int i=0;i<Length;i++){
            int c= Value[i];
            int v=0;
            for(int j=0;j<Length;j++){
                if(goal[j]==c){
                    v=j;
                    break;
                }
            }
            if(Math.abs(v%Size-i%Size)>0) col++;
            if(Math.abs(v/Size - i/Size)>0) row++;
        }
        distance = distance+col+row;
        return distance;
    }
    public int heuristic5(State goalstate) { //Tổng bình phương khoảng cách dịch chuyển
        int[] goal = goalstate.Value;
        int distance = 0;
        for (int i = 0; i < Length; i++) {
            int c = Value[i];
            int v = 0;
            for (int j = 0; j < Length; j++)
                if (c == goal[j]) {
                    v = j;
                    break;
                }
            if (c != 0) {
                int xd = (i % Size) - (v % Size);
                int yd = (i / Size) - (v / Size); // bình phương khoảng cách lên
                distance += xd * xd + yd * yd;
            }
        }
        return distance;
    }
    public int heuristic6(State goalstate) { //Tổng số các ô sai vị trí cộng số ô xung đột tuyến tính
        int[] goal = goalstate.Value;
        int distance = 0;
        int a = 0;
        for (int i = 0; i < Length; i++) {
            if(goal[i]!=Value[i] && goal[i]!=0) distance++;
            //Tính số ô xung đột tuyến tính
            if((i != 0) && (i % Size != Size-1) && (Value[i] == i+1) && (Value[i+1] == i)) a += 2;
            if((i != 0) && (i < Length - Size) && (Value[i] == i+Size)&& (Value[i+Size] == i)) a += 2;
        }
        distance = distance+a;
        return distance;
    }
    public int heuristic7(State goalstate) {//Tổng khoảng cách dịch chuyển ngắn nhất + số ô xung đột tuyến tính
        int[] goal = goalstate.Value;
        int distance = 0;
        int a = 0;
        for (int i = 0; i < Length; i++)
        {
            int c = Value[i];
            int v = 0;
            for (int j = 0; j < Length; j++)
                if (c == goal[j]) {
                    v = j;
                    break;
                }
            if (c != 0) {
                distance += Math.abs((i % Size) - (v % Size)) +Math.abs((i / Size) - (v / Size));
            }
            //Tính số ô xung đột tuyến tính
            if((i != 0) && (i % Size != Size-1) && (Value[i] == i+1) && (Value[i+1] == i)) a += 2;
            if((i != 0) && (i < Length - Size) && (Value[i] == i+Size)&& (Value[i+Size] == i)) a += 2;
        }

        return distance + a;
    }
    public void DOWN() {
        int temp;
        if(blank < Length-Size)
        {
            temp = Value[blank];
            Value[blank] = Value[blank+Size];
            Value[blank+Size] = temp;
            blank=blank+Size;
            count++;
        }
        else return;
    }

    public void UP() {
        int temp;
        if(blank >= Size)
        {
            temp = Value[blank];
            Value[blank] = Value[blank-Size];
            Value[blank-Size] = temp;
            blank=blank-Size;
            count++;
        }
        else return;
    }

    public void RIGHT() {
        int temp;
        if(blank % Size !=Size-1)
        {
            temp = Value[blank];
            Value[blank] = Value[blank+1];
            Value[blank+1] = temp;
            blank=blank+1;
            count++;
        }
        else return;
    }

    public void LEFT() {
        int temp;
        if(blank % Size != 0 )
        {
            temp = Value[blank];
            Value[blank] = Value[blank-1];
            Value[blank-1] = temp;
            blank=blank-1;
            count++;
        }
        else return;
    }

}
  
  
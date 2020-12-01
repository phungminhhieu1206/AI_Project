package puzzle;
public class Main {
    static MainForm window = new MainForm();
    public static void main(String[] args) {        
        window.setDefaultCloseOperation( MainForm.EXIT_ON_CLOSE );
        window.setLocation(250, 0);
        window.setResizable(false);
        window.setVisible(true);                
        window.start();       
    }

}
/* Tài Liệu Tham Khảo:
+Analysis and Implementation of Admissible Heuristics in 8 Puzzle Problem
+Solving the 8-Puzzle using A* Heuristic Search, CS365 Presentation by Aman Dhesi
*/
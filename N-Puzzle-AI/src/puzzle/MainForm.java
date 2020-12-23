package puzzle;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
public class MainForm extends javax.swing.JFrame implements MouseListener, KeyListener, Runnable {
    private javax.swing.JPanel Panel1;
    private javax.swing.JMenuItem addImageMenu;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JTextArea display;
    private javax.swing.JRadioButton bfs;
    private javax.swing.JRadioButton h1;
    private javax.swing.JRadioButton h2;
    private javax.swing.JRadioButton h3;
    private javax.swing.JRadioButton h4;
    private javax.swing.JRadioButton h5;
    private javax.swing.JRadioButton h6;
    private javax.swing.JRadioButton h7;
    private javax.swing.JPanel heuristic;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField moveTextField;
    private javax.swing.JButton newGameButton;
    private javax.swing.JMenuItem option;
    private javax.swing.JRadioButton size1;
    private javax.swing.JRadioButton size2;
    private javax.swing.JRadioButton size3;
    private javax.swing.JRadioButton size4;
    private javax.swing.JPanel sizePanel;
    private javax.swing.JButton solveButton;
    private javax.swing.JTextField time;
    private javax.swing.JPanel typegamePanel;
    private javax.swing.JPanel typesolve;
    protected int Size = 3;
    private int Length = 9;
    private State state;
    private JumbledImage Ju;            // Đối tượng khung chứa các ô chữ
    private ViewImage Vi;                //Tạo ảnh gợi ý
    private int[] Value;                //mảng trạng thái trò chơi
    private Astar astar;                // A*
    protected int count = 0;            // đếm bước di chuyển
    private int times = 0;              //đếm thời gian
    protected int typegame = 0;         //loại game: số , ảnh
    protected boolean issolu = false;         // biến boolean  issolu = true khi tự động tìm lời giải(sau khi nhấn button Solve)
    protected boolean win = false;          // khi hoàn thành trò chơi win = true
    private boolean playtime = false;          // khi đếm thời gian playtime =  true
    private Image image;
    protected Color ColorEBox = Color.yellow;      // màu ô trống
    protected Color ColorBoxs = Color.gray;       // màu ô chữ
    protected int typeImage = 0;                  // lựa chọn hình ảnh có sẵn
    protected boolean mute = false;                // mute = true tắt âm thanh
    protected int speed = 2000;                    // chỉnh tốc độ khi tự động chạy
    private MainForm JF;                           // Đối tượng MainForm
    private GameWon Gw;                             // Khung hiển thị khi game won
    private JProgressBar progressBar;
    private int type = 0;

    public MainForm() {
        initComponents();
        progressBar = new JProgressBar();
        progressBar.setLocation(525, 445);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setBorderPainted(true);
        progressBar.setSize(100, 20);
        add(progressBar);

        Init1();
        addKeyListener(this);
        JF = this;
        this.NewGame();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            throw new RuntimeException("Could not set default look and feel");
        }
        setFocusable(true);
    }

    public void Init1() { //Khởi tạo RadioButton Group
        buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(size1);
        buttonGroup1.add(size2);
        buttonGroup1.add(size3);
        buttonGroup1.add(size4);
        size1.setSelected(true);
        buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(bfs);
        buttonGroup3.add(h1);
        buttonGroup3.add(h2);
        buttonGroup3.add(h3);
        buttonGroup3.add(h4);
        buttonGroup3.add(h5);
        buttonGroup3.add(h6);
        buttonGroup3.add(h7);
        h1.setSelected(true);
    }

    public void Init() {
        issolu = false;
        if (Size == 3) size1.setSelected(true);
        else if (Size == 4) size2.setSelected(true);
        else if (Size == 5) size3.setSelected(true);
        else if (Size == 6) size4.setSelected(true);
        Value = new int[Size * Size];
        state = new State(Size);
        astar = new Astar();
    }

    public void NewGame() {  //Bắt đầu game mới
        Init();
        Length = Size * Size;
        win = false;                        //Đặt lại biến win
        if (Ju != null) this.remove(Ju);    //Xóa khung chứa ô chữ cũ
        if (Vi != null) this.remove(Vi);     //Xóa khung ảnh gợi ý
        if (Gw != null) {
            Gw = null;
            this.removeMouseListener(this);
        }
        count = 0;
        times = 0;
        playtime = false;
        time.setText("   00 : 00");     //Đặt lại đồng hồ đếm thời gian
        display.setText("");
        moveTextField.setText("");
        if (Size < 4) {   //Sinh ngẫu nhiên trạng thái 3x3. Kích thước > 3x3 => độ phức tạp cao nên trộn hình
            do {
                Value = state.createArrayRandom();//tạo trạng thái xuất phát
            } while (!state.Test(Value));//kiểm tra xem trạng thái có hợp lệ ko
        } else Value = state.ArrayTronHinh();//trộn trạng thái đích tới 1 trạng thái bất kì
        if (image != null && typegame > 0) {
            Vi = new ViewImage(image);
            Vi.setLocation(520, 475);
            Vi.setSize(Vi.w, Vi.h);
            this.add(Vi);
            this.repaint();
        }
        Ju = new JumbledImage(image, Size, Value, typegame, ColorEBox, ColorBoxs); //Khởi tạo khung hình

        //Đặt vị trí khung hình trên Frame
        if (typegame == 0) Ju.setLocation(350, 20);
        else {
            int x1 = 370 + (430 - Ju.getWidth()) / 2;
            int y1 = 6 + (430 - Ju.getHeight()) / 2;
            Ju.setLocation(x1, y1);
        }
        Ju.setSize(Ju.getWidth(), Ju.getHeight()); //Đặt kích thước khung hình
        Ju.addMouseListener(this);      //Lắng nghe sự kiện chuột
        this.add(Ju);
        this.repaint();
        solveButton.setEnabled(true);
        solveButton.setText("Solve");
        JF.requestFocus();        //Có tác dụng khi bắt sự kiện phím
    }
    private void Disable() {
        if (Ju != null) {
            Ju.setEnabled(false);
        }
    }

    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        Panel1 = new javax.swing.JPanel();
        heuristic = new javax.swing.JPanel();
        bfs = new javax.swing.JRadioButton();
        h1 = new javax.swing.JRadioButton();
        h2 = new javax.swing.JRadioButton();
        h3 = new javax.swing.JRadioButton();
        h4 = new javax.swing.JRadioButton();
        h5 = new javax.swing.JRadioButton();
        h6 = new javax.swing.JRadioButton();
        h7 = new javax.swing.JRadioButton();
        typesolve = new javax.swing.JPanel();
        sizePanel = new javax.swing.JPanel();
        size2 = new javax.swing.JRadioButton();
        size3 = new javax.swing.JRadioButton();
        size1 = new javax.swing.JRadioButton();
        size4 = new javax.swing.JRadioButton();
        typegamePanel = new javax.swing.JPanel();
        newGameButton = new javax.swing.JButton();
        solveButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        time = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        moveTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        display = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        addImageMenu = new javax.swing.JMenuItem();
        option = new javax.swing.JMenuItem();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("N-Puzzle Game");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage((new ImageIcon(getClass().getResource("/images/Puzzle.png"))).getImage());
        Panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        heuristic.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Heuristic"));
        h2.setText("heuristic2");
        h1.setText("heuristic1");
        bfs.setText("BFS");
        h3.setText("heuristic3");
        h4.setText("heuristic4");
        h5.setText("heuristic5");
        h6.setText("heuristic6");
        h7.setText("heuristic7");
        javax.swing.GroupLayout heuristicLayout = new javax.swing.GroupLayout(heuristic);
        heuristic.setLayout(heuristicLayout);
        heuristicLayout.setHorizontalGroup(
                heuristicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(heuristicLayout.createSequentialGroup()
                                .addGroup(heuristicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(bfs)
                                        .addComponent(h1)
                                        .addComponent(h2)
                                        .addComponent(h3)
                                        .addComponent(h4)
                                        .addComponent(h5)
                                        .addComponent(h6)
                                        .addComponent(h7, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                                .addContainerGap())
        );
        heuristicLayout.setVerticalGroup(
                heuristicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(heuristicLayout.createSequentialGroup()
                                .addComponent(bfs)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(h1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(h2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(h3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(h4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(h5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(h6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(h7)
                                .addContainerGap(26, Short.MAX_VALUE))
        );
        sizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Board size"));
        size2.setText("4x4");
        size2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size2ActionPerformed(evt);
            }
        });
        size3.setText("5x5");
        size3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size3ActionPerformed(evt);
            }
        });
        size1.setText("3x3");
        size1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size1ActionPerformed(evt);
            }
        });
        size4.setText("6x6");
        size4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size4ActionPerformed(evt);
            }
        });
        javax.swing.GroupLayout sizePanelLayout = new javax.swing.GroupLayout(sizePanel);
        sizePanel.setLayout(sizePanelLayout);
        sizePanelLayout.setHorizontalGroup(
                sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sizePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(size1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(size2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(size4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(size3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(27, Short.MAX_VALUE))
        );
        sizePanelLayout.setVerticalGroup(
                sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sizePanelLayout.createSequentialGroup()
                                .addComponent(size1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(size2)
                                .addGap(3, 3, 3)
                                .addComponent(size3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(size4)
                                .addContainerGap(7, Short.MAX_VALUE))
        );
        newGameButton.setText("New Game");
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });
        solveButton.setText("Solve");
        solveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solveButtonActionPerformed(evt);
            }
        });
        jLabel1.setText("Time");
        time.setEditable(false);
        time.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel2.setText("Move");
        moveTextField.setEditable(false);
        moveTextField.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        javax.swing.GroupLayout Panel1Layout = new javax.swing.GroupLayout(Panel1);
        Panel1.setLayout(Panel1Layout);
        Panel1Layout.setHorizontalGroup(
                Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(Panel1Layout.createSequentialGroup()
                                                .addComponent(sizePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(typegamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(Panel1Layout.createSequentialGroup()
                                                .addComponent(heuristic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(typesolve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(Panel1Layout.createSequentialGroup()
                                                .addGap(78, 78, 78)
                                                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(solveButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(newGameButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(Panel1Layout.createSequentialGroup()
                                                .addGap(13, 13, 13)
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(Panel1Layout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(moveTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(time, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(26, Short.MAX_VALUE))
        );
        Panel1Layout.setVerticalGroup(
                Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(Panel1Layout.createSequentialGroup()
                                                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(typegamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(sizePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(typesolve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(heuristic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(newGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(solveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(Panel1Layout.createSequentialGroup()
                                                .addGap(39, 39, 39)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(28, 28, 28)
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(moveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(17, Short.MAX_VALUE))
        );
        display.setColumns(20);
        display.setEditable(false);
        display.setFont(new java.awt.Font("Tahoma", 0, 13));
        display.setRows(5);
        display.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(display);
        jMenu2.setText("Edit Image");
        jMenu2.setFont(new java.awt.Font("Tahoma", 0, 12));
        addImageMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        addImageMenu.setText("Add Image");
        addImageMenu.setRequestFocusEnabled(false);
        addImageMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                addImageMenuaddImageMousePressed(evt);
            }
        });
        jMenu2.add(addImageMenu);
        jMenuBar1.add(jMenu2);
        option.setFont(new java.awt.Font("Tahoma", 0, 12));
        option.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/settings.png"))); // NOI18N
        option.setText("Options");
        option.setRequestFocusEnabled(false);
        setJMenuBar(jMenuBar1);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(Panel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(500, 500, 500))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18))
        );

        pack();
    }
    private void addImageMenuaddImageMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addImageMenuaddImageMousePressed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.jpg; *.png; *.gif) Images", "jpg", "gif", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            typegame = 2;
            File imagefile = chooser.getSelectedFile();
            String s = imagefile.getPath();
            image = (new ImageIcon(s)).getImage();
            typeImage = 6;
            NewGame();
        }
    }
    private void solveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solveButtonActionPerformed
        if (issolu) {
            issolu = false;
            solveButton.setText("Solve");
            return;
        }
        playtime = false;
        times = 0;
        time.setText("   00 : 00");
        moveTextField.setText("");
        int[] initarray = Ju.getValue(); //lấy mảng trạng thái hiện tại
        int[] goalarray = new int[Length]; // mảng trạng thái đích
        for (int i = 0; i < Length; i++)
            goalarray[i] = i;
        State initsta = new State(initarray, Size); //Trạng thái bắt đầu
        State goalsta = new State(goalarray, Size); //Trạng thái đích
        astar.startnode = new Node(initsta, 0);
        astar.goalnode = new Node(goalsta, 0);
        //Lựa chọn các hàm ước lượng
        if (h1.isSelected()) State.heuristic = 1;
        else if (h2.isSelected()) State.heuristic = 2;
        else if (h3.isSelected()) State.heuristic = 3;
        else if (h4.isSelected()) State.heuristic = 4;
        else if (h5.isSelected()) State.heuristic = 5;
        else if (h6.isSelected()) State.heuristic = 6;
        else if (h7.isSelected()) State.heuristic = 7;
        else if (bfs.isSelected()) type = 1;
        progressBar.setVisible(true);
        progressBar.setString("Please wait ...");
        progressBar.setIndeterminate(true);
        issolu = true; // báo cho luồng thực hiện chạy thuật toán
        solveButton.setText("Stop");
        JF.requestFocus();
    }

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.NewGame();
        JF.requestFocus();
    }

    private void size1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!issolu && size1.isSelected()) {
            Size = 3;
            this.NewGame();
        }
    }

    private void size2ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!issolu && size2.isSelected()) {
            Size = 4;
            this.NewGame();
        }
    }

    private void size3ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!issolu && size3.isSelected()) {
            Size = 5;
            this.NewGame();
        }
    }
    private void size4ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!issolu && size4.isSelected()) {
            Size = 6;
            this.NewGame();
        }
    }
    public void solution() {
        playtime = true;
        Thread t1 = new Thread() {
            public synchronized void start() {
                super.start();
            }

            public void run() {
                CountTime();
                super.run();
            }
        };
        t1.start(); //Bắt đầu luồng đếm thời gian
        if (type == 0) {
            display.append("+ Astar Algorithm uses ");
            display.append("Heuristic " + State.heuristic + "\n");
            astar.solveAstar();
            playtime = false; //Ngừng đếm thời gian
            if (astar.Stop != null || !issolu) {
                issolu = false;
                if (astar.Stop != "stop") display.append(astar.Stop);
                solveButton.setText("Solve");
                astar.Stop = null;
                return;
            }
            display.append(" Các node đã được đánh giá: " + astar.count + "\n");
            display.append(" Các node trên cây: " + astar.total_nodes + "\n");
            display.append(" Số bước: " + (astar.KQ.size() - 1) + "\n");
            display.append(" Thời gian tìm ra : " + astar.time_solve + "ms\n\n");
            int numStates = astar.KQ.size();
            int k = numStates - 1;

            int auto = JOptionPane.showConfirmDialog(null, " + Tìm thấy lời giải trong " + k + " bước."
                            + "\n + Trong thời gian " + astar.time_solve + "ms"
                            + "\n + Bạn có muốn tự động chạy không ?",
                    "Autorun", 0, 1, new ImageIcon(getClass().getResource("/images/Puzzle.png")));
            if (auto == 1) //auto = 1 ko tự động chạy
            {
                issolu = false;
                solveButton.setText("Solve");
                return;
            }

            //Lấy các trạng thái kết quả trong KQ để tự động chạy
            for (int i = 0; i < numStates - 1; i++) {
                if (!issolu) {
                    if (Length == Size * Size) solveButton.setEnabled(true);
                    return;
                }
                int j = 0, m = 0;
                j = astar.KQ.elementAt(i).Blank();       //Vị trí ô trống của trạng thái i
                m = astar.KQ.elementAt(i + 1).Blank();     //Vị trí ô trống của trạng thái i+1
                if (j == m + 1) {
                    Ju.LEFT();
                    this.repaint();
                } else if (j == m - 1) {
                    Ju.RIGHT();
                    this.repaint();
                } else if (j == m + Size) {
                    Ju.UP();
                    this.repaint();
                } else if (j == m - Size) {
                    Ju.DOWN();
                    this.repaint();
                }
                count++; //Tăng số bước di chuyển
                moveTextField.setText(" " + count + "/" + k);
                if (Ju.checkWin())       //Kiểm tra xem đã là trạng thái đích chưa
                {
                    win = true;
                    this.Disable();
                    this.repaint();
                    int t = JOptionPane.showConfirmDialog(null, "       Game finished \n   You want to play again ? ",
                            "N-Puzzle", 0, 1, new ImageIcon(getClass().getResource("/images/finish.png")));
                    if (t == 0) {
                        this.NewGame();
                        return;
                    } else {
                        solveButton.setText("Slove");
                        solveButton.setEnabled(false);
                        issolu = false;
                        return;
                    }
                }
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException ex) {
                }
            }
            issolu = false;
        } else {
            type =0;
            display.append("+ BFS Algorithm ");
            astar.solveBFS();
            playtime = false; //Ngừng đếm thời gian
            if (astar.Stop != null || !issolu) {
                issolu = false;
                if (astar.Stop != "stop") display.append(astar.Stop+"\n+\n");
                solveButton.setText("Solve");
                astar.Stop = null;
                return;
            }
            display.append(" Các node đã được đánh giá: " + astar.count + "\n");
            display.append(" Các node trên cây: " + astar.total_nodes + "\n");
            display.append(" Số bước: " + (astar.KQ.size() - 1) + "\n");
            display.append(" Thời gian tìm ra : " + astar.time_solve + "ms\n\n");
            int numStates = astar.KQ.size();
            int k = numStates - 1;

            int auto = JOptionPane.showConfirmDialog(null, " + Tìm thấy lời giải trong " + k + " bước."
                            + "\n + Trong thời gian " + astar.time_solve + "ms"
                            + "\n + Bạn có muốn tự động chạy không ?",
                    "Autorun", 0, 1, new ImageIcon(getClass().getResource("/images/Puzzle.png")));
            if (auto == 1) //auto = 1 ko tự động chạy
            {
                issolu = false;
                solveButton.setText("Solve");
                return;
            }

            //Lấy các trạng thái kết quả trong KQ để tự động chạy
            for (int i = 0; i < numStates - 1; i++) {
                if (!issolu) {
                    if (Length == Size * Size) solveButton.setEnabled(true);
                    return;
                }
                int j = 0, m = 0;
                j = astar.KQ.elementAt(i).Blank();       //Vị trí ô trống của trạng thái i
                m = astar.KQ.elementAt(i + 1).Blank();     //Vị trí ô trống của trạng thái i+1
                if (j == m + 1) {
                    Ju.LEFT();
                    this.repaint();
                } else if (j == m - 1) {
                    Ju.RIGHT();
                    this.repaint();
                } else if (j == m + Size) {
                    Ju.UP();
                    this.repaint();
                } else if (j == m - Size) {
                    Ju.DOWN();
                    this.repaint();
                }
                count++; //Tăng số bước di chuyển
                moveTextField.setText(" " + count + "/" + k);
                if (Ju.checkWin())       //Kiểm tra xem đã là trạng thái đích chưa
                {
                    win = true;
                    this.Disable();
                    this.repaint();
                    int t = JOptionPane.showConfirmDialog(null, "       Game finished \n   You want to play again ? ",
                            "N-Puzzle", 0, 1, new ImageIcon(getClass().getResource("/images/finish.png")));
                    if (t == 0) {
                        this.NewGame();
                        return;
                    } else {
                        solveButton.setText("Slove");
                        solveButton.setEnabled(false);
                        issolu = false;
                        return;
                    }
                }
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException ex) {
                }
            }
            issolu = false;
        }
    }

    public void GameWon() //Khi hiện thị khung GameWon sẽ không sử dụng được MainFrame nữa
    {
        Gw = new GameWon();
        Gw.setLocation(400, 280);
        Gw.setVisible(true);
        Gw.setResizable(false);
        this.addMouseListener(this);
        this.setEnabled(false);
    }
    
    public void ExitGame()
    {        
        System.exit(0);        
    }
    public void CountTime()
    {
        int munites = 0;
        int seconds = 0;
        for(;;)
        {
            if(win || !playtime)
            {
                playtime = false;
                times =  0;
                break;
            }   
            if(times < 60)
            {
                seconds = times;
                munites = 0;
            }
            else
            {
                munites = times / 60;
                seconds = times % 60;
            }
            if(munites < 10)
            {
                if(seconds < 10) time.setText("   0" + munites +" : 0" + seconds);
                else time.setText("   0" + munites +" : " + seconds);
            }
            else
            {
                if(seconds < 10) time.setText("   " + munites +" : 0" + seconds);
                else time.setText("   " + munites +" : " + seconds);
            }
            times++;                    
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {};                     
        }
    }

    public void start(){
        Thread t=new Thread(this);
        t.start();
    }
    public void run() {         
        while(true) {
            if(Gw != null && Gw.isClosed) { //Khi tắt Frame Game Won thì trả lại trạng thái ban đầu cho MainForm
                this.setVisible(true);
                this.setEnabled(true);
                this.removeMouseListener(this);
                Gw = null;
            }
            if(issolu) {
                DisableRadio();
                solution();
                EnableRadio();
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
            }
            else if(playtime) CountTime();
            else {
                if(playtime || issolu) break;
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){};
            }
        }
    }
    public void DisableRadio() {
        size1.setEnabled(false);
        size2.setEnabled(false);
        size3.setEnabled(false);
        size4.setEnabled(false);
        bfs.setEnabled(false);
        h1.setEnabled(false);
        h2.setEnabled(false);
        h3.setEnabled(false);
        h4.setEnabled(false);
        h5.setEnabled(false);
        h6.setEnabled(false);
        h7.setEnabled(false);
    }
    public void EnableRadio() {
        size1.setEnabled(true);
        size2.setEnabled(true);
        size3.setEnabled(true);
        size4.setEnabled(true);
        bfs.setEnabled(true);
        h1.setEnabled(true);
        h2.setEnabled(true);
        h3.setEnabled(true);
        h4.setEnabled(true);
        h5.setEnabled(true);
        h6.setEnabled(true);
        h7.setEnabled(true);
    }
    public void mouseClicked(MouseEvent me) {}
    public void mousePressed(MouseEvent me) {
         if(Gw != null) {
            Gw.setVisible(true);
            if(!mute) Toolkit.getDefaultToolkit().beep();
            return;
        }
        if(issolu || !Ju.isEnabled()) return;
        playtime = true;
        //Gốc tọa độ paint hình (4; 4): Hàm g.translate(4, 4);
        int x = (me.getX() - 4) / Ju.getCw(); //(x; y) tọa độ cột và hàng của ô
        int y = (me.getY() - 4) / Ju.getCh();
        int pos = y * Size + x; //Vị trí ô: từ 0 tới 2^n - 1
        if(Ju.checkWin()) {
            win = true;
            this.Disable();
            solveButton.setEnabled(false);
            this.GameWon();
            return;
        }
        else
        {
            if(Ju.blank >= Size && Ju.blank == pos + Size) { // di chuyển ô trống lên trên
                Ju.UP();
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(Ju.blank < Length - Size && Ju.blank == pos - Size){ // di chuyển ô trống xuống dưới
                Ju.DOWN();
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(Ju.blank % Size != 0 && Ju.blank == pos + 1) { // // di chuyển ô trống sang trái
                Ju.LEFT();
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(Ju.blank % Size != Size - 1 && Ju.blank == pos - 1) {
                Ju.RIGHT();
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(!mute) Toolkit.getDefaultToolkit().beep();
            if(Ju.checkWin()) {
                win = true;
                this.Disable();
                solveButton.setEnabled(false);
                this.GameWon();
                return;
            }
        }
    }
    public void mouseReleased(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
    public void keyTyped(KeyEvent ke) {}   
    public void keyReleased(KeyEvent ke) {}
    public void keyPressed(KeyEvent ke) {
        if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {//Esc dừng tìm kiếm kết quả
            issolu = false;
            return;
        }
        if(ke.getKeyCode() == KeyEvent.VK_F4 && !issolu)  //Alt + F4: Thoát game
            if(ke.isAltDown()) {
                this.ExitGame();
            }
        if(issolu || !Ju.isEnabled()) return;    
        
        if(Ju.checkWin()) {
            win = true;
            JF.requestFocus();       
            this.Disable();
            solveButton.setEnabled(false);
            this.GameWon();
            return;
        }
        else
        {
            if(ke.getKeyCode() == KeyEvent.VK_UP && Ju.blank >= Size) {
                playtime = true;
                Ju.UP();
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(ke.getKeyCode() == KeyEvent.VK_DOWN && Ju.blank < Length - Size) {
                playtime = true;
                Ju.DOWN();
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(ke.getKeyCode() == KeyEvent.VK_LEFT && Ju.blank % Size != 0) {
                playtime = true;
                Ju.LEFT();
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(ke.getKeyCode() == KeyEvent.VK_RIGHT && Ju.blank % Size != Size - 1) {
                playtime = true;
                Ju.RIGHT();
                count++;
                moveTextField.setText("   " + count);  
                this.repaint();
            }            
            if(Ju.checkWin())
            {
                win = true;
                JF.requestFocus();                
                this.Disable();
                solveButton.setEnabled(false);
                this.GameWon();
                return;
            }
        }
    }
}

package puzzle;
import java.awt.*;
import javax.swing.*;
class JumbledImage extends JPanel{
    private int Size;
    private int Length;
    protected int blank;
    protected int[] Value;
    private Image bi;
    private int w, h, cw, ch;  // kích thước thực của ảnh
    private int width, height, cw1, ch1;  // kích thước mới: cw1, ch1 là chiều dài và rộng của 1 ô số
    private boolean win = false;
    private int type;
    private Color ColorEbox;
    private Color ColorBoxs;
    public JumbledImage(Image img, int size, int [] val, int t, Color Eb, Color b) {
        this.bi = img;
        this.type = t;
        this.ColorEbox = Eb;
        this.ColorBoxs = b;
        this.Size =size; 
        Length = Size*Size;
        this.Value = val;
        if(type==0) {
            width = w = 400;
            height = h = 400;
        }
        else {
            width = w = bi.getWidth(this);
            height = h = bi.getHeight(this);  
        }
        InitImage();
    }
    
    public void InitImage() // khởi tạo + chỉnh kích thước ảnh vừa với khung
    {        
        cw = w/Size;
        ch = h/Size;            
        cw1 = cw; 
        ch1 = ch;
        int kt = 430;
        if(w > kt || h > kt)
        {
            if(w > h)
            {
                width = kt;
                height = width * h / w;
            }
            else if(w == h)
            {
                width = kt;
                height = kt;
            }
            else 
            {
                height = kt;
                width = height * w / h;
            }
            
            cw1 = width/Size;
            ch1 = height/Size;
        } 
        else if((w < kt || h < kt) && type != 0)
        {
            if(w > h)
            {
                width = kt;
                height = width * h / w;
            }
            else if(w == h)
            {
                width = kt;
                height = kt;
            }
            else 
            {
                height = kt;
                width = height * w / h;
            }
            
            cw1 = width/Size;
            ch1 = height/Size;
        }
        
        blank = posBlank(Value);
        this.repaint();
    }
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public int getCw()
    {
        return cw1;
    }
    
    public int getCh()
    {
        return ch1;
    }
    
    public int[] getValue()
    {
        return Value;
    }
    
    public void UP() {
        if(blank >= Size)
        {
            int tg = Value[blank];
            Value[blank] = Value[blank - Size];
            Value[blank - Size] = tg;
            blank = blank - Size;    
        }
    }
    
    public void DOWN() {
        if(blank < Length - Size)
        {
            int tg = Value[blank];
            Value[blank] = Value[blank + Size];
            Value[blank + Size] = tg;
            blank = blank + Size;
        }
    }
    
    public void LEFT()
    {
        if(blank % Size != 0)
        {
            int tg = Value[blank];
            Value[blank] = Value[blank - 1];
            Value[blank - 1] = tg;
            blank = blank -1;
        }
    }
    
    public void RIGHT()
    {
        if(blank % Size != Size-1)
        {
            int tg = Value[blank];
            Value[blank] = Value[blank + 1];
            Value[blank + 1] = tg;
            blank = blank + 1;
        }
    }  
    
    public int posBlank(int[] Value) {//Tìm vị trí phần tử blank
        int pos=0;
        for(int i = 0; i < Length; i++)
            if(Value[i] == 0)
            {
                pos = i;
                break;
            }        
        return pos;
    }
  
    public boolean checkWin() { //
        int ok=1;
        for(int i = 1; i < Length; i++)
         {
            if(Value[i] == 0)
                return false;
            if(Value[i] == i)
            {
               continue;
            }
            else
            {
                ok = 0;
                break;
            }
         }
        if(ok==1)
        {
            win = true;
            //this.repaint();
            return true;
        }
        else
            return false;
    }

    public void paint(Graphics g) 
    {
        g.translate(4, 4); //Dịch 
        if(type == 0) // tạo khung số
        {
            g.setColor(ColorEbox); 
            g.fillRect(0, 0, width, height);
            for(int i = 0; i < Length; i++)
            {
                int x = (i % Size) * cw1;
                int y = (i / Size) * ch1;
                if(Value[i] != 0)
                {
                    g.setColor(ColorBoxs);
                    g.fillRect(x, y, cw1 - 1, ch1 - 1);
                    g.setColor(Color.white);
                    g.setFont(new Font("Times New Roman", Font.BOLD, 100/Size));
                    g.drawString(Value[i]+"", x + cw1/2 - 2*Size, y + (2*ch1)/3 - Size);
                }
            }
        }
        else if(type == 1) // tạo khung ảnh và số
        {
            if(!win) // trạng thái bất kì
            {
                g.setColor(ColorEbox);
                g.fillRect(0, 0, width, height);
                int dx, dy, sx, sy;
                for(int i = 0; i < Length; i++)
                {
                    if(Value[i] != 0)
                    {
                        sx = (Value[i] % Size) * cw;
                        sy = (Value[i] / Size) * ch;
                        dx = (i % Size) * cw1;
                        dy = (i / Size) * ch1;            
                        g.drawImage(bi, dx, dy, dx+cw1-1, dy+ch1-1, sx, sy, sx+cw, sy+ch, null);
                        g.setColor(Color.red);
                        g.setFont(new Font("Times New Roman", Font.BOLD, 16));
                        g.drawString(Value[i]+"", dx + cw1/2 - 2*Size, dy + (2*ch1)/3 - Size);
                    }
                }
            }
            else  //trạng thái lúc win
            {         
                g.clearRect(0, 0, width, height);
                g.drawImage(bi, 0, 0, width, height, null);
            }
        }
        else ////tạo khung ảnh
        {
            if(!win)
            {
                g.setColor(ColorEbox);
                g.fillRect(0, 0, width, height);
                int dx, dy, sx, sy;
                for(int i = 0; i < Length; i++)
                {
                    if(Value[i] != 0)
                    {
                        sx = (Value[i] % Size) * cw;
                        sy = (Value[i] / Size) * ch;
                        dx = (i % Size) * cw1;
                        dy = (i / Size) * ch1; 
                        g.drawImage(bi, dx, dy, dx+cw1-1, dy+ch1-1, sx, sy, sx+cw, sy+ch, this);
                    }
                }
            }
            else
            {
                g.clearRect(0, 0, width, height);
                g.drawImage(bi, 0, 0, width, height,this);
            }
        }
    }

}

 

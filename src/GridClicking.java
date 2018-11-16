import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//TODO: LIANG LU
//TODO: can't play after lose; after lose, if flag placed in the wrong place, tell; after lose, reveal all numbers

public class GridClicking extends JPanel {
    private int[][] board;
    private boolean[][] reveal;
    private int[][] flag;
//    private int size, flagCounter;
//    private int mine = 25;
    private int size;
    private int flagCounter = 1;
    private boolean[][] isMine;
    private JButton re = new JButton("Restart");

    public GridClicking(int width, int height) {
        setSize(width, height);

        re.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board = new int[15][15];
                reveal =  new boolean[board.length][board[0].length];
//                flag =  new int[board.length][board[0].length];
                for (int r = 0; r < board.length; r++) {
                    for (int c = 0; c < board[0].length; c++) {
                        flag[r][c] = 1;
                    }
                }
                isMine = new boolean[board.length][board[0].length];
                plantMines(1);
                size = 30;
                grabFocus();
                repaint();
            }
        });
        re.setBounds(500, 150, 75, 20);
        add(re);

        board = new int[15][15];
//        for (int i = 0; i < board.length; i++)
//            for (int j = 0; j < board[0].length; j++)
//                board[i][j] = (int) (2 * Math.random());
        reveal =  new boolean[board.length][board[0].length];
        flag =  new int[board.length][board[0].length];
        isMine = new boolean[board.length][board[0].length];


        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                reveal[r][c] = false;
                isMine[r][c] = false;
                flag[r][c] = 1;
            }
        }

        plantMines(1);
        size = 30;
        setupMouseListener();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                g2.setColor(Color.BLACK);
                g2.drawString("Mine: " + flagCounter, 500, 100);
                repaint();

                if (!reveal[r][c] && flag[r][c] == 1) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(c * size, r * size, size, size);
                }else
                    g2.drawRect(c * size, r * size, size, size);

                if(flag[r][c] == 2) {
                    g2.setColor(Color.GRAY);
                    g2.fillRect(c * size, r * size, size, size);
//                    flagCounter --;
                }

                if(board[r][c] != 0)
                    g2.drawString("" + board[r][c], c * size + size/3, r * size + size / 2);

            }
        }

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if(isMine[r][c] == true) {
                    g2.setColor(new Color(255, 0, 0));
                    g2.setStroke(new BasicStroke(4));
                    Font font = new Font("Verdana", Font.BOLD, 60);
                    g2.setFont(font);
                    g2.drawString("GAME OVER", 100, 500);
                }
            }
        }

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if(didWin() == true) {
                    g2.setColor(new Color(255, 0, 0));
                    g2.setStroke(new BasicStroke(4));
                    Font font = new Font("Verdana", Font.BOLD, 60);
                    g2.setFont(font);
                    g2.drawString("You WIN!!", 100, 500);
                }
            }
        }
    }

    public void revealCell(int r, int c){
        if(r >= 0 && r < board.length && c >= 0 && c < board[0].length) {

            if (reveal[r][c] == false) {
                reveal[r][c] = true;
                if (board[r][c] == 0) {
                    revealCell(r, c + 1);
                    revealCell(r, c - 1);
                    revealCell(r + 1, c);
                    revealCell(r - 1, c);

                    if (isLegal(r - 1, c - 1) && board[r - 1][c - 1] > 0)
                        revealCell(r - 1, c - 1);
                    if (isLegal(r - 1, c + 1) && board[r - 1][c + 1] > 0)
                        revealCell(r - 1, c + 1);
                    if (isLegal(r + 1, c - 1) && board[r + 1][c - 1] > 0)
                        revealCell(r + 1, c - 1);
                    if (isLegal(r + 1, c + 1) && board[r + 1][c + 1] > 0)
                        revealCell(r + 1, c + 1);
                }
            }
        }
        repaint();
    }

    public void flagCell(int r, int c){
        if(isLegal(r, c)){
            if(flag[r][c] == 1) {
                flag[r][c] = 2;
                flagCounter --;
//                reveal[r][c] = true;
            }else {
                flag[r][c] = 1;
                flagCounter ++;
            }
            if(flag[r][c] == 0)
                revealCell(r, c);

        }
        repaint();
    }

    public boolean isLegal(int r, int c){
        if(r >= 0 && r < board.length && c >= 0 && c < board[0].length)
            return true;
        return false;
    }

    public void plantMines(int num) {
        int count = 0;
        while (count < num) {
            int r = (int) (Math.random() * board.length);
            int c = (int) (Math.random() * board[0].length);
            if (board[r][c] != -1) {
                board[r][c] = -1;
                count++;
            }
        }
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board[0].length; c++) {
                if(board[r][c] != -1)
                    board[r][c] = neighborMines(r, c);
            }


    }

    public int neighborMines(int r, int c){
        int count = 0;
        if(board[r][c] != -1){
            if (isLegal(r - 1, c - 1) && board[r - 1][c - 1] == -1)
                count++;
            if (isLegal(r - 1, c + 1) && board[r - 1][c + 1] == -1)
                count++;
            if (isLegal(r + 1, c - 1) && board[r + 1][c - 1] == -1)
                count++;
            if (isLegal(r + 1, c + 1) && board[r + 1][c + 1] == -1)
                count++;
            if (isLegal(r - 1, c) && board[r - 1][c] == -1)
                count++;
            if (isLegal(r, c + 1) && board[r][c + 1] == -1)
                count++;
            if (isLegal(r + 1, c) && board[r + 1][c] == -1)
                count++;
            if (isLegal(r, c - 1) && board[r][c - 1] == -1)
                count++;
        }
        return count;
    }

    public boolean isMine(int r, int c){
        if(board[r][c] == -1)
            return isMine[r][c] = true;
        return isMine[r][c] = false;
    }

    public boolean isCorrect(){
        int count = 0;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (flag[r][c] == 2 && board[r][c] == -1) {
                    count ++;
                }
            }
        }

        if(count == 1)
            return true;
        return false;
    }

    public boolean didWin(){
        if(flagCounter == 0 && isCorrect()){
            return true;
        }
        return false;
    }

    public void setupMouseListener(){
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                int r = y / size;
                int c = x / size;

                if(e.getButton() == MouseEvent.BUTTON1) {
                    revealCell(r, c);
                    if (isLegal(r, c))
                        isMine(r, c);
                }
                else {
                    flagCell(r, c);
//                    flagCounter --;
                }

//                if(board[r][c] == -1 && sum == 20){
//                    plantMines(25);
//                }

//                board[r][c] = (board[r][c] + 1)%2;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    //sets ups the panel and frame.  Probably not much to modify here.
    public static void main(String[] args) {
        JFrame window = new JFrame("Mindsweeper - LIANG LU");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, 600, 600 + 22); //(x, y, w, h) 22 due to title bar.

        GridClicking panel = new GridClicking(600, 600);
        panel.setLayout(null);

        panel.setFocusable(true);
        panel.grabFocus();

        window.add(panel);
        window.setVisible(true);
        window.setResizable(false);
    }

}


/*
//original code for reference (from APCS right after turn in)
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GridClicking extends JPanel {
    private int[][] board;
    private boolean[][] reveal;
    private int[][] flag;
    private int size;
    private int flagCounter = 25;
    private boolean[][] isMine;

    public GridClicking(int width, int height) {
        setSize(width, height);

        board = new int[15][15];

//        for (int i = 0; i < board.length; i++)
//            for (int j = 0; j < board[0].length; j++)
//                board[i][j] = (int) (2 * Math.random());
        reveal =  new boolean[board.length][board[0].length];
        flag =  new int[board.length][board[0].length];
        isMine = new boolean[board.length][board[0].length];

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                reveal[r][c] = false;
                isMine[r][c] = false;
                flag[r][c] = 1;
            }
        }

        plantMines(25);
        size = 30;
        setupMouseListener();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                g2.setColor(Color.BLACK);
                g2.drawString("Mine: " + flagCounter, 500, 100);
                repaint();

                if (!reveal[r][c] && flag[r][c] == 1) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(c * size, r * size, size, size);
                }else
                    g2.drawRect(c * size, r * size, size, size);

                if(flag[r][c] == 2) {
                    g2.setColor(Color.GRAY);
                    g2.fillRect(c * size, r * size, size, size);
//                    flagCounter --;
                }

                if(board[r][c] != 0)
                    g2.drawString("" + board[r][c], c * size + size/3, r * size + size / 2);

            }
        }

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if(isMine[r][c] == true) {
                    g2.setColor(new Color(255, 0, 0));
                    g2.setStroke(new BasicStroke(4));
                    Font font = new Font("Verdana", Font.BOLD, 60);
                    g2.setFont(font);
                    g2.drawString("GAME OVER", 100, 500);
                }
            }
        }

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if(didWin() == true) {
                    g2.setColor(new Color(255, 0, 0));
                    g2.setStroke(new BasicStroke(4));
                    Font font = new Font("Verdana", Font.BOLD, 60);
                    g2.setFont(font);
                    g2.drawString("You WIN!!", 100, 500);
                }
            }
        }
    }

    public void revealCell(int r, int c){
        if(r >= 0 && r < board.length && c >= 0 && c < board[0].length) {

            if (reveal[r][c] == false) {
                reveal[r][c] = true;
                if (board[r][c] == 0) {
                    revealCell(r, c + 1);
                    revealCell(r, c - 1);
                    revealCell(r + 1, c);
                    revealCell(r - 1, c);

                    if (isLegal(r - 1, c - 1) && board[r - 1][c - 1] > 0)
                        revealCell(r - 1, c - 1);
                    if (isLegal(r - 1, c + 1) && board[r - 1][c + 1] > 0)
                        revealCell(r - 1, c + 1);
                    if (isLegal(r + 1, c - 1) && board[r + 1][c - 1] > 0)
                        revealCell(r + 1, c - 1);
                    if (isLegal(r + 1, c + 1) && board[r + 1][c + 1] > 0)
                        revealCell(r + 1, c + 1);
                }
            }
        }
        repaint();
    }

    public void flagCell(int r, int c){
        if(isLegal(r, c)){
            if(flag[r][c] == 1) {
                flag[r][c] = 2;
                flagCounter --;
//                reveal[r][c] = true;
            }else {
                flag[r][c] = 1;
                flagCounter ++;
            }
            if(flag[r][c] == 0)
                revealCell(r, c);

        }
        repaint();
    }

    public boolean isLegal(int r, int c){
        if(r >= 0 && r < board.length && c >= 0 && c < board[0].length)
            return true;
        return false;
    }

    public void plantMines(int num) {
        int count = 0;
        while (count < num) {
            int r = (int) (Math.random() * board.length);
            int c = (int) (Math.random() * board[0].length);
            if (board[r][c] != -1) {
                board[r][c] = -1;
                count++;
            }
        }
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board[0].length; c++) {
                if(board[r][c] != -1)
                    board[r][c] = neighborMines(r, c);
            }


    }

    public int neighborMines(int r, int c){
        int count = 0;
        if(board[r][c] != -1){
            if (isLegal(r - 1, c - 1) && board[r - 1][c - 1] == -1)
                count++;
            if (isLegal(r - 1, c + 1) && board[r - 1][c + 1] == -1)
                count++;
            if (isLegal(r + 1, c - 1) && board[r + 1][c - 1] == -1)
                count++;
            if (isLegal(r + 1, c + 1) && board[r + 1][c + 1] == -1)
                count++;
            if (isLegal(r - 1, c) && board[r - 1][c] == -1)
                count++;
            if (isLegal(r, c + 1) && board[r][c + 1] == -1)
                count++;
            if (isLegal(r + 1, c) && board[r + 1][c] == -1)
                count++;
            if (isLegal(r, c - 1) && board[r][c - 1] == -1)
                count++;
        }
        return count;
    }

    public boolean isMine(int r, int c){
        if(board[r][c] == -1)
            return isMine[r][c] = true;
        return isMine[r][c] = false;
    }

    public boolean isCorrect(){
        int count = 0;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (flag[r][c] == 2 && board[r][c] == -1) {
                    count ++;
                }
            }
        }

        if(count == 25)
            return true;
        return false;
    }

    public boolean didWin(){
        if(flagCounter == 0 && isCorrect()){
            return true;
        }
        return false;
    }

    public void setupMouseListener(){
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                int r = y / size;
                int c = x / size;

                if(e.getButton() == MouseEvent.BUTTON1) {
                    revealCell(r, c);
                    if (isLegal(r, c))
                        isMine(r, c);
                }
                else {
                    flagCell(r, c);
//                    flagCounter --;
                }

//                if(board[r][c] == -1 && sum == 20){
//                    plantMines(25);
//                }

//                board[r][c] = (board[r][c] + 1)%2;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    //sets ups the panel and frame.  Probably not much to modify here.
    public static void main(String[] args) {
        JFrame window = new JFrame("Mindsweeper - LIANG LU");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, 600, 600 + 22); //(x, y, w, h) 22 due to title bar.

        GridClicking panel = new GridClicking(600, 600);

        panel.setFocusable(true);
        panel.grabFocus();

        window.add(panel);
        window.setVisible(true);
        window.setResizable(false);
    }

}
 */
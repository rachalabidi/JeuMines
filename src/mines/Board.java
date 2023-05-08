package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Board extends JPanel {
	private static final long serialVersionUID = 6195235521361212179L;
	
	private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int mines_left;
    private Image[] img;
    private int mines = 40;
    private int rows = 16;
    private int cols = 16;
    private int all_cells;
    private JLabel statusbar;


    public Board(JLabel statusbar) {

        this.statusbar = statusbar;

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
			img[i] =
                    (new ImageIcon(getClass().getClassLoader().getResource((i)
            			    + ".gif"))).getImage();
        }

        setDoubleBuffered(true);

        addMouseListener(new MinesAdapter());
        newGame();
    }


    
    
    public void newGame() {
        initializeGame();
        deployMines();
    }

    private void initializeGame() {
        inGame = true;
        mines_left = mines;
        all_cells = rows * cols;
        field = new int[all_cells];
        for (int i = 0; i < all_cells; i++) {
            field[i] = COVER_FOR_CELL;
        }
        statusbar.setText(Integer.toString(mines_left));
    }

    private void deployMines() {
        Random random = new Random();
        int mines_deployed = 0;
        while (mines_deployed < mines) {
            int position = (int) (all_cells * random.nextDouble());
            if ((position < all_cells) && (field[position] != COVERED_MINE_CELL)) {
                field[position] = COVERED_MINE_CELL;
                mines_deployed++;
                incrementAdjacentCells(position);
            }
        }
    }

    private void incrementAdjacentCells(int position) {
        int current_col = position % cols;
        if (current_col > 0) {
            int cell = position - 1 - cols;
            if (cell >= 0 && field[cell] != COVERED_MINE_CELL) {
                field[cell]++;
            }
            cell = position - 1;
            if (cell >= 0 && field[cell] != COVERED_MINE_CELL) {
                field[cell]++;
            }
            cell = position + cols - 1;
            if (cell < all_cells && field[cell] != COVERED_MINE_CELL) {
                field[cell]++;
            }
        }
        int cell = position - cols;
        if (cell >= 0 && field[cell] != COVERED_MINE_CELL) {
            field[cell]++;
        }
        cell = position + cols;
        if (cell < all_cells && field[cell] != COVERED_MINE_CELL) {
            field[cell]++;
        }
        if (current_col < (cols - 1)) {
            cell = position - cols + 1;
            if (cell >= 0 && field[cell] != COVERED_MINE_CELL) {
                field[cell]++;
            }
            cell = position + cols + 1;
            if (cell < all_cells && field[cell] != COVERED_MINE_CELL) {
                field[cell]++;
            }
            cell = position + 1;
            if (cell < all_cells && field[cell] != COVERED_MINE_CELL) {
                field[cell]++;
            }
        }
    }  


    public void find_empty_cells(int j) {

        int current_col = j % cols;
        int cell;

        if (current_col > 0) { 
            cell = j - cols - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols - 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }

        cell = j - cols;
        if (cell >= 0)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        cell = j + cols;
        if (cell < all_cells)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        if (current_col < (cols - 1)) {
            cell = j - cols + 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }

    }

    public void paint(Graphics g) {

        int cell = 0;
        int uncover = 0;


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                cell = field[(i * cols) + j];

                if (inGame && cell == MINE_CELL)
                    inGame = false;

                if (!inGame) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }


                } else {
                    if (cell > COVERED_MINE_CELL)
                        cell = DRAW_MARK;
                    else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                    (i * CELL_SIZE), this);
            }
        }


        if (uncover == 0 && inGame) {
            inGame = false;
            statusbar.setText("Game won");
        } else if (!inGame)
            statusbar.setText("Game lost");
    }


    class MinesAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean rep = false;



            if (!inGame) {
                newGame();
                repaint();
                return;
            }

            int cellValue = field[(cRow * cols) + cCol];
            switch (e.getButton()) {
                case MouseEvent.BUTTON3: // Right mouse button
                    if (cellValue <= COVERED_MINE_CELL) {
                        if (mines_left > 0) {
                            cellValue += MARK_FOR_CELL;
                            mines_left--;
                            statusbar.setText(Integer.toString(mines_left));
                        } else {
                            statusbar.setText("No marks left");
                        }
                    } else if (cellValue >= MARKED_MINE_CELL) {
                        cellValue -= MARK_FOR_CELL;
                        mines_left++;
                        statusbar.setText(Integer.toString(mines_left));
                    }
                    break;
                case MouseEvent.BUTTON1: // Left mouse button
                    if (cellValue <= COVERED_MINE_CELL) {
                        if (cellValue == MINE_CELL) {
                            inGame = false;
                        } else {
                            cellValue -= COVER_FOR_CELL;
                            rep = true;
                            if (cellValue == EMPTY_CELL) {
                                find_empty_cells((cRow * cols) + cCol);
                            }
                        }
                    }
                    break;
            }

            field[(cRow * cols) + cCol] = cellValue;
                if (rep)
                    repaint();

            }
        }
    }

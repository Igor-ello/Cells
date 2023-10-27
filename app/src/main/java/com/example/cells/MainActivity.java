package com.example.cells;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView[][] cells;
    String[][] cellsText;
    boolean[][] opened, cellsFlag;
    TextView minesCounter, menuText;
    LinearLayout imStopGame;

    final int MINESCONST = 35;
    int minesCurrent = MINESCONST, minesReality = MINESCONST;
    final int WIDTH = 10, HEIGHT = 20;
    boolean isStart = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imStopGame = findViewById(R.id.imStopGame);
        menuText = findViewById(R.id.menuText);
        minesCounter = findViewById(R.id.mines);

        initCells();
        setListeners();

        generate();
    }

    private void setListeners() {
        findViewById(R.id.refresh1).setOnClickListener(view -> {
            resetCells();
            generate();
        });
        findViewById(R.id.refresh2).setOnClickListener(view -> {
            imStopGame.setVisibility(View.GONE);
            resetCells();
            generate();
        });
    }

    public void generate() {
        updateMinesCounter();
        setColorToCells();
        generateAction();
    }

    //States
    public void isRunning(int i, int j){
        if(isStart) {
            if (!cellsText[i][j].equals(String.valueOf(-1))) {
                cellsText[i][j] = String.valueOf(setTextToCell(i, j));
                setTextColorToCell(i, j);
            }
        }
    }

    public void firstRun(int i, int j){
        if(!isStart) {
            isStart = true;
            generateMines(i, j);
            setColorToCells();
            generateAction();
        }
    }

    @SuppressLint("SetTextI18n")
    public void loseTheGame(View view, int i, int j){
        if(cellsFlag[i][j]){
            minesCurrent++;
            cellsFlag[i][j] = false;
            updateMinesCounter();
        }
        setBackColor(view, i, j);

        setPictureToCell(view, ContextCompat.getDrawable(getApplicationContext(), R.drawable.explosion));

        menuText.setText("YOU LOSE!!!");
        imStopGame.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void winTheGame(){
        menuText.setText("YOU WIN!!!");
        imStopGame.setVisibility(View.VISIBLE);
    }

    public void generateAction() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                final int finalI = i;
                final int finalJ = j;

                isRunning(i, j);
                cells[i][j].setOnClickListener(view -> { // короткий клик
                    firstRun(finalI, finalJ); //первый запуск

                    if (cellsText[finalI][finalJ].equals("-1")) { // если бомба
                        loseTheGame(view, finalI, finalJ); //поражение
                    } else {
                        openCells(finalI, finalJ); //открыть клетку
                    }
                });
                cells[i][j].setOnLongClickListener(view -> { //долгий клик
                    if(isStart) { // если клетка не открыта
                        if (!cellsFlag[finalI][finalJ]) {
                            setFlag(view, finalI, finalJ); // поставить флаг
                        } else {
                            deleteFlag(view, finalI, finalJ); // убрать флаг
                        }
                        updateMinesCounter();
                    }

                    if (minesReality == 0) {
                        winTheGame(); // победа
                    }
                    return true;
                });
            }
        }
    }


    //Cells
    public void initCells() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        GridLayout layout = findViewById(R.id.grid);
        layout.removeAllViews(); // Удаляем все существующие представления в GridLayout (если они есть)
        layout.setColumnCount(WIDTH); // Устанавливаем количество столбцов в GridLayout (WIDTH - константа)

        cells = new TextView[HEIGHT][WIDTH];
        cellsText = new String[HEIGHT][WIDTH];
        cellsFlag = new boolean[HEIGHT][WIDTH];
        opened = new boolean[HEIGHT][WIDTH];

        for (int i = 0; i < HEIGHT; i++) { // Создаем цикл для создания кнопок (Cells)
            for (int j = 0; j < WIDTH; j++) {
                // Используем LayoutInflater для надувания макета кнопки из R.layout.cell
                cells[i][j] = (TextView) inflater.inflate(R.layout.cell, layout, false);
                layout.addView(cells[i][j]);
                cells[i][j].setText("");
                cellsText[i][j] = "0"; // базовые значения
                cellsFlag[i][j] = false;
            }
        }
    }

    public void resetCells(){
        minesCurrent = MINESCONST;
        minesReality = MINESCONST;
        cellsText = new String[HEIGHT][WIDTH];
        opened = new boolean[HEIGHT][WIDTH];
        cellsFlag = new boolean[HEIGHT][WIDTH];
        isStart = false;

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j].setText("");
                cellsText[i][j] = "0";
                cellsFlag[i][j] = false;
            }
        }
    }

    public void openCells(int i, int j) {
        if (i < 0 || i >= cells.length || j < 0 || j >= cells[0].length || opened[i][j]) {
            return; // Клетка находится за пределами поля или уже открыта
        }

        opened[i][j] = true;
        setBackColor(i, j);

        if (!cellsText[i][j].equals("0")) cells[i][j].setText(cellsText[i][j]);

        if(cellsFlag[i][j]){
            minesCurrent++;
            updateMinesCounter();
        }


        // Если текущая клетка пуста, рекурсивно раскрываем соседние клетки
        if (cellsText[i][j].equals("0")) {
            openCells(i - 1, j);
            openCells(i + 1, j);
            openCells(i, j - 1);
            openCells(i, j + 1);
            // Диагональ
            openCells(i - 1, j - 1);
            openCells(i - 1, j + 1);
            openCells(i + 1, j - 1);
            openCells(i + 1, j + 1);
        }
    }

    public void setColorToCells() { //TODO покрасить цифры
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                setFrontColor(i, j);
            }
        }
    }


    //Cell
    public int setTextToCell(int i, int j) {
        int count = 0;

        int topLock = i - 1, bottomLock = i + 1, rightLock = j + 1, leftLock = j - 1;
        if (j == 0) leftLock = 0;
        else if (j == WIDTH - 1) rightLock = WIDTH - 1;
        if (i == 0) topLock = 0;
        else if (i == HEIGHT - 1) bottomLock = HEIGHT - 1;

        for (int m = topLock; m <= bottomLock; m++) {
            for (int n = leftLock; n <= rightLock; n++) {
                if (cellsText[m][n].equals("-1")) count++;
            }
        }

        return count;
    }

    public void  setTextColorToCell(int i, int j){
        int[] colors = {R.color.number1, R.color.number2, R.color.number3, R.color.number4, R.color.number5, R.color.number6, R.color.number7, R.color.number8};
        if(!cellsText[i][j].equals("0"))
            cells[i][j].setTextColor(ContextCompat.getColor(getApplicationContext(), colors[Integer.parseInt(cellsText[i][j]) - 1]));
    }

    public void setPictureToCell(View view, Drawable drawable) {
        Drawable[] layers = new Drawable[2];

        Drawable currentBackground = view.getBackground();
        layers[0] = currentBackground; // Текущий фон
        // Получаем значок (изображение) из ресурсов
        Drawable iconDrawable = drawable;
        layers[1] = iconDrawable; // Значок
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        // Устанавливаем LayerDrawable как фон элемента View
        view.setBackground(layerDrawable);
    }


    //Mines
    public void generateMines(int i, int j) {
        int flag = 0;
        while (flag < MINESCONST) {
            int positionX = getRandomPosition(HEIGHT);
            int positionY = getRandomPosition(WIDTH);
            if ((!(cellsText[positionX][positionY].equals("-1")))) {
                cellsText[positionX][positionY] = "-1";
                if(setTextToCell(i, j) != 0)
                    cellsText[positionX][positionY] = "0";
                else flag++;
            }
        }
        minesCurrent = MINESCONST;
        minesReality = MINESCONST;
        updateMinesCounter();
    }

    public void updateMinesCounter(){
        minesCounter.setText(String.valueOf(minesCurrent + " / " + MINESCONST));
    }


    //Flag
    public void setFlag(View view, int i, int j){
        if (minesCurrent - 1 >= 0) {
            minesCurrent--;
            setPictureToCell(view, ContextCompat.getDrawable(this, R.drawable.flag));

            if(cellsText[i][j].equals("-1"))
                minesReality--;
        }
        cellsFlag[i][j] = true;
    }

    public void deleteFlag(View view, int i, int j){
        minesCurrent++;
        setFrontColor(view, i, j);
        cellsFlag[i][j] = false;
    }


    //Colors
    public void setBackColor(View view, int i, int j){
        if((i+j)%2 == 0) view.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGray));
        else view.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGray));
    }
    public void setBackColor(int i, int j){
        if((i+j)%2 == 0) cells[i][j].setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
        else cells[i][j].setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));
    }

    public void setFrontColor(View view, int i, int j){
        if((i+j)%2 == 0) view.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
        else view.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen));
    }
    public void setFrontColor(int i, int j){
        if((i+j)%2 == 0) cells[i][j].setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGreen));
        else cells[i][j].setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGreen));
    }


    //Other
    public int getRandomPosition(int value) {
        return new Random().nextInt(value);
    }

}
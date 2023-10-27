package com.example.cells;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView[][] cells;
    String[][] cellsText;
    boolean[][] opened; // Массив для отслеживания открытых клеток
    TextView minesCounter, refresh, refreshButton, menuText;
    LinearLayout imStopGame;

    final int MINESCONST = 35;
    int minesCurrent = MINESCONST;
    int minesReality = MINESCONST;
    final int WIDTH = 10;
    final int HEIGHT = 20 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imStopGame = findViewById(R.id.imStopGame);
        menuText = findViewById(R.id.menuText);

        generateCells();
        generate();

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(view -> {
            resetCells();
            generate();
        });
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(view -> {
            imStopGame.setVisibility(View.GONE);
            resetCells();
            generate();
        });
    }

    public void generate() {
        minesCounter = findViewById(R.id.mines);
        minesCounter.setText(String.valueOf(minesCurrent + " / " + MINESCONST));

        generateMines();
        generateAction();
        setColorToCells();
    }

    public void generateCells() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        GridLayout layout = findViewById(R.id.grid);
        layout.removeAllViews(); // Удаляем все существующие представления в GridLayout (если они есть)
        layout.setColumnCount(WIDTH); // Устанавливаем количество столбцов в GridLayout (WIDTH - константа)

        cells = new TextView[HEIGHT][WIDTH];
        cellsText = new String[HEIGHT][WIDTH];
        opened = new boolean[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) { // Создаем цикл для создания кнопок (Cells)
            for (int j = 0; j < WIDTH; j++) {
                // Используем LayoutInflater для надувания макета кнопки из R.layout.cell
                cells[i][j] = (TextView) inflater.inflate(R.layout.cell, layout, false);
                layout.addView(cells[i][j]);
                cellsText[i][j] = "0";
            }
        }
    }
    public void resetCells(){
        minesCurrent = MINESCONST;
        minesReality = MINESCONST;
        cellsText = new String[HEIGHT][WIDTH];
        opened = new boolean[HEIGHT][WIDTH];

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j].setText("");
                cellsText[i][j] = "0";
            }
        }
    }
    public void generateMines() {
        int flag = 0;
        while (flag < MINESCONST) {
            int positionX = getRandomPosition(HEIGHT);
            int positionY = getRandomPosition(WIDTH);
            if (!cellsText[positionX][positionY].equals("-1")) {
                cellsText[positionX][positionY] = "-1";
                flag++;
            }
        }
    }

    public void generateAction() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                final int finalI = i;
                final int finalJ = j;

                if (!cellsText[i][j].equals(String.valueOf(-1))) {
                    cellsText[i][j] = String.valueOf(setTextToCell(i, j));
                    setTextColorToCell(i, j);
                }



                cells[i][j].setOnClickListener(view -> {
                    if(hasIcon(view)) {
                        minesCurrent++;
                        minesCounter.setText(String.valueOf(minesCurrent + " / " + MINESCONST));
                    }
                    if (cellsText[finalI][finalJ].equals("-1")) {
                        if((finalI+finalJ)%2 == 0) //TODO разобраться с флагами
                            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
                        else view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));

                        setPictureToCell(view, ContextCompat.getDrawable(getApplicationContext(), R.drawable.explosion));

                        menuText.setText("YOU LOSE!!!");
                        imStopGame.setVisibility(View.VISIBLE);
                    } else {
                        openCells(finalI, finalJ);
                    }
                });
                cells[i][j].setOnLongClickListener(view -> {
                    if(minesCurrent-1 >= 0){
                        minesCurrent--;
                        setPictureToCell(view, ContextCompat.getDrawable(getApplicationContext(), R.drawable.flag));
                    }

                    minesCounter.setText(String.valueOf(minesCurrent + " / " + MINESCONST));
                    if(cellsText[finalI][finalJ].equals("-1"))
                        minesReality--;
                    if (minesReality == 0) {
                        menuText.setText("YOU WIN!!!");
                    }
                    return true;
                });
            }
        }
    }

    public void openCells(int i, int j) {
        if (i < 0 || i >= cells.length || j < 0 || j >= cells[0].length || opened[i][j]) {
            return; // Клетка находится за пределами поля или уже открыта
        }

        opened[i][j] = true;
        if((i+j)%2 == 0) cells[i][j].setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
        else cells[i][j].setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGray));

        cells[i][j].setText(cellsText[i][j]);


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
        if(!cellsText[i][j].equals(""))
            cells[i][j].setTextColor(ContextCompat.getColor(getApplicationContext(), colors[Integer.parseInt(cellsText[i][j])]));
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
    public boolean hasIcon(View view) {
        Drawable background = view.getBackground();
        if (background instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) background;
            int numberOfLayers = layerDrawable.getNumberOfLayers();
            // Проверяем, есть ли второй слой, который будет значком
            if (numberOfLayers >= 2) {
                Drawable iconDrawable = layerDrawable.getDrawable(1); // Второй слой
                return iconDrawable != null;
            }
        }
        return false; // Значок отсутствует
    }




    public void setColorToCells() { //TODO покрасить цифры
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if ((i + j) % 2 == 0) cells[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
                else cells[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen));
                Log.d("MyLog", "setColorToCells()");
            }
        }
    }

    public int getRandomPosition(int value) {
        return new Random().nextInt(value);
    }

}
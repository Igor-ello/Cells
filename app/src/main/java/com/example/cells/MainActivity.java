package com.example.cells;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView[][] cells;
    boolean[][] opened; // Массив для отслеживания открытых клеток
    TextView minesCounter, refreshButton;

    final int MINESCONST = 15;
    int minesCurrent = MINESCONST;
    final int WIDTH = 10;
    final int HEIGHT = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateCells();
        generate();

        refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(view -> {
            resetCells();
            generate();
        });
    }

    public void generate() {
        minesCounter = findViewById(R.id.mines);
        minesCounter.setText("" + minesCurrent + " / " + MINESCONST);

        generateMines();
        generateAction();
        hideTextToCells();
        setColorToCells();
    }

    public void generateCells() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        GridLayout layout = findViewById(R.id.grid);
        layout.removeAllViews(); // Удаляем все существующие представления в GridLayout (если они есть)
        layout.setColumnCount(WIDTH); // Устанавливаем количество столбцов в GridLayout (WIDTH - константа)

        cells = new TextView[HEIGHT][WIDTH];
        opened = new boolean[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) { // Создаем цикл для создания кнопок (Cells)
            for (int j = 0; j < WIDTH; j++) {
                // Используем LayoutInflater для надувания макета кнопки из R.layout.cell
                cells[i][j] = (TextView) inflater.inflate(R.layout.cell, layout, false);
                layout.addView(cells[i][j]);
            }
        }
    }
    public void resetCells(){
        opened = new boolean[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j].setText(String.valueOf(0));
            }
        }
    }
    public void generateMines() {
        int flag = 0;
        while (flag < MINESCONST) {
            int positionX = getRandomPosition(HEIGHT);
            int positionY = getRandomPosition(WIDTH);
            if (!cells[positionX][positionY].getText().equals("-1")) {
                cells[positionX][positionY].setText("-1");
                flag++;
            }
        }
    }

    public void generateAction() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                final int finalI = i;
                final int finalJ = j;

                if (!cells[i][j].getText().equals("-1"))
                    cells[i][j].setText(String.valueOf(setTextToCell(i, j)));

                cells[i][j].setOnClickListener(view -> {
                    if((finalI+finalJ)%2 == 0) view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
                    else view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGreen));
                    if (cells[finalI][finalJ].getText().equals("-1")) {
                        Toast.makeText(getApplicationContext(), "LOSE!!!", Toast.LENGTH_LONG).show();
                    } else {
                        openCells(finalI, finalJ);
                        Log.d("MyLog", "Tap on 0");
                    }
                });
                cells[i][j].setOnLongClickListener(view -> {
                    setFlagToCell(view);
                    minesCurrent--;
                    minesCounter.setText(String.valueOf(minesCurrent + " / " + MINESCONST));
                    if (minesCurrent == 0) {
                        Toast.makeText(getApplicationContext(), "WIN!!!", Toast.LENGTH_LONG).show();
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


        // Если текущая клетка пуста, рекурсивно раскрываем соседние клетки
        if (cells[i][j].getText().equals("0")) {
            openCells(i - 1, j);
            openCells(i + 1, j);
            openCells(i, j - 1);
            openCells(i, j + 1);
        }
    }

    public void hideTextToCells() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j].setTextColor(Color.RED);
                if ((i + j) % 2 == 0)
                    cells[i][j].setTextColor(ContextCompat.getColor(this, R.color.lightGreen));
                else cells[i][j].setTextColor(ContextCompat.getColor(this, R.color.darkGreen));
            }
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
                if (cells[m][n].getText().equals("-1")) count++;
            }
        }

        return count;
    }

    public void setFlagToCell(View view) {
        Drawable[] layers = new Drawable[2];

        Drawable currentBackground = view.getBackground();
        layers[0] = currentBackground; // Текущий фон
        // Получаем значок (изображение) из ресурсов
        Drawable iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.flag);
        layers[1] = iconDrawable; // Значок
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        // Устанавливаем LayerDrawable как фон элемента View
        view.setBackground(layerDrawable);
    }


    public void setColorToCells() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if ((i + j) % 2 == 0) cells[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
                else cells[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen));
            }
        }
    }



    public int getRandomPosition(int value) {
        return new Random().nextInt(value);
    }

}